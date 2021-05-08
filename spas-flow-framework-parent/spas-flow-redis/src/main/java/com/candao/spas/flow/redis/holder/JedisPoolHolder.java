package com.candao.spas.flow.redis.holder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolHolder extends PoolAbstractHolder {

    private JedisPool jedisPool;

    private JedisPoolHolder() {
    }

    public static PoolAbstractHolder create(JedisPool jedisPool) {
        JedisPoolHolder holder = new JedisPoolHolder();
        holder.jedisPool = jedisPool;
        return holder;
    }

    @Override
    public void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    @Override
    public void returnBrokenResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    @Override
    public void destroy() {
        jedisPool.destroy();
    }

    @Override
    public Jedis getResource() {
        return jedisPool.getResource();
    }
}