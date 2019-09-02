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

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.log.ClientLogger;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * Schedule service for pull consumer
 * 周期性调度的服务forPullConsumer
 * 使用这个案例。让我们实现pull模式下的负载均衡
 *      <p>
 *          public class PullScheduleService {

                 public static void main(String[] args) throws MQClientException {

                 final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("GroupName1");

                 scheduleService.setMessageModel(MessageModel.CLUSTERING);
                 scheduleService.registerPullTaskCallback("TopicTest", new PullTaskCallback() {

                @Override
                public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                try {

                long offset = consumer.fetchConsumeOffset(mq, false);
                if (offset < 0) {
                offset = 0;
                }

                PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                System.out.printf("%s%n", offset + "\t" + mq + "\t" + pullResult);
                switch (pullResult.getPullStatus()) {
                case FOUND:
                break;
                case NO_MATCHED_MSG:
                break;
                case NO_NEW_MSG:
                case OFFSET_ILLEGAL:
                break;
                default:
                break;
                }
                consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());

                context.setPullNextDelayTimeMillis(100);
                } catch (Exception e) {
                e.printStackTrace();
                }
                }
                });

                 scheduleService.start();
                 }
                 }
 *      </p>
 * @author ;
 */
public class MQPullConsumerScheduleService {

    private final InternalLogger log = ClientLogger.getLog();


    private final MessageQueueListener messageQueueListener = new MessageQueueListenerImpl();
    /**
     * 一个队列一个拉取线程
     */
    private final ConcurrentMap<MessageQueue, PullTaskImpl> taskTable = new ConcurrentHashMap<MessageQueue, PullTaskImpl>();
    /**
     * defaultMqPullConsumer
     */
    private DefaultMQPullConsumer defaultMQPullConsumer;
    /**
     * 拉取线程
     */
    private int pullThreadNums = 20;
    /**
     * 某个topic的 PullTaskCallback
     */
    private ConcurrentMap<String, PullTaskCallback> callbackTable = new ConcurrentHashMap<String, PullTaskCallback>();
    /**
     * scheduledThreadPoolExecutor
     */
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * 集群消费模式
     * @param consumerGroup ;
     */
    public MQPullConsumerScheduleService(final String consumerGroup) {
        this.defaultMQPullConsumer = new DefaultMQPullConsumer(consumerGroup);
        this.defaultMQPullConsumer.setMessageModel(MessageModel.CLUSTERING);
    }

    /**
     * 添加任务
     * @param topic topic
     * @param mqNewSet 最新被分配的队列
     */
    public void putTask(String topic, Set<MessageQueue> mqNewSet) {
        //一个队列一个线程
        Iterator<Entry<MessageQueue, PullTaskImpl>> it = this.taskTable.entrySet().iterator();
        while (it.hasNext()) {
            Entry<MessageQueue, PullTaskImpl> next = it.next();
            //找到指定topic的
            if (next.getKey().getTopic().equals(topic)) {
                //如果mqNewSet不包含此queue。就移除
                if (!mqNewSet.contains(next.getKey())) {
                    next.getValue().setCancelled(true);
                    it.remove();
                }
            }
        }

        /*
         * 添加新的被分配到的MessageQueue
         */
        for (MessageQueue mq : mqNewSet) {
            if (!this.taskTable.containsKey(mq)) {
                PullTaskImpl command = new PullTaskImpl(mq);
                this.taskTable.put(mq, command);
                this.scheduledThreadPoolExecutor.schedule(command, 0, TimeUnit.MILLISECONDS);

            }
        }
    }

    /**
     * 启动
     * @throws MQClientException ;
     */
    public void start() throws MQClientException {
        final String group = this.defaultMQPullConsumer.getConsumerGroup();
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
            this.pullThreadNums,
            new ThreadFactoryImpl("PullMsgThread-" + group)
        );

        this.defaultMQPullConsumer.setMessageQueueListener(this.messageQueueListener);

        this.defaultMQPullConsumer.start();

