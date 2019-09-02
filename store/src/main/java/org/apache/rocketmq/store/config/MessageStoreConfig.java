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
import org.apache.rocketmq.common.annotation.ImportantField;
import org.apache.rocketmq.store.ConsumeQueue;
/**
 * Broker中持久化消息的相关配置
 * @author ;
 */
public class MessageStoreConfig {
    //The root directory in which the log data is kept
    /**
     * 消息持久化跟路径
     */
    @ImportantField
    private String storePathRootDir = System.getProperty("user.home") + File.separator + "store";

    //The directory in which the commitlog is kept
    /**
     * commitlog的存储路径
     */
    @ImportantField
    private String storePathCommitLog = System.getProperty("user.home") + File.separator + "store"
        + File.separator + "commitlog";

    /**
     * CommitLog file size,default is 1G
     * commitlog的最大的大小为1G
     */
    private int mapedFileSizeCommitLog = 1024 * 1024 * 1024;
    /**
     * 消费队列的文件的大小，默认30w
     * ConsumeQueue file size,default is 30W
     */
    private int mapedFileSizeConsumeQueue = 300000 * ConsumeQueue.CQ_STORE_UNIT_SIZE;
    /**
     * enable consume queue ext
     * 是否启用ConsumeQueue扩展属性
     */
    private boolean enableConsumeQueueExt = false;
    /**
     *  ConsumeQueue extend file size, 48M
     * ConsumeQueue扩展文件大小默认48MB
     */
    private int mappedFileSizeConsumeQueueExt = 48 * 1024 * 1024;
    /**
     * Bit count of filter bit map.
     * this will be set by pipe of calculate filter bit map.
     * ConsumeQueue扩展过滤bitmap大小
     */
    private int bitMapLengthConsumeQueueExt = 64;

    /**
     * CommitLog flush interval
     * commitlog刷盘频率
     *  flush data to disk
     */
    @ImportantField
    private int flushIntervalCommitLog = 500;

    /**
     * commitlog提交频率
     * Only used if TransientStorePool enabled
     * flush data to FileChannel
     */
    @ImportantField
    private int commitIntervalCommitLog = 200;

    /**
     * introduced since 4.0.x. Determine whether to use mutex reentrantLock when putting message.<br/>
     * By default it is set to false indicating using spin lock when putting message.
     * 消息存储到commitlog文件时获取锁类型，如果为true使用ReentrantLock否则使用自旋锁
     */
    private boolean useReentrantLockWhenPutMessage = false;

