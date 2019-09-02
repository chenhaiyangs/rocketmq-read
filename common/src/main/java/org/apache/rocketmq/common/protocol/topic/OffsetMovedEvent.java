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

package org.apache.rocketmq.common.protocol.topic;

import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.protocol.RemotingSerializable;

/**
 * 偏移量移动的事件
 * @author ;
 */
public class OffsetMovedEvent extends RemotingSerializable {
    /**
     * 消费组
     */
    private String consumerGroup;
    /**
     * 队列信息
     */
    private MessageQueue messageQueue;
    /**
     * 请求发起时队列的偏移量
     */
    private long offsetRequest;
    /**
     * 最新的偏移量，即下次发起请求时的偏移量
     */
    private long offsetNew;

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public long getOffsetRequest() {
        return offsetRequest;
    }

    public void setOffsetRequest(long offsetRequest) {
        this.offsetRequest = offsetRequest;
    }

    public long getOffsetNew() {
        return offsetNew;
    }

    public void setOffsetNew(long offsetNew) {
        this.offsetNew = offsetNew;
    }

    @Override
    public String toString() {
        return "OffsetMovedEvent [consumerGroup=" + consumerGroup + ", messageQueue=" + messageQueue
            + ", offsetRequest=" + offsetRequest + ", offsetNew=" + offsetNew + "]";
    }
}
