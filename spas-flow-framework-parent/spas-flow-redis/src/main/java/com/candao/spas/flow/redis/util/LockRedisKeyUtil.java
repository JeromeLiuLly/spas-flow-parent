package com.candao.spas.flow.redis.util;

import com.candao.spas.flow.core.exception.RedisException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class LockRedisKeyUtil {
    private LockRedisKeyUtil() {
    }

    /**
     * 是否执行封锁key操作
     */
    private static final AtomicBoolean IsLock = new AtomicBoolean(false);
    /**
     * 封锁key对应的是否前缀匹配，如: aaaa->全匹配, bbbb*->对执行的key前面四位为bbbb便数需要封锁的key 存储的数据为:
     * map.put("aaaa",false), map.put("bbbb",true); 用key的对应的value来判断是否前缀匹配
     **/
    private static final ConcurrentHashMap<String, Boolean> LockKeyToPrefixMap = new ConcurrentHashMap<String, Boolean>();
    private static final String ANY = "*";

    /**
     * 设置是否执行封锁key操作
     *
     * @param isLock
     */
    public static void setLock(boolean isLock) {
        IsLock.set(isLock);
    }

    /**
     * 设置封锁key，封锁的key列表，若有多个用逗号隔开，支持*号前匹配，如: aaaa*,bbb,ccc
     *
     * @param lockKey
     */
    public static void setLockKey(String lockKey) {
        boolean oldIsLock = IsLock.get();
        // 设置之前，先设置为不封锁key，防止更新封锁Map有多线程问题，执行完毕设置为原来的值
        try {
            setLock(false);
            LockKeyToPrefixMap.clear();
            if (StringUtils.isBlank(lockKey)) {
                return;
            }
            String[] keyArr = lockKey.split(",");
            if (keyArr == null || keyArr.length <= 0) {
                return;
            }
            for (String key : keyArr) {
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                // 全封锁
                if (ANY.equals(key)) {
                    LockKeyToPrefixMap.put(key, true);
                    continue;
                }
                boolean prefix = key.substring(key.length() - 1).equals(ANY);
                LockKeyToPrefixMap.put(prefix ? key.substring(0, key.length() - 1) : key, prefix);
            }
        } finally {
            setLock(oldIsLock);
        }
        info();
    }

    private static void info() {
        log.info("lock key info................");
        log.info("IsLock:" + IsLock.get());
        LockKeyToPrefixMap.forEach((key, prefix) -> {
            log.info("key:" + key + "\t" + "prefix:" + prefix);
        });
        log.info("lock key info................");
    }

    /**
     * 检测指定的key是否被封锁，有则抛出异常，无，直接返回
     *
     * @param checkKey 检测的key
     */
    public static void checkLock(String checkKey) {
        // 封锁开关设置为false，直接跳过
        if (!IsLock.get()) {
            return;
        }
        if (StringUtils.isBlank(checkKey) || LockKeyToPrefixMap.isEmpty()) {
            return;
        }
        LockKeyToPrefixMap.forEach((key, prefix) -> {
            // 1 所有封锁 （这种情况应该是不存在的）
            if (ANY.equals(key)) {
                throw new RedisException("[" + checkKey + "]已经被封锁");
            }
            // 2 全匹配封锁
            if (!prefix && checkKey.equals(key)) {
                throw new RedisException("[" + checkKey + "]已经被封锁");
            }
            // 3 前缀匹配封锁
            if (prefix && checkKey.startsWith(key)) {
                throw new RedisException("[" + checkKey + "]已经被封锁");
            }
        });
    }
}