    /**
     * Whether schedule flush,default is real-time
     * 表示await方法等待FlushIntervalCommitlog,如果为true表示使用Thread.sleep方法等待
     */
    @ImportantField
    private boolean flushCommitLogTimed = false;
    /**
     * ConsumeQueue flush interval
     * consumuQueue文件刷盘频率
     */
    private int flushIntervalConsumeQueue = 1000;
    /**
     * 清除过期文件线程调度频率
     * Resource reclaim interval
     */
    private int cleanResourceInterval = 10000;
    /**
     * 删除commitlog文件的时间间隔，删除一个文件后等一下再删除一个文件
     * CommitLog removal interval
     */
    private int deleteCommitLogFilesInterval = 100;
    /**
     * ConsumeQueue removal interval
     * 删除consumequeue文件时间间隔
     */
    private int deleteConsumeQueueFilesInterval = 100;
    /**
     * 销毁MappedFile被拒绝的最大存活时间，默认120s。
     * 清除过期文件线程在初次销毁mappedfile时，
     * 如果该文件被其他线程引用，引用次数大于0.
     * 则设置MappedFile的可用状态为false，
     * 并设置第一次删除时间，下一次清理任务到达时，
     * 如果系统时间大于初次删除时间加上本参数，
     * 则将ref次数一次减1000，
     * 直到引用次数小于0，则释放物理资源
     */
    private int destroyMapedFileIntervalForcibly = 1000 * 120;
    /**
     * 重试删除文件间隔，配合destorymapedfileintervalforcibly
     */
    private int redeleteHangedFileInterval = 1000 * 120;
    /**
     * 磁盘文件空间充足情况下，默认每天什么时候执行删除过期文件，默认04表示凌晨4点
     * hen to delete,default is at 4 am
     */
    @ImportantField
    private String deleteWhen = "04";
    /**
     * commitlog目录所在分区的最大使用比例，
     * 如果commitlog目录所在的分区使用比例大于该值，则触发过期文件删除
     */
    private int diskMaxUsedSpaceRatio = 75;
    // The number of hours to keep a log file before deleting it (in hours)
    /**
     * 文件保留时间，默认72小时，
     * 表示非当前写文件最后一次更新时间加上filereservedtime小与当前时间，该文件将被清理
     */
    @ImportantField
    private int fileReservedTime = 72;
    /**
     * Flow control for ConsumeQueue
     * 流控字段，目前没使用
     */
    private int putMsgIndexHightWater = 600000;
    /**
     * The maximum size of a single log file,default is 512K
     * 默认允许的最大消息体默认4M
     */
    private int maxMessageSize = 1024 * 1024 * 4;
    /**
     * 文件恢复时是否校验CRC
     * Whether check the CRC32 of the records consumed.
     * This ensures no on-the-wire or on-disk corruption to the messages occurred.
     * This check adds some overhead,so it may be disabled in cases seeking extreme performance.
     */
    private boolean checkCRCOnRecover = true;
    /**
     * 一次刷盘至少需要脏页的数量，针对commitlog文件
     * How many pages are to be flushed when flush CommitLog
     */
    private int flushCommitLogLeastPages = 4;
    /**
     * 一次提交至少需要脏页的数量,默认4页,针对 commitlog文件
     * How many pages are to be committed when commit data to file
     */
    private int commitCommitLogLeastPages = 4;
    /**
     * Flush page size when the disk in warming state
     * 用字节0填充整个文件的,每多少页刷盘一次。默认4096页,异步刷盘模式生效
     */
    private int flushLeastPagesWhenWarmMapedFile = 1024 / 4 * 16;
    /**
     * 一次刷盘至少需要脏页的数量,默认2页,针对 Consume文件
     * How many pages are to be flushed when flush ConsumeQueue
     */
    private int flushConsumeQueueLeastPages = 2;
    /**
     * commitlog两次刷盘的最大间隔,
     * 如果超过该间隔,将fushCommitLogLeastPages要求直接执行刷盘操作
     */
    private int flushCommitLogThoroughInterval = 1000 * 10;
    /**
     * Commitlog两次提交的最大间隔,如果超过该间隔,将忽略commitCommitLogLeastPages直接提交
     */
    private int commitCommitLogThoroughInterval = 200;
    /**
     * Consume两次刷盘的最大间隔,如果超过该间隔,将忽略
     */
    private int flushConsumeQueueThoroughInterval = 1000 * 60;
    /**
     * 一次服务端消息拉取,消息在内存中传输允许的最大传输字节数默认256kb
     */
    @ImportantField
    private int maxTransferBytesOnMessageInMemory = 1024 * 256;
    /**
     * 一次服务消息拉取,消息在内存中传输运行的最大消息条数,默认为32条
     */
    @ImportantField
    private int maxTransferCountOnMessageInMemory = 32;
    /**
     * 一次服务消息端消息拉取,消息在磁盘中传输允许的最大字节
     */
    @ImportantField
    private int maxTransferBytesOnMessageInDisk = 1024 * 64;
    /**
     * 一次消息服务端消息拉取,消息在磁盘中传输允许的最大条数,默认为8条
     */
    @ImportantField
    private int maxTransferCountOnMessageInDisk = 8;
    /**
     * 访问消息在内存中比率,默认为40
     */
    @ImportantField
    private int accessMessageInMemoryMaxRatio = 40;
    /**
     * 是否支持消息索引文件
     */
    @ImportantField
    private boolean messageIndexEnable = true;
    /**
     * 单个索引文件hash槽的个数,默认为五百万
     */
    private int maxHashSlotNum = 5000000;
    /**
     * 单个索引文件索引条目的个数,默认为两千万
     */
    private int maxIndexNum = 5000000 * 4;
    /**
     * 一次查询消息最大返回消息条数,默认64条
     */
    private int maxMsgsNumBatch = 64;
    /**
     * 消息索引是否安全,
     * 默认为 false,
     * 文件恢复时选择文件检测点（commitlog.consumeque）
     * 的最小的与文件最后更新对比，
     * 如果为true，文件恢复时选择文件检测点保存的索引更新时间作为对比
     */
    @ImportantField
    private boolean messageIndexSafe = false;
    /**
     * Master监听端口,从服务器连接该端口,默认为10912
     */
    private int haListenPort = 10912;
    /**
     * Master与Slave心跳包发送间隔
     */
    private int haSendHeartbeatInterval = 1000 * 5;
    /**
     * Master与save长连接空闲时间,超过该时间将关闭连接
     */
    private int haHousekeepingInterval = 1000 * 20;
    /**
     * 一次HA主从同步传输的最大字节长度,默认为32K
     */
    private int haTransferBatchSize = 1024 * 32;
    /**
     * Master服务器IP地址与端口号
     */
    @ImportantField
    private String haMasterAddress = null;
    /**
     * 允许从服务器落户的最大偏移字节数,默认为256M。超过该值则表示该Slave不可用
     */
    private int haSlaveFallbehindMax = 1024 * 1024 * 256;
    /**
     * broker角色,分为 ASYNC_MASTER SYNC_MASTER, SLAVE
     */
    @ImportantField
    private BrokerRole brokerRole = BrokerRole.ASYNC_MASTER;
    /**
     * 刷盘方式,默认为 ASYNC_FLUSH(异步刷盘),可选值SYNC_FLUSH(同步刷盘)
     */
    @ImportantField
    private FlushDiskType flushDiskType = FlushDiskType.ASYNC_FLUSH;
    /**
     * 同步刷盘超时时间
     */
    private int syncFlushTimeout = 1000 * 5;
    /**
     * 延迟队列等级（s=秒，m=分，h=小时）
     */
    private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
    /**
     * 延迟队列拉取进度刷盘间隔。默认10s
     */
    private long flushDelayOffsetInterval = 1000 * 10;
    /**
     * 是否支持强行删除过期文件
     */
    @ImportantField
    private boolean cleanFileForciblyEnable = true;
    /**
     * 是否温和地使用 MappedFile如果为true,将不强制将内存映射文件锁定在内存中
     */
    private boolean warmMapedFileEnable = false;
    /**
     * 从服务器是否坚持 offset检测
     */
    private boolean offsetCheckInSlave = false;
    /**
     * 是否支持 PutMessage Lock锁打印信息
     */
    private boolean debugLockEnable = false;
    /**
     * 是否允许重复复制,默认为 false
     */
    private boolean duplicationEnable = false;
    /**
     * 是否统计磁盘的使用情况,默认为true
     */
    private boolean diskFallRecorded = true;
    /**
     * putMessage锁占用超过该时间,表示 PageCache忙
     */
    private long osPageCacheBusyTimeOutMills = 1000;
    /**
     * 查询消息默认返回条数,默认为32
     */
    private int defaultQueryMaxNum = 32;

