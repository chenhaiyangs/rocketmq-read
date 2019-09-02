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
package org.apache.rocketmq.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.rocketmq.common.annotation.ImportantField;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.help.FAQUrl;
import org.apache.rocketmq.logging.InternalLogger;
import org.apache.rocketmq.logging.InternalLoggerFactory;

/**
 * MixAll工具类。
 * 出镜率非常高的工具类
 * @author ;
 */
public class MixAll {
    private static final InternalLogger log = InternalLoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    //一大波常量的定义

    public static final String ROCKETMQ_HOME_ENV = "ROCKETMQ_HOME";
    public static final String ROCKETMQ_HOME_PROPERTY = "rocketmq.home.dir";
    public static final String NAMESRV_ADDR_ENV = "NAMESRV_ADDR";
    public static final String NAMESRV_ADDR_PROPERTY = "rocketmq.namesrv.addr";
    public static final String MESSAGE_COMPRESS_LEVEL = "rocketmq.message.compressLevel";
    public static final String DEFAULT_NAMESRV_ADDR_LOOKUP = "jmenv.tbsite.net";
    public static final String WS_DOMAIN_NAME = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
    public static final String WS_DOMAIN_SUBGROUP = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
    //http://jmenv.tbsite.net:8080/rocketmq/nsaddr
    //public static final String WS_ADDR = "http://" + WS_DOMAIN_NAME + ":8080/rocketmq/" + WS_DOMAIN_SUBGROUP;
    /**
     * Will be created at broker when isAutoCreateTopicEnable
     * 当可以自动创建topic时，会有这个系统topic
     */
    public static final String AUTO_CREATE_TOPIC_KEY_TOPIC = "TBW102";
    /**
     * 压测topic
     */
    public static final String BENCHMARK_TOPIC = "BenchmarkTest";
    public static final String DEFAULT_PRODUCER_GROUP = "DEFAULT_PRODUCER";
    public static final String DEFAULT_CONSUMER_GROUP = "DEFAULT_CONSUMER";
    /**
     * ToolsConsumer
     */
    public static final String TOOLS_CONSUMER_GROUP = "TOOLS_CONSUMER";
    /**
     * FilterServer消费组
     */
    public static final String FILTERSRV_CONSUMER_GROUP = "FILTERSRV_CONSUMER";
    public static final String MONITOR_CONSUMER_GROUP = "__MONITOR_CONSUMER";
    public static final String CLIENT_INNER_PRODUCER_GROUP = "CLIENT_INNER_PRODUCER";
    public static final String SELF_TEST_PRODUCER_GROUP = "SELF_TEST_P_GROUP";
    /**
     * 自测消费组
     */
    public static final String SELF_TEST_CONSUMER_GROUP = "SELF_TEST_C_GROUP";
    /**
     * 系统topic。自测test topic
     */
    public static final String SELF_TEST_TOPIC = "SELF_TEST_TOPIC";
    /**
     * 偏移量移动事件topic
     */
    public static final String OFFSET_MOVED_EVENT = "OFFSET_MOVED_EVENT";
    /**
     * ONS http代理消费组
     */
    public static final String ONS_HTTP_PROXY_GROUP = "CID_ONS-HTTP-PROXY";
    /**
     * CID ONSAPI权限消费组
     */
    public static final String CID_ONSAPI_PERMISSION_GROUP = "CID_ONSAPI_PERMISSION";
    /**
     * CID权限Onwer消费组
     */
    public static final String CID_ONSAPI_OWNER_GROUP = "CID_ONSAPI_OWNER";
    /**
     * CID ONSAPI 拉取消费组
     */
    public static final String CID_ONSAPI_PULL_GROUP = "CID_ONSAPI_PULL";
    /**
     * 系统消费组
     */
    public static final String CID_RMQ_SYS_PREFIX = "CID_RMQ_SYS_";

