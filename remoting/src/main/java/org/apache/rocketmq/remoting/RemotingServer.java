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
package org.apache.rocketmq.remoting;

import io.netty.channel.Channel;
import java.util.concurrent.ExecutorService;
import org.apache.rocketmq.remoting.common.Pair;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.exception.RemotingTooMuchRequestException;
import org.apache.rocketmq.remoting.netty.NettyRequestProcessor;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

/**
 * 远程Server
 * @author ;
 */
public interface RemotingServer extends RemotingService {
    /**
     * 注册一个针对requestCode的requestProcessor
     * @param requestCode 请求码
     * @param processor 处理器
     * @param executor 线程池
     */
    void registerProcessor(final int requestCode, final NettyRequestProcessor processor, final ExecutorService executor);

    /**
     * 注册一个默认处理器
     * @param processor processor
     * @param executor 线程池
     */
    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);
    /**
     * 监听本地端口
     * @return ;
     */
    int localListenPort();

    /**
     * 根据requestCode获取NettyRequestProcessor和ExecutorService的元组
     * @param requestCode ;
     * @return ;
     */
    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);

    /**
     * 同步执行
     * @param channel channel
     * @param request request
     * @param timeoutMillis 超时
     * @return ;
     * @throws InterruptedException ;
     * @throws RemotingSendRequestException  ;
     * @throws RemotingTimeoutException ;
     */
    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
        final long timeoutMillis) throws InterruptedException, RemotingSendRequestException,
        RemotingTimeoutException;

    /**
     * 异步执行
     * @param channel channel
     * @param request request
     * @param timeoutMillis timeout
     * @param invokeCallback  异步回调勾子
     * @throws InterruptedException ;
     * @throws RemotingTooMuchRequestException ;
     * @throws RemotingTimeoutException ;
     * @throws RemotingSendRequestException ;
     */
    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
        final InvokeCallback invokeCallback) throws InterruptedException,
        RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

    /**
     * invokeOneWay
     * @param channel channel
     * @param request request
     * @param timeoutMillis 超时时间
     * @throws InterruptedException ;
     * @throws RemotingTooMuchRequestException ;
     * @throws RemotingTimeoutException ;
     * @throws RemotingSendRequestException ;
     */
    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
        RemotingSendRequestException;

}
