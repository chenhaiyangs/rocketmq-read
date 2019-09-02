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
package org.apache.rocketmq.client;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Base interface for MQ management
 * Mq的admin管理台
 * @author ;
 */
public interface MQAdmin {
    /**
     * Creates an topic
     * 创建topic
     * @param key accesskey
     * @param newTopic topic name
     * @param queueNum topic's queue number
     * @throws MQClientException ;
     */
    void createTopic(final String key, final String newTopic, final int queueNum)
        throws MQClientException;

    /**
     * Creates an topic
     * 创建topic
     * @param key accesskey
     * @param newTopic topic name
     * @param queueNum topic's queue number
     * @param topicSysFlag topic system flag
     * @throws MQClientException ;
     */
    void createTopic(String key, String newTopic, int queueNum, int topicSysFlag)
        throws MQClientException;

    /**
     * Gets the message queue offset according to some time in milliseconds<br>
     * be cautious to call because of more IO overhead
     * 查询队列的偏移量
     * @param mq Instance of MessageQueue
     * @param timestamp from when in milliseconds.
     * @return offset
     * @throws MQClientException ;
     */
    long searchOffset(final MessageQueue mq, final long timestamp) throws MQClientException;

    /**
     * Gets the max offset
     * 最大的偏移量
     * @param mq Instance of MessageQueue
     * @return the max offset
     * @throws MQClientException ;
     */
    long maxOffset(final MessageQueue mq) throws MQClientException;

    /**
     * Gets the minimum offset
     * 最小的偏移量
     * @param mq Instance of MessageQueue
     * @return the minimum offset
     * @throws MQClientException ;
     */
    long minOffset(final MessageQueue mq) throws MQClientException;

    /**
     * Gets the earliest stored message time
     * 最早的消息存储时间
     * @param mq Instance of MessageQueue
     * @return the time in microseconds
     * @throws MQClientException ;
     */
    long earliestMsgStoreTime(final MessageQueue mq) throws MQClientException;

    /**
     * Query message according tto message id
     * 根据offsetMsgid查询一条消息
     * @param offsetMsgId message id
     * @return message
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     * @throws MQClientException ;
     */
    MessageExt viewMessage(final String offsetMsgId) throws RemotingException, MQBrokerException,
        InterruptedException, MQClientException;

    /**
     * Query messages
     * 批量查询Messages
     * @param topic message topic
     * @param key message key index word
     * @param maxNum max message number
     * @param begin from when
     * @param end to when
     * @return Instance of QueryResult
     * @throws InterruptedException ;
     * @throws MQClientException ;
     */
    QueryResult queryMessage(final String topic, final String key, final int maxNum, final long begin,
        final long end) throws MQClientException, InterruptedException;

    /**
     * 根据msgId 查询msg
     * @return The {@code MessageExt} of given msgId
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     * @throws MQClientException ;
     */
    @SuppressWarnings("all")
    MessageExt viewMessage(String topic,
        String msgId) throws RemotingException, MQBrokerException, InterruptedException, MQClientException;

}