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
package org.apache.rocketmq.client.impl.consumer;

import java.util.Set;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;

/**
 * Consumer inner interface
 * mqConsumerInner接口
 */
public interface MQConsumerInner {
    /**
     * 消费者名称
     * @return ;
     */
    String groupName();

    /**
     * 消息模型
     * @return ;
     */
    MessageModel messageModel();

    /**
     * 消费类型。push or pull
     * @return ;
     */
    ConsumeType consumeType();

    /**
     * 从哪里开始消费
     * @return ;
     */
    ConsumeFromWhere consumeFromWhere();

    /**
     * SubscriptionData数据
     * @return ;
     */
    Set<SubscriptionData> subscriptions();

    /**
     * 执行rebalance
     */
    void doRebalance();

    /**
     * 持久化消费偏移量
     */
    void persistConsumerOffset();

    /**
     * 更新topic的SubscribeInfox信息
     * @param topic topic
     * @param info 队列
     */
    void updateTopicSubscribeInfo(final String topic, final Set<MessageQueue> info);

    /**
     * 是否需要更新isSubscribeTopicNeedUpdate
     * @param topic topic
     * @return ;
     */
    boolean isSubscribeTopicNeedUpdate(final String topic);

    boolean isUnitMode();

    /**
     * 获取consumer的运行时信息
     * @return ;
     */
    ConsumerRunningInfo consumerRunningInfo();
}
