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
package org.apache.rocketmq.broker.transaction.queue;

import org.apache.rocketmq.common.MixAll;

import java.nio.charset.Charset;

/**
 * 事务消息工具类
 * @author ;
 */
public class TransactionalMessageUtil {
    /**
     * 移除tag：d
     */
    public static final String REMOVETAG = "d";
    public static Charset charset = Charset.forName("utf-8");

    /**
     * 删除预处理消息(prepare)，
     * 其实是将消息存储在主题为：RMQ_SYS_TRANS_OP_HALF_TOPIC的主题中，代表这些消息已经被处理（提交或回滚）
     * @return ;
     */
    public static String buildOpTopic() {
        return MixAll.RMQ_SYS_TRANS_OP_HALF_TOPIC;
    }

    /**
     * 第一次提交事务消息，会存在这个主题里
     * @return ;
     */
    public static String buildHalfTopic() {
        return MixAll.RMQ_SYS_TRANS_HALF_TOPIC;
    }

    /**
     * Half Topic消费进度，默认消费者是CID_RMQ_SYS_TRANS
     * 每次取prepare消息判断回查时，从该消费进度开始依次获取消息。
     * @return ;
     */
    public static String buildConsumerGroup() {
        return MixAll.CID_SYS_RMQ_TRANS;
    }

}
