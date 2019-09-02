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

/**
 * Netty的Channel的事件监听器
 * @author ;
 */
public interface ChannelEventListener {
    /**
     * 当channelConnect时触发
     * @param remoteAddr 远程地址
     * @param channel channel
     */
    void onChannelConnect(final String remoteAddr, final Channel channel);

    /**
     * 当channel关闭时触发
     * @param remoteAddr 远程地址
     * @param channel channel
     */
    void onChannelClose(final String remoteAddr, final Channel channel);

    /**
     * 当channel异常时触发
     * @param remoteAddr ;
     * @param channel channel
     */
    void onChannelException(final String remoteAddr, final Channel channel);

    /**
     * 当channel空闲了以后触发
     * @param remoteAddr 远程地址
     * @param channel channel
     */
    void onChannelIdle(final String remoteAddr, final Channel channel);
}