    /**
     * Commitlog是否开启 transientStorePool机制,默认为 false
     */
    @ImportantField
    private boolean transientStorePoolEnable = false;
    /**
     * transientStorePool中缓存 ByteBuffer个数,默认5个
     */
    private int transientStorePoolSize = 5;
    /**
     * 从 transientStorepool中获取 ByteBuffer是否支持快速失败
     */
    private boolean fastFailIfNoBufferInStorePool = false;

    public boolean isDebugLockEnable() {
        return debugLockEnable;
    }

    public void setDebugLockEnable(final boolean debugLockEnable) {
        this.debugLockEnable = debugLockEnable;
    }

    public boolean isDuplicationEnable() {
        return duplicationEnable;
    }

    public void setDuplicationEnable(final boolean duplicationEnable) {
        this.duplicationEnable = duplicationEnable;
    }

    public long getOsPageCacheBusyTimeOutMills() {
        return osPageCacheBusyTimeOutMills;
    }

    public void setOsPageCacheBusyTimeOutMills(final long osPageCacheBusyTimeOutMills) {
        this.osPageCacheBusyTimeOutMills = osPageCacheBusyTimeOutMills;
    }

    public boolean isDiskFallRecorded() {
        return diskFallRecorded;
    }

    public void setDiskFallRecorded(final boolean diskFallRecorded) {
        this.diskFallRecorded = diskFallRecorded;
    }

