package com.candao.spas.flow.redis;


import com.candao.spas.flow.core.utils.StringUtil;
import com.candao.spas.flow.jackson.EasyJsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


@Slf4j
public class RedisDataSourceFactory {

	private RedisDataSourceFactory(){}
	
	private static final RedisDataSourceFactory INSTANCE = new RedisDataSourceFactory();
	/**数据源持有Map, key:数据源名称*/
	private static final Map<String, RedisDataSource> SourceMap = new HashMap<>();
	
	public static  RedisDataSourceFactory getInstance() {
		return INSTANCE;
	}
	
	
	/**
	 * 创建数据源
	 * @param name			数据源名称
	 * @param initParam     redis初始化配置参数
	 */
	public void create(RedisDataSourceName name,RedisInitParam initParam) {
		if(SourceMap.containsKey(name.getName())) {
			log.info("已经存在数据源:" + name);
			return ;
		}
		SourceMap.put(name.getName(), RedisDataSource.create(initParam));
		log.info("创建数据源:" + name + "\tinitParam:" + initParam);
	}
	
	/**
	 * 创建数据源(Sentinel版本)
	 * @param name			数据源名称
	 * @param initParam     redis初始化配置参数
	 */
	public void create(RedisDataSourceName name,RedisInitParamForSentinel initParam) {
		if(SourceMap.containsKey(name.getName())) {
			log.info("已经存在数据源:" + name);
			return ;
		}
		SourceMap.put(name.getName(), RedisDataSource.create(initParam));
		log.info("创建数据源(Sentinel版本):" + name + "\tinitParam:" + initParam);
	}
	
	
	/**
	 * 移除指定数据源，然后进行创建数据源
	 * @param name			数据源名称
	 * @param initParam		redis初始化配置参数
	 */
	public void removeAndCreate(RedisDataSourceName name,RedisInitParam initParam){
		log.info("移除指定数据源，然后进行创建数据源 start....");
		RedisDataSource ds =  SourceMap.get(name.getName());
		if (ds != null) {
			log.info("移除[" + name.getName() + "]");
			SourceMap.remove(name.getName());
			ds.destroy();
		}
		create( name, initParam);
		log.info("移除指定数据源，然后进行创建数据源 end....");
	}
	
	
	/**
	 * 移除指定数据源，然后进行创建数据源(Sentinel模式)
	 * @param name			数据源名称
	 * @param initParam		redis初始化配置参数
	 */
	public void removeAndCreate(RedisDataSourceName name,RedisInitParamForSentinel initParam){
		log.info("(Sentinel模式)移除指定数据源，然后进行创建数据源 start....");
		RedisDataSource ds =  SourceMap.get(name.getName());
		if (ds != null) {
			log.info("(Sentinel模式)移除[" + name.getName() + "]");
			SourceMap.remove(name.getName());
			ds.destroy();
		}
		create( name, initParam);
		log.info("(Sentinel模式)移除指定数据源，然后进行创建数据源 end....");
	}
	
	/**
	 * 获取默认数据源
	 * @return
	 */
	public RedisDataSource getDefaultSource() {
		return SourceMap.get(RedisDataSourceName.DeFault.getName());
	}
	
	/**
	 * 获取指定数据源
	 * @param dataSourceName  数据源名称(若只有一个数据源，便获取默认数据源)
	 * @return
	 */
	public RedisDataSource getSource(RedisDataSourceName dataSourceName) {
		if(SourceMap.size() == 1) {
			//如果只有一个数据源，便获取默认数据源即可
			return getDefaultSource();
		}
		return SourceMap.get(dataSourceName.getName());
	}
	
	/**
	 * 从配置文件中加载1~N个数据源
	 * @param properties
	 *        default={"server":"192.168.1.26","port":6379,"password":"password001"}
	 * 		  international={"server":"192.168.1.22","port":6379,"password":"password001"}
	 */
	public void create(Properties properties) {
		Set<Object> keySet =  properties.keySet();
		if (keySet == null || keySet.isEmpty()) {
			return ;
		}
		for(Object key : keySet) {
			String value = properties.getProperty((String)key);
			RedisDataSourceName sourceName =  RedisDataSourceName.getRedisDataSourceName((String)key);
			if (StringUtil.isNullOrBlank(value) || sourceName == null) {
				throw new IllegalArgumentException(key + "=" + value);
			}
			RedisInitParam initParam = EasyJsonUtils.toJavaObject(value, RedisInitParam.class);
			if (initParam == null ) {
				throw new IllegalArgumentException("RedisInitParam is null");
			}
			if (StringUtil.isNullOrBlank(initParam.getServer())) {
				throw new IllegalArgumentException("Server is null");
			}
			create(sourceName,initParam);
		}
	}
	
}