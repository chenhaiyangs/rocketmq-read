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
package org.apache.rocketmq.client.consumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.QueryResult;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.consumer.store.OffsetStore;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.consumer.DefaultMQPullConsumerImpl;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.MessageDecoder;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Default pulling consumer
 * Pull模式。客户端需要保证偏移量
 *    <code>
 *       PullConsumer的一个Demo。需要消费者自己维持偏移量。方便下一次拉取
 *       工业上用的极少
 *
 *       public class PullConsumer {
 *           private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();
 *
 *           public static void main(String[] args) throws MQClientException {
 *               DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("please_rename_unique_group_name_5");
 *
 *               consumer.start();
 *
 *               Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("TopicTest1");
 *               for (MessageQueue mq : mqs) {
 *                   System.out.printf("Consume from the queue: %s%n", mq);
 *                  SINGLE_MQ:
 *                   while (true) {
 *                       try {
 *                           PullResult pullResult =
 *                                   consumer.pullBlockIfNotFound(mq, null, getMessageQueueOffset(mq), 32);
 *                           System.out.printf("%s%n", pullResult);
 *                            putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
 *                          switch (pullResult.getPullStatus()) {
 *                              case FOUND:
 *                                   break;
 *                               case NO_MATCHED_MSG:
 *                                   break;
 *                               case NO_NEW_MSG:
 *                                   break SINGLE_MQ;
 *                               case OFFSET_ILLEGAL:
 *                                   break;
 *                               default:
 *                                    break;
 *                           }
 *                        } catch (Exception e) {
 *                           e.printStackTrace();
 *                       }
 *                   }
 *               }
 *
 *                consumer.shutdown();
 *           }
 *
 *           private static long getMessageQueueOffset(MessageQueue mq) {
 *               Long offset = OFFSE_TABLE.get(mq);
 *               if (offset != null) {
 *                   return offset;
 *               }
 *
 *                return 0;
 *           }
 *
 *           private static void putMessageQueueOffset(MessageQueue mq, long offset) {
 v               OFFSE_TABLE.put(mq, offset);
 *           }
 *
 *       }
 * </code>
 * @author ;
 */
public class DefaultMQPullConsumer extends ClientConfig implements MQPullConsumer {
    /**
     * defaultMqPullConsumer;
     */
    protected final transient DefaultMQPullConsumerImpl defaultMQPullConsumerImpl;

    /**
     * Do the same thing for the same Group, the application must be set,and
     * guarantee Globally unique
     * 消费组
     */
    private String consumerGroup;
    /**
     * Long polling mode, the Consumer connection max suspend time, it is not
     * recommended to modify
     * broker挂起最大时间
     */
    private long brokerSuspendMaxTimeMillis = 1000 * 20;
    /**
     * Long polling mode, the Consumer connection timeout(must greater than
     * brokerSuspendMaxTimeMillis), it is not recommended to modify
     * 消息超时时间当挂起时.30分钟
     */
    private long consumerTimeoutMillisWhenSuspend = 1000 * 30;
    /**
     * The socket timeout in milliseconds
     * 消费长轮询超时时间。10 秒
     */
    private long consumerPullTimeoutMillis = 1000 * 10;
    /**
     * Consumption pattern,default is clustering
     * 消费模式：集群消费
     */
    private MessageModel messageModel = MessageModel.CLUSTERING;
    /**
     * Message queue listener
     * MessageQueueListener
     */
    private MessageQueueListener messageQueueListener;
    /**
     * Offset Storage
     * 消费者端的偏移量存储
     */
    private OffsetStore offsetStore;
    /**
     * Topic set you want to register
     * 注册的topic名称
     */
    private Set<String> registerTopics = new HashSet<String>();
    /**
     * Queue allocation algorithm
     * 队列分配算法
     */
    private AllocateMessageQueueStrategy allocateMessageQueueStrategy = new AllocateMessageQueueAveragely();
    /**
     * Whether the unit of subscription group
     */
    private boolean unitMode = false;
    /**
     * 最大重试的消费的次数
     */
    private int maxReconsumeTimes = 16;

    public DefaultMQPullConsumer() {
        this(MixAll.DEFAULT_CONSUMER_GROUP, null);
    }

    /**
     * 底层使用的仍然是defaultMQPullConsumerImpl
     * @param consumerGroup consumerGroup
     * @param rpcHook rpcHook
     */
    public DefaultMQPullConsumer(final String consumerGroup, RPCHook rpcHook) {
        this.consumerGroup = consumerGroup;
        defaultMQPullConsumerImpl = new DefaultMQPullConsumerImpl(this, rpcHook);
    }

