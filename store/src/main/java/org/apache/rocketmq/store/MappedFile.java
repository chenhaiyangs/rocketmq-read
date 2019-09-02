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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.rocketmq.common.UtilAll;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.InternalLoggerFactory;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageExtBatch;
import org.apache.rocketmq.store.config.FlushDiskType;
import org.apache.rocketmq.store.util.LibC;
import sun.nio.ch.DirectBuffer;

/**
 * MappedFile
 * @author ;
 */
public class MappedFile extends ReferenceResource {
    /**
     * osPache_size大小为4kb
     */
    public static final int OS_PAGE_SIZE = 1024 * 4;
    protected static final InternalLogger log = InternalLoggerFactory.getLogger(LoggerName.STORE_LOGGER_NAME);

    /**
     * 总共映射的虚拟内存的大小
     */
    private static final AtomicLong TOTAL_MAPPED_VIRTUAL_MEMORY = new AtomicLong(0);
    /**
     * 总共映射的文件数量
     */
    private static final AtomicInteger TOTAL_MAPPED_FILES = new AtomicInteger(0);



    /**
     * 写的偏移量
     */
    protected final AtomicInteger wrotePosition = new AtomicInteger(0);
    /**
     * ADD BY ChenYang
     * 提交的偏移量
     */
    protected final AtomicInteger committedPosition = new AtomicInteger(0);
    /**
     * 已经刷盘的偏移量
     */
    private final AtomicInteger flushedPosition = new AtomicInteger(0);
    /**
     * 文件大小
     */
    protected int fileSize;
    /**
     * 文件channel
     */
    protected FileChannel fileChannel;
    /**
     * Message will put to here first, and then reput to FileChannel if writeBuffer is not null.
     * 消息先会写到writeBuffer这个变量，然后会put到FileChannel
     */
    protected ByteBuffer writeBuffer = null;
    /**
     * TransientStorePool瞬时存储池
     */
    protected TransientStorePool transientStorePool = null;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件起始偏移量
     */
    private long fileFromOffset;
    /**
     * 文件
     */
    private File file;
    /**
     * 内存映射文件
     */
    private MappedByteBuffer mappedByteBuffer;
    /**
     * 存储时间戳
     */
    private volatile long storeTimestamp = 0;
    /**
     * 是队列里第一个创建出的mappedFile
     */
    private boolean firstCreateInQueue = false;

    /**
     * 下面是一堆的构造函数
     */
    public MappedFile() {
    }
    public MappedFile(final String fileName, final int fileSize) throws IOException {
        init(fileName, fileSize);
    }
    public MappedFile(final String fileName, final int fileSize,
        final TransientStorePool transientStorePool) throws IOException {
        init(fileName, fileSize, transientStorePool);
    }

    /**
     * 工具性质的方法：确保目录是ok的。没有就自动创建
     * @param dirName 地址
     */
    public static void ensureDirOK(final String dirName) {
        if (dirName != null) {
            File f = new File(dirName);
            if (!f.exists()) {
                boolean result = f.mkdirs();
                log.info(dirName + " mkdir " + (result ? "OK" : "Failed"));
            }
        }
    }

