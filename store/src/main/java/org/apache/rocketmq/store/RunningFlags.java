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

/**
 * storage的运行时信息
 * @author ;
 */
public class RunningFlags {
    /**
     * 不能读标志
     */
    private static final int NOT_READABLE_BIT = 1;
    /**
     * 不可写标志
     */
    private static final int NOT_WRITEABLE_BIT = 1 << 1;
    /**
     * 写逻辑队列错误
     */
    private static final int WRITE_LOGICS_QUEUE_ERROR_BIT = 1 << 2;
    /**
     * 写索引文件错误
     */
    private static final int WRITE_INDEX_FILE_ERROR_BIT = 1 << 3;
    /**
     * 磁盘满了错误
     */
    private static final int DISK_FULL_BIT = 1 << 4;

    /**
     * 运行时的flag
     */
    private volatile int flagBits = 0;

    public RunningFlags() {
    }

    public int getFlagBits() {
        return flagBits;
    }

    /**
     * 标记为可读
     * @return ;
     */
    public boolean getAndMakeReadable() {
        boolean result = this.isReadable();
        if (!result) {
            this.flagBits &= ~NOT_READABLE_BIT;
        }
        return result;
    }

    /**
     * 是否可读 与NOT_READABLE_BIT=0就是可读
     * @return ；
     */
    public boolean isReadable() {
        if ((this.flagBits & NOT_READABLE_BIT) == 0) {
            return true;
        }

        return false;
    }

    /**
     * 式其不可读 或 NOT_READABLE_BIT
     * @return ;
     */
    public boolean getAndMakeNotReadable() {
        boolean result = this.isReadable();
        if (result) {
            this.flagBits |= NOT_READABLE_BIT;
        }
        return result;
    }

    /**
     * 设置为可写
     */
    public boolean getAndMakeWriteable() {
        boolean result = this.isWriteable();
        if (!result) {
            this.flagBits &= ~NOT_WRITEABLE_BIT;
        }
        return result;
    }

    /**
     * 是否可写。
     * @return ;
     */
    public boolean isWriteable() {
        if ((this.flagBits & (NOT_WRITEABLE_BIT | WRITE_LOGICS_QUEUE_ERROR_BIT | DISK_FULL_BIT | WRITE_INDEX_FILE_ERROR_BIT)) == 0) {
            return true;
        }

        return false;
    }

    /**
     * or consume queue, just ignore the DISK_FULL_BIT
     * @return consumeQueue是否可写
     */
    public boolean isCQWriteable() {
        if ((this.flagBits & (NOT_WRITEABLE_BIT | WRITE_LOGICS_QUEUE_ERROR_BIT | WRITE_INDEX_FILE_ERROR_BIT)) == 0) {
            return true;
        }

        return false;
    }

    /**
     * 设置为不可写
     * @return ;
     */
    public boolean getAndMakeNotWriteable() {
        boolean result = this.isWriteable();
        if (result) {
            this.flagBits |= NOT_WRITEABLE_BIT;
        }
        return result;
    }

    /**
     * 标记为逻辑队列错误
     */
    public void makeLogicsQueueError() {
        this.flagBits |= WRITE_LOGICS_QUEUE_ERROR_BIT;
    }

    /**
     * 是否逻辑队列错误
     * @return ;
     */
    public boolean isLogicsQueueError() {
        if ((this.flagBits & WRITE_LOGICS_QUEUE_ERROR_BIT) == WRITE_LOGICS_QUEUE_ERROR_BIT) {
            return true;
        }

        return false;
    }

    /**
     * 设置索引文件错误
     */
    public void makeIndexFileError() {
        this.flagBits |= WRITE_INDEX_FILE_ERROR_BIT;
    }

    /**
     * 是否写索引文件错误
     * @return ;
     */
    public boolean isIndexFileError() {
        if ((this.flagBits & WRITE_INDEX_FILE_ERROR_BIT) == WRITE_INDEX_FILE_ERROR_BIT) {
            return true;
        }

        return false;
    }

    /**
     * 设置磁盘文件满了
     * @return ;
     */
    public boolean getAndMakeDiskFull() {
        boolean result = !((this.flagBits & DISK_FULL_BIT) == DISK_FULL_BIT);
        this.flagBits |= DISK_FULL_BIT;
        return result;
    }

    /**
     * 设置磁盘ok
     * @return ;
     */
    public boolean getAndMakeDiskOK() {
        boolean result = !((this.flagBits & DISK_FULL_BIT) == DISK_FULL_BIT);
        this.flagBits &= ~DISK_FULL_BIT;
        return result;
    }
}
