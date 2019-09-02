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
package org.apache.rocketmq.store;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.InternalLoggerFactory;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.apache.rocketmq.store.util.LibC;
import sun.nio.ch.DirectBuffer;

/**
 * 短暂的存储池
 * 用于池化管理多个ByteBuffer对象,进行借与还的操作
 *
 * 通常有如下两种方式进行读写：
 *
 * 第一种，Mmap+PageCache的方式，读写消息都走的是pageCache，
 * 这样子读写都在pagecache里面不可避免会有锁的问题，
 * 在并发的读写操作情况下，会出现缺页中断降低，内存加锁，污染页的回写。
 * 第二种，DirectByteBuffer(堆外内存)+PageCache的两层架构方式，
 * 这样子可以实现读写消息分离，
 * 写入消息时候写到的是DirectByteBuffer——堆外内存中,
 * 读消息走的是PageCache(对于,DirectByteBuffer是两步刷盘，一步是刷到PageCache，还有一步是刷到磁盘文件中)，
 * 带来的好处就是，避免了内存操作的很多容易堵的地方，降低了时延，
 * 比如说缺页中断降低，内存加锁，污染页的回写。
 *
 *
 * 开启transientStorePoolEnable
 *
 * 在broker配置文件中将transientStorePoolEnable设置为true。
 *
 * 方案依据：
 * 启用“读写”分离，消息发送时消息先追加到DirectByteBuffer(堆外内存)中，
 * 然后在异步刷盘机制下，会将DirectByteBuffer中的内容提交到PageCache，
 * 然后刷写到磁盘。消息拉取时，直接从PageCache中拉取，实现了读写分离，减轻了PageCaceh的压力，能从根本上解决该问题。
 * 方案缺点：
 * 会增加数据丢失的可能性，
 * 如果Broker JVM进程异常退出，
 * 提交到PageCache中的消息是不会丢失的，
 * 但存在堆外内存(DirectByteBuffer)中但还未提交到PageCache中的这部分消息，将
 * 会丢失。但通常情况下，RocketMQ进程退出的可能性不大。
 * @author ;
 */
public class TransientStorePool {
    private static final InternalLogger log = InternalLoggerFactory.getLogger(LoggerName.STORE_LOGGER_NAME);

    /**
     * 池的大小有多少,默认5
     */
    private final int poolSize;
    /**
     * 每个commitLog文件大小，默认1G
     */
    private final int fileSize;
    /**
     * 双端队列记录可用的buffers
     */
    private final Deque<ByteBuffer> availableBuffers;
    /**
     * 消息存储配置
     */
    private final MessageStoreConfig storeConfig;

    public TransientStorePool(final MessageStoreConfig storeConfig) {
        this.storeConfig = storeConfig;
        this.poolSize = storeConfig.getTransientStorePoolSize();
        this.fileSize = storeConfig.getMapedFileSizeCommitLog();
        this.availableBuffers = new ConcurrentLinkedDeque<>();
    }

    /**
     * It's a heavy init method.
     * 初始化操作，比较重
     */
    public void init() {
        for (int i = 0; i < poolSize; i++) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(fileSize);
            final long address = ((DirectBuffer) byteBuffer).address();
            Pointer pointer = new Pointer(address);
            LibC.INSTANCE.mlock(pointer, new NativeLong(fileSize));
            availableBuffers.offer(byteBuffer);
        }
    }

    /**
     * 销毁：执行unlock系统调用
     */
    public void destroy() {
        for (ByteBuffer byteBuffer : availableBuffers) {
            final long address = ((DirectBuffer) byteBuffer).address();
            Pointer pointer = new Pointer(address);
            LibC.INSTANCE.munlock(pointer, new NativeLong(fileSize));
        }
    }

    /**
     * 归还buffer
     * @param byteBuffer ;
     */
    public void returnBuffer(ByteBuffer byteBuffer) {
        byteBuffer.position(0);
        byteBuffer.limit(fileSize);
        this.availableBuffers.offerFirst(byteBuffer);
    }

    /**
     * 借一个buffer
     * @return ;
     */
    public ByteBuffer borrowBuffer() {
        ByteBuffer buffer = availableBuffers.pollFirst();
        if (availableBuffers.size() < poolSize * 0.4) {
            log.warn("TransientStorePool only remain {} sheets.", availableBuffers.size());
        }
        return buffer;
    }

    /**
     * 可用的buffer的数量
     * @return ;
     */
    public int remainBufferNumbs() {
        if (storeConfig.isTransientStorePoolEnable()) {
            return availableBuffers.size();
        }
        return Integer.MAX_VALUE;
    }
}
