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

package org.apache.rocketmq.common.protocol;

/**
 * 请求码
 * @author ;
 */
public class RequestCode {
    /**
     * 发送消息
     */
    public static final int SEND_MESSAGE = 10;

    public static final int PULL_MESSAGE = 11;
    /**
     * 查询消息类请求
     */
    public static final int QUERY_MESSAGE = 12;
    public static final int QUERY_BROKER_OFFSET = 13;
    /**
     * 查询消费偏移量
     */
    public static final int QUERY_CONSUMER_OFFSET = 14;
    /**
     * 更新消费偏移量
     */
    public static final int UPDATE_CONSUMER_OFFSET = 15;
    /**
     * 更新或者是创建topic
     */
    public static final int UPDATE_AND_CREATE_TOPIC = 17;
    /**
     * 去NameServer获取所有的topicConfig
     */
    public static final int GET_ALL_TOPIC_CONFIG = 21;
    public static final int GET_TOPIC_CONFIG_LIST = 22;

    public static final int GET_TOPIC_NAME_LIST = 23;
    /**
     * 更新Broker的配置
     */
    public static final int UPDATE_BROKER_CONFIG = 25;
    /**
     * 获取Broker的配置
     */
    public static final int GET_BROKER_CONFIG = 26;

    public static final int TRIGGER_DELETE_FILES = 27;
    /**
     * 获取Broker的运行时信息
     */
    public static final int GET_BROKER_RUNTIME_INFO = 28;
    /**
     * 根据时间戳查询偏移量
     */
    public static final int SEARCH_OFFSET_BY_TIMESTAMP = 29;
    /**
     * 获取最大的偏移量
     */
    public static final int GET_MAX_OFFSET = 30;
    /**
     * 获取队列里最小的偏移量
     */
    public static final int GET_MIN_OFFSET = 31;
    /**
     * 获取最早的消息的存储的时间
     */
    public static final int GET_EARLIEST_MSG_STORETIME = 32;
    /**
     * 根据id查询Message
     */
    public static final int VIEW_MESSAGE_BY_ID = 33;
    /**
     * 处理客户端的心跳
     */
    public static final int HEART_BEAT = 34;

    public static final int UNREGISTER_CLIENT = 35;
    /**
     *  consumer消费失败的消息发回broker。
     */
    public static final int CONSUMER_SEND_MSG_BACK = 36;

    public static final int END_TRANSACTION = 37;
    /**
     * 获取消费列表bygroup
     */
    public static final int GET_CONSUMER_LIST_BY_GROUP = 38;

    public static final int CHECK_TRANSACTION_STATE = 39;
    /**
     * 通知消费者消息id列表变化
     */
    public static final int NOTIFY_CONSUMER_IDS_CHANGED = 40;
    /**
     * 批量Lock队列
     */
    public static final int LOCK_BATCH_MQ = 41;
    /**
     * 批量UnLock队列
     */
    public static final int UNLOCK_BATCH_MQ = 42;
    /**
     * 获取所有消费者的offset
     */
    public static final int GET_ALL_CONSUMER_OFFSET = 43;
    /**
     * 获取所有延迟的偏移量
     */
    public static final int GET_ALL_DELAY_OFFSET = 45;

    public static final int CHECK_CLIENT_CONFIG = 46;

    public static final int PUT_KV_CONFIG = 100;

    public static final int GET_KV_CONFIG = 101;

    public static final int DELETE_KV_CONFIG = 102;
    /**
     * 注册Broker命令
     */
    public static final int REGISTER_BROKER = 103;
    /**
     * 取消注册Broker
     */
    public static final int UNREGISTER_BROKER = 104;
    /**
     * 根据topic获取一个topic的路由
     */
    public static final int GET_ROUTEINTO_BY_TOPIC = 105;

    public static final int GET_BROKER_CLUSTER_INFO = 106;
    /**
     * 更新获取创建cousumer信息
     */
    public static final int UPDATE_AND_CREATE_SUBSCRIPTIONGROUP = 200;
    /**
     * 获取全部的订阅信息配置
     */
    public static final int GET_ALL_SUBSCRIPTIONGROUP_CONFIG = 201;
    /**
     * 获取topic的状态信息
     */
    public static final int GET_TOPIC_STATS_INFO = 202;
    /**
     * 获取消费者连接列表
     */
    public static final int GET_CONSUMER_CONNECTION_LIST = 203;
    /**
     * 获取生产者的连接列表
     */
    public static final int GET_PRODUCER_CONNECTION_LIST = 204;
    public static final int WIPE_WRITE_PERM_OF_BROKER = 205;

