package com.candao.spas.flow.redis.data;

import redis.clients.jedis.Jedis;

public interface RedisCall<T> {
	
	T execute(Jedis jedis) ;
}