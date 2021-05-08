package com.candao.spas.flow.redis.util;

import java.util.concurrent.ConcurrentHashMap;

public class LockObjectUtil {
    private static final ConcurrentHashMap<String, Object> objectMap = new ConcurrentHashMap<String, Object>();

    /**
     * 获取一个指定的对象锁
     *
     * @param key
     * @return
     */
    public static Object getLockObject(String key) {
        objectMap.putIfAbsent(key, new Object());
        return objectMap.get(key);
    }

    /**
     * 释放对象锁
     *
     * @param key
     */
    public static void disposeLock(String key) {
        objectMap.remove(key);
    }
}