    public boolean isWarmMapedFileEnable() {
        return warmMapedFileEnable;
    }

    public void setWarmMapedFileEnable(boolean warmMapedFileEnable) {
        this.warmMapedFileEnable = warmMapedFileEnable;
    }

    public int getMapedFileSizeCommitLog() {
        return mapedFileSizeCommitLog;
    }

    public void setMapedFileSizeCommitLog(int mapedFileSizeCommitLog) {
        this.mapedFileSizeCommitLog = mapedFileSizeCommitLog;
    }

    public int getMapedFileSizeConsumeQueue() {

        int factor = (int) Math.ceil(this.mapedFileSizeConsumeQueue / (ConsumeQueue.CQ_STORE_UNIT_SIZE * 1.0));
        return (int) (factor * ConsumeQueue.CQ_STORE_UNIT_SIZE);
    }

    public void setMapedFileSizeConsumeQueue(int mapedFileSizeConsumeQueue) {
        this.mapedFileSizeConsumeQueue = mapedFileSizeConsumeQueue;
    }

    public boolean isEnableConsumeQueueExt() {
        return enableConsumeQueueExt;
    }

    public void setEnableConsumeQueueExt(boolean enableConsumeQueueExt) {
        this.enableConsumeQueueExt = enableConsumeQueueExt;
    }

    public int getMappedFileSizeConsumeQueueExt() {
        return mappedFileSizeConsumeQueueExt;
    }

    public void setMappedFileSizeConsumeQueueExt(int mappedFileSizeConsumeQueueExt) {
        this.mappedFileSizeConsumeQueueExt = mappedFileSizeConsumeQueueExt;
    }

    public int getBitMapLengthConsumeQueueExt() {
        return bitMapLengthConsumeQueueExt;
    }

    public void setBitMapLengthConsumeQueueExt(int bitMapLengthConsumeQueueExt) {
        this.bitMapLengthConsumeQueueExt = bitMapLengthConsumeQueueExt;
    }

    public int getFlushIntervalCommitLog() {
        return flushIntervalCommitLog;
    }

    public void setFlushIntervalCommitLog(int flushIntervalCommitLog) {
        this.flushIntervalCommitLog = flushIntervalCommitLog;
    }

    public int getFlushIntervalConsumeQueue() {
        return flushIntervalConsumeQueue;
    }

    public void setFlushIntervalConsumeQueue(int flushIntervalConsumeQueue) {
        this.flushIntervalConsumeQueue = flushIntervalConsumeQueue;
    }

    public int getPutMsgIndexHightWater() {
        return putMsgIndexHightWater;
    }

    public void setPutMsgIndexHightWater(int putMsgIndexHightWater) {
        this.putMsgIndexHightWater = putMsgIndexHightWater;
    }

    public int getCleanResourceInterval() {
        return cleanResourceInterval;
    }

