package com.candao.spas.flow.redis;

public class RedisInitParam {

    private String server;

    private int port;

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
     * 一个简单测试使用的初始化参数(ip和端口)
     *
     * @param server ip
     * @param port   端口
     * @return
     */
    public static RedisInitParam createSimple(String server, int port) {
        RedisInitParam param = new RedisInitParam();
        param.server = server;
        param.port = port;
        return param;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
        return "RedisInitParam [server=" + server + ", port=" + port + ", password=" + "****you can't see******" + ", database="
                + database + ", maxIdleCount=" + maxIdleCount + ", maxTotalCount=" + maxTotalCount + ", maxWaitMillis="
                + maxWaitMillis + "]";
    }

}