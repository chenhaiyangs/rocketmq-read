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
package org.apache.rocketmq.client.impl;

/**
 * 查询Broker的Result
 * @author ;
 */
public class FindBrokerResult {
    /**
     * Broker地址
     */
    private final String brokerAddr;
    /**
     * 是否是slave节点
     */
    private final boolean slave;
    /**
     * brokerVersion
     */
    private final int brokerVersion;

    public FindBrokerResult(String brokerAddr, boolean slave) {
        this.brokerAddr = brokerAddr;
        this.slave = slave;
        this.brokerVersion = 0;
    }

    public FindBrokerResult(String brokerAddr, boolean slave, int brokerVersion) {
        this.brokerAddr = brokerAddr;
        this.slave = slave;
        this.brokerVersion = brokerVersion;
    }

    public String getBrokerAddr() {
        return brokerAddr;
    }

    public boolean isSlave() {
        return slave;
    }

    public int getBrokerVersion() {
        return brokerVersion;
    }
}