    public void setCleanResourceInterval(int cleanResourceInterval) {
        this.cleanResourceInterval = cleanResourceInterval;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public boolean isCheckCRCOnRecover() {
        return checkCRCOnRecover;
    }

    public boolean getCheckCRCOnRecover() {
        return checkCRCOnRecover;
    }

    public void setCheckCRCOnRecover(boolean checkCRCOnRecover) {
        this.checkCRCOnRecover = checkCRCOnRecover;
    }

    public String getStorePathCommitLog() {
        return storePathCommitLog;
    }

    public void setStorePathCommitLog(String storePathCommitLog) {
        this.storePathCommitLog = storePathCommitLog;
    }

    public String getDeleteWhen() {
        return deleteWhen;
    }

    public void setDeleteWhen(String deleteWhen) {
        this.deleteWhen = deleteWhen;
    }

    public int getDiskMaxUsedSpaceRatio() {
        if (this.diskMaxUsedSpaceRatio < 10)
            return 10;

        if (this.diskMaxUsedSpaceRatio > 95)
            return 95;

        return diskMaxUsedSpaceRatio;
    }

    public void setDiskMaxUsedSpaceRatio(int diskMaxUsedSpaceRatio) {
        this.diskMaxUsedSpaceRatio = diskMaxUsedSpaceRatio;
    }

    public int getDeleteCommitLogFilesInterval() {
        return deleteCommitLogFilesInterval;
    }

    public void setDeleteCommitLogFilesInterval(int deleteCommitLogFilesInterval) {
        this.deleteCommitLogFilesInterval = deleteCommitLogFilesInterval;
    }

    public int getDeleteConsumeQueueFilesInterval() {
        return deleteConsumeQueueFilesInterval;
    }

    public void setDeleteConsumeQueueFilesInterval(int deleteConsumeQueueFilesInterval) {
        this.deleteConsumeQueueFilesInterval = deleteConsumeQueueFilesInterval;
    }

    public int getMaxTransferBytesOnMessageInMemory() {
        return maxTransferBytesOnMessageInMemory;
    }

    public void setMaxTransferBytesOnMessageInMemory(int maxTransferBytesOnMessageInMemory) {
        this.maxTransferBytesOnMessageInMemory = maxTransferBytesOnMessageInMemory;
    }

    public int getMaxTransferCountOnMessageInMemory() {
        return maxTransferCountOnMessageInMemory;
    }

    public void setMaxTransferCountOnMessageInMemory(int maxTransferCountOnMessageInMemory) {
        this.maxTransferCountOnMessageInMemory = maxTransferCountOnMessageInMemory;
    }

    public int getMaxTransferBytesOnMessageInDisk() {
        return maxTransferBytesOnMessageInDisk;
    }

    public void setMaxTransferBytesOnMessageInDisk(int maxTransferBytesOnMessageInDisk) {
        this.maxTransferBytesOnMessageInDisk = maxTransferBytesOnMessageInDisk;
    }

    public int getMaxTransferCountOnMessageInDisk() {
        return maxTransferCountOnMessageInDisk;
    }

    public void setMaxTransferCountOnMessageInDisk(int maxTransferCountOnMessageInDisk) {
        this.maxTransferCountOnMessageInDisk = maxTransferCountOnMessageInDisk;
    }

    public int getFlushCommitLogLeastPages() {
        return flushCommitLogLeastPages;
    }

    public void setFlushCommitLogLeastPages(int flushCommitLogLeastPages) {
        this.flushCommitLogLeastPages = flushCommitLogLeastPages;
    }

    public int getFlushConsumeQueueLeastPages() {
        return flushConsumeQueueLeastPages;
    }

    public void setFlushConsumeQueueLeastPages(int flushConsumeQueueLeastPages) {
        this.flushConsumeQueueLeastPages = flushConsumeQueueLeastPages;
    }

    public int getFlushCommitLogThoroughInterval() {
        return flushCommitLogThoroughInterval;
    }

    public void setFlushCommitLogThoroughInterval(int flushCommitLogThoroughInterval) {
        this.flushCommitLogThoroughInterval = flushCommitLogThoroughInterval;
    }

    public int getFlushConsumeQueueThoroughInterval() {
        return flushConsumeQueueThoroughInterval;
    }

    public void setFlushConsumeQueueThoroughInterval(int flushConsumeQueueThoroughInterval) {
        this.flushConsumeQueueThoroughInterval = flushConsumeQueueThoroughInterval;
    }

    public int getDestroyMapedFileIntervalForcibly() {
        return destroyMapedFileIntervalForcibly;
    }

    public void setDestroyMapedFileIntervalForcibly(int destroyMapedFileIntervalForcibly) {
        this.destroyMapedFileIntervalForcibly = destroyMapedFileIntervalForcibly;
    }

    public int getFileReservedTime() {
        return fileReservedTime;
    }

    public void setFileReservedTime(int fileReservedTime) {
        this.fileReservedTime = fileReservedTime;
    }

    public int getRedeleteHangedFileInterval() {
        return redeleteHangedFileInterval;
    }

    public void setRedeleteHangedFileInterval(int redeleteHangedFileInterval) {
        this.redeleteHangedFileInterval = redeleteHangedFileInterval;
    }

    public int getAccessMessageInMemoryMaxRatio() {
        return accessMessageInMemoryMaxRatio;
    }

    public void setAccessMessageInMemoryMaxRatio(int accessMessageInMemoryMaxRatio) {
        this.accessMessageInMemoryMaxRatio = accessMessageInMemoryMaxRatio;
    }

    public boolean isMessageIndexEnable() {
        return messageIndexEnable;
    }

    public void setMessageIndexEnable(boolean messageIndexEnable) {
        this.messageIndexEnable = messageIndexEnable;
    }

    public int getMaxHashSlotNum() {
        return maxHashSlotNum;
    }

    public void setMaxHashSlotNum(int maxHashSlotNum) {
        this.maxHashSlotNum = maxHashSlotNum;
    }

    public int getMaxIndexNum() {
        return maxIndexNum;
    }

    public void setMaxIndexNum(int maxIndexNum) {
        this.maxIndexNum = maxIndexNum;
    }

    public int getMaxMsgsNumBatch() {
        return maxMsgsNumBatch;
    }

    public void setMaxMsgsNumBatch(int maxMsgsNumBatch) {
        this.maxMsgsNumBatch = maxMsgsNumBatch;
    }

    public int getHaListenPort() {
        return haListenPort;
    }

    public void setHaListenPort(int haListenPort) {
        this.haListenPort = haListenPort;
    }

    public int getHaSendHeartbeatInterval() {
        return haSendHeartbeatInterval;
    }

    public void setHaSendHeartbeatInterval(int haSendHeartbeatInterval) {
        this.haSendHeartbeatInterval = haSendHeartbeatInterval;
    }

    public int getHaHousekeepingInterval() {
        return haHousekeepingInterval;
    }

    public void setHaHousekeepingInterval(int haHousekeepingInterval) {
        this.haHousekeepingInterval = haHousekeepingInterval;
    }

    public BrokerRole getBrokerRole() {
        return brokerRole;
    }

    public void setBrokerRole(BrokerRole brokerRole) {
        this.brokerRole = brokerRole;
    }

    public void setBrokerRole(String brokerRole) {
        this.brokerRole = BrokerRole.valueOf(brokerRole);
    }

    public int getHaTransferBatchSize() {
        return haTransferBatchSize;
    }

    public void setHaTransferBatchSize(int haTransferBatchSize) {
        this.haTransferBatchSize = haTransferBatchSize;
    }

    public int getHaSlaveFallbehindMax() {
        return haSlaveFallbehindMax;
    }

    public void setHaSlaveFallbehindMax(int haSlaveFallbehindMax) {
        this.haSlaveFallbehindMax = haSlaveFallbehindMax;
    }

    public FlushDiskType getFlushDiskType() {
        return flushDiskType;
    }

    public void setFlushDiskType(FlushDiskType flushDiskType) {
        this.flushDiskType = flushDiskType;
    }

    public void setFlushDiskType(String type) {
        this.flushDiskType = FlushDiskType.valueOf(type);
    }

    public int getSyncFlushTimeout() {
        return syncFlushTimeout;
    }

    public void setSyncFlushTimeout(int syncFlushTimeout) {
        this.syncFlushTimeout = syncFlushTimeout;
    }

    public String getHaMasterAddress() {
        return haMasterAddress;
    }

    public void setHaMasterAddress(String haMasterAddress) {
        this.haMasterAddress = haMasterAddress;
    }

    public String getMessageDelayLevel() {
        return messageDelayLevel;
    }

    public void setMessageDelayLevel(String messageDelayLevel) {
        this.messageDelayLevel = messageDelayLevel;
    }

    public long getFlushDelayOffsetInterval() {
        return flushDelayOffsetInterval;
    }

    public void setFlushDelayOffsetInterval(long flushDelayOffsetInterval) {
        this.flushDelayOffsetInterval = flushDelayOffsetInterval;
    }

    public boolean isCleanFileForciblyEnable() {
        return cleanFileForciblyEnable;
    }

    public void setCleanFileForciblyEnable(boolean cleanFileForciblyEnable) {
        this.cleanFileForciblyEnable = cleanFileForciblyEnable;
    }

    public boolean isMessageIndexSafe() {
        return messageIndexSafe;
    }

    public void setMessageIndexSafe(boolean messageIndexSafe) {
        this.messageIndexSafe = messageIndexSafe;
    }

    public boolean isFlushCommitLogTimed() {
        return flushCommitLogTimed;
    }

    public void setFlushCommitLogTimed(boolean flushCommitLogTimed) {
        this.flushCommitLogTimed = flushCommitLogTimed;
    }

    public String getStorePathRootDir() {
        return storePathRootDir;
    }

    public void setStorePathRootDir(String storePathRootDir) {
        this.storePathRootDir = storePathRootDir;
    }

    public int getFlushLeastPagesWhenWarmMapedFile() {
        return flushLeastPagesWhenWarmMapedFile;
    }

    public void setFlushLeastPagesWhenWarmMapedFile(int flushLeastPagesWhenWarmMapedFile) {
        this.flushLeastPagesWhenWarmMapedFile = flushLeastPagesWhenWarmMapedFile;
    }

    public boolean isOffsetCheckInSlave() {
        return offsetCheckInSlave;
    }

    public void setOffsetCheckInSlave(boolean offsetCheckInSlave) {
        this.offsetCheckInSlave = offsetCheckInSlave;
    }

    public int getDefaultQueryMaxNum() {
        return defaultQueryMaxNum;
    }

    public void setDefaultQueryMaxNum(int defaultQueryMaxNum) {
        this.defaultQueryMaxNum = defaultQueryMaxNum;
    }

    /**
     * Enable transient commitLog store poll only if transientStorePoolEnable is true and the FlushDiskType is
     * ASYNC_FLUSH
     * 是否启用瞬时池子：transientStorePoolEnable为true，并且必须是异步刷盘并且必须是master
     * @return <tt>true</tt> or <tt>false</tt>
     */
    public boolean isTransientStorePoolEnable() {
        return transientStorePoolEnable && FlushDiskType.ASYNC_FLUSH == getFlushDiskType()
            && BrokerRole.SLAVE != getBrokerRole();
    }

    public void setTransientStorePoolEnable(final boolean transientStorePoolEnable) {
        this.transientStorePoolEnable = transientStorePoolEnable;
    }

    public int getTransientStorePoolSize() {
        return transientStorePoolSize;
    }

    public void setTransientStorePoolSize(final int transientStorePoolSize) {
        this.transientStorePoolSize = transientStorePoolSize;
    }

    public int getCommitIntervalCommitLog() {
        return commitIntervalCommitLog;
    }

    public void setCommitIntervalCommitLog(final int commitIntervalCommitLog) {
        this.commitIntervalCommitLog = commitIntervalCommitLog;
    }

    public boolean isFastFailIfNoBufferInStorePool() {
        return fastFailIfNoBufferInStorePool;
    }

    public void setFastFailIfNoBufferInStorePool(final boolean fastFailIfNoBufferInStorePool) {
        this.fastFailIfNoBufferInStorePool = fastFailIfNoBufferInStorePool;
    }

    public boolean isUseReentrantLockWhenPutMessage() {
        return useReentrantLockWhenPutMessage;
    }

    public void setUseReentrantLockWhenPutMessage(final boolean useReentrantLockWhenPutMessage) {
        this.useReentrantLockWhenPutMessage = useReentrantLockWhenPutMessage;
    }

    public int getCommitCommitLogLeastPages() {
        return commitCommitLogLeastPages;
    }

    public void setCommitCommitLogLeastPages(final int commitCommitLogLeastPages) {
        this.commitCommitLogLeastPages = commitCommitLogLeastPages;
    }

    public int getCommitCommitLogThoroughInterval() {
        return commitCommitLogThoroughInterval;
    }

    public void setCommitCommitLogThoroughInterval(final int commitCommitLogThoroughInterval) {
        this.commitCommitLogThoroughInterval = commitCommitLogThoroughInterval;
    }

}
