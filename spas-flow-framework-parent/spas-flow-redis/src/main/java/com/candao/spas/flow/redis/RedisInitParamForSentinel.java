package com.candao.spas.flow.redis;

import com.candao.spas.flow.core.utils.StringUtil;

import java.util.HashSet;
import java.util.Set;

public class RedisInitParamForSentinel {
    /**
     * Sentinel下，一般最多配置三个Sentinel，故这里用 serverA,B,C来代替
     */

    /**
     * sentinel server A,such as : 192.168.0.1:26379
     */
    private String serverA;

    /**
     * sentinel server B
     */
    private String serverB;

    /**
     * sentinel server C
     */
    private String serverC;

    /**
     * 配置的Sentinel集群masterName
     */
    private String masterName;

    /**
     * 密码
     */
    private String password;

    /**
     * redis的槽，默认为第0个槽
     */
    private int database;

    /**
     * 最大空闲连接数
     */
    private int maxIdleCount = 8;

    /**
     * 最大连接数
     */
    private int maxTotalCount = 50;

    /**
     * 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间, 默认-1
     */
    private int maxWaitMillis = 5000;

    /**
     * 检测参数异常
     *
     * @return
     */
    public void checkErrorData() {
        //3个地址都是为空，那当然是有问题了
        if (StringUtil.isNullOrBlank(serverA) && StringUtil.isNullOrBlank(serverB) && StringUtil.isNullOrBlank(serverC)) {
            throw new IllegalArgumentException("must be have one server");
        }

        if (StringUtil.isNullOrBlank(masterName)) {
            throw new IllegalArgumentException("masterName can't be null");
        }

        if (StringUtil.isNullOrBlank(password)) {
            throw new IllegalArgumentException("password can't be null");
        }

    }

    public Set<String> getSentinelSet() {
        Set<String> sentinels = new HashSet<String>();
        if (!StringUtil.isNullOrBlank(serverA)) {
            sentinels.add(serverA);
        }
        if (!StringUtil.isNullOrBlank(serverB)) {
            sentinels.add(serverB);
        }
        if (!StringUtil.isNullOrBlank(serverC)) {
            sentinels.add(serverC);
        }
        return sentinels;
    }


    public String getServerA() {
        return serverA;
    }

    public void setServerA(String serverA) {
        this.serverA = serverA;
    }

    public String getServerB() {
        return serverB;
    }

    public void setServerB(String serverB) {
        this.serverB = serverB;
    }

    public String getServerC() {
        return serverC;
    }

    public void setServerC(String serverC) {
        this.serverC = serverC;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getMaxIdleCount() {
        return maxIdleCount;
    }

    public void setMaxIdleCount(int maxIdleCount) {
        this.maxIdleCount = maxIdleCount;
    }

    public int getMaxTotalCount() {
        return maxTotalCount;
    }

    public void setMaxTotalCount(int maxTotalCount) {
        this.maxTotalCount = maxTotalCount;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    @Override
    public String toString() {
        return "RedisInitParamForSentinel [serverA=" + serverA + ", serverB=" + serverB + ", serverC=" + serverC
                + ", masterName=" + masterName + ", password=" + "****you can't see******" + ", database=" + database + ", maxIdleCount="
                + maxIdleCount + ", maxTotalCount=" + maxTotalCount + ", maxWaitMillis=" + maxWaitMillis + "]";
    }

}