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
package org.apache.rocketmq.store;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 引用的资源
 * @author ;
 */
public abstract class ReferenceResource {
    /**
     * 引用计数
     */
    protected final AtomicLong refCount = new AtomicLong(1);
    /**
     * 是否可用
     */
    protected volatile boolean available = true;
    /**
     * 是否清除完毕
     */
    protected volatile boolean cleanupOver = false;
    /**
     * 第一次shutdown的时间戳
     */
    private volatile long firstShutdownTimestamp = 0;

    /**
     * 是否存在引用
     * @return ;
     */
    public synchronized boolean hold() {
        if (this.isAvailable()) {
            //refCount.getAndIncrement() >0,返回true
            if (this.refCount.getAndIncrement() > 0) {
                return true;
            } else {
                //减一
                this.refCount.getAndDecrement();
            }
        }

        return false;
    }

    /**
     * 是否可用
     * @return ;
     */
    public boolean isAvailable() {
        return this.available;
    }

    /**
     * shutdown
     * @param intervalForcibly 周期
     */
    public void shutdown(final long intervalForcibly) {
        if (this.available) {
            this.available = false;
            this.firstShutdownTimestamp = System.currentTimeMillis();
            this.release();

            //不可用，但是getRefCount>0
        } else if (this.getRefCount() > 0) {
            //判断当前时间距离第一次shutdown超过了intervalForcibly
            if ((System.currentTimeMillis() - this.firstShutdownTimestamp) >= intervalForcibly) {
                //计数-为负数
                this.refCount.set(-1000 - this.getRefCount());
                this.release();
            }
        }
    }

    /**
     * release
     * 释放引用
     */
    public void release() {
        long value = this.refCount.decrementAndGet();
        if (value > 0) {
            return;
        }

        //如果释放小于0了，就
        synchronized (this) {
            this.cleanupOver = this.cleanup(value);
        }
    }

    public long getRefCount() {
        return this.refCount.get();
    }

    /**
     * 子类实现cleanup
     * @param currentRef 当前的引用
     * @return ;
     */
    public abstract boolean cleanup(final long currentRef);

    /**
     * 是否清除完毕
     * @return ;
     */
    public boolean isCleanupOver() {
        return this.refCount.get() <= 0 && this.cleanupOver;
    }
}
