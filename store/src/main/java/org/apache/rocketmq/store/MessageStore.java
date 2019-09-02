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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageExtBatch;
import org.apache.rocketmq.store.stats.BrokerStatsManager;

/**
 * This class defines contracting interfaces to implement, allowing third-party vendor to use customized message store.
 * 消息存储模块
 * @author ;
 */
public interface MessageStore {

    /**
     * Load previously stored messages.
     * 加载数据
     * @return true if success; false otherwise.
     */
    boolean load();

    /**
     * Launch this message store.
     * 启动
     * @throws Exception if there is any error.
     */
    void start() throws Exception;

    /**
     * Shutdown this message store.
     * 关闭
     */
    void shutdown();

    /**
     * Destroy this message store. Generally, all persistent files should be removed after invocation.
     * 销毁，此动作会销毁所有持久化的数据
     */
    void destroy();

    /**
     * Store a message into store.
     *
     * @param msg Message instance to store
     * @return result of store operation.
     * 添加消息
     */
    PutMessageResult putMessage(final MessageExtBrokerInner msg);

    /**
     * Store a batch of messages.
     *
     * @param messageExtBatch Message batch.
     * @return result of storing batch messages.
     * 添加批量消息
     */
    PutMessageResult putMessages(final MessageExtBatch messageExtBatch);

    /**
     * Query at most <code>maxMsgNums</code> messages belonging to <code>topic</code> at <code>queueId</code> starting
     * from given <code>offset</code>. Resulting messages will further be screened using provided message filter.
     * 根据消费者group,topic,队列id,队列偏移量，最大消息个数，拉取消息，支持通过MessageFilter进行消息过滤
     * @param group Consumer group that launches this query.
     * @param topic Topic to query.
     * @param queueId Queue ID to query.
     * @param offset Logical offset to start from.
     * @param maxMsgNums Maximum count of messages to query.
     * @param messageFilter Message filter used to screen desired messages.
     * @return Matched messages.
     */
    GetMessageResult getMessage(final String group, final String topic, final int queueId,
        final long offset, final int maxMsgNums, final MessageFilter messageFilter);

    /**
     * Get maximum offset of the topic queue.
     *
     * @param topic Topic name.
     * @param queueId Queue ID.
     * @return Maximum offset at present.
     * 获取某个topic的队列id的最大的topic
     */
    long getMaxOffsetInQueue(final String topic, final int queueId);

    /**
     * Get the minimum offset of the topic queue.
     * 获取某个topic以及队列id的最小偏移量
     * @param topic Topic name.
     * @param queueId Queue ID.
     * @return Minimum offset at present.
     */
    long getMinOffsetInQueue(final String topic, final int queueId);

    /**
     * Get the offset of the message in the commit log, which is also known as physical offset.
     * 获取某个队列的topic的队列的id的commitlog的offset。
     * @param topic Topic of the message to lookup.
     * @param queueId Queue ID.
     * @param consumeQueueOffset offset of consume queue.
     * @return physical offset.
     *
     */
    long getCommitLogOffsetInQueue(final String topic, final int queueId, final long consumeQueueOffset);

    /**
     * Look up the physical offset of the message whose store timestamp is as specified.
     *
     * @param topic Topic of the message.
     * @param queueId Queue ID.
     * @param timestamp Timestamp to look up.
     * @return physical offset which matches.
     * 根据时间戳，topic，以及队列id获取偏移量
     */
    long getOffsetInQueueByTime(final String topic, final int queueId, final long timestamp);

    /**
     * Look up the message by given commit log offset.
     *
     * @param commitLogOffset physical offset.
     * @return Message whose physical offset is as specified.
     * 根据偏移量获取一个MessageExt
     */
    MessageExt lookMessageByOffset(final long commitLogOffset);

    /**
     * Get one message from the specified commit log offset.
     *
     * @param commitLogOffset commit log offset.
     * @return wrapped result of the message.
     * 根据commitlogOffset获取一个SelectMappedBufferResult
     */
    SelectMappedBufferResult selectOneMessageByOffset(final long commitLogOffset);

    /**
     * Get one message from the specified commit log offset.
     *
     * @param commitLogOffset commit log offset.
     * @param msgSize message size.
     * @return wrapped result of the message.
     * 根据commitlog的offset和msgSize，获取结果
     */
    SelectMappedBufferResult selectOneMessageByOffset(final long commitLogOffset, final int msgSize);

    /**
     * Get the running information of this store.
     *
     * @return message store running info.
     * 获取消息持久化截至的运行时数据的信息
     */
    String getRunningDataInfo();

    /**
     * Message store runtime information, which should generally contains various statistical information.
     *
     * @return runtime information of the message store in format of key-value pairs.
     * 获取持久化的允许时间。kv结构
     */
    HashMap<String, String> getRuntimeInfo();

    /**
     * Get the maximum commit log offset.
     * 获取 commitlog的最大的物理的偏移量
     * @return maximum commit log offset.
     */
    long getMaxPhyOffset();

    /**
     * Get the minimum commit log offset.
     * 获取最小的commitlog的偏移量
     * @return minimum commit log offset.
     */
    long getMinPhyOffset();

    /**
     * Get the store time of the earliest message in the given queue.
     * 获取最早的消息的存储时间。根据topic和队列id
     * @param topic Topic of the messages to query.
     * @param queueId Queue ID to find.
     * @return store time of the earliest message.
     */
    long getEarliestMessageTime(final String topic, final int queueId);

