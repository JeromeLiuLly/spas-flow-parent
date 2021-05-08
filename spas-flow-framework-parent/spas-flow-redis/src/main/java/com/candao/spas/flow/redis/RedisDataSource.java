package com.candao.spas.flow.redis;


import com.candao.spas.flow.core.exception.RedisException;
import com.candao.spas.flow.core.utils.EasyJsonUtils;
import com.candao.spas.flow.core.utils.StringUtil;
import com.candao.spas.flow.redis.data.RedisCall;
import com.candao.spas.flow.redis.holder.JedisPoolHolder;
import com.candao.spas.flow.redis.holder.JedisSentinelPoolHolder;
import com.candao.spas.flow.redis.holder.PoolAbstractHolder;
import com.candao.spas.flow.redis.util.CollectionUtils;
import com.candao.spas.flow.redis.util.DateUtil;
import com.candao.spas.flow.redis.util.LockRedisKeyUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class RedisDataSource {
    private PoolAbstractHolder poolHolder;
    /**
     * 默认数据过期时间 3天
     */
    public static final int DEFAULT_EXPIRE_SECONDS = 3 * 24 * 60 * 60;
    /**
     * flag默认过期时间为1天
     */
    private static final int FLAG_EXPIRE_SECONDS = 1 * 24 * 60 * 60;
    /**
     * 一次可能获取的最大列表长度，暂时设置为5000条数据
     */
    private static final int OnceMayBeGetSize = 5000;

    private static JedisPool pool;

    private RedisDataSource() {
    }

    /**
     * 创建数据源
     *
     * @param initParam
     * @return
     */
    public static RedisDataSource create(RedisInitParam initParam) {
        log.info("redis初始化参数：" + initParam);
        RedisDataSource ds = new RedisDataSource();
        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大空闲连接数
        config.setMaxIdle(initParam.getMaxIdleCount());
        // 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxWaitMillis(initParam.getMaxWaitMillis());
        // 最大连接数, 默认8个
        config.setMaxTotal(initParam.getMaxTotalCount());
        pool = new JedisPool(
                config, initParam.getServer(),
                initParam.getPort(),
                5000,
                StringUtils.isBlank(initParam.getPassword()) ? null : initParam.getPassword()
                , initParam.getDatabase());
        // 创建连接池
        ds.poolHolder = JedisPoolHolder.create(pool);
        return ds;
    }

    /**
     * 创建数据源(Sentinel版本)
     *
     * @param initParam
     * @return
     */
    public static RedisDataSource create(RedisInitParamForSentinel initParam) {
        log.info("redis初始化参数：" + initParam);
        RedisDataSource ds = new RedisDataSource();
        // 建立连接池配置参数
        JedisPoolConfig config = new JedisPoolConfig();
        // 最大空闲连接数
        config.setMaxIdle(initParam.getMaxIdleCount());
        // 设置最大阻塞时间，记住是毫秒数milliseconds
        config.setMaxWaitMillis(initParam.getMaxWaitMillis());
        // 最大连接数, 默认8个
        config.setMaxTotal(initParam.getMaxTotalCount());
        ds.poolHolder = JedisSentinelPoolHolder.create(new JedisSentinelPool(initParam.getMasterName(),
                initParam.getSentinelSet(),
                config,
                initParam.getPassword()));
        return ds;
    }

    /**
     * 是否存在指定的key
     *
     * @param key
     * @return
     */
    public boolean existsKey(String key) {
        try {
            return execute(RedisMethod.ExistsKey, key, (j) -> {
                return j.exists(key);
            });
        } catch (RedisException e) {
            log.error("redis发生异常", e);
            return false;
        }
    }

    /**
     * key表达式,如：brand_*
     *
     * @param keyExpression
     * @return
     */
    public Set<String> getKeys(String keyExpression) {
        try {
            return execute(RedisMethod.KeyExpression, keyExpression, (j) -> {
                return j.keys(keyExpression);
            });
        } catch (RedisException e) {
            log.error( "redis发生异常", e);
            return Collections.emptySet();
        }
    }

    /**
     * 移除指定的key
     *
     * @param key
     */
    public void delKey(String key) {
        try {
            execute(RedisMethod.DelKey, key, (j) -> {
                return j.del(key);
            });
        } catch (RedisException e) {
			log.error("redis发生异常", e);
        }
    }

    /**
     * 获取标记key<br/>
     * flagkey = key + "_flag"
     *
     * @param key
     * @return
     */
    public String getFlagKey(String key) {
        return key + "_flag";
    }

    /**
     * 移除标记key<br/>
     * flagkey = key + "_flag"
     *
     * @param key
     */
    public void delFlagKey(String key) {
        delKey(getFlagKey(key));
    }

    /**
     * 事物移除指定的key及flag key（原子性）
     *
     * @param key-原始key
     */
    public void delKeyAndFlagKey(String key) {
        try {
            execute(RedisMethod.DelKeyAndFlagKey, key, (j) -> {
                return j.del(getFlagKey(key)) + j.del(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 设置指定key的过期时间为默认过期时间（3天）
     *
     * @param key
     */
    public void setKeyDefaultExpireTime(String key) {
        setKeyExpireTime(key, DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 单独设置指定key的过期时间
     *
     * @param key-key
     * @param expireSeconds--过期时间(秒)
     */
    public void setKeyExpireTime(String key, int expireSeconds) {
        if (expireSeconds <= 0) {
            return;
        }
        try {
            execute(RedisMethod.SetKeyExpireTime, key, (j) -> {
                return j.expire(key, expireSeconds);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 单独设置指定key的过期时间
     *
     * @param key-key
     * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
     */
    public void setKeyExpireTime(String key, String expireTime) {
        long toTime = DateUtil.getTime(expireTime, DateUtil.pattern1);
        int expireSeconds = (int) (toTime - System.currentTimeMillis()) / 1000;
        setKeyExpireTime(key, expireSeconds);
    }

    /**
     * 查询缓存失效时间
     *
     * @param key
     * @return -2:key不存在,
     * -1：存在但没有设置剩余生存时间时,
     * 大于0：以秒为单位，返回 key 的剩余生存时间
     * -3:redis报错
     * @author Guoden 2016年12月14日
     */
    public long getKeyExpireTime(String key) {
        try {
            return execute(RedisMethod.GetKeyExpireTime, key, (j) -> {
                return j.ttl(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -3;
        }
    }
    // ===============================key-value======================

    /**
     * 从缓存取得字符串
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        try {
            return execute(RedisMethod.GetValue, key, (j) -> {
                return j.get(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 从缓存取得对象
     *
     * @param key
     * @param c
     * @return
     */
    public <T> T getValue(String key, Class<T> c) {
        return Optional.ofNullable(getValue(key)).map(d -> EasyJsonUtils.toJavaObject(d, c)).orElse(null);
    }

    /**
     * 从缓存取得对象
     *
     * @param key
     * @param type
     * @return
     */
    public <T> T getValue(String key, Type type) {
        return (T) Optional.ofNullable(getValue(key)).map(d -> EasyJsonUtils.toJavaObject(d, type)).orElse(null);
    }

    private void logDataMayBeMuch(String key, int size) {
        if (size >= OnceMayBeGetSize) {
            log.info("一次性获取数据过多 key[" + key + "] size[" + size + "]");
        }
    }

    /**
     * 从缓存取得对象，数据对象存放的位置与id一致，如果id不存在，对应位置的数据对象为null
     *
     * @param keys
     * @param c
     * @return
     */
    public <T> List<T> getValues(String[] keys, Class<T> c) {
        if (keys == null || keys.length <= 0) {
            return Collections.emptyList();
        }
        try {
            return execute(RedisMethod.GetValues, StringUtil.listToString(Arrays.asList(keys), ","), (j) -> {
                List<String> results = j.mget(keys);
                List<T> list = Optional.ofNullable(results).orElse(Collections.emptyList())
                        .stream()
                        .map(s -> {
                            return !StringUtil.isNullOrEmpty(s) && !s.equals("nil") ? EasyJsonUtils.toJavaObject(s, c) : null;
                        })
                        .collect(Collectors.toList());
                logDataMayBeMuch(StringUtil.listToString(Arrays.asList(keys), ","), list.size());
                return list;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 从缓存取得对象，数据对象存放的位置与id一致，如果id不存在，对应位置的数据对象为空字符串
     *
     * @param keys
     * @return json 字符串
     */
    public List<String> getValues(List<String> keys) {
        if (keys == null || keys.size() <= 0) {
            return Collections.emptyList();
        }
        try {
            return execute(RedisMethod.GetValues, StringUtil.listToString(keys, ","), (j) -> {
                List<String> results = j.mget(keys.toArray(new String[keys.size()]));
                List<String> list = Optional.ofNullable(results).orElse(Collections.emptyList()).stream()
                        .map(s -> {
                            return !StringUtil.isNullOrEmpty(s) && !s.equals("nil") ? s : "";
                        })
                        .collect(Collectors.toList());
                logDataMayBeMuch(StringUtil.listToString(keys, ","), list.size());
                return list;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 保存字符串(如果对象已经存在，则覆盖)
     *
     * @param key-key
     * @param value-value
     * @param expireSeconds-过期时间(秒)
     */
    public void setValue(String key, String value, int expireSeconds) {
        setValue(key, value, expireSeconds, false);
    }

    /**
     * 保存字符串(如果对象已经存在，则覆盖)
     *
     * @param key-key
     * @param value-value
     * @param expireSeconds-过期时间(秒)
     * @param needSetFlag -是否设置其flag，其过期时间为expireSeconds
     */
    public void setValue(String key, String value, int expireSeconds, boolean needSetFlag) {
        try {
            execute(RedisMethod.SetValue, key, (j) -> {
                onSetValue(j, key, value, expireSeconds, needSetFlag);
                return true;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * set操作，与StandbyRedisClient共用
     *
     * @param jedis
     * @param key
     * @param value
     * @param expireSeconds
     * @param needSetFlag
     */
    protected void onSetValue(Jedis jedis, String key, String value, int expireSeconds, boolean needSetFlag) {
        //剩余过期毫秒数
        long expireTime = jedis.pttl(key);
        String flagKey = getFlagKey(key);
        jedis.set(key, value);
        if (needSetFlag) {
            jedis.set(flagKey, EasyJsonUtils.toJsonString(true));
        }
        if (expireSeconds > 0) {
            if (needSetFlag) {
                //如果数据的过期时间大于等于默认的flag过期时间，则取flag的默认过期时间，否则取数据过期时间(flag的过期时间不能比数据的长)
                jedis.expire(flagKey, expireSeconds >= FLAG_EXPIRE_SECONDS ? FLAG_EXPIRE_SECONDS : expireSeconds);
            }
            jedis.expire(key, expireSeconds);
        } else {// 不设置过期时间则需设置回原有的过期时间
            // 获取key过期时间，因为set后原有的key过期时间将被清空
            if (expireTime > 0) {
                int time = (int) Math.ceil((float) expireTime / 1000);
                if (needSetFlag) {
                    jedis.expire(flagKey, time);
                }
                jedis.expire(key, time);// 再设置原有剩余秒数 向上取整，如2.1 则为3
            }
        }
    }

    /**
     * 保存字符串(如果对象已经存在，则覆盖)
     *
     * @param key-key
     * @param value-value
     * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
     */
    public void setValue(String key, String value, String expireTime) {
        long toTime = DateUtil.getTime(expireTime, DateUtil.pattern1);
        int expireSeconds = (int) (toTime - System.currentTimeMillis()) / 1000;
        if (expireSeconds > 0) {
            setValue(key, value, expireSeconds);
        }
    }

    /**
     * 保存单个对象(如果对象已经存在，则覆盖)
     *
     * @param key
     * @param value
     */
    public <T> void setValue(String key, T value) {
        setValue(key, value, 0);
    }

    /**
     * 保存单个对象(如果对象已经存在，则覆盖)
     *
     * @param key
     * @param value
     * @param expireSeconds-过期时间(秒)
     */
    public <T> void setValue(String key, T value, int expireSeconds) {
        if (value != null) {
            setValue(key, EasyJsonUtils.toJsonString(value), expireSeconds);
        }
    }

    /**
     * 保存单个对象(如果对象已经存在，则覆盖)-包括flag
     *
     * @param key
     * @param value
     * @param expireSeconds-value和flag的过期时间(秒)
     * @param needSetFlag -是否设置flag，其过期时间为expireSeconds
     */
    public <T> void setValue(String key, T value, int expireSeconds, boolean needSetFlag) {
        if (value != null) {
            setValue(key, EasyJsonUtils.toJsonString(value), expireSeconds, needSetFlag);
        }
    }

    /**
     * 保存字符串(如果对象已经存在，则覆盖)
     *
     * @param key-key
     * @param value-value
     * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
     */
    public <T> void setValue(String key, T value, String expireTime) {
        if (value != null) {
            setValue(key, EasyJsonUtils.toJsonString(value), expireTime);
        }
    }
    // ===============================sexnx==================================

    /**
     * 保存对象（如果不存在key的话）
     *
     * @param key
     * @param value
     * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
     */
    public <T> boolean setnx(String key, T value) {
        return setnx(key, EasyJsonUtils.toJsonString(value));
    }

    /**
     * 保存对象（如果不存在key的话）
     *
     * @param key
     * @param value
     * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
     */
    public <T> boolean setnx(String key, T value, int expireSeconds) {
        return setnx(key, EasyJsonUtils.toJsonString(value), expireSeconds);
    }

    /**
     * 保存字符串（如果不存在key的话）
     *
     * @param key
     * @param value
     * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
     */
    public boolean setnx(String key, String value) {
        return setnx(key, value, 0);
    }

    /**
     * 保存字符串（如果不存在key的话）
     *
     * @param key
     * @param value
     * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
     */
    public boolean setnx(String key, String value, int expireSeconds) {
        try {
            return execute(RedisMethod.Setnx, key, (j) -> {
                // 1 if the key was set 0 if the key was not set
                boolean setnxOK = j.setnx(key, value) == 1;
                // 设置过期时间
                if (expireSeconds > 0) {
                    if (setnxOK) { // 设置成功了，则设置失效时间
                        j.expire(key, expireSeconds);
                    } else if (!setnxOK && j.pttl(key) < 0) {// 或者由于某些异常状态setnx执行成功 但expire没有成功，可能会导致锁永远释放不掉，这里强制设置过期时间
                        j.expire(key, expireSeconds);
                    }
                }
                return setnxOK;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return false;
        }
    }
    // ===============================key-list======================

    /**
     * 添加一个list列表到原有列表尾部
     *
     * @param key
     * @param list
     */
    public <T> void addList(String key, List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        try {
            execute(RedisMethod.AddList, key, (jedis) -> {
                String[] ss = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ss[i] = EasyJsonUtils.toJsonString(list.get(i));
                }
                return jedis.rpush(key, ss);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 添加一个value到原有列表到尾部
     *
     * @param key
     * @param value
     */
    public <T> void addList(String key, T value) {
        if (value == null) {
            return;
        }
        try {
            execute(RedisMethod.AddList, key, (jedis) -> {
                String[] ss = new String[1];
                ss[0] = EasyJsonUtils.toJsonString(value);
                return jedis.rpush(key, ss);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 添加一个list列表到原有列表头部
     *
     * @param key
     * @param list
     */
    public <T> void addListToHead(String key, List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        try {
            execute(RedisMethod.AddListToHead, key, (jedis) -> {
                String[] ss = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ss[i] = EasyJsonUtils.toJsonString(list.get(i));
                }
                return jedis.lpush(key, ss);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 添加一个value到原有列表头部
     *
     * @param key
     * @param value
     */
    public <T> void addListToHead(String key, T value) {
        if (value == null) {
            return;
        }
        try {
            execute(RedisMethod.AddListToHead, key, (jedis) -> {
                String[] ss = new String[1];
                ss[0] = EasyJsonUtils.toJsonString(value);
                return jedis.lpush(key, EasyJsonUtils.toJsonString(value));
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 设置list中指定索引的值
     *
     * @param key
     * @param index-索引
     * @param value
     */
    public <T> void setListElement(String key, int index, T value) {
        if (value == null) {
            return;
        }
        try {
            execute(RedisMethod.SetListElement, key, (jedis) -> {
                return jedis.lset(key, index, EasyJsonUtils.toJsonString(value));
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 返回某个范围内的集合，无结果集则返回空list
     *
     * @param key
     * @param start-起始索引（包含，从0开始）
     * @param end-结束索引（包含）
     * @return
     */
    public List<String> getListRange(String key, int start, int end) {
        try {
            return execute(RedisMethod.GetListRange, key, (jedis) -> {
                List<String> list = jedis.lrange(key, start, end);
                if (CollectionUtils.isEmpty(list)) {
                    return Collections.emptyList();
                }
                logDataMayBeMuch(key, list.size());
                return list;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 返回某个范围内的集合，无结果集则返回空list
     *
     * @param key
     * @param start-起始索引（包含，从0开始）
     * @param end-结束索引（包含）
     * @param c-具体类
     * @return
     */
    public <T> List<T> getListRange(String key, int start, int end, Class<T> c) {
        List<String> slist = getListRange(key, start, end);
        List<T> list = new ArrayList<T>();
        for (String s : slist) {
            list.add(EasyJsonUtils.toJavaObject(s, c));
        }
        return list;
    }

    /**
     * 分页获取list
     *
     * @param key
     * @param pageNow-当前页数
     * @param pageSize-每页记录数
     * @param c
     * @return
     */
    public <T> List<T> getListPage(String key, int pageNow, int pageSize, Class<T> c) {
        int startIndex = (pageNow - 1) * pageSize;
        int endIndex = pageNow * pageSize - 1;
        return getListRange(key, startIndex, endIndex, c);
    }

    /**
     * 获取指定key下的列表所有记录
     *
     * @param key
     * @param c
     * @return
     */
    public <T> List<T> getAllList(String key, Class<T> c) {
        return getListRange(key, 0, -1, c);
    }

    /**
     * 获取列表第一个元素
     *
     * @param key
     * @param c
     * @return
     */
    public <T> T getListFirstElement(String key, Class<T> c) {
        try {
            return execute(RedisMethod.GetListFirstElement, key, (jedis) -> {
                return Optional.ofNullable(jedis.lindex(key, 0)).map(s -> EasyJsonUtils.toJavaObject(s, c)).orElse(null);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 返回并删除list中的首元素
     *
     * @param key
     * @return
     */
    public <T> T getListPop(String key, Class<T> c) {
        String s = getListPop(key);
        return StringUtil.isNullOrBlank(s) ? null : EasyJsonUtils.toJavaObject(s, c);
    }

    /**
     * 返回并删除list中的首元素
     *
     * @param key
     * @return
     */
    public <T> String getListPop(String key) {
        try {
            return execute(RedisMethod.GetListPop, key, (jedis) -> {
                return jedis.lpop(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 返回并删除list中的尾元素
     *
     * @param key
     * @return
     */
    public <T> T getListPopOfLast(String key, Class<T> c) {
        return EasyJsonUtils.toJavaObject(getListPopOfLast(key), c);
    }

    /**
     * 返回并删除list中的尾元素
     *
     * @param key
     * @return
     */
    public <T> String getListPopOfLast(String key) {
        try {
            return execute(RedisMethod.GetListPop, key, (jedis) -> {
                return jedis.rpop(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 获取列表最后一个元素
     *
     * @param key
     * @param c
     * @return
     */
    public <T> T getListLastElement(String key, Class<T> c) {
        try {
            return execute(RedisMethod.GetListLastElement, key, (jedis) -> {
                return Optional.ofNullable(jedis.lindex(key, -1)).map(s -> EasyJsonUtils.toJavaObject(s, c)).orElse(null);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 返回list的集合个数
     *
     * @param key
     * @return
     */
    public int getListSize(String key) {
        try {
            return execute(RedisMethod.GetListSize, key, (jedis) -> {
                return jedis.llen(key).intValue();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 删除list的指定对象
     *
     * @param key
     * @param values
     */
    @SuppressWarnings("unchecked")
    public <T> long removeValueFromList(String key, T... values) {
        if (values == null || values.length <= 0) {
            return 0;
        }
        try {
            return execute(RedisMethod.RemoveValueFromList, key, (jedis) -> {
                return Stream.of(values).mapToLong((s) -> {
                    return jedis.lrem(key, 0, EasyJsonUtils.toJsonString(s));
                }).sum();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 删除一个列表
     *
     * @param key
     */
    public void removeList(String key) {
        delKey(key);
    }
    //===========================set start==============================

    /**
     * set增加元素
     *
     * @param key   set的key
     * @param value 元素列表
     * @return 插入到set的元素数量，若某个元素重复，则那个元素不算插入数量(重复不插入), int sAmount = RedisClient.sAdd(key, "java","c","mongo","mongo");
     * 此时sAmount为3
     */
    @SuppressWarnings("unchecked")
    public <T> int sAdd(final String key, final T... value) {
        if (StringUtil.isNullOrBlank(key) || value == null || value.length <= 0) {
            return -1;
        }
        try {
            return execute(RedisMethod.SAdd, key, (j) -> {
                String[] val = new String[value.length];
                for (int i = 0; i < value.length; i++) {
                    val[i] = EasyJsonUtils.toJsonString(value[i]);
                }
                return j.sadd(key, val).intValue();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * set中元素的数量
     *
     * @param key
     * @return
     */
    public int sSize(final String key) {
        try {
            return execute(RedisMethod.SSize, key, (j) -> {
                return j.scard(key).intValue();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * set中元素的集合
     *
     * @param key
     * @return
     */
    public <T> Set<T> sSet(final String key, Class<T> c) {
        Set<String> set;
        try {
            set = execute(RedisMethod.SSet, key, (f) -> {
                return f.smembers(key);
            });
        } catch (RedisException e) {
            return Collections.emptySet();
        }
        Set<T> resultSet = Optional.ofNullable(set).orElse(Collections.emptySet())
                .stream().map((s) -> {
                    return EasyJsonUtils.toJavaObject(s, c);
                }).collect(Collectors.toSet());
        logDataMayBeMuch(key, resultSet.size());
        return resultSet;
    }

    /**
     * 删除set中的元素
     *
     * @param key     set的key
     * @param members 需要删除的元素列表
     * @return 返回删除元素的个数
     */
    @SuppressWarnings("unchecked")
    public <T> int sRemove(final String key, T... members) {
        if (StringUtil.isNullOrBlank(key) || members == null || members.length <= 0) {
            return -1;
        }
        final String[] val = new String[members.length];
        for (int i = 0; i < members.length; i++) {
            val[i] = EasyJsonUtils.toJsonString(members[i]);
        }
        try {
            return execute(RedisMethod.SRemove, key, (j) -> {
                return j.srem(key, val).intValue();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 判断某个元素是否存在set中
     *
     * @param key    set的key
     * @param member 元素
     * @return
     */
    public <T> boolean sExists(final String key, final T member) {
        if (StringUtil.isNullOrBlank(key)) {
            return false;
        }
        try {
            return execute(RedisMethod.SExists, key, (j) -> {
                return j.sismember(key, EasyJsonUtils.toJsonString(member));
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return false;
        }
    }
    //===========================set end==============================
    //===========================SortedSet start==============================

    /**
     * 【SortedSet】	新增元素
     *
     * @param key    SortedSet的key名称
     * @param score  元素的分值
     * @param member 元素
     * @return 增加元素数量
     */
    public long zadd(String key, double score, String member) {
        if (StringUtil.isNullOrBlank(key) || StringUtil.isNullOrBlank(member)) {
            return -1;
        }
        try {
            return execute(RedisMethod.Zadd, key, (jedis) -> {
                return jedis.zadd(key, score, member);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 【SortedSet】	批量新增元素
     *
     * @param key          SortedSet的key名称
     * @param scoreMembers 元素的Map
     * @return 增加元素数量
     */
    public long zadd(String key, Map<String, Double> scoreMembers) {
        if (StringUtil.isNullOrBlank(key) || scoreMembers == null || scoreMembers.isEmpty()) {
            return -1;
        }
        try {
            return execute(RedisMethod.Zadd, key, (jedis) -> {
                return jedis.zadd(key, scoreMembers);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 【SortedSet】	移除元素
     *
     * @param key     SortedSet的key名称
     * @param members 元素的可变参数列表
     * @return 移除元素数量
     */
    public long zrem(String key, String... members) {
        if (StringUtil.isNullOrBlank(key) || members == null || members.length <= 0) {
            return -1;
        }
        try {
            return execute(RedisMethod.Zrem, key, (jedis) -> {
                return jedis.zrem(key, members);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * <p>【SortedSet】积分从小到大获取Set的坐标从start到end的有序集合(LinkedHashSet) </p>
     * 如：   <br/>
     * <p>倒数前三名：zrange("xxxx", 0, 2) </p>
     *
     * @param key   SortedSet的key名称
     * @param start 开始坐标(0为第一个，1为第二个)，此start数值不能为负数
     * @param end   结束坐标(0为第一个，1为第二个，-1为倒数第一个,-2为倒数第二个)
     * @return
     */
    public Set<String> zrange(String key, long start, long end) {
        if (StringUtil.isNullOrBlank(key)) {
            return Collections.emptySet();
        }
        try {
            return execute(RedisMethod.Zrange, key, (jedis) -> {
                return jedis.zrange(key, start, end);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptySet();
        }
    }

    /**
     * 【SortedSet】积分从小到大获取Set的有序集合(LinkedHashSet)
     *
     * @param key SortedSet的key名称
     * @return
     */
    public Set<String> zrangeAll(String key) {
        return zrange(key, 0, -1);
    }

    /**
     * <p>【SortedSet】积分从大到小获取Set的坐标从start到end的有序集合(LinkedHashSet) </p>
     * 如：   <br/>
     *
     * <p>1 排名前三甲 : zrevrange("xxxx", 0, 2)</p>
     *
     * @param key   SortedSet的key名称
     * @param start 开始坐标(0为第一个，1为第二个)，此start数值不能为负数
     * @param end   结束坐标(0为第一个，1为第二个，-1为倒数第一个,-2为倒数第二个)
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        if (StringUtil.isNullOrBlank(key)) {
            return Collections.emptySet();
        }
        try {
            return execute(RedisMethod.Zrange, key, (jedis) -> {
                return jedis.zrevrange(key, start, end);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptySet();
        }
    }

    /**
     * 【SortedSet】积分从大到小获取Set的有序集合(LinkedHashSet)
     *
     * @param key SortedSet的key名称
     * @return
     */
    public Set<String> zrevrangeAll(String key) {
        return zrevrange(key, 0, -1);
    }
    //===========================SortedSet end==============================

    /**
     * 保存Java map to Redis map
     *
     * @param hashKey
     * @param map
     */
    public <T> void addToHashMap(String hashKey, Map<String, T> map) {
        if (map == null || map.isEmpty()) {
            return;
        }
        try {
            execute(RedisMethod.AddToHashMap, hashKey, (jedis) -> {
                return map.keySet().stream().mapToLong(
                        (key) -> {
                            String value = EasyJsonUtils.toJsonString(map.get(key));
                            return jedis.hset(hashKey, key, value);
                        }
                )
                        .sum();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 保存Java value to Redis map
     *
     * @param hashKey
     * @param key
     * @param value
     */
    public <T> void addToHashMap(String hashKey, String key, T value) {
        if (value == null) {
            return;
        }
        try {
            execute(RedisMethod.AddToHashMap, hashKey, (jedis) -> {
                return jedis.hset(hashKey, key, EasyJsonUtils.toJsonString(value));
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 保存Java value to Redis map
     *
     * @param hashKey
     * @param key
     * @param json
     */
    public <T> void addToHashMap(String hashKey, String key, String json) {
        if (StringUtils.isBlank(json)) {
            return;
        }
        try {
            execute(RedisMethod.AddToHashMap, hashKey, (jedis) -> {
                return jedis.hset(hashKey, key, json);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 从hashmap中返回某个key的值
     *
     * @param hashKey
     * @param key
     * @return
     */
    public String getValueFromHashMap(String hashKey, String key) {
        try {
            return execute(RedisMethod.GetValueFromHashMap, hashKey, (jedis) -> {
                return Optional.ofNullable(jedis.hget(hashKey, key)).orElse(null);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 从hashmap中返回某个key的值
     *
     * @param hashKey
     * @param key
     * @param c-类型
     * @return
     */
    public <T> T getValueFromHashMap(String hashKey, String key, Class<T> c) {
        try {
            return execute(RedisMethod.GetValueFromHashMap, hashKey, (jedis) -> {
                return Optional.ofNullable(jedis.hget(hashKey, key)).map(s -> EasyJsonUtils.toJavaObject(s, c)).orElse(null);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 从hashmap中返回多个key的值
     *
     * @param hashKey
     * @param c       类型
     * @param keys    多个key
     * @return 如果查询的key都不存在返回一个空集合，如果部分不存在，对应的位置的值为null
     */
    public <T> List<T> getValuesFromHashMap(String hashKey, Class<T> c, String... keys) {
        if (keys == null || keys.length <= 0) {
            return Collections.emptyList();
        }
        try {
            return execute(RedisMethod.GetValuesFromHashMap, hashKey, (jedis) -> {
                List<String> list = jedis.hmget(hashKey, keys);
                List<T> endList = Optional.ofNullable(list).orElse(Collections.emptyList())
                        .stream()
                        .map((val) -> StringUtil.isNullOrBlank(val) ? null : EasyJsonUtils.toJavaObject(val, c))
                        .collect(Collectors.toList());
                logDataMayBeMuch(hashKey, endList.size());
                return endList;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 从hashmap中返回某个key的值
     *
     * @param hashKey
     * @param key
     * @param typeOfT -类型
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueFromHashMap(String hashKey, String key, Type typeOfT) {
        try {
            return execute(RedisMethod.GetValueFromHashMap, hashKey, (jedis) -> {
                return (T) Optional.ofNullable(jedis.hget(hashKey, key)).map(s -> EasyJsonUtils.toJavaObject(s, typeOfT)).orElse(null);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return null;
        }
    }

    /**
     * 返回map对象
     *
     * @param hashKey
     * @return
     */
    public <T> Map<String, T> getAllFromHashMap(String hashKey, Class<T> c) {
        try {
            return execute(RedisMethod.GetAllFromHashMap, hashKey, (jedis) -> {
                Map<String, String> map = jedis.hgetAll(hashKey);
                if (map == null || map.isEmpty()) {
                    return Collections.emptyMap();
                }
                //Collectors.toMap，其中的value不能为空，故换成forEach
                Map<String, T> tMap = Maps.newHashMap();
                map.keySet().forEach((k) -> {
                    String v = map.get(k);
                    T t = Optional.ofNullable(v).filter(f -> !"null".equals(v)).map(f -> EasyJsonUtils.toJavaObject(v, c)).orElse(null);
                    tMap.put(k, t);
                });
                logDataMayBeMuch(hashKey, tMap.size());
                return tMap;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptyMap();
        }
    }

    /**
     * hashmap的size
     *
     * @param hashKey
     * @return
     */
    public int getSizeFromHashMap(String hashKey) {
        try {
            return execute(RedisMethod.GetSizeFromHashMap, hashKey, (jedis) -> {
                return jedis.hlen(hashKey).intValue();
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    public String type(String key) {
        try {
            return execute(RedisMethod.Type, key, (jedis) -> {
                return jedis.type(key);
            });
        } catch (RedisException e) {
            log.error("redis发生异常", e);
            return null;
        }
    }

    /**
     * 删除
     *
     * @param hashKey
     * @param keys
     * @return 返回删除map中的记录条数
     */
    public long removeFromHashMap(String hashKey, String... keys) {
        if (keys == null || keys.length <= 0) {
            return 0;
        }
        try {
            return execute(RedisMethod.RemoveFromHashMap, hashKey, (jedis) -> {
                return jedis.hdel(hashKey, keys);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 获取某个Map的所有key集合
     *
     * @param hashKey
     * @return
     */
    public Set<String> getKeysFromHashMap(String hashKey) {
        try {
            return execute(RedisMethod.GetKeysFromHashMap, hashKey, (jedis) -> {
                Set<String> set = jedis.hkeys(hashKey);
                if (set == null || set.isEmpty()) {
                    return Collections.emptySet();
                }
                logDataMayBeMuch(hashKey, set.size());
                return set;
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return Collections.emptySet();
        }
    }

    /**
     * 删除某Map所有的元素
     *
     * @param hashKey
     */
    public void removeHashMap(String hashKey) {
        delKey(hashKey);
    }

    /**
     * 在hashmap中是否存在指定的key
     *
     * @param hashKey
     * @param key
     */
    public boolean hasKeyFromHashMap(String hashKey, String key) {
        try {
            return execute(RedisMethod.HasKeyFromHashMap, hashKey, (jedis) -> {
                return jedis.hexists(hashKey, key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return false;
        }
    }

    /**
     * 将 key 中储存的数字值+1并返回
     *
     * @param key
     * @return 出现异常，返回-1
     */
    public long incr(String key) {
        try {
            return execute(RedisMethod.Incr, key, (jedis) -> {
                return jedis.incr(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * 将 key 中储存的数字值-1并返回
     *
     * @param key
     * @return 出现异常，返回-1
     */
    public long decr(String key) {
        try {
            return execute(RedisMethod.Decr, key, (jedis) -> {
                return jedis.decr(key);
            });
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return -1;
        }
    }

    /**
     * redis是否可能已经死亡
     *
     * @return
     */
    public boolean mayBeDead() {
        String key = "RedisMayBeDead_" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        try {
            setValue(key, key, 10);
            String val = getValue(key);
            return !key.equals(val);
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
            return true;
        }
    }

    /**
     * 正常释放jedis
     *
     * @param jedis
     */
    private void closeResource(Jedis jedis) {
        try {
            if (jedis != null) {
                poolHolder.returnResource(jedis);
            }
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 异常释放jedis
     *
     * @param jedis
     */
    private void returnBrokenResource(Jedis jedis) {
        try {
            if (jedis != null) {
                poolHolder.returnBrokenResource(jedis);
            }
        } catch (RedisException e) {
			log.error( "redis发生异常", e);
        }
    }

    /**
     * 销毁整个redis线程连接
     */
    public void destroy() {
        if (poolHolder != null) {
            poolHolder.destroy();
        }
    }

    /**
     * Redis执行的模板
     *
     * @param call
     * @return
     */
    private <T> T execute(RedisMethod method, String key, RedisCall<T> call) throws RedisException {
        LockRedisKeyUtil.checkLock(key);
        Jedis jedis = null;
        try {
            jedis = poolHolder.getResource();
            if (method.needLog()) {
                log.info("RedisMethod:" + method.getMethodName() + "\tkey:" + key);
            }
            return call.execute(jedis);
        } catch (RedisException e) {
			log.error("redis 异常",e);
            returnBrokenResource(jedis);
            throw e;
        } finally {
            closeResource(jedis);
        }
    }

    public JedisPool getJedisPool(){
        return pool;
    }
}