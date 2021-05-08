package com.candao.spas.flow.redis.holder;

import redis.clients.jedis.Jedis;

public abstract class PoolAbstractHolder {
    /**
     * 正常释放jedis
     *
     * @param jedis
     */
    public abstract void returnResource(Jedis jedis);


    /**
     * 异常释放jedis
     *
     * @param jedis
     */
    public abstract void returnBrokenResource(Jedis jedis);

    /**
     * 销毁整个redis线程连接
     */
    public abstract void destroy();

    /**
     * 获取Redis实例
     *
     * @return
     */
    public abstract Jedis getResource();
}