    public DefaultMQPullConsumer(final String consumerGroup) {
        this(consumerGroup, null);
    }
    public DefaultMQPullConsumer(RPCHook rpcHook) {
        this(MixAll.DEFAULT_CONSUMER_GROUP, rpcHook);
    }

    //运维begin功能=================================
    @Override
    public void createTopic(String key, String newTopic, int queueNum) throws MQClientException {
        createTopic(key, newTopic, queueNum, 0);
    }
    @Override
    public void createTopic(String key, String newTopic, int queueNum, int topicSysFlag) throws MQClientException {
        this.defaultMQPullConsumerImpl.createTopic(key, newTopic, queueNum, topicSysFlag);
    }
    @Override
    public long searchOffset(MessageQueue mq, long timestamp) throws MQClientException {
        return this.defaultMQPullConsumerImpl.searchOffset(mq, timestamp);
    }
    @Override
    public long maxOffset(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.maxOffset(mq);
    }
    @Override
    public long minOffset(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.minOffset(mq);
    }
    @Override
    public long earliestMsgStoreTime(MessageQueue mq) throws MQClientException {
        return this.defaultMQPullConsumerImpl.earliestMsgStoreTime(mq);
    }
    @Override
    public MessageExt viewMessage(String offsetMsgId) throws RemotingException, MQBrokerException,
        InterruptedException, MQClientException {
        return this.defaultMQPullConsumerImpl.viewMessage(offsetMsgId);
    }
    @Override
    public QueryResult queryMessage(String topic, String key, int maxNum, long begin, long end)
        throws MQClientException, InterruptedException {
        return this.defaultMQPullConsumerImpl.queryMessage(topic, key, maxNum, begin, end);
    }
    //运维end功能=================================

    public AllocateMessageQueueStrategy getAllocateMessageQueueStrategy() {
        return allocateMessageQueueStrategy;
    }

    public void setAllocateMessageQueueStrategy(AllocateMessageQueueStrategy allocateMessageQueueStrategy) {
        this.allocateMessageQueueStrategy = allocateMessageQueueStrategy;
    }

    public long getBrokerSuspendMaxTimeMillis() {
        return brokerSuspendMaxTimeMillis;
    }

