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

package org.apache.rocketmq.common.protocol.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.apache.rocketmq.common.MixAll;

/**
 * Broker的配置信息
 */
public class BrokerData implements Comparable<BrokerData> {
    /**
     * 集群名称
     */
    private String cluster;
    /**
     * brokerName
     */
    private String brokerName;
    /**
     * brokerId对应的addresss
     */
    private HashMap<Long, String> brokerAddrs;
    /**
     * broker备用连接地址
     */
    private HashMap<Long,String> backUpBrokerAddrs;

    private final Random random = new Random();

    public BrokerData() {

    }

    public BrokerData(String cluster, String brokerName,
                      HashMap<Long, String> brokerAddrs,
                      HashMap<Long,String> backUpBrokerAddrs) {
        this.cluster = cluster;
        this.brokerName = brokerName;
        this.brokerAddrs = brokerAddrs;
        this.backUpBrokerAddrs=backUpBrokerAddrs;
    }

    /**
     * Selects a (preferably master) broker address from the registered list.
     * If the master's address cannot be found, a slave broker address is selected in a random manner.
     *
     * @return Broker address.
     * 选择一个Brokder地址，优先选择Master的地址，没有就是从列表里随机选择一个
     */
    public String selectBrokerAddr() {
        String addr = this.brokerAddrs.get(MixAll.MASTER_ID);

        if (addr == null) {
            List<String> addrs = new ArrayList<String>(brokerAddrs.values());
            return addrs.get(random.nextInt(addrs.size()));
        }

        return addr;
    }

    public HashMap<Long, String> getBackUpBrokerAddrs() {
        return backUpBrokerAddrs;
    }

    public void setBackUpBrokerAddrs(HashMap<Long, String> backUpBrokerAddrs) {
        this.backUpBrokerAddrs = backUpBrokerAddrs;
    }

    public HashMap<Long, String> getBrokerAddrs() {
        return brokerAddrs;
    }

    public void setBrokerAddrs(HashMap<Long, String> brokerAddrs) {
        this.brokerAddrs = brokerAddrs;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((brokerAddrs == null) ? 0 : brokerAddrs.hashCode());
        result = prime * result + ((brokerName == null) ? 0 : brokerName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BrokerData other = (BrokerData) obj;
        if (brokerAddrs == null) {
            if (other.brokerAddrs != null)
                return false;
        } else if (!brokerAddrs.equals(other.brokerAddrs))
            return false;
        if (brokerName == null) {
            if (other.brokerName != null)
                return false;
        } else if (!brokerName.equals(other.brokerName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BrokerData [brokerName=" + brokerName + ", brokerAddrs=" + brokerAddrs + "]";
    }

    @Override
    public int compareTo(BrokerData o) {
        return this.brokerName.compareTo(o.getBrokerName());
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }
}
