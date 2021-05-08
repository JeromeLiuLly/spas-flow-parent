package com.candao.spas.flow.redis.holder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisSentinelPoolHolder extends PoolAbstractHolder {
    private JedisSentinelPool jedisSentinelPool;

    private JedisSentinelPoolHolder() {
    }

    public static PoolAbstractHolder create(JedisSentinelPool jedisSentinelPool) {
        JedisSentinelPoolHolder holder = new JedisSentinelPoolHolder();
        holder.jedisSentinelPool = jedisSentinelPool;
        return holder;
    }

    @Override
    public void returnResource(Jedis jedis) {
        jedisSentinelPool.returnResource(jedis);
    }

    @Override
    public void returnBrokenResource(Jedis jedis) {
        jedisSentinelPool.returnBrokenResource(jedis);
    }

    @Override
    public void destroy() {
        jedisSentinelPool.destroy();
    }

    @Override
    public Jedis getResource() {
        return jedisSentinelPool.getResource();
    }
}