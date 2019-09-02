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
package org.apache.rocketmq.client.consumer.store;

import java.util.Map;
import java.util.Set;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Offset store interface
 * 偏移量存储接口
 * 当广播消费时，偏移量是客户端存储
 * @author ;
 */
public interface OffsetStore {
    /**
     * load数据
     * @throws MQClientException ;
     */
    void load() throws MQClientException;

    /**
     * 更新队列的偏移量
     * @param mq mq
     * @param offset offset
     * @param increaseOnly 是否只增加
     */
    void updateOffset(final MessageQueue mq, final long offset, final boolean increaseOnly);

    /**
     * Get offset from local storage
     * @param mq 队列
     * @param type 读取偏移量类型
     * @return The fetched offset
     */
    long readOffset(final MessageQueue mq, final ReadOffsetType type);

    /**
     * Persist all offsets,may be in local storage or remote name server
     * @param mqs 队列
     * 持久化所在队列的全部偏移量。或者是在本地或者是在远程
     */
    void persistAll(final Set<MessageQueue> mqs);

    /**
     * Persist the offset,may be in local storage or remote name server
     * @param mq
     * 持久化某个队列的偏移量
     */
    void persist(final MessageQueue mq);

    /**
     * Remove offset
     * @param mq 队列
     * 移除某个队列的偏移量
     */
    void removeOffset(MessageQueue mq);

    /**
     * 克隆偏移量table
     * @return The cloned offset table of given topic
     */
    Map<MessageQueue, Long> cloneOffsetTable(String topic);

    /**
     * 更新消费偏移量到broker
     * @param mq mq
     * @param offset offset
     * @param isOneway isOneway?
     */
    void updateConsumeOffsetToBroker(MessageQueue mq, long offset, boolean isOneway) throws RemotingException,
        MQBrokerException, InterruptedException, MQClientException;
}
