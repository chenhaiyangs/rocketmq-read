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

import java.util.List;
import java.util.Set;
import org.apache.rocketmq.client.consumer.AllocateMessageQueueStrategy;
import org.apache.rocketmq.client.consumer.MessageQueueListener;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * RelalancePullImpl
 * Pull模式实际上无需负载消息队列
 * 这里的实现都无用
 * @author ;
 */
public class RebalancePullImpl extends RebalanceImpl {

    private final DefaultMQPullConsumerImpl defaultMQPullConsumerImpl;

    /**
     * 构造函数，RebalancePullImpl把所有参数都指定为了null
     * @param defaultMQPullConsumerImpl ;
     */
    public RebalancePullImpl(DefaultMQPullConsumerImpl defaultMQPullConsumerImpl) {
        //一堆的都是null
        this(null, null, null, null, defaultMQPullConsumerImpl);
    }
    public RebalancePullImpl(String consumerGroup, MessageModel messageModel,
        AllocateMessageQueueStrategy allocateMessageQueueStrategy,
        MQClientInstance mQClientFactory, DefaultMQPullConsumerImpl defaultMQPullConsumerImpl) {

        super(consumerGroup, messageModel, allocateMessageQueueStrategy, mQClientFactory);
        this.defaultMQPullConsumerImpl = defaultMQPullConsumerImpl;
    }

    /**
     * 触发messageQueueListener。空不执行。默认的pullRequest为空
     * @param topic topic
     * @param mqAll  mqqll
     * @param mqDivided 被选中的
     */
    @Override
    public void messageQueueChanged(String topic, Set<MessageQueue> mqAll, Set<MessageQueue> mqDivided) {
        MessageQueueListener messageQueueListener = this.defaultMQPullConsumerImpl.getDefaultMQPullConsumer().getMessageQueueListener();
        if (messageQueueListener != null) {
            try {
                messageQueueListener.messageQueueChanged(topic, mqAll, mqDivided);
            } catch (Throwable e) {
                log.error("messageQueueChanged exception", e);
            }
        }
    }

    /**
     * 移除多余的队列偏移量
     * @param mq mq mq
     * @param pq oq oq
     * @return ;
     */
    @Override
    public boolean removeUnnecessaryMessageQueue(MessageQueue mq, ProcessQueue pq) {
        this.defaultMQPullConsumerImpl.getOffsetStore().persist(mq);
        this.defaultMQPullConsumerImpl.getOffsetStore().removeOffset(mq);
        return true;
    }

    @Override
    public ConsumeType consumeType() {
        return ConsumeType.CONSUME_ACTIVELY;
    }

    @Override
    public void removeDirtyOffset(final MessageQueue mq) {
        this.defaultMQPullConsumerImpl.getOffsetStore().removeOffset(mq);
    }

    @Override
    public long computePullFromWhere(MessageQueue mq) {
        return 0;
    }

    @Override
    public void dispatchPullRequest(List<PullRequest> pullRequestList) {
    }
}
