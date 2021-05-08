package com.candao.spas.flow.core.utils;

import java.util.Hashtable;
import java.util.Map;

/**
 * 时间格式
 * 
 * @author lion.chen
 * @version 1.0.0 2017年7月26日 上午11:32:43
 */
public class FastDateFormat {
	/**
	 * yyyy-MM-dd
	 */
	public static final FastDateFormat DATE = new FastDateFormat("yyyy-MM-dd");
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final FastDateFormat DATE_TIME = new FastDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * yyyyMMdd_HHmmss
	 */
	public static final FastDateFormat DATE_TIME2 = new FastDateFormat("yyyyMMdd_HHmmss");
	/**
	 * yyyyMMdd_HHmmssS
	 */
	public static final FastDateFormat FULL_DATE_TIME2 = new FastDateFormat("yyyyMMdd_HHmmssS");
	
	/**
	 * yyyyMMdd_HHmmssS
	 */
	public static final FastDateFormat DATE_TIME_HOUR = new FastDateFormat("yyyyMMddHH");
	/**
	 * yyyy-MM-dd HH:mm:ss.S
	 */
	public static final FastDateFormat FULL_DATE_TIME = new FastDateFormat("yyyy-MM-dd HH:mm:ss.S");
	/**
	 * HH:mm
	 */
	public static final FastDateFormat MINI_TIME = new FastDateFormat("HH:mm");
	/**
	 * HH:mm:ss
	 */
	public static final FastDateFormat TIME = new FastDateFormat("HH:mm:ss");
	/**
	 * HH
	 */
	public static final FastDateFormat HOUR = new FastDateFormat("HH");
	/**
	 * mm
	 */
	public static final FastDateFormat MINUTE = new FastDateFormat("mm");
	/**
	 * ss
	 */
	public static final FastDateFormat SECOND = new FastDateFormat("ss");
	/**
	 * yyyy年MM月dd
	 */
	public static final FastDateFormat DATE_CN = new FastDateFormat("yyyy年MM月dd");
	/**
	 * yyyy年MM月dd日 HH时mm分ss秒
	 */
	public static final FastDateFormat DATE_TIME_CN = new FastDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
	/**
	 * HH时mm分
	 */
	public static final FastDateFormat MINI_TIME_CN = new FastDateFormat("HH时mm分");
	/**
	 * HH时mm分ss秒
	 */
	public static final FastDateFormat TIME_CN = new FastDateFormat("HH时mm分ss秒");
	/**
	 * yyyy年MM月dd日 HH时mm分ss.SSS
	 */
	public static final FastDateFormat FULL_DATE_TIME_CN = new FastDateFormat("yyyy年MM月dd日  HH时mm分ss.SSS");
	/**
	 * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 */
	public static final FastDateFormat UTC_FULL_DATE_TIME = new FastDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	/**
	 * yyyy-MM-dd'T'HH:mm:ss'Z'
	 */
	public static final FastDateFormat UTC_DATE_TIME = new FastDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final Map<String, FastDateFormat> appends = new Hashtable<String, FastDateFormat>();
	public String value;
	private FastDateFormat(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return value;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FastDateFormat) {
			return FastStringUtils.equals(((FastDateFormat) obj).value, this.value);
		}
		return super.equals(obj);
	}
	/**
	 * 获取默认时间格式
	 * 
	 * @return
	 */
	public static FastDateFormat getDefault() {
		return DATE_TIME;
	}
	/**
	 * 新加格式
	 * 
	 * @param value
	 * @return
	 */
	public static FastDateFormat getDateFormat(String value) {
		if (appends.containsKey(value)) {
			return appends.get(value);
		}
		FastDateFormat f = new FastDateFormat(value);
		appends.put(value, f);
		return f;
	}
}