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

package org.apache.rocketmq.common.protocol.body;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.remoting.protocol.RemotingSerializable;

/**
 * 集群信息
 * @author ;
 */
public class ClusterInfo extends RemotingSerializable {
    /**
     * brokerName
     */
    private HashMap<String, BrokerData> brokerAddrTable;
    /**
     * clusterName,对应的所有的broker
     */
    private HashMap<String, Set<String>> clusterAddrTable;

    public HashMap<String, BrokerData> getBrokerAddrTable() {
        return brokerAddrTable;
    }
    public void setBrokerAddrTable(HashMap<String, BrokerData> brokerAddrTable) {
        this.brokerAddrTable = brokerAddrTable;
    }

    public HashMap<String, Set<String>> getClusterAddrTable() {
        return clusterAddrTable;
    }
    public void setClusterAddrTable(HashMap<String, Set<String>> clusterAddrTable) {
        this.clusterAddrTable = clusterAddrTable;
    }

    /**
     * 获取一个集群下的所有的broker的地址
     * @param cluster cluster
     * @return ;
     */
    public String[] retrieveAllAddrByCluster(String cluster) {
        List<String> addrs = new ArrayList<String>();
        if (clusterAddrTable.containsKey(cluster)) {
            Set<String> brokerNames = clusterAddrTable.get(cluster);
            for (String brokerName : brokerNames) {
                BrokerData brokerData = brokerAddrTable.get(brokerName);
                if (null != brokerData) {
                    addrs.addAll(brokerData.getBrokerAddrs().values());
                }
            }
        }

        return addrs.toArray(new String[] {});
    }

    /**
     * 获取所有的集群名称
     * @return ;
     */
    public String[] retrieveAllClusterNames() {
        return clusterAddrTable.keySet().toArray(new String[] {});
    }
}
