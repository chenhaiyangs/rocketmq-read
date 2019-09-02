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

import org.apache.rocketmq.common.message.Message;

/**
 * This interface will be removed in the version 5.0.0, interface {@link TransactionListener} is recommended.
 * 本地事务执行器。需要用户来实现。当prepare发送成功，会回调本地事务执行起
 * @author ;
 */
@Deprecated
public interface LocalTransactionExecuter {
    /**
     * 执行本地事务
     * @param msg 消息
     * @param arg 参数
     * @return ;
     */
    LocalTransactionState executeLocalTransactionBranch(final Message msg, final Object arg);
}