    public static final List<String> LOCAL_INET_ADDRESS = getLocalInetAddress();
    public static final String LOCALHOST = localhost();
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final long MASTER_ID = 0L;
    public static final long CURRENT_JVM_PID = getPID();
    /**
     * 重试的topic的前缀
     */
    public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";
    /**
     * 死信队列前缀
     */
    public static final String DLQ_GROUP_TOPIC_PREFIX = "%DLQ%";
    /**
     * 系统topic前缀
     */
    public static final String SYSTEM_TOPIC_PREFIX = "rmq_sys_";
    public static final String UNIQUE_MSG_QUERY_FLAG = "_UNIQUE_KEY_QUERY";
    public static final String DEFAULT_TRACE_REGION_ID = "DefaultRegion";
    public static final String CONSUME_CONTEXT_TYPE = "ConsumeContextType";

    /**
     * 我们知道，第一次提交到消息服务器，消息的主题被替换为RMQ_SYS_TRANS_HALF_TOPIC
     */
    public static final String RMQ_SYS_TRANS_HALF_TOPIC = "RMQ_SYS_TRANS_HALF_TOPIC";
    /**
     * 删除预处理消息(prepare)，
     * 其实是将消息存储在主题为：RMQ_SYS_TRANS_OP_HALF_TOPIC的主题中，代表这些消息已经被处理（提交或回滚）
     */
    public static final String RMQ_SYS_TRANS_OP_HALF_TOPIC = "RMQ_SYS_TRANS_OP_HALF_TOPIC";
    /**
     * Half Topic消费进度，默认消费者是CID_RMQ_SYS_TRANS，每次取prepare消息判断回查时，从该消费进度开始依次获取消息。
     */
    public static final String CID_SYS_RMQ_TRANS = "CID_RMQ_SYS_TRANS";

    /**
     * 获取NameServer的http端点
     * @return ;
     */
    public static String getWSAddr() {
        String wsDomainName = System.getProperty("rocketmq.namesrv.domain", DEFAULT_NAMESRV_ADDR_LOOKUP);
        String wsDomainSubgroup = System.getProperty("rocketmq.namesrv.domain.subgroup", "nsaddr");
        String wsAddr = "http://" + wsDomainName + ":8080/rocketmq/" + wsDomainSubgroup;
        if (wsDomainName.indexOf(":") > 0) {
            wsAddr = "http://" + wsDomainName + "/rocketmq/" + wsDomainSubgroup;
        }
        return wsAddr;
    }

    /**
     * 获取重试队列的队列名称
     * @param consumerGroup 消费组
     * @return ;
     */
    public static String getRetryTopic(final String consumerGroup) {
        return RETRY_GROUP_TOPIC_PREFIX + consumerGroup;
    }

    /**
     * 是否是系统消费组
     * @param consumerGroup consumergroup
     * @return ;
     */
    public static boolean isSysConsumerGroup(final String consumerGroup) {
        return consumerGroup.startsWith(CID_RMQ_SYS_PREFIX);
    }

    /**
     * 是否是系统topic
     * @param topic 系统topic
     * @return ;
     */
    public static boolean isSystemTopic(final String topic) {
        return topic.startsWith(SYSTEM_TOPIC_PREFIX);
    }

    public static String getDLQTopic(final String consumerGroup) {
        return DLQ_GROUP_TOPIC_PREFIX + consumerGroup;
    }

    /**
     * Broker的VIP通道，端口-2
     * @param isChange 是否改动
     * @param brokerAddr brokder地址
     * @return ;
     */
    public static String brokerVIPChannel(final boolean isChange, final String brokerAddr) {
        if (isChange) {
            String[] ipAndPort = brokerAddr.split(":");
            String brokerAddrNew = ipAndPort[0] + ":" + (Integer.parseInt(ipAndPort[1]) - 2);
            return brokerAddrNew;
        } else {
            return brokerAddr;
        }
    }

