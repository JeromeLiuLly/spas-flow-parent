package com.candao.spas.flow.redis;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisClient {
	
	/**
	 * 获取指定数据源
	 * @param dataSourceName  数据源名称(若只有一个数据源，便获取默认数据源)
	 * @return
	 */
	public static RedisDataSource getSource(RedisDataSourceName dataSourceName) {
		return RedisDataSourceFactory.getInstance().getSource(dataSourceName);
	}
	
	/**
	 * 是否存在指定的key
	 * @param key
	 * @return
	 */
	public static boolean existsKey(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().existsKey(key);
	}
	/**
	 * key表达式,如：brand_*    危险Api，不能调用
	 * @param keyExpression
	 * @return
	 */
	@SuppressWarnings("unused")
	private static Set<String> getKeys(String keyExpression) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getKeys(keyExpression);
	}
	/**
	 * 移除指定的key
	 * @param key
	 */
	public static void delKey(String key) {
		RedisDataSourceFactory.getInstance().getDefaultSource().delKey(key);
	}
	/**
	 * 获取标记key<br/>
	 * flagkey = key + "_flag"
	 * @param key
	 * @return
	 */
	public static String getFlagKey(String key) {
		return key + "_flag";
	}
	/**
	 * 移除标记key<br/>
	 * flagkey = key + "_flag"
	 * @param key
	 */
	public static void delFlagKey(String key) {
		delKey(getFlagKey(key));
	}
	/**
	 * 事物移除指定的key及flag key（原子性）
	 * @param key-原始key
	 */
	public static void delKeyAndFlagKey(String key) {
		RedisDataSourceFactory.getInstance().getDefaultSource().delKeyAndFlagKey(key);
	}
	
	/**
	 * 设置指定key的过期时间为默认过期时间（3天）
	 * @param key
	 */
	public static void setKeyDefaultExpireTime(String key) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setKeyDefaultExpireTime( key);
	}
	/**
	 * 单独设置指定key的过期时间
	 * @param key-key
	 * @param expireSeconds--过期时间(秒)
	 */
	public static void setKeyExpireTime(String key, int expireSeconds) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setKeyExpireTime(key, expireSeconds);
	}
	/**
	 * 单独设置指定key的过期时间
	 * @param key-key
	 * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
	 */
	public static void setKeyExpireTime(String key, String expireTime) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setKeyExpireTime(key, expireTime);
	}
	/**
	 * 查询缓存失效时间
	 * @param key
	 * @return  -2:key不存在,  
	 * 			-1：存在但没有设置剩余生存时间时,
	 * 			 大于0：以秒为单位，返回 key 的剩余生存时间
	 * 			-3:redis报错
	 * @author Guoden 2016年12月14日
	 */
	public static long getKeyExpireTime(String key){
		return RedisDataSourceFactory.getInstance().getDefaultSource().getKeyExpireTime(key);
	}
	// ===============================key-value======================
	/**
	 * 从缓存取得字符串
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValue(key);
	}
	/**
	 * 从缓存取得对象
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T> T getValue(String key, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValue(key, c);
	}
	
	
	/**
	 * 从缓存取得对象
	 * @param key
	 * @param type
	 * @return
	 */
	public static <T> T getValue(String key, Type type) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValue(key, type);
	}
	
	/**
	 * 从缓存取得对象，数据对象存放的位置与id一致，如果id不存在，对应位置的数据对象为null
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T> List<T> getValues(String[] keys, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValues(keys, c);
	}
	/**
	 * 从缓存取得对象，数据对象存放的位置与id一致，如果id不存在，对应位置的数据对象为空字符串
	 * @param keys
	 * @return
	 */
	public static List<String> getValues(List<String> keys) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValues(keys);
	}
	/**
	 * 保存字符串(如果对象已经存在，则覆盖)
	 * @param key
	 * @param value
	 */
	public static void setValue(String key, String value) {
		setValue(key, value, 0);
	}
	/**
	 * 保存字符串(如果对象已经存在，则覆盖)
	 * @param key-key
	 * @param value-value
	 * @param expireSeconds-过期时间(秒)
	 */
	public static void setValue(String key, String value, int expireSeconds) {
		setValue(key, value, expireSeconds, false);
	}
	
	/**
	 * 保存字符串(如果对象已经存在，则覆盖)
	 * @param key-key
	 * @param value-value
	 * @param expireSeconds-过期时间(秒)
	 * @param needSetFlagexpireSeconds-是否设置其flag，其过期时间为expireSeconds
	 */
	private static void setValue(String key, String value, int expireSeconds, boolean needSetFlag) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setValue( key,  value,  expireSeconds,  needSetFlag);
	}
	/**
	 * 保存字符串(如果对象已经存在，则覆盖)
	 * @param key-key
	 * @param value-value
	 * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
	 */
	public static void setValue(String key, String value, String expireTime) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setValue( key,  value,  expireTime);
	}
	/**
	 * 保存单个对象(如果对象已经存在，则覆盖)
	 * @param key
	 * @param value
	 */
	public static <T> void setValue(String key, T value) {
		setValue(key, value, 0);
	}
	/**
	 * 保存单个对象(如果对象已经存在，则覆盖)
	 * @param key
	 * @param value
	 * @param expireSeconds-过期时间(秒)
	 */
	public static <T> void setValue(String key, T value, int expireSeconds) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setValue( key,  value,  expireSeconds);
	}
	
	/**
	 * 保存单个对象(如果对象已经存在，则覆盖)-包括flag
	 * @param key
	 * @param value
	 * @param expireSeconds-value和flag的过期时间(秒)
	 * @param needSetFlag -是否设置flag，其过期时间为expireSeconds
	 */
	public static <T> void setValue(String key, T value, int expireSeconds, boolean needSetFlag) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setValue( key,  value,  expireSeconds,  needSetFlag);
	}
	/**
	 * 保存字符串(如果对象已经存在，则覆盖)
	 * @param key-key
	 * @param value-value
	 * @param expireTime-过期时间，类型：yyyy-MM-dd HH:mm:ss
	 */
	public static <T> void setValue(String key, T value, String expireTime) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setValue( key,  value,  expireTime);
	}
	// ===============================sexnx==================================
	/**
	 * 保存对象（如果不存在key的话）
	 * @param key
	 * @param value
	 * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
	 */
	public static <T> boolean setnx(String key, T value) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().setnx( key,  value);
	}
	/**
	 * 保存对象（如果不存在key的话）
	 * @param key
	 * @param value
	 * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
	 */
	public static <T> boolean setnx(String key, T value, int expireSeconds) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().setnx( key,  value,  expireSeconds);
	}
	/**
	 * 保存字符串（如果不存在key的话）
	 * @param key
	 * @param value
	 * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
	 */
	public static boolean setnx(String key, String value) {
		return setnx(key, value, 0);
	}
	/**
	 * 保存字符串（如果不存在key的话）
	 * @param key
	 * @param value
	 * @return 不存在key，则保存成功，返回true，存在key，则保存失败，返回false
	 */
	public static boolean setnx(String key, String value, int expireSeconds) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().setnx( key,  value,  expireSeconds);
	}
	// ===============================key-list======================
	/**
	 * 添加一个list列表到原有列表尾部
	 * @param key
	 * @param list
	 */
	public static <T> void addList(String key, List<T> list) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addList( key, list);
	}
	/**
	 * 添加一个value到原有列表到尾部
	 * @param key
	 * @param value
	 */
	public static <T> void addList(String key, T value) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addList( key, value);
	}
	/**
	 * 添加一个list列表到原有列表头部
	 * @param key
	 * @param list
	 */
	public static <T> void addListToHead(String key, List<T> list) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addListToHead( key, list);
	}
	/**
	 * 添加一个value到原有列表头部
	 * @param key
	 * @param value
	 */
	public static <T> void addListToHead(String key, T value) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addListToHead(key, value);
	}
	/**
	 * 设置list中指定索引的值
	 * @param key
	 * @param index-索引
	 * @param value
	 */
	public static <T> void setListElement(String key, int index, T value) {
		RedisDataSourceFactory.getInstance().getDefaultSource().setListElement( key,  index,  value);
	}
	/**
	 * 返回某个范围内的集合，无结果集则返回空list
	 * @param key
	 * @param start-起始索引（包含，从0开始）
	 * @param end-结束索引（包含）
	 * @return
	 */
	public static List<String> getListRange(String key, int start, int end) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListRange( key,  start,  end);
	}
	/**
	 * 返回某个范围内的集合，无结果集则返回空list
	 * @param key
	 * @param start-起始索引（包含，从0开始）
	 * @param end-结束索引（包含）
	 * @param c-具体类
	 * @return
	 */
	public static <T> List<T> getListRange(String key, int start, int end, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListRange( key,  start,  end,  c);
	}
	/**
	 * 分页获取list
	 * @param key
	 * @param pageNow-当前页数
	 * @param pageSize-每页记录数
	 * @param c
	 * @return
	 */
	public static <T> List<T> getListPage(String key, int pageNow, int pageSize, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListPage( key,  pageNow,  pageSize,  c);
	}
	/**
	 * 获取指定key下的列表所有记录
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T> List<T> getAllList(String key, Class<T> c) {
		return getListRange(key, 0, -1, c);
	}
	/**
	 * 获取列表第一个元素
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T> T getListFirstElement(String key, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListFirstElement( key, c);
	}
	/**
	 * 返回并删除list中的首元素
	 * @param key
	 * @return
	 */
	public static <T> T getListPop(String key, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListPop( key, c);
	}
	
	/**
	 * 返回并删除list中的首元素
	 * @param key
	 * @return
	 */
	public static <T> String getListPop(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListPop( key);
	}
	
	
	/**
	 * 返回并删除list中的尾元素
	 * @param key
	 * @return
	 */
	public static  <T> T getListPopOfLast(String key, Class<T> c){
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListPopOfLast(key, c);
	}
	
	/**
	 * 返回并删除list中的尾元素
	 * @param key
	 * @return
	 */
	public  static <T> String getListPopOfLast(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListPopOfLast(key);
	}
	/**
	 * 获取列表最后一个元素
	 * @param key
	 * @param c
	 * @return
	 */
	public static <T> T getListLastElement(String key, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListLastElement( key, c);
	}
	/**
	 * 返回list的集合个数
	 * @param key
	 * @return
	 */
	public static int getListSize(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getListSize( key);
	}
	/**
	 * 删除list的指定对象
	 * @param key
	 * @param values
	 */
	@SuppressWarnings("unchecked")
	public static <T> long removeValueFromList(String key, T... values) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().removeValueFromList( key, values);
	}
	/**
	 * 删除一个列表
	 * @param key
	 */
	public static void removeList(String key) {
		delKey(key);
	}
	
	//===========================set start==============================
		
	
		/**
		 * set增加元素
		 * @param key	 set的key
		 * @param value  元素列表
		 * @return	插入到set的元素数量，若某个元素重复，则那个元素不算插入数量(重复不插入),  
		 *          int sAmount = RedisClient.sAdd(key, "java","c","mongo","mongo");
		 *          此时sAmount为3
		 */
		@SuppressWarnings("unchecked")
		public static <T> int sAdd(final String key,final T... value){
			return RedisDataSourceFactory.getInstance().getDefaultSource().sAdd(key, value);
		}
		
		
		/**
		 * set中元素的数量
		 * @param key
		 * @return
		 */
		public static int sSize(final String key) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().sSize(key);
		}
		
		
		/**
		 * set中元素的集合
		 * @param key
		 * @return
		 */
		public static<T> Set<T> sSet(final String key, Class<T> c) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().sSet(key, c);
		}
		
		
		/**
		 * 删除set中的元素
		 * @param key		set的key
		 * @param members	需要删除的元素列表
		 * @return	返回删除元素的个数
		 */
		@SuppressWarnings("unchecked")
		public static <T> int sRemove(final String key,T... members){
			return RedisDataSourceFactory.getInstance().getDefaultSource().sRemove(key, members);
		}
		
		
		
		/**
		 * 判断某个元素是否存在set中
		 * @param key		set的key
		 * @param member	元素
		 * @return
		 */
		public static<T>  boolean sExists(final String key,final T member) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().sExists(key, member);
			
		}
		
		
		//===========================set end==============================
		
		
		//===========================SortedSet start==============================
		/**
		 * 【SortedSet】	新增元素	
		 * @param key		SortedSet的key名称
		 * @param score	           元素的分值
		 * @param member    元素
		 * @return			增加元素数量
		 */
		public  static long zadd(String key, double score,String member) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zadd(key, score, member);
		}
		
		/**
		 * 【SortedSet】	批量新增元素	
		 * @param key			   SortedSet的key名称
		 * @param scoreMembers     元素的Map
		 * @return					增加元素数量
		 */
		public static long zadd(String key,Map<String, Double> scoreMembers) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zadd(key, scoreMembers);
		}
		
		
		/**
		 * 【SortedSet】	移除元素
		 * @param key			SortedSet的key名称
		 * @param members		元素的可变参数列表
		 * @return				移除元素数量
		 */
		public  static long zrem(String key,String... members) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zrem(key, members);
		}
		
		
		/**
		 * <p>【SortedSet】积分从小到大获取Set的坐标从start到end的有序集合(LinkedHashSet) </p>
		 *  如：   <br/>
		 *  <p>倒数前三名：zrange("xxxx", 0, 2) </p>
		 *  
		 * @param key		SortedSet的key名称
		 * @param start     开始坐标(0为第一个，1为第二个)，此start数值不能为负数
		 * @param end		结束坐标(0为第一个，1为第二个，-1为倒数第一个,-2为倒数第二个)
		 * @return
		 */
		public static Set<String> zrange(String key, long start, long end) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zrange(key, start, end);
		}
		
		/**
		 * 【SortedSet】积分从小到大获取Set的有序集合(LinkedHashSet)
		 * @param key  SortedSet的key名称
		 * @return
		 */
		public  static Set<String> zrangeAll(String key) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zrangeAll(key);
		}
		
		/**
		 * <p>【SortedSet】积分从大到小获取Set的坐标从start到end的有序集合(LinkedHashSet) </p>
		 *  如：   <br/>
		 *  
		 *  <p>1 排名前三甲 : zrevrange("xxxx", 0, 2)</p>
		 * 
		 * @param key		SortedSet的key名称
		 * @param start     开始坐标(0为第一个，1为第二个)，此start数值不能为负数
		 * @param end		结束坐标(0为第一个，1为第二个，-1为倒数第一个,-2为倒数第二个)
		 * @return
		 */
		public  static Set<String> zrevrange(String key, long start, long end) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zrevrange(key, start, end);
		}
		
		/**
		 * 【SortedSet】积分从大到小获取Set的有序集合(LinkedHashSet)
		 * @param key  SortedSet的key名称
		 * @return
		 */
		public  static Set<String> zrevrangeAll(String key) {
			return RedisDataSourceFactory.getInstance().getDefaultSource().zrevrangeAll(key);
		}
		
		
		//===========================SortedSet end==============================
	
	/**
	 * 保存Java map to Redis map
	 * @param hashKey
	 * @param map
	 */
	public static <T> void addToHashMap(String hashKey, Map<String, T> map) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addToHashMap( hashKey, map);
	}
	/**
	 * 保存Java value to Redis map
	 * @param hashKey
	 * @param key
	 * @param value
	 */
	public static <T> void addToHashMap(String hashKey, String key, T value) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addToHashMap( hashKey,  key, value);
	}
	/**
	 * 保存Java value to Redis map
	 * @param hashKey
	 * @param key
	 * @param json
	 */
	public static <T> void addToHashMap(String hashKey, String key, String json) {
		RedisDataSourceFactory.getInstance().getDefaultSource().addToHashMap( hashKey,  key, json);
	}
	/**
	 * 从hashmap中返回某个key的值
	 * @param hashKey
	 * @param key
	 * @param c-类型
	 * @return
	 */
	public static <T> T getValueFromHashMap(String hashKey, String key, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValueFromHashMap( hashKey,  key,  c);
	}
	/**
	 * 从hashmap中返回某个key的值
	 * @param hashKey
	 * @param key
	 * @return
	 */
	public static String getValueFromHashMap(String hashKey, String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValueFromHashMap(hashKey,  key);
	}
	
	/**
	 * 从hashmap中返回多个key的值
	 * @param hashKey
	 * @param c          类型
	 * @param keys       多个key
	 * @return	                          如果查询的key都不存在返回一个空集合，如果部分不存在，对应的位置的值为null
	 */
	public static <T> List<T> getValuesFromHashMap(String hashKey,Class<T> c,String...keys) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValuesFromHashMap(hashKey, c, keys);
	}
	/**
	 * 从hashmap中返回某个key的值
	 * @param hashKey
	 * @param key
	 * @param typeOfT -类型
	 * @return
	 */
	public static <T> T getValueFromHashMap(String hashKey, String key, Type typeOfT) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getValueFromHashMap( hashKey,  key,  typeOfT);
	}
	/**
	 * 返回map对象
	 * @param hashKey
	 * @return
	 */
	public static <T> Map<String, T> getAllFromHashMap(String hashKey, Class<T> c) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getAllFromHashMap( hashKey,  c);
	}
	/**
	 * hashmap的size
	 * @param hashKey
	 * @return
	 */
	public static int getSizeFromHashMap(String hashKey) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getSizeFromHashMap( hashKey);
	}
	
	public static String type(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().type(key);
	}
	
	/**
	 * 删除
	 * @param hashKey
	 * @param keys
	 * @return  返回删除map中的记录条数
	 */
	public static long removeFromHashMap(String hashKey, String... keys) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().removeFromHashMap(hashKey, keys);
	}
	/**
	 * 获取某个Map的所有key集合
	 * @param hashKey
	 * @return
	 */
	public static Set<String> getKeysFromHashMap(String hashKey) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().getKeysFromHashMap(hashKey);
	}
	
	/**
	 * 删除某Map所有的元素
	 * @param hashKey
	 */
	public static void removeHashMap(String hashKey) {
		delKey(hashKey);
	}
	/**
	 * 在hashmap中是否存在指定的key
	 * @param hashKey
	 * @param keys
	 */
	public static boolean hasKeyFromHashMap(String hashKey, String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().hasKeyFromHashMap(hashKey, key);
	}
	
	/**
	 * 将 key 中储存的数字值+1并返回
	 * @param key
	 * @return 出现异常，返回0
	 */
	public  static long incr(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().incr(key);
	}
	
	
	/**
	 * 将 key 中储存的数字值-1并返回
	 * @param key
	 * @return 出现异常，返回0
	 */
	public static long decr(String key) {
		return RedisDataSourceFactory.getInstance().getDefaultSource().decr(key);
	}
	
	
	/**
	 * redis是否可能已经死亡
	 * @return
	 */
	public static boolean mayBeDead() {
		return RedisDataSourceFactory.getInstance().getDefaultSource().mayBeDead();
	}
}