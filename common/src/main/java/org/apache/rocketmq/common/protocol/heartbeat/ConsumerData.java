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

/**
 * $Id: ConsumerData.java 1835 2013-05-16 02:00:50Z vintagewang@apache.org $
 */
package org.apache.rocketmq.common.protocol.heartbeat;

import java.util.HashSet;
import java.util.Set;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * 消费者数据
 * @author ;
 */
public class ConsumerData {
    /**
     * groupName
     */
    private String groupName;
    /**
     * 消费类型
     */
    private ConsumeType consumeType;
    /**
     * 消息模式，广播or集群
     */
    private MessageModel messageModel;
    /**
     * 从哪里消费ConsumeFromWhere
     */
    private ConsumeFromWhere consumeFromWhere;
    /**
     * tag数据
     */
    private Set<SubscriptionData> subscriptionDataSet = new HashSet<SubscriptionData>();
    /**
     * unitMode
     */
    private boolean unitMode;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ConsumeType getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(ConsumeType consumeType) {
        this.consumeType = consumeType;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public ConsumeFromWhere getConsumeFromWhere() {
        return consumeFromWhere;
    }

    public void setConsumeFromWhere(ConsumeFromWhere consumeFromWhere) {
        this.consumeFromWhere = consumeFromWhere;
    }

    public Set<SubscriptionData> getSubscriptionDataSet() {
        return subscriptionDataSet;
    }

    public void setSubscriptionDataSet(Set<SubscriptionData> subscriptionDataSet) {
        this.subscriptionDataSet = subscriptionDataSet;
    }

    public boolean isUnitMode() {
        return unitMode;
    }

    public void setUnitMode(boolean isUnitMode) {
        this.unitMode = isUnitMode;
    }

    @Override
    public String toString() {
        return "ConsumerData [groupName=" + groupName + ", consumeType=" + consumeType + ", messageModel="
            + messageModel + ", consumeFromWhere=" + consumeFromWhere + ", unitMode=" + unitMode
            + ", subscriptionDataSet=" + subscriptionDataSet + "]";
    }
}
