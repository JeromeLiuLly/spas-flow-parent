package com.candao.spas.flow.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间帮助类
 * 
 * @author jameslei
 * @version 1.0.0 2017年4月26日 下午7:06:59
 */
public class FastDateUtils extends DateUtils {
	/** 锁对象 */
	private static final Object lockObj = new Object();
	/** 存放不同的日期模板格式的sdf的Map */
	private static final Map<FastDateFormat, ThreadLocal<SimpleDateFormat>> dateFormatMap = new HashMap<FastDateFormat, ThreadLocal<SimpleDateFormat>>();
	public static final long minutes_1 = 1 * 60 * 1000;
	public static final long minutes_3 = 3 * 60 * 1000;
	public static final long minutes_5 = 5 * 60 * 1000;
	public static final long minutes_10 = 10 * 60 * 1000;
	public static final long minutes_15 = 15 * 60 * 1000;
	public static final long minutes_20 = 20 * 60 * 1000;
	public static final long minutes_30 = 30 * 60 * 1000;
	public static final long minutes_45 = 45 * 60 * 1000;
	public static final long hours_1 = 1 * 60 * 60 * 1000;
	public static final long hours_2 = 1 * 60 * 60 * 1000;
	public static final long hours_3 = 1 * 60 * 60 * 1000;
	/**
	 * 是否为Timestamp
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Timestamp isTimestamp(String dateStr) {
		Date date = isDate(dateStr);
		return date != null ? new Timestamp(date.getTime()) : null;
	}
	/**
	 * 是否为UTC时间格式
	 * 
	 * @param dataStr
	 * @return
	 */
	public static Date isUTCDate(String dateStr) {
		if (FastStringUtils.isBlank(dateStr)) {
			return null;
		}
		String newDateStr = FastStringUtils.trimToEmpty(dateStr);
		Date date = null;
		try {
			int len = newDateStr.length();
			if (len == 20) {
				date = getDate(newDateStr, FastDateFormat.UTC_DATE_TIME);
			}
			else if (len == 24) {
				date = getDate(newDateStr, FastDateFormat.UTC_FULL_DATE_TIME);
			}
		} catch (Throwable e) {
			// 转换异常则代表不是时间格式字符串
		}
		return date;
	}
	/**
	 * 是否为时间格式 -格式包括(yyyy-MM-dd;yyyy-MM-dd HH:mm:ss;yyyy-MM-dd HH:mm:ss.S)
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date isDate(String dateStr) {
		if (FastStringUtils.isBlank(dateStr)) {
			return null;
		}
		String newDateStr = FastStringUtils.trimToEmpty(dateStr);
		Date date = null;
		try {
			int len = newDateStr.length();
			// yyyy-MM-dd
			if (len == 10)
				date = getDate(newDateStr, FastDateFormat.DATE);
			// yyyy-MM-dd HH:mm:ss
			else if (len == 19)
				date = getDate(newDateStr, FastDateFormat.DATE_TIME);
			// yyyy-MM-dd HH:mm:ss.S
			else if (len == 21)
				date = getDate(newDateStr, FastDateFormat.FULL_DATE_TIME);
		} catch (Throwable e) {
			// 转换异常则代表不是时间格式字符串
		}
		return date;
	}
	/**
	 * 是否为时间格式 -格式(HH:mm:ss)
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Date isTime(String dateStr) {
		if (FastStringUtils.isBlank(dateStr)) {
			return null;
		}
		String newDateStr = FastStringUtils.trimToEmpty(dateStr);
		Date date = null;
		try {
			int len = newDateStr.length();
			if (len == 8)
				date = getDate(newDateStr, FastDateFormat.TIME);
		} catch (Throwable e) {
			// 转换异常则代表不是时间格式字符串
		}
		return date;
	}
	/**
	 * 第一次调用get将返回null<br/>
	 * 获取线程的变量副本，如果不覆盖initialValue，第一次get返回null，<br/>
	 * 故需要初始化一个SimpleDateFormat，并set到threadLocal中<br/>
	 * 
	 * @return
	 */
	public static DateFormat getDateFormat() {
		return getDateFormat(FastDateFormat.DATE_TIME);
	}
	public static DateFormat getUTFDateFormat() {
		return getDateFormat(FastDateFormat.UTC_DATE_TIME);
	}
	/**
	 * 第一次调用get将返回null<br/>
	 * 获取线程的变量副本，如果不覆盖initialValue，第一次get返回null，<br/>
	 * 故需要初始化一个SimpleDateFormat，并set到threadLocal中<br/>
	 * 
	 * @param fastDateFormat
	 * @return
	 */
	public static synchronized DateFormat getDateFormat(FastDateFormat fastDateFormat) {
		if (fastDateFormat == null)
			return null;
		ThreadLocal<SimpleDateFormat> tl = dateFormatMap.get(fastDateFormat);
		if (tl == null) {
			synchronized (lockObj) {
				tl = dateFormatMap.get(fastDateFormat);
				if (tl == null) {
					tl = new ThreadLocal<SimpleDateFormat>() {
						@Override
						protected SimpleDateFormat initialValue() {
							SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fastDateFormat.value);
							if (fastDateFormat.equals(FastDateFormat.UTC_FULL_DATE_TIME)
									|| fastDateFormat.equals(FastDateFormat.UTC_DATE_TIME))
								simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
							return simpleDateFormat;
						}
					};
					dateFormatMap.put(fastDateFormat, tl);
				}
			}
		}
		return tl.get();
	}
	/**
	 * 获取当前时间的指定格式字符串
	 * 
	 * @param pattern-如：yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurTimeStr(FastDateFormat dateFormat) {
		return getDateFormat(dateFormat).format(new Date());
	}
	/**
	 * 获取当前时间的默认字符串形式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getDefaultCurTimeStr() {
		return getDateFormat().format(new Date());
	}
	/**
	 * 格式化时间
	 * 
	 * @param createTime
	 * @param dateFormat
	 * @return
	 */
	public static String format(Date createTime, FastDateFormat dateFormat) {
		if (createTime == null)
			return FastStringUtils.EMPTY;
		DateTime dt = new DateTime(createTime);
		return dt.toString(dateFormat.value);
	}
	/**
	 * 返回指定时间格式的毫秒数
	 * 
	 * @param pattern - FastDateFormat
	 * @return
	 */
	public static long getTime(String timeStr, FastDateFormat dateFormat) {
		try {
			Date date = getDateFormat(dateFormat).parse(timeStr);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 返回指定时间格式的日期Date
	 * 
	 * @param timeStr
	 * @param pattern FastDateFormat
	 * @return
	 */
	public static Date getDate(String timeStr, FastDateFormat dateFormat) {
		try {
			return getDateFormat(dateFormat).parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 获取某个时间加天数或者减天数的日期
	 * 
	 * @param date
	 * @param days 加天数，次为正数，减天数为负数
	 * @return
	 */
	public static Date getDateAfter(Date date, int days) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);// 把日期往后增加一天.整数往后推,负数往前移动
		return calendar.getTime();
	}
	/**
	 * 获取某个时间加分钟数或者减分钟数的日期
	 * 
	 * @param date
	 * @param minutes 加分钟数为正数，减为负数
	 * @return
	 */
	public static Date getDateAfterByMinutes(Date date, int minutes) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);// 整数往后推,负数往前移动
		return calendar.getTime();
	}
	/**
	 * 根据 date 对象格式化时间，返回字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		if (date == null)
			return FastStringUtils.EMPTY;
		return getDateFormat(FastDateFormat.DATE_TIME).format(date);
	}
	/**
	 * 根据 毫秒数 getTime 格式化时间，返回字符串
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDate(Long time) {
		if (time == null || time <= 0)
			return FastStringUtils.EMPTY;
		Date formatDate = new Date(time);
		return getDateFormat(FastDateFormat.DATE_TIME).format(formatDate);
	}
	/**
	 * 根据 timestamp 对象格式化时间，返回字符串
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String formatTimestamp(Timestamp timestamp) {
		return formatDate(timestamp.getTime());
	}
	/**
	 * 获取一个当前的时间的 timestamp
	 * 
	 * @return
	 */
	public static Timestamp getTimeStamp() {
		return new Timestamp(new Date().getTime());
	}
	/**
	 * 获取今天，格式为yyyy-HH-dd
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Date getToday() {
		Date date = new Date();
		String strToDay = getDateFormat(FastDateFormat.DATE).format(date) + " 00:00:00"; // 格式为 2017-07-24 00:00:00
		return isDate(strToDay);
	}
	// 转换为秒
	public static long toSecond(long diffTime) {
		return diffTime / 1000;
	}
	public static long toMinutes(long diffTime) {
		return diffTime / (1000 * 60);
	}
	public static double toPreciseMinutes(long diffTime) {
		return diffTime / (1000 * 60.0);
	}
	public static long toHours(long diffTime) {
		return diffTime / (1000 * 60 * 60);
	}
	public static double toPreciseHours(long diffTime) {
		return diffTime / (1000 * 60 * 60.0);
	}
	public static String getCurTimeStr(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	/**
	 * 比较时间大小
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static int compareTo(Date end, Date begin) {
		if (begin == null && end == null)
			return 0;
		else if (end == null)
			return -1;
		else if (begin == null)
			return 1;
		else
			return end.compareTo(begin);
	}
	/**
	 * 比较时间大小,end比较大或者相等的话返回true
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean compareDate(Date end, Date begin) {
		return compareTo(end, begin) < 0 ? false : true;
	}
	public static long getSecond(Date begin, Date end) {
		return toSecond(getDiff(begin, end));
	}
	public static long getDiffHours(Date begin, Date end) {
		return toHours(getDiff(begin, end));
	}
	public static double getPreciseDiffHours(Date begin, Date end) {
		return toPreciseHours(getDiff(begin, end));
	}
	public static long getDiffMinutes(Date begin, Date end) {
		return toMinutes(getDiff(begin, end));
	}
	public static double getPreciseDiffMinutes(Date begin, Date end) {
		return toPreciseMinutes(getDiff(begin, end));
	}
	public static long getDiff(Date begin, Date end) {
		if (begin == null || end == null)
			return 0;
		long begin_time = begin.getTime();
		long end_time = end.getTime();
		return end_time - begin_time;
	}
	/*
	 * 毫秒转化时分秒毫秒
	 */
	public static String formatMillisecond(Long ms) {
		Integer ss = 1000;
		Integer mi = ss * 60;
		Integer hh = mi * 60;
		Integer dd = hh * 24;
		Long day = ms / dd;
		Long hour = (ms - day * dd) / hh;
		Long minute = (ms - day * dd - hour * hh) / mi;
		Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}
	/**
	 * 根据毫秒数 转为可读时间 -> 15:00:05 格式
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String getDateFormatString(long millisecond) {
		millisecond = millisecond / 1000; // -> 秒
		long hour = millisecond / 3600, minute = millisecond / 60 % 60, second = millisecond % 60;
		return formatLong(hour) + ":" + formatLong(minute) + ":" + formatLong(second);
	}
	private static String formatLong(long longTime) {
		return longTime < 10 ? "0" + longTime : String.valueOf(longTime);
	}
	/**
	 * 传入参数 beginDate 格式为 2017-11-15 格式字符串 ，统一转换为查询专用格式Date
	 * 
	 * @param beginDateStr
	 * @return
	 */
	public static Date getBeginDate(String beginDateStr) {
		if (StringUtils.isNotEmpty(beginDateStr))
			return FastDateUtils.isDate(beginDateStr + " 00:00:00");
		return null;
	}
	/**
	 * 传入参数 endDateStr 格式为 2017-11-15 格式字符串 ，统一转换为查询专用格式Date
	 * 
	 * @param endDateStr
	 * @return
	 */
	public static Date getEndDate(String endDateStr) {
		if (StringUtils.isNotEmpty(endDateStr))
			return FastDateUtils.isDate(endDateStr + " 23:59:59");
		return null;
	}
	/**
	 * 去掉时间的秒数 2019-01-07 17:04:01 -> 2019-01-07 17:04:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date missDatess(Date date) {
		String second = FastDateUtils.getDateFormat(FastDateFormat.SECOND).format(date);
		if ("00".equals(second)) {
			return date;
		} else {
			String strToDay = FastDateUtils.getDateFormat(FastDateFormat.DATE).format(date) + " "
					+ FastDateUtils.getDateFormat(FastDateFormat.MINI_TIME).format(date) + ":00";
			return FastDateUtils.isDate(strToDay);
		}
	}
}