        log.info("MQPullConsumerScheduleService start OK, {} {}",
            this.defaultMQPullConsumer.getConsumerGroup(), this.callbackTable);
    }

    /**
     * 注册pulltaskCallback
     * 会回调其doPullTask方法
     */
    public void registerPullTaskCallback(final String topic, final PullTaskCallback callback) {
        this.callbackTable.put(topic, callback);
        this.defaultMQPullConsumer.registerMessageQueueListener(topic, null);
    }

    /**
     * 停止
     */
    public void shutdown() {
        if (this.scheduledThreadPoolExecutor != null) {
            this.scheduledThreadPoolExecutor.shutdown();
        }

        if (this.defaultMQPullConsumer != null) {
            this.defaultMQPullConsumer.shutdown();
        }
    }

    public ConcurrentMap<String, PullTaskCallback> getCallbackTable() {
        return callbackTable;
    }

    public void setCallbackTable(ConcurrentHashMap<String, PullTaskCallback> callbackTable) {
        this.callbackTable = callbackTable;
    }

    public int getPullThreadNums() {
        return pullThreadNums;
    }

    public void setPullThreadNums(int pullThreadNums) {
        this.pullThreadNums = pullThreadNums;
    }

    public DefaultMQPullConsumer getDefaultMQPullConsumer() {
        return defaultMQPullConsumer;
    }

    public void setDefaultMQPullConsumer(DefaultMQPullConsumer defaultMQPullConsumer) {
        this.defaultMQPullConsumer = defaultMQPullConsumer;
    }

    public MessageModel getMessageModel() {
        return this.defaultMQPullConsumer.getMessageModel();
    }

    public void setMessageModel(MessageModel messageModel) {
        this.defaultMQPullConsumer.setMessageModel(messageModel);
    }

    /**
     * messageQuqueListenerImpl
     */
    class MessageQueueListenerImpl implements MessageQueueListener {
        @Override
        public void messageQueueChanged(String topic, Set<MessageQueue> mqAll, Set<MessageQueue> mqDivided) {
            MessageModel messageModel =
                MQPullConsumerScheduleService.this.defaultMQPullConsumer.getMessageModel();
            switch (messageModel) {
                //广播消费
                case BROADCASTING:
                    MQPullConsumerScheduleService.this.putTask(topic, mqAll);
                    break;
                    //集群消费
                case CLUSTERING:
                    MQPullConsumerScheduleService.this.putTask(topic, mqDivided);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * pullTaskImpl
     * @author ;
     */
    class PullTaskImpl implements Runnable {
        /**
         * 队列
         */
        private final MessageQueue messageQueue;
        /**
         * 是否取消
         */
        private volatile boolean cancelled = false;

        public PullTaskImpl(final MessageQueue messageQueue) {
            this.messageQueue = messageQueue;
        }

        @Override
        public void run() {
            String topic = this.messageQueue.getTopic();
            if (!this.isCancelled()) {
                PullTaskCallback pullTaskCallback =
                    MQPullConsumerScheduleService.this.callbackTable.get(topic);
                if (pullTaskCallback != null) {
                    final PullTaskContext context = new PullTaskContext();
                    context.setPullConsumer(MQPullConsumerScheduleService.this.defaultMQPullConsumer);
                    try {
                        //执行doPullTask
                        pullTaskCallback.doPullTask(this.messageQueue, context);
                    } catch (Throwable e) {
                        context.setPullNextDelayTimeMillis(1000);
                        log.error("doPullTask Exception", e);
                    }

                    if (!this.isCancelled()) {
                        MQPullConsumerScheduleService.this.scheduledThreadPoolExecutor.schedule(this,
                            context.getPullNextDelayTimeMillis(), TimeUnit.MILLISECONDS);
                    } else {
                        log.warn("The Pull Task is cancelled after doPullTask, {}", messageQueue);
                    }
                } else {
                    log.warn("Pull Task Callback not exist , {}", topic);
                }
            } else {
                log.warn("The Pull Task is cancelled, {}", messageQueue);
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public MessageQueue getMessageQueue() {
            return messageQueue;
        }
    }
}
