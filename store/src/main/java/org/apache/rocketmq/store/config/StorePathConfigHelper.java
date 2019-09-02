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
package org.apache.rocketmq.store.config;

import java.io.File;

/**
 * 存储path配置工具类
 * @author ;
 */
public class StorePathConfigHelper {

    /**
     * 获取某一个消费队列的存储path
     * @param rootDir rootDir
     * @return ;
     */
    public static String getStorePathConsumeQueue(final String rootDir) {
        return rootDir + File.separator + "consumequeue";
    }

    /**
     * 获取某一个消费队列扩展配置的文件地址
     * @param rootDir root
     * @return ;
     */
    public static String getStorePathConsumeQueueExt(final String rootDir) {
        return rootDir + File.separator + "consumequeue_ext";
    }

    /**
     * 获取存储path的Index
     * @param rootDir rootDir
     * @return ;
     */
    public static String getStorePathIndex(final String rootDir) {
        return rootDir + File.separator + "index";
    }

    /**
     * 获取checkPoint文件的地址
     * @param rootDir 跟路径
     * @return ;
     */
    public static String getStoreCheckpoint(final String rootDir) {
        return rootDir + File.separator + "checkpoint";
    }

    /**
     * 获取终止文件地址
     * @param rootDir ;
     * @return ;
     */
    public static String getAbortFile(final String rootDir) {
        return rootDir + File.separator + "abort";
    }

    /**
     * 获取锁文件地址
     * @param rootDir rootDir
     * @return ;
     */
    public static String getLockFile(final String rootDir) {
        return rootDir + File.separator + "lock";
    }

    /**
     * 获取延迟偏移量的StorePath
     * @param rootDir ;
     * @return ;
     */
    public static String getDelayOffsetStorePath(final String rootDir) {
        return rootDir + File.separator + "config" + File.separator + "delayOffset.json";
    }


    public static String getTranStateTableStorePath(final String rootDir) {
        return rootDir + File.separator + "transaction" + File.separator + "statetable";
    }
    public static String getTranRedoLogStorePath(final String rootDir) {
        return rootDir + File.separator + "transaction" + File.separator + "redolog";
    }

}
