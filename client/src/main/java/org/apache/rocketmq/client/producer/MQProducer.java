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
package org.apache.rocketmq.client.producer;

import java.util.Collection;
import java.util.List;
import org.apache.rocketmq.client.MQAdmin;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
/**
 * mq生产者
 * @author ;
 */
public interface MQProducer extends MQAdmin {
    /**
     * 启动
     * @throws MQClientException ;
     */
    void start() throws MQClientException;

    /**
     * shutdown
     */
    void shutdown();

    /**
     * 获取生产者的MessageQueue
     * @param topic topic
     * @return ;
     * @throws MQClientException ;
     */
    List<MessageQueue> fetchPublishMessageQueues(final String topic) throws MQClientException;

    /**
     * 同步发送消息
     * @param msg msg
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg) throws MQClientException, RemotingException, MQBrokerException,
        InterruptedException;

    /**
     * 同步发送
     * @param msg msg
     * @param timeout 带超时时间
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg, final long timeout) throws MQClientException,
        RemotingException, MQBrokerException, InterruptedException;

    /**
     * 异步发送
     * @param msg msg
     * @param sendCallback 发送回调勾子
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final SendCallback sendCallback) throws MQClientException,
        RemotingException, InterruptedException;

    /**
     * 带超时的异步发送
     * @param msg msg
     * @param sendCallback 发送回调
     * @param timeout 超时
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final SendCallback sendCallback, final long timeout)
        throws MQClientException, RemotingException, InterruptedException;

    /**
     * oneway发送
     * @param msg msg
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void sendOneway(final Message msg) throws MQClientException, RemotingException, InterruptedException;

    /**
     * 选择一个队列进行同步发送
     * @param msg msg
     * @param mq 队列
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg, final MessageQueue mq) throws MQClientException,
        RemotingException, MQBrokerException, InterruptedException;

    /**
     * 选择一个队列
     * @param msg msg
     * @param mq mq
     * @param timeout 超时
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg, final MessageQueue mq, final long timeout)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException;

    /**
     * 异步选取一个队列发送消息
     * @param msg msg
     * @param mq mq
     * @param sendCallback sendCallback 执行callback
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback)
        throws MQClientException, RemotingException, InterruptedException;

    /**
     * 异步选取一个队列发送消息，带超时
     * @param msg ;
     * @param mq ;
     * @param sendCallback ;
     * @param timeout ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final MessageQueue mq, final SendCallback sendCallback, long timeout)
        throws MQClientException, RemotingException, InterruptedException;

    /**
     * oneWay发送
     * @param msg msg
     * @param mq ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void sendOneway(final Message msg, final MessageQueue mq) throws MQClientException,
        RemotingException, InterruptedException;

    /**
     * 通过MessageQueueSelector同步发送Message
     * @param msg msg
     * @param selector selector
     * @param arg arg
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException;

    /**
     * 通过MessageQueueSelector同步发送Message。带超时时间
     * @param msg ;
     * @param selector ;
     * @param arg ;
     * @param timeout ;
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Message msg, final MessageQueueSelector selector, final Object arg,
        final long timeout) throws MQClientException, RemotingException, MQBrokerException,
        InterruptedException;

    /**
     * 异步发送消息，通过MessageQueueSelector选取一个队列
     * @param msg msg
     * @param selector ;
     * @param arg ;
     * @param sendCallback ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final MessageQueueSelector selector, final Object arg,
        final SendCallback sendCallback) throws MQClientException, RemotingException,
        InterruptedException;

    /**
     * 异步发送Message，通过MessageQueueSelector选取一个队列，带超时时间
     * @param msg ;
     * @param selector ;
     * @param arg ;
     * @param sendCallback ;
     * @param timeout ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void send(final Message msg, final MessageQueueSelector selector, final Object arg,
        final SendCallback sendCallback, final long timeout) throws MQClientException, RemotingException,
        InterruptedException;

    /**
     * 同步发送
     * @param msg msg
     * @param selector selector
     * @param arg arg
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws InterruptedException ;
     */
    void sendOneway(final Message msg, final MessageQueueSelector selector, final Object arg)
        throws MQClientException, RemotingException, InterruptedException;

    /**
     * 发送消息在事务里
     * @param msg ;
     * @param tranExecuter tranExecuter本地事务执行器
     * @param arg ;
     * @return ;
     * @throws MQClientException ;
     */
    TransactionSendResult sendMessageInTransaction(final Message msg,
        final LocalTransactionExecuter tranExecuter, final Object arg) throws MQClientException;

    /**
     * 发送消息在事务里
     * @param msg msg
     * @param arg arg
     * @return ;
     * @throws MQClientException ;
     */
    TransactionSendResult sendMessageInTransaction(final Message msg,
        final Object arg) throws MQClientException;

    /**
     * for batch 批量发送消息
     * @param msgs msgs
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Collection<Message> msgs) throws MQClientException, RemotingException, MQBrokerException,
        InterruptedException;

    /**
     * 批量发送消息，带超时
     * @param msgs ;
     * @param timeout 超时
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Collection<Message> msgs, final long timeout) throws MQClientException,
        RemotingException, MQBrokerException, InterruptedException;

    /**
     * 选取一个队列发送批量消息
     * @param msgs msgs
     * @param mq 队列
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Collection<Message> msgs, final MessageQueue mq) throws MQClientException,
        RemotingException, MQBrokerException, InterruptedException;

    /**
     * 选取一个队列发送批量消息
     * @param msgs msgs
     * @param mq mq
     * @param timeout 超时
     * @return ;
     * @throws MQClientException ;
     * @throws RemotingException ;
     * @throws MQBrokerException ;
     * @throws InterruptedException ;
     */
    SendResult send(final Collection<Message> msgs, final MessageQueue mq, final long timeout)
        throws MQClientException, RemotingException, MQBrokerException, InterruptedException;
}
