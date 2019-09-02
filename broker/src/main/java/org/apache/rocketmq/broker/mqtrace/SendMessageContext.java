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
package org.apache.rocketmq.broker.mqtrace;

import java.util.Properties;
import org.apache.rocketmq.common.message.MessageType;
import org.apache.rocketmq.store.stats.BrokerStatsManager;

/**
 * 发送信息的一次上下文
 * @author ;
 */
public class SendMessageContext {
    /**
     * 生产者
     */
    private String producerGroup;
    /**
     * topic
     */
    private String topic;
    /**
     * msgid
     */
    private String msgId;
    /**
     * 原始msgid
     */
    private String originMsgId;
    /**
     * 队列id
     */
    private Integer queueId;
    /**
     * 队列偏移量
     */
    private Long queueOffset;
    /**
     * broker地址
     */
    private String brokerAddr;
    /**
     * 客户端地址
     */
    private String bornHost;
    /**
     * body长度
     */
    private int bodyLength;
    /**
     * code
     */
    private int code;
    /**
     * 错误码
     */
    private String errorMsg;
    /**
     * mq配置
     */
    private String msgProps;
    /**
     * mqtrace轨迹
     */
    private Object mqTraceContext;
    /**
     * 扩展extProps
     */
    private Properties extProps;
    /**
     * 区域id
     */
    private String brokerRegionId;
    /**
     * msg唯一的key
     */
    private String msgUniqueKey;
    /**
     * bornTimeStamp
     */
    private long bornTimeStamp;
    /**
     * 消息类型
     */
    private MessageType msgType = MessageType.Trans_msg_Commit;
    /**
     * 是否成功
     */
    private boolean isSuccess = false;
    //For Commercial
    /**
     * owner
     */
    private String commercialOwner;
    private BrokerStatsManager.StatsType commercialSendStats;
    /**
     * 发送的size
     */
    private int commercialSendSize;
    private int commercialSendTimes;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(final boolean success) {
        isSuccess = success;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(final MessageType msgType) {
        this.msgType = msgType;
    }

    public String getMsgUniqueKey() {
        return msgUniqueKey;
    }

    public void setMsgUniqueKey(final String msgUniqueKey) {
        this.msgUniqueKey = msgUniqueKey;
    }

    public long getBornTimeStamp() {
        return bornTimeStamp;
    }

    public void setBornTimeStamp(final long bornTimeStamp) {
        this.bornTimeStamp = bornTimeStamp;
    }

    public String getBrokerRegionId() {
        return brokerRegionId;
    }

    public void setBrokerRegionId(final String brokerRegionId) {
        this.brokerRegionId = brokerRegionId;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getOriginMsgId() {
        return originMsgId;
    }

    public void setOriginMsgId(String originMsgId) {
        this.originMsgId = originMsgId;
    }

    public Integer getQueueId() {
        return queueId;
    }

    public void setQueueId(Integer queueId) {
        this.queueId = queueId;
    }

    public Long getQueueOffset() {
        return queueOffset;
    }

    public void setQueueOffset(Long queueOffset) {
        this.queueOffset = queueOffset;
    }

    public String getBrokerAddr() {
        return brokerAddr;
    }

    public void setBrokerAddr(String brokerAddr) {
        this.brokerAddr = brokerAddr;
    }

    public String getBornHost() {
        return bornHost;
    }

    public void setBornHost(String bornHost) {
        this.bornHost = bornHost;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMsgProps() {
        return msgProps;
    }

    public void setMsgProps(String msgProps) {
        this.msgProps = msgProps;
    }

    public Object getMqTraceContext() {
        return mqTraceContext;
    }

    public void setMqTraceContext(Object mqTraceContext) {
        this.mqTraceContext = mqTraceContext;
    }

    public Properties getExtProps() {
        return extProps;
    }

    public void setExtProps(Properties extProps) {
        this.extProps = extProps;
    }

    public String getCommercialOwner() {
        return commercialOwner;
    }

    public void setCommercialOwner(final String commercialOwner) {
        this.commercialOwner = commercialOwner;
    }

    public BrokerStatsManager.StatsType getCommercialSendStats() {
        return commercialSendStats;
    }

    public void setCommercialSendStats(final BrokerStatsManager.StatsType commercialSendStats) {
        this.commercialSendStats = commercialSendStats;
    }

    public int getCommercialSendSize() {
        return commercialSendSize;
    }

    public void setCommercialSendSize(final int commercialSendSize) {
        this.commercialSendSize = commercialSendSize;
    }

    public int getCommercialSendTimes() {
        return commercialSendTimes;
    }

    public void setCommercialSendTimes(final int commercialSendTimes) {
        this.commercialSendTimes = commercialSendTimes;
    }
}