    /**
     * 工具性质的方式：释放byteBuffer占用的内存区域
     * @param buffer bffer
     */
    public static void clean(final ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect() || buffer.capacity() == 0) {
            return;
        }
        //获取viewedBuffer的cleaner。并执行其clean函数，释放内存
        invoke(invoke(viewed(buffer), "cleaner"), "clean");
    }

    /**
     * 工具性质的方法： 反射执行其方法
     * @param target 类
     * @param methodName 方法名
     * @param args 参数
     * @return ;
     */
    private static Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    /**
     * 工具类型的静态方法：反射获取Method
     * @param target 类
     * @param methodName 方法名
     * @param args 参数
     * @return ;
     * @throws NoSuchMethodException ;
     */
    private static Method method(Object target, String methodName, Class<?>[] args)
        throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    /**
     * 获取view bytebuffer的函数名。并执行其函数
     * @param buffer buffer
     * @return ;
     */
    private static ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";

        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null) {
            return buffer;
        }
        else {
            return viewed(viewedBuffer);
        }
    }

    public static int getTotalMappedFiles() {
        return TOTAL_MAPPED_FILES.get();
    }

    public static long getTotalMappedVirtualMemory() {
        return TOTAL_MAPPED_VIRTUAL_MEMORY.get();
    }

    /**
     * 初始化
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param transientStorePool 瞬时池
     * @throws IOException ;
     */
    public void init(final String fileName, final int fileSize,
        final TransientStorePool transientStorePool) throws IOException {
        init(fileName, fileSize);

        this.writeBuffer = transientStorePool.borrowBuffer();
        this.transientStorePool = transientStorePool;
    }

    /**
     * 初始化池子
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @throws IOException ;
     */
    private void init(final String fileName, final int fileSize) throws IOException {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.file = new File(fileName);
        this.fileFromOffset = Long.parseLong(this.file.getName());
        boolean ok = false;

        ensureDirOK(this.file.getParent());

        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(MapMode.READ_WRITE, 0, fileSize);
            TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(fileSize);
            TOTAL_MAPPED_FILES.incrementAndGet();
            ok = true;
        } catch (FileNotFoundException e) {
            log.error("create file channel " + this.fileName + " Failed. ", e);
            throw e;
        } catch (IOException e) {
            log.error("map file " + this.fileName + " Failed. ", e);
            throw e;
        } finally {
            if (!ok && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }

    /**
     * 获取更新时间戳
     * @return  ;
     */
    public long getLastModifiedTimestamp() {
        return this.file.lastModified();
    }

    /**
     * 获取文件大写
     * @return ;
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 获取文件channel
     * @return ;
     */
    public FileChannel getFileChannel() {
        return fileChannel;
    }

    /**
     * 拼接Message
     * @param msg 消息
     * @param cb 拼接消息的回调
     * @return ;
     */
    public AppendMessageResult appendMessage(final MessageExtBrokerInner msg, final AppendMessageCallback cb) {
        return appendMessagesInner(msg, cb);
    }

    /**
     * 拼接BatchMessage
     * @param messageExtBatch ;
     * @param cb 拼接message的回调
     * @return ;
     */
    public AppendMessageResult appendMessages(final MessageExtBatch messageExtBatch, final AppendMessageCallback cb) {
        return appendMessagesInner(messageExtBatch, cb);
    }

    /**
     * 拼接Message
     * @param messageExt 消息
     * @param cb 回调用
     * @return ;
     */
    public AppendMessageResult appendMessagesInner(final MessageExt messageExt, final AppendMessageCallback cb) {
        assert messageExt != null;
        assert cb != null;
        //当前的写位置
        int currentPos = this.wrotePosition.get();

        //如果小于文件size
        if (currentPos < this.fileSize) {

            ByteBuffer byteBuffer = writeBuffer != null ? writeBuffer.slice() : this.mappedByteBuffer.slice();
            //标记写入位置
            byteBuffer.position(currentPos);
            AppendMessageResult result = null;
            if (messageExt instanceof MessageExtBrokerInner) {
                //委托给AppendMessageCallback
                result = cb.doAppend(this.getFileFromOffset(), byteBuffer, this.fileSize - currentPos, (MessageExtBrokerInner) messageExt);
            } else if (messageExt instanceof MessageExtBatch) {
                //委托给AppendMessageCallback
                result = cb.doAppend(this.getFileFromOffset(), byteBuffer, this.fileSize - currentPos, (MessageExtBatch) messageExt);
            } else {
                return new AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR);
            }
            //重置当前的wrotePosition
            this.wrotePosition.addAndGet(result.getWroteBytes());
            this.storeTimestamp = result.getStoreTimestamp();
            return result;
        }
        //否则抛出未知错误
        log.error("MappedFile.appendMessage return null, wrotePosition: {} fileSize: {}", currentPos, this.fileSize);
        return new AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR);
    }

    /**
     * 获取文件的开始offset
     * @return ;
     */
    public long getFileFromOffset() {
        return this.fileFromOffset;
    }

    /**
     * 拼接byte消息
     * @param data data
     * @return ;
     */
    public boolean appendMessage(final byte[] data) {
        int currentPos = this.wrotePosition.get();

        if ((currentPos + data.length) <= this.fileSize) {
            try {
                this.fileChannel.position(currentPos);
                this.fileChannel.write(ByteBuffer.wrap(data));
            } catch (Throwable e) {
                log.error("Error occurred when append message to mappedFile.", e);
            }
            this.wrotePosition.addAndGet(data.length);
            return true;
        }
        return false;
    }

    /**
     * Content of data from offset to offset + length will be wrote to file.
     * 从指定的位置拼接消息
     * @param offset The offset of the subarray to be used.
     * @param length The length of the subarray to be used.
     */
    public boolean appendMessage(final byte[] data, final int offset, final int length) {
        int currentPos = this.wrotePosition.get();

        if ((currentPos + length) <= this.fileSize) {
            try {
                this.fileChannel.position(currentPos);
                this.fileChannel.write(ByteBuffer.wrap(data, offset, length));
            } catch (Throwable e) {
                log.error("Error occurred when append message to mappedFile.", e);
            }
            this.wrotePosition.addAndGet(length);
            return true;
        }

        return false;
    }

    /**
     * 刷盘，参数为当前flushLeastPages
     * @return The current flushed position
     */
    public int flush(final int flushLeastPages) {
        if (this.isAbleToFlush(flushLeastPages)) {
            if (this.hold()) {
                int value = getReadPosition();

                try {
                    //We only append data to fileChannel or mappedByteBuffer, never both.
                    if (writeBuffer != null || this.fileChannel.position() != 0) {
                        this.fileChannel.force(false);
                    } else {
                        this.mappedByteBuffer.force();
                    }
                } catch (Throwable e) {
                    log.error("Error occurred when force data to disk.", e);
                }
                //刷盘以后，更新flushedPosition到writePosition
                this.flushedPosition.set(value);
                this.release();
            } else {
                //已经在flush中了
                log.warn("in flush, hold failed, flush offset = " + this.flushedPosition.get());
                this.flushedPosition.set(getReadPosition());
            }
        }
        //返回flushedPosition
        return this.getFlushedPosition();
    }

    /**
     * 提交
     * @param commitLeastPages 提交的页数
     * @return ;
     */
    public int commit(final int commitLeastPages) {
        if (writeBuffer == null) {
            //no need to commit data to file channel, so just regard wrotePosition as committedPosition.
            return this.wrotePosition.get();
        }
        if (this.isAbleToCommit(commitLeastPages)) {
            if (this.hold()) {
                commit0(commitLeastPages);
                this.release();
            } else {
                log.warn("in commit, hold failed, commit offset = " + this.committedPosition.get());
            }
        }

        // All dirty data has been committed to FileChannel.
        //所有的脏页都写进了FileChannel
        if (writeBuffer != null && this.transientStorePool != null && this.fileSize == this.committedPosition.get()) {
            //归还buffer
            this.transientStorePool.returnBuffer(writeBuffer);
            //重置writeBuffer为null
            this.writeBuffer = null;
        }
        //返回committedPosition的偏移量
        return this.committedPosition.get();
    }

    /**
     * 提交数据
     * @param commitLeastPages ;
     */
    protected void commit0(final int commitLeastPages) {
        int writePos = this.wrotePosition.get();
        int lastCommittedPosition = this.committedPosition.get();

        if (writePos - this.committedPosition.get() > 0) {
            try {
                //返回一个新的buffer,但和原来的是一个引用
                ByteBuffer byteBuffer = writeBuffer.slice();
                byteBuffer.position(lastCommittedPosition);
                byteBuffer.limit(writePos);
                this.fileChannel.position(lastCommittedPosition);
                this.fileChannel.write(byteBuffer);
                //将提交的committedPosition重置为写偏移量
                this.committedPosition.set(writePos);
            } catch (Throwable e) {
                log.error("Error occurred when commit data to FileChannel.", e);
            }
        }
    }

    /**
     * 是否可以flush
     * @param flushLeastPages 至少flush的脏页数量。
     * @return ;
     */
    private boolean isAbleToFlush(final int flushLeastPages) {
        int flush = this.flushedPosition.get();
        int write = getReadPosition();

        if (this.isFull()) {
            return true;
        }

        if (flushLeastPages > 0) {
            //目前未刷盘的页的数量大于flushLeastPages，才可以刷
            return ((write / OS_PAGE_SIZE) - (flush / OS_PAGE_SIZE)) >= flushLeastPages;
        }
        //flushLeastPages=0,只需返回写的位置大于flush即可。就代表可以刷
        return write > flush;
    }

    /**
     * 是否可以提交
     * @param commitLeastPages 需要提交的页
     * @return ;
     */
    protected boolean isAbleToCommit(final int commitLeastPages) {
        int commit = this.committedPosition.get();
        int write = this.wrotePosition.get();

        if (this.isFull()) {
            return true;
        }

        //目前未刷盘的页的数量大于commitLeastPages，才可以提交
        if (commitLeastPages > 0) {
            return ((write / OS_PAGE_SIZE) - (commit / OS_PAGE_SIZE)) >= commitLeastPages;
        }
        //直接返回可以commit。只要写偏移量大于commit
        return write > commit;
    }

    /**
     * 获取已经刷盘的偏移量
     * @return ;
     */
    public int getFlushedPosition() {
        return flushedPosition.get();
    }

    /**
     * 设置已经刷盘的位置
     * @param pos 位置
     */
    public void setFlushedPosition(int pos) {
        this.flushedPosition.set(pos);
    }

    /**
     * 文件已经写满
     * @return ;
     */
    public boolean isFull() {
        return this.fileSize == this.wrotePosition.get();
    }

    /**
     * 查询MappedByteBuffer
     * @param pos 位置
     * @param size 写入大小
     * @return ;
     */
    public SelectMappedBufferResult selectMappedBuffer(int pos, int size) {
        int readPosition = getReadPosition();
        if ((pos + size) <= readPosition) {

            if (this.hold()) {
                //slice() 方法根据现有缓冲区创建一个子缓冲区
                ByteBuffer byteBuffer = this.mappedByteBuffer.slice();
                //起始位置
                byteBuffer.position(pos);
                ByteBuffer byteBufferNew = byteBuffer.slice();
                //limit位置
                byteBufferNew.limit(size);
                //返回结果
                return new SelectMappedBufferResult(this.fileFromOffset + pos, byteBufferNew, size, this);
            } else {
                log.warn("matched, but hold failed, request pos: " + pos + ", fileFromOffset: "
                    + this.fileFromOffset);
            }
        } else {
            log.warn("selectMappedBuffer request pos invalid, request pos: " + pos + ", size: " + size
                + ", fileFromOffset: " + this.fileFromOffset);
        }

        return null;
    }

    /**
     * 查询mappeedBuffer,根据位置。截取pos之后的所有的文件
     * @param pos pos 。
     * @return ;
     */
    public SelectMappedBufferResult selectMappedBuffer(int pos) {

        int readPosition = getReadPosition();
        if (pos < readPosition && pos >= 0) {
            if (this.hold()) {
                ByteBuffer byteBuffer = this.mappedByteBuffer.slice();
                byteBuffer.position(pos);
                int size = readPosition - pos;
                ByteBuffer byteBufferNew = byteBuffer.slice();
                byteBufferNew.limit(size);
                return new SelectMappedBufferResult(this.fileFromOffset + pos, byteBufferNew, size, this);
            }
        }

        return null;
    }

    /**
     * 执行cleanup
     * @param currentRef 当前的引用
     * @return ;
     */
    @Override
    public boolean cleanup(final long currentRef) {
        //仍然在使用，不能cleanup
        if (this.isAvailable()) {
            log.error("this file[REF:" + currentRef + "] " + this.fileName
                + " have not shutdown, stop unmapping.");
            return false;
        }

        //如果已经cleanover,返回true
        if (this.isCleanupOver()) {
            log.error("this file[REF:" + currentRef + "] " + this.fileName
                + " have cleanup, do not do it again.");
            return true;
        }

        //清除文件的虚拟内存
        clean(this.mappedByteBuffer);
        //映射的虚拟内存大小，减去本文件的大小
        TOTAL_MAPPED_VIRTUAL_MEMORY.addAndGet(this.fileSize * (-1));
        //映射文件的总数减一
        TOTAL_MAPPED_FILES.decrementAndGet();
        log.info("unmap file[REF:" + currentRef + "] " + this.fileName + " OK");
        return true;
    }

    /**
     * 销毁文件
     * @param intervalForcibly 周期
     * @return ;
     */
    public boolean destroy(final long intervalForcibly) {
        //shutdown
        this.shutdown(intervalForcibly);

        //执行cleanupOver成功
        if (this.isCleanupOver()) {
            try {
                //fileChannel.close()
                this.fileChannel.close();
                log.info("close file channel " + this.fileName + " OK");

                long beginTime = System.currentTimeMillis();
                boolean result = this.file.delete();
                log.info("delete file[REF:" + this.getRefCount() + "] " + this.fileName
                    + (result ? " OK, " : " Failed, ") + "W:" + this.getWrotePosition() + " M:"
                    + this.getFlushedPosition() + ", "
                    + UtilAll.computeEclipseTimeMilliseconds(beginTime));
            } catch (Exception e) {
                log.warn("close file channel " + this.fileName + " Failed. ", e);
            }

            return true;
        } else {
            log.warn("destroy mapped file[REF:" + this.getRefCount() + "] " + this.fileName
                + " Failed. cleanupOver: " + this.cleanupOver);
        }

        return false;
    }

    /**
     * 获取写的偏移量
     * @return ;
     */
    public int getWrotePosition() {
        return wrotePosition.get();
    }

    /**
     * 设置写的偏移
     * @param pos pos
     */
    public void setWrotePosition(int pos) {
        this.wrotePosition.set(pos);
    }

    /**
     * 有效数据的最大的写position
     * @return The max position which have valid data
     */
    public int getReadPosition() {
        return this.writeBuffer == null ? this.wrotePosition.get() : this.committedPosition.get();
    }

    /**
     * 设置提交的偏移量
     * @param pos pos
     */
    public void setCommittedPosition(int pos) {
        this.committedPosition.set(pos);
    }

    /**
     * 预热，这里预热的主要目的是在mmap之后无物理内存建立map关联关系。减少缺页
     * @param type 刷盘类型
     * @param pages 页数
     */
    public void warmMappedFile(FlushDiskType type, int pages) {

        long beginTime = System.currentTimeMillis();
        ByteBuffer byteBuffer = this.mappedByteBuffer.slice();

        int flush = 0;
        long time = System.currentTimeMillis();

        for (int i = 0, j = 0; i < this.fileSize; i += MappedFile.OS_PAGE_SIZE, j++) {

           // 需注意的是，仅分配内存并调用 mlock 并不会为调用进程锁定这些内存，
            // 因为对应的分页可能是写时复制（copy-on-write）的5。因此，你应该在每个页面中写入一个假的值：
            byteBuffer.put(i, (byte) 0);
            /*
             * force flush when flush disk type is sync
             * 同步刷盘
             */
            if (type == FlushDiskType.SYNC_FLUSH) {
                if ((i / OS_PAGE_SIZE) - (flush / OS_PAGE_SIZE) >= pages) {
                    flush = i;
                    mappedByteBuffer.force();
                }
            }

            /*
             *  阻止gc: prevent gc
             */
            if (j % 1000 == 0) {
                log.info("j={}, costTime={}", j, System.currentTimeMillis() - time);
                time = System.currentTimeMillis();
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
            }
        }

        /*
         * 同步刷盘
         * force flush when prepare load finished
         */
        if (type == FlushDiskType.SYNC_FLUSH) {
            log.info("mapped file warm-up done, force to disk, mappedFile={}, costTime={}",
                this.getFileName(), System.currentTimeMillis() - beginTime);
            mappedByteBuffer.force();
        }
        log.info("mapped file warm-up done. mappedFile={}, costTime={}", this.getFileName(),
            System.currentTimeMillis() - beginTime);
        //mlock
        this.mlock();
    }

    public String getFileName() {
        return fileName;
    }

    public MappedByteBuffer getMappedByteBuffer() {
        return mappedByteBuffer;
    }

    public ByteBuffer sliceByteBuffer() {
        return this.mappedByteBuffer.slice();
    }

    public long getStoreTimestamp() {
        return storeTimestamp;
    }

    public boolean isFirstCreateInQueue() {
        return firstCreateInQueue;
    }

    public void setFirstCreateInQueue(boolean firstCreateInQueue) {
        this.firstCreateInQueue = firstCreateInQueue;
    }

    /**
     * mlock系统调用
     */
    public void mlock() {
        final long beginTime = System.currentTimeMillis();
        final long address = ((DirectBuffer) (this.mappedByteBuffer)).address();
        Pointer pointer = new Pointer(address);
        {
            int ret = LibC.INSTANCE.mlock(pointer, new NativeLong(this.fileSize));
            log.info("mlock {} {} {} ret = {} time consuming = {}", address, this.fileName, this.fileSize, ret, System.currentTimeMillis() - beginTime);
        }
        {
            int ret = LibC.INSTANCE.madvise(pointer, new NativeLong(this.fileSize), LibC.MADV_WILLNEED);
            log.info("madvise {} {} {} ret = {} time consuming = {}", address, this.fileName, this.fileSize, ret, System.currentTimeMillis() - beginTime);
        }
    }

    /**
     * unlock系统调用
     */
    public void munlock() {
        final long beginTime = System.currentTimeMillis();
        final long address = ((DirectBuffer) (this.mappedByteBuffer)).address();
        Pointer pointer = new Pointer(address);
        int ret = LibC.INSTANCE.munlock(pointer, new NativeLong(this.fileSize));
        log.info("munlock {} {} {} ret = {} time consuming = {}", address, this.fileName, this.fileSize, ret, System.currentTimeMillis() - beginTime);
    }

    /**
     * testable
     * @return 获取文件
     */
    File getFile() {
        return this.file;
    }

    @Override
    public String toString() {
        return this.fileName;
    }
}
