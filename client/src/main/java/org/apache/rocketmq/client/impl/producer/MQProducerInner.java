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
package org.apache.rocketmq.client.impl.producer;

import java.util.Set;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.header.CheckTransactionStateRequestHeader;

/**
 * mq的生产者inner实现
 * @author ;
 */
public interface MQProducerInner {
    /**
     * 获取发布的topic列表
     * @return ;
     */
    Set<String> getPublishTopicList();

    /**
     * 发布的topic是否需要更新
     * @param topic topic
     * @return ;
     */
    boolean isPublishTopicNeedUpdate(final String topic);

    /**
     * 获取checklistener
     * @return ;
     */
    TransactionCheckListener checkListener();

    /**
     * 获取 transactionListener
     * @return ;
     */
    TransactionListener getCheckListener();

    /**
     * 回查事务状态
     * @param addr server地址
     * @param msg msg
     * @param checkRequestHeader ;
     */
    void checkTransactionState(
        final String addr,
        final MessageExt msg,
        final CheckTransactionStateRequestHeader checkRequestHeader);

    /**
     * 更新updateTopicPublishInfo
     * @param topic ;
     * @param info ;
     */
    void updateTopicPublishInfo(final String topic, final TopicPublishInfo info);

    /**
     * 是否是unitMode
     * @return ;
     */
    boolean isUnitMode();
}