    /**
     * Get the store time of the earliest message in this store.
     * 获取最早的消息存储时间。
     * @return timestamp of the earliest message in this store.
     */
    long getEarliestMessageTime();

    /**
     * Get the store time of the message specified.
     *
     * @param topic message topic.
     * @param queueId queue ID.
     * @param consumeQueueOffset consume queue offset.
     * @return store timestamp of the message.
     * 根据消息的偏移量获取消息的读取时间
     */
    long getMessageStoreTimeStamp(final String topic, final int queueId, final long consumeQueueOffset);

    /**
     * Get the total number of the messages in the specified queue.
     *
     * @param topic Topic
     * @param queueId Queue ID.
     * @return total number.
     * 获取某个队列里的消息总量
     */
    long getMessageTotalInQueue(final String topic, final int queueId);

    /**
     * Get the raw commit log data starting from the given offset, which should used for replication purpose.
     *
     * @param offset starting offset.
     * @return commit log data.
     * 根据偏移量获取getCommitLogData数据
     */
    SelectMappedBufferResult getCommitLogData(final long offset);

    /**
     * Append data to commit log.
     *
     * @param startOffset starting offset.
     * @param data data to append.
     * @return true if success; false otherwise.
     * 把数据拼接到commitlog。从开始偏移量开始
     */
    boolean appendToCommitLog(final long startOffset, final byte[] data);

    /**
     * Execute file deletion manually.
     * 手工执行删除文件命令
     */
    void executeDeleteFilesManually();

    /**
     * Query messages by given key.
     * 根据给定的key查询一个Message
     * @param topic topic of the message.
     * @param key message key.
     * @param maxNum maximum number of the messages possible.
     * @param begin begin timestamp.
     * @param end end timestamp.
     */
    QueryMessageResult queryMessage(final String topic, final String key, final int maxNum, final long begin,
        final long end);

    /**
     * Update HA master address.
     *
     * @param newAddr new address.
     * 更新master的地址。slave节点会使用
     */
    void updateHaMasterAddress(final String newAddr);

    /**
     * Return how much the slave falls behind.
     * 返回slave落后于master的偏移量。复制时用
     * @return number of bytes that slave falls behind.
     */
    long slaveFallBehindMuch();

    /**
     * Return the current timestamp of the store.
     * 获取存储引擎的当前时间戳
     * @return current time in milliseconds since 1970-01-01.
     */
    long now();

    /**
     * Clean unused topics.
     *
     * @param topics all valid topics.
     * @return number of the topics deleted.
     * 清除没有使用的topic的数据
     */
    int cleanUnusedTopic(final Set<String> topics);

    /**
     * Clean expired consume queues.
     * 清除过期的消费队列的信息
     */
    void cleanExpiredConsumerQueue();

    /**
     * Check if the given message has been swapped out of the memory.
     * 检查某个消息是否已经不再内存里了
     * @param topic topic.
     * @param queueId queue ID.
     * @param consumeOffset consume queue offset.
     * @return true if the message is no longer in memory; false otherwise.
     */
    boolean checkInDiskByConsumeOffset(final String topic, final int queueId, long consumeOffset);

    /**
     * Get number of the bytes that have been stored in commit log and not yet dispatched to consume queue.
     * 获取还没有被消费的数据大小 bytes[]
     * @return number of the bytes to dispatch.
     */
    long dispatchBehindBytes();

    /**
     * Flush the message store to persist all data.
     * 持久化消息全部到磁盘
     * @return maximum offset flushed to persistent storage device.
     */
    long flush();

    /**
     * Reset written offset.
     * 重置写偏移量
     * @param phyOffset new offset.
     * @return true if success; false otherwise.
     */
    boolean resetWriteOffset(long phyOffset);

    /**
     * Get confirm offset.
     * 获取已经确认的偏移里
     * @return confirm offset.
     */
    long getConfirmOffset();

    /**
     * Set confirm offset.
     * 设置setConfirmOffset
     * @param phyOffset confirm offset to set.
     */
    void setConfirmOffset(long phyOffset);

    /**
     * Check if the operation system page cache is busy or not.
     * 检查OSPageCache是否繁忙
     * @return true if the OS page cache is busy; false otherwise.
     */
    boolean isOSPageCacheBusy();

    /**
     * Get lock time in milliseconds of the store by far.
     * putMessage的锁的超时时间
     * @return lock time in milliseconds.
     */
    long lockTimeMills();

    /**
     * Check if the transient store pool is deficient.
     *
     * @return true if the transient store pool is running out; false otherwise.
     */
    boolean isTransientStorePoolDeficient();

    /**
     * Get the dispatcher list.
     * 获取要分发的内容
     * @return list of the dispatcher.
     */
    LinkedList<CommitLogDispatcher> getDispatcherList();

    /**
     * Get consume queue of the topic/queue.
     * 获取消费队列
     * @param topic Topic.
     * @param queueId Queue ID.
     * @return Consume queue.
     */
    ConsumeQueue getConsumeQueue(String topic, int queueId);

    /**
     * Get BrokerStatsManager of the messageStore.
     * 获取Broker状态管理器
     * @return BrokerStatsManager.
     */
    BrokerStatsManager getBrokerStatsManager();
}