    /**
     * 获取进程PID
     * @return ;
     */
    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        if (processName != null && processName.length() > 0) {
            try {
                return Long.parseLong(processName.split("@")[0]);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * 将字符串存储到文件。并将之前的文件存储成bak
     * @param str str
     * @param fileName 文件名
     * @throws IOException ;
     */
    public static void string2File(final String str, final String fileName) throws IOException {

        //临时文件
        String tmpFile = fileName + ".tmp";
        string2FileNotSafe(str, tmpFile);
        //备份文件
        String bakFile = fileName + ".bak";
        String prevContent = file2String(fileName);
        if (prevContent != null) {
            string2FileNotSafe(prevContent, bakFile);
        }

        //删除filename
        File file = new File(fileName);
        file.delete();
        //讲临时文件重命名为fileName
        file = new File(tmpFile);
        file.renameTo(new File(fileName));
    }

    /**
     * 字符串变成文件，主要是创建parent
     * @param str str
     * @param fileName fileName
     * @throws IOException ;
     */
    public static void string2FileNotSafe(final String str, final String fileName) throws IOException {
        File file = new File(fileName);
        File fileParent = file.getParentFile();
        if (fileParent != null) {
            fileParent.mkdirs();
        }
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(str);
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    /**
     * 文件变成字符串
     * @param fileName 文件名
     * @return ;
     * @throws IOException ;
     */
    public static String file2String(final String fileName) throws IOException {
        File file = new File(fileName);
        return file2String(file);
    }
    /**
     * 文件变成字符串
     * @param file 文件
     * @return ;
     * @throws IOException ;
     */
    public static String file2String(final File file) throws IOException {
        if (file.exists()) {
            byte[] data = new byte[(int) file.length()];
            boolean result;

            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                int len = inputStream.read(data);
                result = len == data.length;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            if (result) {
                return new String(data);
            }
        }
        return null;
    }

    /**
     * 文件变成字符串
     * @param url url
     * @return ;
     */
    public static String file2String(final URL url) {
        InputStream in = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setUseCaches(false);
            in = urlConnection.getInputStream();
            int len = in.available();
            byte[] data = new byte[len];
            in.read(data, 0, len);
            return new String(data, "UTF-8");
        } catch (Exception ignored) {
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }

    /**
     * 打印对象的属性
     * @param logger logger
     * @param object object
     */
    public static void printObjectProperties(final InternalLogger logger, final Object object) {
        printObjectProperties(logger, object, false);
    }

    /**
     * 打印对象的属性
     * @param logger logger
     * @param object object
     * @param onlyImportantField 是否只打印onlyImportantField的文件
     */
    public static void printObjectProperties(final InternalLogger logger, final Object object,
        final boolean onlyImportantField) {

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                if (!name.startsWith("this")) {
                    Object value = null;
                    try {
                        field.setAccessible(true);
                        value = field.get(object);
                        if (null == value) {
                            value = "";
                        }
                    } catch (IllegalAccessException e) {
                        log.error("Failed to obtain object properties", e);
                    }

                    if (onlyImportantField) {
                        Annotation annotation = field.getAnnotation(ImportantField.class);
                        if (null == annotation) {
                            continue;
                        }
                    }

                    if (logger != null) {
                        logger.info(name + "=" + value);
                    }
                }
            }
        }
    }

    /**
     *properties变成字符串
     * @param properties ;
     * @return ;
     */
    public static String properties2String(final Properties properties) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey().toString() + "=" + entry.getValue().toString() + "\n");
            }
        }
        return sb.toString();
    }

    /**
     * 字符串变成properties
     * @param str str
     * @return ;
     */
    public static Properties string2Properties(final String str) {
        Properties properties = new Properties();
        try {
            InputStream in = new ByteArrayInputStream(str.getBytes(DEFAULT_CHARSET));
            properties.load(in);
        } catch (Exception e) {
            log.error("Failed to handle properties", e);
            return null;
        }

        return properties;
    }

    /**
     * 对象变成properties
     * @param object ;
     * @return ;
     */
    public static Properties object2Properties(final Object object) {
        Properties properties = new Properties();

        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                String name = field.getName();
                if (!name.startsWith("this")) {
                    Object value = null;
                    try {
                        field.setAccessible(true);
                        value = field.get(object);
                    } catch (IllegalAccessException e) {
                        log.error("Failed to handle properties", e);
                    }

                    if (value != null) {
                        properties.setProperty(name, value.toString());
                    }
                }
            }
        }

        return properties;
    }

    /**
     * 反射的方式，properties变成对象
     * @param p p
     * @param object obj
     */
    public static void properties2Object(final Properties p, final Object object) {
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String mn = method.getName();
            if (mn.startsWith("set")) {
                try {
                    String tmp = mn.substring(4);
                    String first = mn.substring(3, 4);

                    String key = first.toLowerCase() + tmp;
                    String property = p.getProperty(key);
                    if (property != null) {
                        Class<?>[] pt = method.getParameterTypes();
                        if (pt != null && pt.length > 0) {
                            String cn = pt[0].getSimpleName();
                            Object arg = null;
                            if (cn.equals("int") || cn.equals("Integer")) {
                                arg = Integer.parseInt(property);
                            } else if (cn.equals("long") || cn.equals("Long")) {
                                arg = Long.parseLong(property);
                            } else if (cn.equals("double") || cn.equals("Double")) {
                                arg = Double.parseDouble(property);
                            } else if (cn.equals("boolean") || cn.equals("Boolean")) {
                                arg = Boolean.parseBoolean(property);
                            } else if (cn.equals("float") || cn.equals("Float")) {
                                arg = Float.parseFloat(property);
                            } else if (cn.equals("String")) {
                                arg = property;
                            } else {
                                continue;
                            }
                            method.invoke(object, arg);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * properties 是否相等
     * @param p1 p1
     * @param p2 p2
     * @return
     */
    public static boolean isPropertiesEqual(final Properties p1, final Properties p2) {
        return p1.equals(p2);
    }

    /**
     * 获取本地网络地址getLocalInetAddress列表
     * @return ;
     */
    public static List<String> getLocalInetAddress() {
        List<String> inetAddressList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    inetAddressList.add(addrs.nextElement().getHostAddress());
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("get local inet address fail", e);
        }

        return inetAddressList;
    }

    /**
     * 获取localhost
     * @return ;
     */
    private static String localhost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Throwable e) {
            try {
                String candidatesHost = getLocalhostByNetworkInterface();
                if (candidatesHost != null) {
                    return candidatesHost;
                }
            } catch (Exception ignored) {
            }

            throw new RuntimeException("InetAddress java.net.InetAddress.getLocalHost() throws UnknownHostException" + FAQUrl.suggestTodo(FAQUrl.UNKNOWN_HOST_EXCEPTION), e);
        }
    }


    /**
     * 通过网卡获取localhost
     * Reverse logic comparing to RemotingUtil method, consider refactor in RocketMQ 5.0
     * @return ;
     * @throws SocketException ;
     */
    public static String getLocalhostByNetworkInterface() throws SocketException {
        List<String> candidatesHost = new ArrayList<String>();
        Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();

        while (enumeration.hasMoreElements()) {
            NetworkInterface networkInterface = enumeration.nextElement();
            // Workaround for docker0 bridge
            if ("docker0".equals(networkInterface.getName()) || !networkInterface.isUp()) {
                continue;
            }
            Enumeration<InetAddress> addrs = networkInterface.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress address = addrs.nextElement();
                if (address.isLoopbackAddress()) {
                    continue;
                }
                //ipv4的地址更高的优先级
                if (address instanceof Inet6Address) {
                    candidatesHost.add(address.getHostAddress());
                    continue;
                }
                return address.getHostAddress();
            }
        }

        if (!candidatesHost.isEmpty()) {
            return candidatesHost.get(0);
        }
        return null;
    }

    /**
     * 比较并且简单的增长target的value
     * @param target ;
     * @param value ;
     * @return ;
     */
    public static boolean compareAndIncreaseOnly(final AtomicLong target, final long value) {
        long prev = target.get();
        while (value > prev) {
            boolean updated = target.compareAndSet(prev, value);
            if (updated) {
                return true;
            }
            prev = target.get();
        }

        return false;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
