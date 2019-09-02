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
package org.apache.rocketmq.store.util;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

/**
 * 系统调用库
 * JNA是建立在JNI技术基础之上的一个Java类库，它使您可以方便地使用java直接访问动态链接库中的函数。
 * 很简单，不需要写一行C代码，就可以直接在Java中调用外部动态链接库中的函数！
 * @author ;
 * 下面来解释下JNA的使用方法
 *
 *   （1）需要定义一个接口，继承自Library 或StdCallLibrary
 *  默认的是继承Library ，如果动态链接库里的函数是以stdcall方式输出的，那么就继承StdCallLibrary，比如众所周知的kernel32库。比如上例中的接口定义：
 *
 *   public interface CLibrary extends Library {
 *
 *   }
 *
 *
 *   （2）接口内部定义
 *    接口内部需要一个公共静态常量：INSTANCE，通过这个常量，就可以获得这个接口的实例，从而使用接口的方法，也就是调用外部dll/so的函数。
 *
 *    第 一个参数是动态链接库dll/so的名称，但不带.dll或.so这样的后缀，这符合JNI的规范，因为带了后缀名就不可以跨操作系统平台了。搜索动态链 接库路径的顺序是：先从当前类的当前文件夹找，如果没有找到，再在工程当前文件夹下面找win32/win64文件夹，找到后搜索对应的dll文件，如果 找不到再到WINDOWS下面去搜索，再找不到就会抛异常了。比如上例中printf函数在Windows平台下所在的dll库名称是msvcrt，而在 其它平台如Linux下的so库名称是c。
 *    第二个参数是本接口的Class类型。JNA通过这个Class类型，根据指定的.dll/.so文件，动态创建接口的实例。该实例由JNA通过反射自动生成。
 */
public interface LibC extends Library {
    LibC INSTANCE = (LibC) Native.loadLibrary(Platform.isWindows() ? "msvcrt" : "c", LibC.class);
    /**
     * madvise指令会用到，指定的内存预计在不久的将来访问
     */
    int MADV_WILLNEED = 3;
    /**
     * madvise指令会用到，MADV_DONTNEED标志告诉内核，刚刚分配出去的内存在近期内不会使用，内核可以该内存对应的物理页回收。
     */
    int MADV_DONTNEED = 4;

    /**
     * MCL_CURRENT mlockall使用，则仅仅当前已分配的内存会被锁定，之后分配的内存则不会；
     */
    int MCL_CURRENT = 1;
    /**
     * mlockall使用 MCL_FUTURE 则会锁定之后分配的所有内存。
     */
    int MCL_FUTURE = 2;
    /**
     *  mlockall使用, MCL_CURRENT|MCL_FUTURE 将已经及将来分配的所有内存锁定在物理内存中
     *  一起使用MCL_CURRENT,MCL_FUTURE
     */
    int MCL_ONFAULT = 4;

    /*
     *
     * msync函数使用。。
     * sync memory asynchronously
     * 调用会立即返回，不等到更新的完成；
     */
    int MS_ASYNC = 0x0001;
    /*
     * msync函数使用。。
     * invalidate mappings & caches
     * 取MS_INVALIDATE（通知使用该共享区域的进程，数据已经改变）时，在共享内容更改之后，使得文件的其他映射失效，
     * 从而使得共享该文件的其他进程去重新获取最新值；
     */
    int MS_INVALIDATE = 0x0002;
    /*
     * msync函数使用。。
     * synchronous memory sync
     * 调用会等到更新完成之后返回；
     */
    int MS_SYNC = 0x0004;

    /**
     * 系统调用 mlock 家族允许程序在物理内存上锁住它的部分或全部地址空间。
     * 这将阻止Linux 将这个内存页调度到交换空间（swap space），即使该程序已有一段时间没有访问这段空间。
     * @param var1 开始的指针
     * @param var2 锁住的长度
     * @return mLock的调用结果
     */
    int mlock(Pointer var1, NativeLong var2);

    /**
     * 系统调用 munlock 家族允许程序在物理内存上释放它的部分或全部地址空间。
     * @param var1 开始的指针
     * @param var2 长度
     * @return 解锁结果
     */
    int munlock(Pointer var1, NativeLong var2);

    /**
     *  函数建议内核，在从 addr 指定的地址开始，长
     *  度等于 len 参数值的范围内，
     *  该区域的用户虚拟内存应遵循特定的使用模式。
     *  内核使用这些信息优化与指定范围关联的资源的处理和维护过程。
     *  如果使用 madvise() 函数的程序明确了解其内存访问模式，则使用此函数可以提高系统性能。
     *  实现是一次性先将一段数据读入到映射内存区域，这样就减少了缺页异常的产生。
     * @param var1 开始指针
     * @param var2 长度
     * @param var3 使用建议
     * @return ;
     */
    int madvise(Pointer var1, NativeLong var2, int var3);

    /**
     * 函数解释：将s中当前位置后面的n个字节 （typedef unsigned int size_t ）用 ch 替换并返回 s 。
     * memset：作用是在一段内存块中填充某个给定的值，它是对较大的结构体或数组进行清零操作的一种最快方法 [1]  。
     * @param p 指针
     * @param v 要替换的内容
     * @param len 最后几个字节
     * @return 主要是为了给末尾填充指定值。
     */
    Pointer memset(Pointer p, int v, long len);

    /**
     * 如果你希望程序的全部地址空间被锁定在物理内存中，调用这个函数
     * @param flags flags
     * @return ;
     */
    int mlockall(int flags);

    /**
     * 可以通过调用msync()函数来实现磁盘文件内容与共享内存区中的内容一致,即同步操作.
     * @param p 开始指针
     * @param length  长度
     * @param flags flag值
     * @return ;
     */
    int msync(Pointer p, NativeLong length, int flags);
}
