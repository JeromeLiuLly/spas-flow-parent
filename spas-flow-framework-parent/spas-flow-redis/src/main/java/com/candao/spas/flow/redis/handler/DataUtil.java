package com.candao.spas.flow.redis.handler;

import com.candao.spas.flow.core.exception.RedisException;
import com.candao.spas.flow.redis.RedisClient;
import com.candao.spas.flow.redis.RedisDataSource;
import com.candao.spas.flow.redis.RedisDataSourceFactory;
import com.candao.spas.flow.redis.data.ListDataGeter;
import com.candao.spas.flow.redis.data.MapDataGeter;
import com.candao.spas.flow.redis.data.SetDataGeter;
import com.candao.spas.flow.redis.data.StringDataGeter;
import com.candao.spas.flow.redis.util.LockObjectUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DataUtil {

    /**
     * 一天的过期秒数
     */
    private static final int ONE_DAY_EXPIRE_SECONDS = 1 * 24 * 60 * 60;


    /**
     * 获取redis中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> T getDataFromRedisOrDataGeter(String cacheKey, Class<T> clazz, StringDataGeter<T> dataGeter) {
        return getDataFromRedisOrDataGeter(cacheKey, clazz, dataGeter, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
    }


    /**
     * 在DataUtil中每访问一次，有某个概率提升数据有效期
     *
     * @param cacheKey
     * @param expireSeconds
     */
    private static void haveChanceToSetKeyExpireTime(String cacheKey, int expireSeconds) {
        //百分之一的机会
        if (ThreadLocalRandom.current().nextInt(100) == 1) {
            RedisClient.setKeyExpireTime(cacheKey, expireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : expireSeconds);
        }
    }

    /**
     * 删除指定key并返回value
     *
     * @return
     */
    public static <T> T delKeyAndReturnVal(String key, Class<T> tClass) {
        T value = RedisDataSourceFactory.getInstance().getDefaultSource().getValue(key, tClass);
        RedisDataSourceFactory.getInstance().getDefaultSource().delKey(key);
        return value;
    }


    /**
     * @param cacheKey         校验/获取 缓存的key
     * @param clazz            返回缓存类型
     * @param dayExpireSeconds 设置时间
     * @param dataGeter        往redis中存入多组key-value
     * @return T
     * @desc
     * @author Carter
     * @date 2020/12/8
     */
    public static <T> T existForSet(String cacheKey, Class<T> clazz, int dayExpireSeconds, MapDataGeter<T> dataGeter) {
        boolean result = RedisClient.existsKey(cacheKey);
        if (result) {
            return RedisClient.getValue(cacheKey, clazz);
        }
        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {
                Map<String, T> data;
                try {
                    data = dataGeter.getMap();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (MapUtils.isNotEmpty(data)) {
                    data.forEach((k, v) -> RedisClient.setnx(k, v, dayExpireSeconds));
                } else {
                    return null;
                }
                return data.get(cacheKey);
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }
    }

    /**
     * 获取redis中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @param expireSeconds-缓存失效时间
     * @return
     */
    public static <T> T getDataFromRedisOrDataGeter(String cacheKey, Class<T> clazz, StringDataGeter<T> dataGeter, int expireSeconds) {

        T val = RedisClient.getValue(cacheKey, clazz);
        if (val != null) {
            //让热数据一直热下去 <单个key/value>
            haveChanceToSetKeyExpireTime(cacheKey, expireSeconds);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(cacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {// 为提升性能 这里不使用分布式锁
                // 标记key，防止集合为空时还不停从数据库中读取数据
                T data = null;
                try {
                    data = dataGeter.getString();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (data != null) {
                    // 这里最后才设置flag，以免分布式环境下dataGeter.getData();获取慢，导致后续请求从缓存中取不到数据
                    //有值不设置flag
                    RedisClient.setValue(cacheKey, data, expireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : expireSeconds, false);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间为一天
                    RedisClient.setValue(RedisClient.getFlagKey(cacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                }
                return data;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }

    }


    /**
     * 获取redis中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param type-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> T getDataFromRedisOrDataGeter(String cacheKey, java.lang.reflect.Type type, StringDataGeter<T> dataGeter) {
        return getDataFromRedisOrDataGeter(cacheKey, type, dataGeter, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
    }


    /**
     * 获取redis中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param type-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @param expireSeconds-缓存失效时间
     * @return
     */
    public static <T> T getDataFromRedisOrDataGeter(String cacheKey, java.lang.reflect.Type type, StringDataGeter<T> dataGeter, int expireSeconds) {

        T val = RedisClient.getValue(cacheKey, type);
        if (val != null) {
            //让热数据一直热下去 <单个key/value>
            haveChanceToSetKeyExpireTime(cacheKey, expireSeconds);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(cacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {// 为提升性能 这里不使用分布式锁
                // 标记key，防止集合为空时还不停从数据库中读取数据
                T data = null;
                try {
                    data = dataGeter.getString();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (data != null) {
                    // 这里最后才设置flag，以免分布式环境下dataGeter.getData();获取慢，导致后续请求从缓存中取不到数据
                    //有值不设置flag
                    RedisClient.setValue(cacheKey, data, expireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : expireSeconds, false);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间为一天
                    RedisClient.setValue(RedisClient.getFlagKey(cacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                }
                return data;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }

    }


    /**
     * 获取redis中获取指定list，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @param flagKeyExpireSeconds  flagkey的失效时间
     * @return
     */
    public static <T> List<T> getListFromRedisOrDataGeter(String cacheKey, Class<T> clazz, ListDataGeter<T> dataGeter, int flagKeyExpireSeconds) {
        List<T> val = RedisClient.getAllList(cacheKey, clazz);
        if (val != null && !val.isEmpty()) {
            //让热数据一直热下去
            haveChanceToSetKeyExpireTime(cacheKey, flagKeyExpireSeconds);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(cacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {
                List<T> list = null;
                try {
                    list = dataGeter.getList();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (CollectionUtils.isEmpty(list)) {
                    // 不存在数据则往缓存中插入一个flag，过期时间为一天
                    RedisClient.setValue(RedisClient.getFlagKey(cacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                } else {
                    RedisClient.delKey(cacheKey);
                    RedisClient.addList(cacheKey, list);
                    RedisClient.setKeyExpireTime(cacheKey, flagKeyExpireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : flagKeyExpireSeconds);
                }
                return list;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }
    }


    /**
     * 获取redis中获取指定set，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> Set<T> getSetFromRedisOrDataGeter(String cacheKey, Class<T> clazz, SetDataGeter<T> dataGeter) {
        return getSetFromRedisOrDataGeter(cacheKey, clazz, dataGeter, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 获取redis中获取指定set，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @param flagKeyExpireSeconds  flagkey的失效时间
     * @return
     */
    private static <T> Set<T> getSetFromRedisOrDataGeter(String cacheKey, Class<T> clazz, SetDataGeter<T> dataGeter, int flagKeyExpireSeconds) {
        Set<T> val = RedisClient.sSet(cacheKey, clazz);
        if (!CollectionUtils.isEmpty(val)) {
            //让热数据一直热下去
            haveChanceToSetKeyExpireTime(cacheKey, flagKeyExpireSeconds);
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {
                // 标记key，防止集合为空时还不停从数据库中读取数据
                String flagCacheKey = RedisClient.getFlagKey(cacheKey);
                if (RedisClient.existsKey(flagCacheKey)) {
                    return RedisClient.sSet(cacheKey, clazz);
                }
                Set<T> set = null;
                try {
                    set = dataGeter.getSet();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (set != null && set.size() > 0) {
                    RedisClient.delKey(cacheKey);
                    RedisClient.sAdd(cacheKey, set.toArray());
                    RedisClient.setKeyExpireTime(cacheKey, flagKeyExpireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : flagKeyExpireSeconds);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间60s
                    RedisClient.setValue(flagCacheKey, true, ONE_DAY_EXPIRE_SECONDS);
                }
                return set;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }
    }

    /**
     * 获取redis中获取指定list，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     * 注：该方法会自动创建一个cacheKey + "_flag"的缓存key，作为标记
     *
     * @param cacheKey-缓存key
     * @param clazz-获取目标类型
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> List<T> getListFromRedisOrDataGeter(String cacheKey, Class<T> clazz, ListDataGeter<T> dataGeter) {
        return getListFromRedisOrDataGeter(cacheKey, clazz, dataGeter,/*0*/RedisDataSource.DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 获取redis map中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     *
     * @param hashCacheKey-hashmap的key
     * @param cacheKey-具体数据key
     * @param clazz-目标类型（T对象类型）
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> T getDataFromRedisMapOrDataGeter(String hashCacheKey, String cacheKey, Class<T> clazz, StringDataGeter<T> dataGeter) {
        T val = RedisClient.getValueFromHashMap(hashCacheKey, cacheKey, clazz);
        if (val != null) {
            //让热数据一直热下去
            haveChanceToSetKeyExpireTime(hashCacheKey, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(hashCacheKey + cacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {
                T data = null;
                try {
                    data = dataGeter.getString();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (data != null) {
                    RedisClient.addToHashMap(hashCacheKey, cacheKey, data);
                    RedisClient.setKeyExpireTime(hashCacheKey, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间60s
                    RedisClient.setValue(RedisClient.getFlagKey(hashCacheKey + cacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                }
                return data;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }
    }


    /**
     * 获取redis map中获取指定data，如果redis中不存在，则从DataGeter接口中获取，并且写入redis<br/>
     *
     * @param hashCacheKey-hashmap的key
     * @param cacheKey-具体数据key
     * @param dataGeter-数据获取器，原始数据源
     * @param type-目标类型（T对象类型）
     * @return
     */
    public static <T> T getDataFromRedisMapOrDataGeter(String hashCacheKey, String cacheKey, StringDataGeter<T> dataGeter, java.lang.reflect.Type type) {
        T val = RedisClient.getValueFromHashMap(hashCacheKey, cacheKey, type);
        if (val != null) {
            //让热数据一直热下去
            haveChanceToSetKeyExpireTime(hashCacheKey, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(hashCacheKey + cacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(cacheKey);
            synchronized (lockObject) {
                T data = null;
                try {
                    data = dataGeter.getString();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (data != null) {
                    RedisClient.addToHashMap(hashCacheKey, cacheKey, data);
                    RedisClient.setKeyExpireTime(hashCacheKey, RedisDataSource.DEFAULT_EXPIRE_SECONDS);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间60s
                    RedisClient.setValue(RedisClient.getFlagKey(hashCacheKey + cacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                }
                return data;
            }
        } finally {
            LockObjectUtil.disposeLock(cacheKey);
        }
    }

    /**
     * 获取redis中指定的map，如果redis中不存在，则从StringMapDataGeter接口中获取，并且写入redis<br/>
     *
     * @param hashCacheKey-hashmap的key
     * @param clazz-Map数据value值类型（T对象类型）
     * @param dataGeter-数据获取器，原始数据源
     * @param flagKeyExpireSeconds-失效时间
     * @return
     */
    public static <T> Map<String, T> getAllMapFromRedisOrDataGeter(String hashCacheKey, Class<T> clazz, MapDataGeter<T> dataGeter, int flagKeyExpireSeconds) {
        Map<String, T> val = RedisClient.getAllFromHashMap(hashCacheKey, clazz);
        if (val != null && !val.isEmpty()) {
            //让热数据一直热下去
            haveChanceToSetKeyExpireTime(hashCacheKey, flagKeyExpireSeconds);
            return val;
        }
        if (RedisClient.existsKey(RedisClient.getFlagKey(hashCacheKey))) {
            return val;
        }

        try {
            Object lockObject = LockObjectUtil.getLockObject(hashCacheKey);
            synchronized (lockObject) {
                Map<String, T> map = null;
                try {
                    map = dataGeter.getMap();
                } catch (RedisException e) {
                    e.printStackTrace();
                    throw e;
                }
                if (map != null && !map.isEmpty()) {
                    RedisClient.delKey(hashCacheKey);
                    RedisClient.addToHashMap(hashCacheKey, map);
                    RedisClient.setKeyExpireTime(hashCacheKey, flagKeyExpireSeconds <= 0 ? RedisDataSource.DEFAULT_EXPIRE_SECONDS : flagKeyExpireSeconds);
                } else {
                    // 不存在数据则往缓存中插入一个flag，过期时间60s
                    RedisClient.setValue(RedisClient.getFlagKey(hashCacheKey), true, ONE_DAY_EXPIRE_SECONDS);
                }
                return map;

            }
        } finally {
            LockObjectUtil.disposeLock(hashCacheKey);
        }
    }

    /**
     * 获取redis中指定的map，如果redis中不存在，则从StringMapDataGeter接口中获取，并且写入redis<br/>
     *
     * @param hashCacheKey-hashmap的key
     * @param clazz-Map数据value值类型（T对象类型）
     * @param dataGeter-数据获取器，原始数据源
     * @return
     */
    public static <T> Map<String, T> getAllMapFromRedisOrDataGeter(String hashCacheKey, Class<T> clazz, MapDataGeter<T> dataGeter) {
        return getAllMapFromRedisOrDataGeter(hashCacheKey, clazz, dataGeter,/*0*/RedisDataSource.DEFAULT_EXPIRE_SECONDS);
    }


}