package com.candao.spas.flow.core.utils;

import org.apache.commons.lang3.StringUtils;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 字符串工具类
 * 
 * @author ljb
 * @version 1.0.0 2014-5-29 下午4:59:25
 */
public class StringUtil {
	private static final char PHONEFIRSTCHAR = '1';
	private static Random random = new Random();
	public static String nullIfEmpty(String str) {
		String s = str == null ? null : str.trim();
		if (isNullOrEmpty(s) || s.equals("null")) {
			return null;
		}
		return s;
	}
	/**
	 * 判断字符串是否为null或空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}
	/**
	 * 判断字符串是否为null或空字符串
	 * 
	 * @param str
	 * @return tt
	 */
	public static boolean isNullOrBlank(String str) {
		return str == null || "".equals(str.trim());
	}
	/**
	 * 是否存在空的参数
	 * 
	 * @param strs
	 * @return 存在空的参数，返回true，否则false
	 */
	public static boolean isExistsNullOrBlank(String... strs) {
		if (strs == null || strs.length <= 0) {
			return false;
		}
		for (String s : strs) {
			if (isNullOrBlank(s)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断数组array是null或empty
	 * 
	 * @param array
	 * @return
	 */
	public static boolean isNullOrEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
	/**
	 * 判断集合c是null或empty
	 * 
	 * @param c
	 * @return c == null || c.isEmpty()
	 */
	public static boolean isNullOrEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}
	/**
	 * 判断集合c既不是null也不是empty
	 * 
	 * @param c
	 * @return c != null && !c.isEmpty()
	 */
	public static boolean isNotNullEmpty(Collection<?> c) {
		return !isNullOrEmpty(c);
	}
	/**
	 * 判断Map m是null或empty
	 * 
	 * @return c == null || c.isEmpty()
	 */
	public static boolean isNullOrEmpty(Map<?, ?> m) {
		return m == null || m.isEmpty();
	}
	/**
	 * 如果str为null则返回空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String getEmptyIfNull(String str) {
		return str == null ? "" : str;
	}
	/**
	 * 如果str为null则返回空字符串，否则，去掉字符串两边的空格返回
	 * 
	 * @param str
	 * @return str == null ? "" : str.trim()
	 */
	public static String getEmptyOrTrim(String str) {
		return str == null ? "" : str.trim();
	}
	public static boolean isTrue(Boolean b) {
		return b != null ? b.booleanValue() : false;
	}
	/**
	 * 将时间字符串过滤成 hh:mm的形式
	 * 
	 * @param s-原始时间字符串，如：07:00      6:00 14:10:00
	 * @param defaultTimeStr-返回默认字符串
	 * @return
	 */
	public static String filterTohhmm(String s, String defaultTimeStr) {
		if (isNullOrEmpty(s)) {
			return defaultTimeStr;
		}
		if (s.length() == 4) {
			return "0" + s;
		}
		if (s.length() > 5) {
			return s.substring(0, 5);
		}
		return s;
	}
	/**
	 * 将指定的字符串s按照splitTag符号分割成list
	 * 
	 * @param s-需要分割的字符串
	 * @param splitTag-分割符
	 * @return
	 */
	public static List<String> splitToList(String s, String splitTag) {
		if (s == null || splitTag == null) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		String[] arr = s.split("[" + splitTag + "]");
		for (String ss : arr) {
			if (!isNullOrEmpty(ss)) {
				list.add(ss);
			}
		}
		return list;
	}
	/**
	 * 将List<T>转化成以splitTag为分割符拼接的字符串
	 * 
	 * @param list-需转换的list
	 * @param splitTag-分割符
	 * @return
	 */
	public static <T> String listToString(List<T> list, String splitTag) {
		if (list == null || list.isEmpty() || isNullOrBlank(splitTag)) {
			return "";
		}
		String s = "";
		for (T t : list) {
			s += String.valueOf(t) + splitTag;
		}
		if (s.endsWith(splitTag)) {// 移除最后一个分隔符
			s = s.substring(0, s.lastIndexOf(splitTag));
		}
		return s;
	}
	/**
	 * 将List<T>转化成以splitTag为分割符拼接的字符串
	 *
	 * @param list-需转换的list
	 * @param splitTag-分割符
	 * @return
	 */
	public static <T> String listToString(Collection<T> list, String splitTag) {
		if (list == null || list.isEmpty() || isNullOrBlank(splitTag)) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (T t : list) {
			builder.append(String.valueOf(t)).append(splitTag);
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}
	/**
	 * 删除emoji等字符
	 * 
	 * @param str
	 * @return
	 * @author Guoden
	 */
	public static String removeNonBmpUnicode(String str) {
		if (str == null) {
			return null;
		}
		str = str.replaceAll("[^\u0000-\uFFFF]", "");
		return str;
	}
	/**
	 * 验证邮箱
	 * 
	 * @param emails 多个用","分割
	 * @return
	 */
	public static boolean checkEmails(String emails) {
		List<String> list = StringUtil.splitToList(emails, ",");// 多个用",分割"
		if (list == null || list.size() == 0) {
			return false;
		}
		for (String email : list) {
			if (!checkEmail(email)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		boolean flag = false;
		try {
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
//			flag = matcher.matches() && emailValid(email);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	/**
	 * 将字符串转换成Integer集合
	 * 
	 * @param s        待拆分字符串
	 * @param splitTag 拆分符号
	 * @return
	 * @author guoden 2016年7月19日
	 */
	public static List<Integer> splitToIntegerList(String s, String splitTag) {
		if (s == null || splitTag == null) {
			return null;
		}
		List<Integer> ids = new ArrayList<Integer>();
		for (String str : s.split(splitTag)) {
			if (!isNullOrBlank(str)) {
				ids.add(Integer.valueOf(str));
			}
		}
		return ids;
	}
	public static String join(List<?> list, String separator) {
		if (list != null) {
			return StringUtils.join(list.toArray(), separator);
		} else {
			return "";
		}
	}
	/**
	 * 命名方式转换：下划线法转驼峰法(首字母大写)
	 * 
	 * @param name
	 * @return
	 */
	public static String lineToHump(String name) {
		if (isNullOrBlank(name)) {
			return name;
		}
		name = name.toLowerCase();
		Pattern pattern = Pattern.compile("_(\\w)");
		Matcher matcher = pattern.matcher(name);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}
	/**
	 * 将给定的字符前面补0，直到长度达到length
	 * 
	 * @param length
	 * @return
	 */
	public static String toString(String str, int length) {
		if (str == null || "".equals(str))
			return "";
		int len = length - str.length();
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < len; i++) {
			result.append(0);
		}
		result.append(str);
		return result.toString();
	}
	/**
	 * 判断字符串是否为整数(大部分情况可行)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+");
		return pattern.matcher(str).matches();
	}
	/**
	 * 判断字符串是否为浮点数(大部分情况可行)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isFloat(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+\\.[0-9]+");
		return pattern.matcher(str).matches();
	}
	/**
	 * 判断字符串是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (StringUtil.isNullOrBlank(str) || str.contains(" ")) {// 空字符串或包含空格
			return false;
		} else {
			String[] arr = str.split("[.]");
			if (str.contains(".") && arr.length != 2) {
				return false;
			}
			if (!isInteger(arr[0])) {
				return false;
			}
			if (arr[0].indexOf("0") == 0 && arr[0].length() > 1) {// 0开头并且整数部分位数大于1的过滤掉
				return false;
			}
		}
		boolean isExist = false;
		for (int i = str.length(); --i >= 0;) {
			char c = str.charAt(i);
			if (!Character.isDigit(c)) {
				// 只能包含一个点,并且点不能在开头或者结尾
				if (!isExist && ".".equals(String.valueOf(c)) && i != 0 && i != str.length()) {
					isExist = true;
					continue;
				}
				// 负数允许通过
				if ("-".equals(String.valueOf(c)) && i == 0 && str.length() > 1) {
					continue;
				}
				return false;
			} else if (((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')) && ((c < '0') || (c > '9'))) {// 过滤半角英文数字
				return false;
			}
		}
		return true;
	}
	/**
	 * 判断是否为日期格式 yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static boolean checkDate(String date) {
		boolean flag = false;
		try {
			String check = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(date);
			flag = matcher.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
	public static String toString(boolean rs) {
		return rs ? "成功" : "失败";
	}
	static final String SEED = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	static final AtomicInteger atoInt = new AtomicInteger();
	/**
	 * 取一定长度的随机字符串,以当前时间开头，前12位是日期+时间(yyMMddHHmmss)，3位AutoId，剩余随机位，建议长度大于18位
	 * 
	 * @param length
	 * @return
	 */
	public static String buildSerialNo(int length) {
		return buildSerialNo("yyMMddHHmmss", SEED, length);
	}
	/**
	 * 获取一定长度的随机字符串,以当前时间开头，前12位是日期+时间(yyMMddHHmmss)，3位AutoId，剩余随机位，建议长度大于18位
	 * 
	 * @param seed   指定随机字符串的范围
	 * @param length 指定字符串长度
	 * @return 一定长度的字符串
	 */
	public static String buildSerialNo(String seed, int length) {
		return buildSerialNo("yyMMddHHmmss", seed, length);
	}
	/**
	 * 获取一定长度的随机字符串,以当前时间开头，前n位根据时间格式生成，3位AutoId，剩余随机位，建议长度大于18位
	 * 
	 * @param pattern 前缀时间的格式，建议预留2位随机位
	 * @param seed    指定随机字符串的范围
	 * @param length  指定字符串长度
	 * @return 一定长度的字符串
	 */
	public static String buildSerialNo(String pattern, String seed, int length) {
		String timeStr = FastDateUtils.getCurTimeStr(pattern);
		StringBuilder sb = new StringBuilder(length).append(timeStr);
		String atoString = new DecimalFormat("000").format(atoInt.incrementAndGet() % 1000);
		sb.append(atoString);
		int base = timeStr.length() + atoString.length();
		for (int i = base; i < length; i++) {
			int num = random.nextInt(seed.length());
			sb.append(seed.charAt(num));
		}
		return sb.toString();
	}
	/**
	 * 判断字符串长度(中文1个长度，英文数字0.5个长度)
	 * 
	 * @param string
	 * @return
	 * @author Sunny 2018年11月14日
	 */
	public static double countStringLengthBetweenCHAndEN(String string) {
		Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
		double len = 0;
		for (int i = 0; i < string.length(); i++) {
			char charAt = string.charAt(i);
			boolean matches = pattern.matcher(String.valueOf(charAt)).matches();
			if (matches) {
				len += 1;
			} else {
				len += 0.5;
			}
		}
		return len;
	}
	/**
	 * 是否纯数字
	 * 
	 * @param str
	 * @return
	 * @author Sunny 2018年11月19日
	 */
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		return pattern.matcher(str).matches();
	}
	/**
	 * 是否含有特殊字符
	 * 
	 * @param str
	 * @return
	 * @author Sunny 2018年11月26日
	 */
	public static boolean isContainSpecialChar(String str) {
		// /\'*&^"#!$
		String[] special = { "/", "\\", "'", "*", "&", "^", "\"", "#", "!", "$" };
		List<String> specialList = Arrays.asList(special);
		for (String ss : specialList) {
			if (str.contains(ss)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 字符串是否两位小数
	 * 
	 * @param str
	 * @return
	 * @author Sunny 2018年11月14日
	 */
	public static boolean isTwoDecimal(String str) {
		Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
		return pattern.matcher(str).matches();
	}
	/**
	 * 是否隐私号码
	 * 
	 * @param phone 手机号码
	 * @return true-是 false-否
	 */
	public static boolean isPrivacyPhone(String phone) {
		return !openPhone(phone);
	}
	/**
	 * 非隐私号码
	 * 
	 * @param phone
	 * @return
	 */
	private static boolean openPhone(String phone) {
		if (StringUtil.isNullOrBlank(phone)) {
			throw new RuntimeException("手机号码不能为空");
		}
		return phone.length() == 11 && phone.charAt(0) == PHONEFIRSTCHAR;
	}
	public static void main(String[] args) {
		// String[] special = {"/","\\","'","*","&","^","\"","#","!","$"};
		// System.out.println(isContainSpecialChar("ビジネスの機会"));
//		String s = "１２３４５６";
		// String s = "45.03";
		// System.out.println(isNumeric(s));
//		System.out.println(listToString(Arrays.asList("joneli@51wm.com", "a"), ","));
		// List<Integer> list = new ArrayList<Integer>();
//		list.add(1);
//		list.add(2);
//		list.add(3);
		// System.out.println(listToString(list, "|"));
		// System.out.println(StringUtil.isNullOrBlank(null));
		// System.out.println(StringUtil.isNullOrBlank(""));
		// System.out.println(StringUtil.isNullOrBlank(" "));
		// System.out.println(StringUtil.isNullOrBlank("afaf"));
//		String seed="0123456789";
//		Set<String> set=new HashSet<>();
//		for (int i = 0; i < 1000; i++) {
////			String sn=buildSerialNo(seed, 18);
//			String sn=buildSerialNo(32);
//			System.out.println(sn);
//			if(set.contains(sn)) {
//				System.out.println("出现重复！！！！"+sn);
//			}
//			set.add(sn);
//		}
		// System.out.println(buildSeriaNoByRedis("11", 18));
//		System.out.println(isContainSpecialChar("我99"));
//        for (int i = 0; i < 10; i++) {
//            System.out.println((int)(Math.random()*10));
//        }
	}
}
//