    public static final int GET_ALL_TOPIC_LIST_FROM_NAMESERVER = 206;
    /**
     * 删除订阅信息
     */
    public static final int DELETE_SUBSCRIPTIONGROUP = 207;
    /**
     * 获取消费状态
     */
    public static final int GET_CONSUME_STATS = 208;

    public static final int SUSPEND_CONSUMER = 209;

    public static final int RESUME_CONSUMER = 210;
    public static final int RESET_CONSUMER_OFFSET_IN_CONSUMER = 211;
    public static final int RESET_CONSUMER_OFFSET_IN_BROKER = 212;

    public static final int ADJUST_CONSUMER_THREAD_POOL = 213;

    public static final int WHO_CONSUME_THE_MESSAGE = 214;
    /**
     * 删除某个Broker中的topic
     */
    public static final int DELETE_TOPIC_IN_BROKER = 215;

    public static final int DELETE_TOPIC_IN_NAMESRV = 216;
    public static final int GET_KVLIST_BY_NAMESPACE = 219;
    /**
     * 重置消费客户端偏移量
     */
    public static final int RESET_CONSUMER_CLIENT_OFFSET = 220;
    /**
     * 从客户端获取消费者的状态
     */
    public static final int GET_CONSUMER_STATUS_FROM_CLIENT = 221;
    /**
     * broker重置偏移量
     */
    public static final int INVOKE_BROKER_TO_RESET_OFFSET = 222;
    /**
     * 从Broker获取消费者状态
     */
    public static final int INVOKE_BROKER_TO_GET_CONSUMER_STATUS = 223;
    /**
     * 查询topic的消费者by who
     */
    public static final int QUERY_TOPIC_CONSUME_BY_WHO = 300;

    public static final int GET_TOPICS_BY_CLUSTER = 224;
    /**
     * 注册FilterServer
     */
    public static final int REGISTER_FILTER_SERVER = 301;
    public static final int REGISTER_MESSAGE_FILTER_CLASS = 302;
    /**
     * 查询消费timespan
     */
    public static final int QUERY_CONSUME_TIME_SPAN = 303;

    public static final int GET_SYSTEM_TOPIC_LIST_FROM_NS = 304;
    /**
     * 从Broker获取系统topic列表
     */
    public static final int GET_SYSTEM_TOPIC_LIST_FROM_BROKER = 305;
    /**
     * 清除超时的消费队列
     */
    public static final int CLEAN_EXPIRED_CONSUMEQUEUE = 306;
    /**
     * 获取消费者运行时信息
     */
    public static final int GET_CONSUMER_RUNNING_INFO = 307;
    /**
     * 查询当前的偏移量
     */
    public static final int QUERY_CORRECTION_OFFSET = 308;
    /**
     * 直接消费信息
     */
    public static final int CONSUME_MESSAGE_DIRECTLY = 309;
    /**
     * 发送消息v2
     */
    public static final int SEND_MESSAGE_V2 = 310;

    public static final int GET_UNIT_TOPIC_LIST = 311;

    public static final int GET_HAS_UNIT_SUB_TOPIC_LIST = 312;

    public static final int GET_HAS_UNIT_SUB_UNUNIT_TOPIC_LIST = 313;

    public static final int CLONE_GROUP_OFFSET = 314;
    /**
     * 查询Broker的状态数据
     */
    public static final int VIEW_BROKER_STATS_DATA = 315;

    public static final int CLEAN_UNUSED_TOPIC = 316;
    /**
     * 获取consumer的消费者的状态
     */
    public static final int GET_BROKER_CONSUME_STATS = 317;

    /**
     * update the config of name server
     */
    public static final int UPDATE_NAMESRV_CONFIG = 318;

    /**
     * get config from name server
     */
    public static final int GET_NAMESRV_CONFIG = 319;
    /**
     * 发送批量消息
     */
    public static final int SEND_BATCH_MESSAGE = 320;
    /**
     * 查询消费队列
     */
    public static final int QUERY_CONSUME_QUEUE = 321;

    public static final int QUERY_DATA_VERSION = 322;
}
