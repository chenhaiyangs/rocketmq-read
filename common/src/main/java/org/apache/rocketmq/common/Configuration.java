/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.common;

import org.apache.rocketmq.logging.InternalLogger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 配置类
 * @author ;
 */
public class Configuration {

    private final InternalLogger log;
    /**
     * 配置对象列表
     */
    private List<Object> configObjectList = new ArrayList<Object>(4);
    /**
     * 消息存储的path
     */
    private String storePath;
    /**
     * 从配置获取storepath
     */
    private boolean storePathFromConfig = false;
    /**
     * storePathObject
     */
    private Object storePathObject;
    /**
     * storePathField的字段
     */
    private Field storePathField;
    /**
     * 数据版本
     */
    private DataVersion dataVersion = new DataVersion();
    /**
     * 读写锁
     */
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * All properties include configs in object and extend properties.
     * 所有的配置
     */
    private Properties allConfigs = new Properties();

    public Configuration(InternalLogger log) {
        this.log = log;
    }
    public Configuration(InternalLogger log, Object... configObjects) {
        this.log = log;
        if (configObjects == null || configObjects.length == 0) {
            return;
        }
        for (Object configObject : configObjects) {
            registerConfig(configObject);
        }
    }
    public Configuration(InternalLogger log, String storePath, Object... configObjects) {
        this(log, configObjects);
        this.storePath = storePath;
    }

    /**
     * register config object
     * 注册config对象
     * @return the current Configuration object
     */
    public Configuration registerConfig(Object configObject) {
        try {
            readWriteLock.writeLock().lockInterruptibly();

            try {
                Properties registerProps = MixAll.object2Properties(configObject);
                merge(registerProps, this.allConfigs);
                configObjectList.add(configObject);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("registerConfig lock error");
        }
        return this;
    }

    /**
     * register config properties
     * 注册properties
     * @return the current Configuration object
     */
    public Configuration registerConfig(Properties extProperties) {
        if (extProperties == null) {
            return this;
        }

        try {
            readWriteLock.writeLock().lockInterruptibly();
            try {
                merge(extProperties, this.allConfigs);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("register lock error. {}" + extProperties);
        }

        return this;
    }

    /**
     * The store path will be gotten from the field of object.
     * 设置StorePath从config对象里，对象和字段
     * @throws java.lang.RuntimeException if the field of object is not exist.
     */
    public void setStorePathFromConfig(Object object, String fieldName) {
        assert object != null;

        try {
            readWriteLock.writeLock().lockInterruptibly();

            try {
                this.storePathFromConfig = true;
                this.storePathObject = object;
                // check
                this.storePathField = object.getClass().getDeclaredField(fieldName);
                assert this.storePathField != null
                    && !Modifier.isStatic(this.storePathField.getModifiers());
                this.storePathField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("setStorePathFromConfig lock error");
        }
    }

    /**
     * 获取storePath
     * @return ;
     */
    private String getStorePath() {
        String realStorePath = null;
        try {
            readWriteLock.readLock().lockInterruptibly();

            try {
                realStorePath = this.storePath;

                if (this.storePathFromConfig) {
                    try {
                        realStorePath = (String) storePathField.get(this.storePathObject);
                    } catch (IllegalAccessException e) {
                        log.error("getStorePath error, ", e);
                    }
                }
            } finally {
                readWriteLock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("getStorePath lock error");
        }

        return realStorePath;
    }

    public void setStorePath(final String storePath) {
        this.storePath = storePath;
    }

    /**
     * 更新配置
     * @param properties ;
     */
    public void update(Properties properties) {
        try {
            readWriteLock.writeLock().lockInterruptibly();

            try {
                // the property must be exist when update
                mergeIfExist(properties, this.allConfigs);

                for (Object configObject : configObjectList) {
                    // not allConfigs to update...
                    //properties更新config对象
                    MixAll.properties2Object(properties, configObject);
                }

                this.dataVersion.nextVersion();

            } finally {
                readWriteLock.writeLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("update lock error, {}", properties);
            return;
        }

        persist();
    }

    /**
     * 将配置持久化到storeconfig
     */
    public void persist() {
        try {
            readWriteLock.readLock().lockInterruptibly();

            try {
                String allConfigs = getAllConfigsInternal();

                MixAll.string2File(allConfigs, getStorePath());
            } catch (IOException e) {
                log.error("persist string2File error, ", e);
            } finally {
                readWriteLock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("persist lock error");
        }
    }

    /**
     * 获取全部配置的String版本
     * @return ;
     */
    public String getAllConfigsFormatString() {
        try {
            readWriteLock.readLock().lockInterruptibly();

            try {
                return getAllConfigsInternal();
            } finally {
                readWriteLock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("getAllConfigsFormatString lock error");
        }

        return null;
    }

    /**
     * 获取数据版本
     * @return ;
     */
    public String getDataVersionJson() {
        return this.dataVersion.toJson();
    }


    /**
     * 获取所有的配置
     * @return ;
     */
    public Properties getAllConfigs() {
        try {
            readWriteLock.readLock().lockInterruptibly();

            try {

                return this.allConfigs;

            } finally {
                readWriteLock.readLock().unlock();
            }
        } catch (InterruptedException e) {
            log.error("getAllConfigs lock error");
        }

        return null;
    }

    /**
     * 获取全部的配置
     * @return ;
     */
    private String getAllConfigsInternal() {
        StringBuilder stringBuilder = new StringBuilder();

        // reload from config object ?
        for (Object configObject : this.configObjectList) {
            Properties properties = MixAll.object2Properties(configObject);
            if (properties != null) {
                merge(properties, this.allConfigs);
            } else {
                log.warn("getAllConfigsInternal object2Properties is null, {}", configObject.getClass());
            }
        }

        {
            stringBuilder.append(MixAll.properties2String(this.allConfigs));
        }

        return stringBuilder.toString();
    }


    /**
     * 合并propertirs
     * @param from from
     * @param to to
     */
    private void merge(Properties from, Properties to) {
        for (Object key : from.keySet()) {
            Object fromObj = from.get(key), toObj = to.get(key);
            if (toObj != null && !toObj.equals(fromObj)) {
                log.info("Replace, key: {}, value: {} -> {}", key, toObj, fromObj);
            }
            to.put(key, fromObj);
        }
    }
    private void mergeIfExist(Properties from, Properties to) {
        for (Object key : from.keySet()) {
            if (!to.containsKey(key)) {
                continue;
            }

            Object fromObj = from.get(key), toObj = to.get(key);
            if (toObj != null && !toObj.equals(fromObj)) {
                log.info("Replace, key: {}, value: {} -> {}", key, toObj, fromObj);
            }
            to.put(key, fromObj);
        }
    }

}