    public void setBrokerSuspendMaxTimeMillis(long brokerSuspendMaxTimeMillis) {
        this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public long getConsumerPullTimeoutMillis() {
        return consumerPullTimeoutMillis;
    }

    public void setConsumerPullTimeoutMillis(long consumerPullTimeoutMillis) {
        this.consumerPullTimeoutMillis = consumerPullTimeoutMillis;
    }

    public long getConsumerTimeoutMillisWhenSuspend() {
        return consumerTimeoutMillisWhenSuspend;
    }

    public void setConsumerTimeoutMillisWhenSuspend(long consumerTimeoutMillisWhenSuspend) {
        this.consumerTimeoutMillisWhenSuspend = consumerTimeoutMillisWhenSuspend;
    }

    public MessageModel getMessageModel() {
        return messageModel;
    }

    public void setMessageModel(MessageModel messageModel) {
        this.messageModel = messageModel;
    }

    public MessageQueueListener getMessageQueueListener() {
        return messageQueueListener;
    }

    public void setMessageQueueListener(MessageQueueListener messageQueueListener) {
        this.messageQueueListener = messageQueueListener;
    }

    public Set<String> getRegisterTopics() {
        return registerTopics;
    }

    public void setRegisterTopics(Set<String> registerTopics) {
        this.registerTopics = registerTopics;
    }

    /**
     * 将消息发回broker
     * @param msg ;
     * @param delayLevel ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     * @throws MQClientException ;
     */
    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel)
        throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        this.defaultMQPullConsumerImpl.sendMessageBack(msg, delayLevel, null);
    }
    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel, String brokerName)
        throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        this.defaultMQPullConsumerImpl.sendMessageBack(msg, delayLevel, brokerName);
    }

    /**
     * 获取所有的该topic的消费的队列
     * @param topic message topic
     * @return ;
     * @throws MQClientException ;
     */
    @Override
    public Set<MessageQueue> fetchSubscribeMessageQueues(String topic) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchSubscribeMessageQueues(topic);
    }

    /**
     * 启动
     * @throws MQClientException ;
     */
    @Override
    public void start() throws MQClientException {
        this.defaultMQPullConsumerImpl.start();
    }

    /**
     * 停止
     */
    @Override
    public void shutdown() {
        this.defaultMQPullConsumerImpl.shutdown();
    }

    /**
     * 注册MessageQueueListener
     * @param topic topic
     * @param listener listener
     */
    @Override
    public void registerMessageQueueListener(String topic, MessageQueueListener listener) {
        synchronized (this.registerTopics) {
            this.registerTopics.add(topic);
            if (listener != null) {
                this.messageQueueListener = listener;
            }
        }
    }

    /**
     * 从一个队列拉取消息
     * @param mq from which message queue
     * @param subExpression subscription expression.it only support or operation such as "tag1 || tag2 || tag3" <br> if
     * null or * expression,meaning subscribe
     * all
     * @param offset from where to pull
     * @param maxNums max pulling numbers
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    @Override
    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums);
    }

    /**
     * 从一个队列拉取消息
     * @param mq ;
     * @param subExpression ;
     * @param offset ;
     * @param maxNums ;
     * @param timeout ;
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    @Override
    public PullResult pull(MessageQueue mq, String subExpression, long offset, int maxNums, long timeout)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums, timeout);
    }

    /**
     * pull，pull成功，回调用PullCallback
     * @param mq ;
     * @param subExpression ;
     * @param offset ;
     * @param maxNums ;
     * @param pullCallback ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    @Override
    public void pull(MessageQueue mq, String subExpression, long offset, int maxNums, PullCallback pullCallback)
        throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums, pullCallback);
    }
    @Override
    public void pull(MessageQueue mq, String subExpression, long offset, int maxNums, PullCallback pullCallback,
        long timeout)
        throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQPullConsumerImpl.pull(mq, subExpression, offset, maxNums, pullCallback, timeout);
    }

    /**
     * 如果没找到消息，就阻塞
     * @param mq  ;
     * @param subExpression ;
     * @param offset ;
     * @param maxNums ;
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    @Override
    public PullResult pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.defaultMQPullConsumerImpl.pullBlockIfNotFound(mq, subExpression, offset, maxNums);
    }

    /**
     * 如果没有找到消息，就阻塞
     * @param mq mq
     * @param subExpression ;
     * @param offset ;
     * @param maxNums ;
     * @param pullCallback ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    @Override
    public void pullBlockIfNotFound(MessageQueue mq, String subExpression, long offset, int maxNums,
        PullCallback pullCallback)
        throws MQClientException, RemotingException, InterruptedException {
        this.defaultMQPullConsumerImpl.pullBlockIfNotFound(mq, subExpression, offset, maxNums, pullCallback);
    }

    /**
     * 更新消费偏移量
     * @param mq mq
     * @param offset offset
     * @throws MQClientException ;
     */
    @Override
    public void updateConsumeOffset(MessageQueue mq, long offset) throws MQClientException {
        this.defaultMQPullConsumerImpl.updateConsumeOffset(mq, offset);
    }

    /**
     * 获取消费者的偏移量
     * @param mq mq
     * @param fromStore fromstore
     * @return ;
     * @throws MQClientException ;
     */
    @Override
    public long fetchConsumeOffset(MessageQueue mq, boolean fromStore) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchConsumeOffset(mq, fromStore);
    }

    /**
     * 抓取messagequeue 在负载均衡里
     * @param topic message topic
     * @return l
     * @throws MQClientException ;
     */
    @Override
    public Set<MessageQueue> fetchMessageQueuesInBalance(String topic) throws MQClientException {
        return this.defaultMQPullConsumerImpl.fetchMessageQueuesInBalance(topic);
    }

    /**
     * viewMessage。。查询消息
     * @param topic topic
     * @param uniqKey uniqkey
     * @return ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     * @throws MQClientException ;
     */
    @Override
    public MessageExt viewMessage(String topic,
        String uniqKey) throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        try {
            MessageDecoder.decodeMessageId(uniqKey);
            return this.viewMessage(uniqKey);
        } catch (Exception e) {
            // Ignore
        }
        return this.defaultMQPullConsumerImpl.queryMessageByUniqKey(topic, uniqKey);
    }

    @Override
    public void sendMessageBack(MessageExt msg, int delayLevel, String brokerName, String consumerGroup)
        throws RemotingException, MQBrokerException, InterruptedException, MQClientException {
        this.defaultMQPullConsumerImpl.sendMessageBack(msg, delayLevel, brokerName, consumerGroup);
    }

    public OffsetStore getOffsetStore() {
        return offsetStore;
    }

    public void setOffsetStore(OffsetStore offsetStore) {
        this.offsetStore = offsetStore;
    }

    public DefaultMQPullConsumerImpl getDefaultMQPullConsumerImpl() {
        return defaultMQPullConsumerImpl;
    }

    @Override
    public boolean isUnitMode() {
        return unitMode;
    }

    @Override
    public void setUnitMode(boolean isUnitMode) {
        this.unitMode = isUnitMode;
    }

    public int getMaxReconsumeTimes() {
        return maxReconsumeTimes;
    }

    public void setMaxReconsumeTimes(final int maxReconsumeTimes) {
        this.maxReconsumeTimes = maxReconsumeTimes;
    }
}
