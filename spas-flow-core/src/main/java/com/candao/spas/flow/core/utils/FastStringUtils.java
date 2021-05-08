package com.candao.spas.flow.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 扩展StringUtils类 
 * lion.chen
 * 
 * <b>修改历史:</b> <br/>
 *  version 1.0.0 2014-6-9 LionChen Initial Version <br/>
 */
public class FastStringUtils extends StringUtils{
	/**
	 * 手机正则
	 */
	private static final String mobile_regex = "^1[2-9][\\d+]{9}$";
	
	private static final String SEARCH_STR_$ = "${%s}";
	private static final String SEARCH_STR_PLACEHOLDER = "{%s}";
	private static final String IDS_REGEX = "\\d+|((\\d+\\,)+\\d+)";
	private static final String IDS_SHARP_REGEX = "\\d+|((\\d+\\#)+\\d+)";
	private static final String BOOLEAN_REGEX = "^true|false|TRUE|FALSE$";
	private static final String blank_regex = "\\s";
	
	public static final String SPLIT_COMMA = ",";
	public static final String SPLIT_SEMICOLON = ";";
	public static final String SPLIT_SHARP = "#";
	public static final String SPLIT_UNDERLINE = "_";
	public static final String SPLIT_LINE = "-";
	public static final String SPLIT_VERTICAL = "|";
	public static final String SPLIT_AND = "&";
	public static final String SPLIT_EQUAL = "=";
	public static final String SPLIT_QUESTION_MARK = "?";
	public static final String SPLIT_PERCENT = "%";
	
	public static final String DOUBEL_REGEX = "^(\\-?|\\+?)(\\d+|\\d+\\.\\d+)$";
	public static final String INT_REGEX = "^(\\-?|\\+?)\\d+$";
	public static final String emoji_pattern = "[^\u0000-\uFFFF]";
	
	public static final String email_pattern = "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";
	
	/**
	 * 创建随机唯一uuid
	 * @return
	 */
	public static String createRandomUUID(){
		return createRandomUUID(true);
	}
	
	/**
	 * 创建随机唯一uuid
	 * @param isCleanLine
	 * @return
	 */
	public static String createRandomUUID(boolean isCleanLine){
		
		String r = UUID.randomUUID().toString();
		
		return isCleanLine ? r.replaceAll(SPLIT_LINE, EMPTY) : r;
	}
	
	/**
	 * 判断是否为手机号码
	 * 	-1开头第二位数为2-9,11位字符
	 * @param s
	 * @return
	 */
	public static final boolean isMobile(String s){
		return s != null && s.matches(mobile_regex);
	}
	
	/**
	 * 过滤emoji表情字符
	 * @param str
	 * @return
	 */
	public static String emojiFilter(String str){
		
		return emojiFilter(str, EMPTY);
	}
	
	/**
	 * 过滤emoji表情字符
	 * @param str
	 * @param reStr
	 * @return
	 */
	public static String emojiFilter(String str, String reStr){
		
		if(str == null) return EMPTY;
		
        return str.replaceAll(emoji_pattern, reStr);  
	}
	
	public static boolean isNum(String data){
		
		return data != null && data.matches(DOUBEL_REGEX);
	}
	
	public static boolean isBoolean(String value){
		
		return value != null && value.matches(BOOLEAN_REGEX);
	}
	
	public static boolean getBoolean(String value){
		
		return getBoolean(value, false);
	}
	
	public static int getInt(String value){
		return getInt(value, 0);
	}
	
	public static int getInt(String value, int defaultValue){
		return isNumeric(value) ? Integer.valueOf(value) : defaultValue;
	}
	
	public static boolean isInt(String value){
		return value != null && value.matches(INT_REGEX);
	}
	
	public static boolean getBoolean(String value, boolean defaultValue){
		
		return isBoolean(value) ? Boolean.valueOf(value) : defaultValue;
	}
	
	public static boolean isDouble(String value){
		boolean is = false;
		try{
			Double.parseDouble(value);
			is = true;
		}catch(Throwable e){
		}
		return is;
	}
	
	public static float getFloat(String value){
		return getFloat(value, 0f);
	}
	
	public static float getFloat(String value, float defaultValue){
		float v = defaultValue;
		try{
			v = Float.parseFloat(value);
		}catch(Throwable e){
		}
		return v;
	}
	
	public static double getDoubleValue(String value, int decimals){
		if(FastStringUtils.isDouble(value)){
			double d = Double.valueOf(value);
			return getDoubleValue(d, decimals);
		}else{
			return 0;
		}
	}
	
	public static boolean startsWith(final CharSequence str, final Collection<String> strs){
		
		if(isEmpty(str) || strs == null || strs.isEmpty()){
			return false;
		}
		
		for(String string : strs){
			if(startsWith(str, string)) return true;
		}
		
		return false;
	}
	
	public static boolean startsWith(final CharSequence str, final Set<String> strs){
		
		if(isEmpty(str) || strs == null || strs.isEmpty()){
			return false;
		}
		
		for(String string : strs){
			if(startsWith(str, string)) return true;
		}
		
		return false;
	}
	
	public static double getDoubleValue(double value, int decimals){
		double d = Double.valueOf(value);
		BigDecimal b = new BigDecimal(d);
		return b.setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static boolean isIds(String ids){
		return StringUtils.isNotEmpty(ids) && ids.matches(IDS_REGEX);
	}
	
	public static boolean isSharpIds(String ids){
		return StringUtils.isNotEmpty(ids) && ids.matches(IDS_SHARP_REGEX);
	}
	
	public static Integer[] getIdsOfInt(String ids){
		
		Integer[] result = {};
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			result = new Integer[tempIds.length];
			for(int i = 0; i < tempIds.length; i++){
				result[i] = Integer.valueOf(tempIds[i]);
			}
		}
		
		return result;
	}
	
	public static List<Integer> cleanIdsOfInt(List<Integer> ids){
		
		List<Integer> idList = new ArrayList<Integer>();
		
		if(ids == null || ids.isEmpty()){
			return idList;
		}
		
		for(Integer id : ids){
			if(id != null && id > 0){
				idList.add(id);
			}
		}
		
		return idList;
	}
	
	public static List<Integer> cleanIds(Integer[] ids){
		
		List<Integer> idList = new ArrayList<Integer>();
		
		if(ids == null || ids.length == 0){
			return idList;
		}
		
		for(Integer id : ids){
			if(id != null && id > 0){
				idList.add(id);
			}
		}
		
		return idList;
	}
	
	public static List<Long> cleanIds(Long[] ids){
		
		List<Long> idList = new ArrayList<Long>();
		
		if(ids == null || ids.length == 0){
			return idList;
		}
		
		for(Long id : ids){
			if(id != null && id > 0){
				idList.add(id);
			}
		}
		
		return idList;
	}
	
	public static List<Long> cleanIdsOfLong(List<Long> ids){
		
		List<Long> idList = new ArrayList<Long>();
		
		if(ids == null || ids.isEmpty()){
			return idList;
		}
		
		for(Long id : ids){
			if(id != null && id > 0){
				idList.add(id);
			}
		}
		
		return idList;
	}
	
	public static Long[] getIdsOfLong(String ids){
		
		Long[] result = {};
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			result = new Long[tempIds.length];
			for(int i = 0; i < tempIds.length; i++){
				result[i] = Long.valueOf(tempIds[i]);
			}
		}
		
		return result;
	}
	
	public static int[] getIdsOfMiniInt(String ids){
		
		int[] result = {};
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			result = new int[tempIds.length];
			for(int i = 0; i < tempIds.length; i++){
				result[i] = Integer.valueOf(tempIds[i]);
			}
		}
		
		return result;
	}
	
	public static long[] getIdsOfMiniLong(String ids){
		
		long[] result = {};
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			result = new long[tempIds.length];
			for(int i = 0; i < tempIds.length; i++){
				result[i] = Long.valueOf(tempIds[i]);
			}
		}
		
		return result;
	}
	
	
	public static List<Integer> getIdListOfInt(String ids){
		
		List<Integer> result = new ArrayList<Integer>();
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			for(String s : tempIds){
				result.add(Integer.valueOf(s));
			}
		}
		
		return result;
	}
	
	public static List<Long> getIdListOfLong(String ids){
		
		List<Long> result = new ArrayList<Long>();
		
		if(isIds(ids)){
			String[] tempIds = ids.split(SPLIT_COMMA);
			for(String s : tempIds){
				result.add(Long.valueOf(s));
			}
		}
		
		return result;
	}
	
	public static Integer[] getSharpIdsOfInt(String ids, boolean isClean){
		List<Integer> idList = getSharpIdListOfInt(ids, isClean);
		return idList.toArray(new Integer[idList.size()]);
	}
	
	public static List<Long> getSharpIdListOfLong(String ids){
		
		List<Long> result = new ArrayList<Long>();
		
		if(isSharpIds(ids)){
			String[] tempIds = ids.split(SPLIT_SHARP);
			for(String s : tempIds){
				result.add(Long.valueOf(s));
			}
		}
		
		return result;
	}
	
	public static List<Long> getSharpIdListOfLong(String ids, boolean isClean){
		
		List<Long> result = new ArrayList<Long>();
		
		if(isSharpIds(ids)){
			String[] tempIds = ids.split(SPLIT_SHARP);
			for(String s : tempIds){
				result.add(Long.valueOf(s));
			}
		}
		
		if(isClean){
			result = cleanIdsOfLong(result);
		}
		
		return result;
	}
	
	public static List<Integer> getSharpIdListOfInt(String ids){
		
		List<Integer> result = new ArrayList<Integer>();
		
		if(isSharpIds(ids)){
			String[] tempIds = ids.split(SPLIT_SHARP);
			for(String s : tempIds){
				result.add(Integer.valueOf(s));
			}
		}
		
		return result;
	}
	
	public static List<Integer> getSharpIdListOfInt(String ids, boolean isClean){
		
		List<Integer> result = new ArrayList<Integer>();
		
		if(isSharpIds(ids)){
			String[] tempIds = ids.split(SPLIT_SHARP);
			for(String s : tempIds){
				result.add(Integer.valueOf(s));
			}
		}
		
		if(isClean){
			result = cleanIdsOfInt(result);
		}
		
		return result;
	}
	
	public static List<String> newArrayList(String...strs){
		
		List<String> result = new ArrayList<String>();
		
		if(strs != null){
			for(String s : strs){
				if(s != null){
					result.add(s);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 字符串连接
	 * @param text
	 * @param strs
	 * @return
	 */
	public static String append(String text, String...strs){
		
		if(text == null || strs == null || strs.length == 0){
			return text;
		}
		
		StringBuffer stringBuffer = new StringBuffer(text);
		
		for(String s : strs){
			stringBuffer.append(s);
		}
		
		return stringBuffer.toString();
	}
	
	/**
	 * 替换 “{key}”占位符
	 * @param text
	 * @param key
	 * @param value
	 * @return
	 */
	public static String placeholder(String text, String key, String value){
		
		if(StringUtils.isEmpty(key)){
			return text;
		}
		
		return format(text, value, String.format(SEARCH_STR_PLACEHOLDER, key));
	}
	
	/**
	 * 格式化类似于 “${key}”表达式
	 * @param text
	 * @param paramMap
	 * @return
	 */
	public static String $format(String text, Map<String, Object> paramMap){
		
		if(StringUtils.isEmpty(text) || paramMap == null || paramMap.isEmpty()){
			return text;
		}
		
		String result = text;
		
		Iterator<String> iterator = paramMap.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			Object value = paramMap.get(key);
			result = $format(result, key, value == null ? StringUtils.EMPTY : String.valueOf(value));
		}
		
		return result;
	}
	
	/**
	 * 格式化类似于 “${key}”表达式
	 * @param text
	 * @param key
	 * @param value
	 * @return
	 */
	public static String $format(String text, String key, String value){
		
		if(StringUtils.isEmpty(key)){
			return text;
		}
		
		return format(text, value, String.format(SEARCH_STR_$, key));
	}
	
	/**
	 * 格式化字符串
	 * @param text
	 * @param replacement
	 * @param searchString
	 * @return
	 */
	public static String format(String text, String replacement, String searchString){
		
		if(isEmpty(text) || isEmpty(searchString)){
			return text;
		}
		
		replacement = replacement == null ? StringUtils.EMPTY : replacement;
		
		return StringUtils.replace(text, searchString, replacement);
	}
	
	/**
     * 查找正则字符串是否包含
     * 
     * @param str
     * @param regex
     * @return
     */
    public static boolean indexOfReg(String str, String regex) {
        if (StringUtils.isEmpty(str) || StringUtils.isEmpty(regex)) {
            return false;
        }
        return str.matches(regex);
    }
    
    /**
     * 字符串数组中获取对应规则的字符，返回第一个匹配的字符
     * 
     * @param
     * @param strs
     * @return
     */
    public static String getStrByArray(StringFilter stringFilter, String... strs) {
    	List<String> resultStr = getResultStrByArray(stringFilter, strs);
        return resultStr != null && !resultStr.isEmpty() ? resultStr.get(0) : null;
    }
    
    /**
     * 字符串数组中获取对应规则的字符索引，返回第一个匹配的字符索引
     * 
     * @param
     * @param strs
     * @return
     */
    public static int getStrIndexByArray(final String regex, String... strs) {
        return getStrIndexByArray(new StringFilter(){
			public boolean checkStr(String s) {
				return s.matches(regex);
			}}, strs);
    }
    
    public static String join(Iterable<?> iterable){
    	return join(iterable, SPLIT_COMMA, true);
    }
    
    public static String join(Iterable<?> iterable, boolean isAddNullOrEmpty){
    	return join(iterable, SPLIT_COMMA, isAddNullOrEmpty);
    }
    
    public static String join(Iterable<?> iterable, String separator, boolean isAddNullOrEmpty){
    	
    	String tempSeparator = separator == null ? StringUtils.EMPTY : separator;
    	List<String> tempList = new ArrayList<String>();
    	
    	if(iterable != null){
    		Iterator<?> iterator = iterable.iterator();
    		while(iterator.hasNext()){
    			Object next = iterator.next();
    			String str = next == null ? StringUtils.EMPTY : String.valueOf(next);
    			if(StringUtils.isNotEmpty(str) || (isAddNullOrEmpty && StringUtils.isEmpty(str))){
    				tempList.add(str);
    			}
    		}
    	}
    	
    	return join(tempList, tempSeparator);
    }
    
    public static String join(int[] array){
    	return join(array, SPLIT_COMMA);
    }
    
    public static String join(int[] array, String separator){
    	
    	if(array == null || array.length == 0){
    		return EMPTY;
    	}
    	
    	List<String> tempList = new ArrayList<String>();
    	for(int i : array){
    		tempList.add(String.valueOf(i));
    	}
    	
    	return join(tempList, separator);
    }
    
    public static String join(Integer[] array){
    	return join(array, SPLIT_COMMA, false);
    }
    
    public static String join(Integer[] array, boolean isAddNullOrEmpty){
    	return join(array, SPLIT_COMMA, isAddNullOrEmpty);
    }
    
    public static String join(Integer[] array, String separator){
    	
    	return join(array, separator, false);
    }
    
    public static String join(Integer[] array, String separator, boolean isAddNullOrEmpty){
    	
    	if(array == null || array.length == 0){
    		return EMPTY;
    	}
    	
    	List<String> tempList = new ArrayList<String>();
    	for(Integer i : array){
    		if(isAddNullOrEmpty == false && i == null) continue;
    		tempList.add(String.valueOf(i));
    	}
    	
    	return join(tempList, separator);
    }
    
    public static String join(long[] array){
    	return join(array, SPLIT_COMMA);
    }
    
    public static String join(long[] array, String separator){
    	
    	if(array == null || array.length == 0){
    		return EMPTY;
    	}
    	
    	List<String> tempList = new ArrayList<String>();
    	for(long i : array){
    		tempList.add(String.valueOf(i));
    	}
    	
    	return join(tempList, separator);
    }
    
    public static String join(Long[] array){
    	return join(array, SPLIT_COMMA, false);
    }
    
    public static String join(Long[] array, boolean isAddNullOrEmpty){
    	return join(array, SPLIT_COMMA, isAddNullOrEmpty);
    }
    
    public static String join(Long[] array, String separator){
    	
    	return join(array, separator, false);
    }
    
    public static String join(Long[] array, String separator, boolean isAddNullOrEmpty){
    	
    	if(array == null || array.length == 0){
    		return EMPTY;
    	}
    	
    	List<String> tempList = new ArrayList<String>();
    	for(Long i : array){
    		if(isAddNullOrEmpty == false && i == null) continue;
    		tempList.add(String.valueOf(i));
    	}
    	
    	return join(tempList, separator);
    }
    
    public static String join(String[] array){
    	return join(array, SPLIT_COMMA, false);
    }
    
    public static String join(String[] array, boolean isAddNullOrEmpty){
    	return join(array, SPLIT_COMMA, isAddNullOrEmpty);
    }
    
    public static String join(String[] array, String separator){
    	
    	return join(array, separator, false);
    }
    
    public static String join(String[] array, String separator, boolean isAddNullOrEmpty){
    	
    	if(array == null || array.length == 0){
    		return EMPTY;
    	}
    	
    	List<String> tempList = new ArrayList<String>();
    	for(String i : array){
    		if(isAddNullOrEmpty == false && i == null) continue;
    		tempList.add(String.valueOf(i));
    	}
    	
    	return join(tempList, separator);
    }
    
    /**
     * 字符串数组中获取对应规则的字符索引，返回第一个匹配的字符索引
     * 
     * @param
     * @param strs
     * @return
     */
    public static int getStrIndexByArray(StringFilter stringFilter, String... strs) {
    	int index = -1;
    	
    	if (strs != null && strs.length > 0 && stringFilter != null) {
            for (int i = 0,len = strs.length; i < len; i++) {
                if (stringFilter.checkStr(strs[i])) {
                	index = i;
                	break;
                }
            }
        }
    	
        return index;
    }
    
    /**
     * 返回字符串数组中获取对应规则的字符集合
     * 
     * @param
     * @param strs
     * @return
     */
    public static List<String> getResultStrByArray(StringFilter stringFilter, String... strs) {
    	List<String> list = new ArrayList<String>();
    	
        if (strs != null && strs.length > 0 && stringFilter != null) {
            for (String s : strs) {
                if (stringFilter.checkStr(s)) {
                	list.add(s);
                }
            }
        }
        return list;
    }
    
    /**
     * 清空所有空格
     * @param s
     * @return
     */
    public static String cleanBlank(String s){
    	
    	if(s == null){
    		return s;
    	}
    	
    	return s.replaceAll(blank_regex, EMPTY);
    }
    
    /**
     * 字符串数组中获取对应规则的字符,返回第一个匹配的字符
     * 
     * @param
     * @param strs
     * @return
     */
    public static String getStrByArray(final String regex, String... strs) {
    	if(regex == null){
    		
    		return null;
    	}
    	
        return getStrByArray(new StringFilter(){
			public boolean checkStr(String s) {
				return s.matches(regex);
			}}, strs);
    }
    public static boolean matches(String str, String regex){
    	
    	return regex == null ? true : str != null && str.matches(regex);
    }
    
    public static List<Serializable> toLongList(String str){
    	
    	return toLongList(str, SPLIT_COMMA);
    }
    
    public static boolean isEmpty(List<?> list){
    	
    	return !isNotEmpty(list);
    }
    
    public static boolean isNotEmpty(List<?> list){
    	
    	return list != null && !list.isEmpty();
    }
    
    public static void remove(List<?> list, Object...objects){
    	if(isNotEmpty(list) && objects != null && objects.length > 0){
    		list.removeAll(Arrays.asList(objects));
    	}
    }
    
    public static List<Integer> filterSameInt(List<Integer> list){
    	if(isNotEmpty(list)){
    		HashSet<Integer> h = new HashSet<Integer>(list);
    		return new ArrayList<Integer>(h);
    	}
    	return list;
    }
    
    public static List<Long> filterSameLong(List<Long> list){
    	if(isNotEmpty(list)){
    		HashSet<Long> h = new HashSet<Long>(list);
    		return new ArrayList<Long>(h);
    	}
    	return list;
    }
    
    public static List<String> filterSame(List<String> list){
    	if(isNotEmpty(list)){
    		HashSet<String> h = new HashSet<String>(list);
    		return new ArrayList<String>(h);
    	}
    	return list;
    }
    
    public static boolean isValid(Integer i){
    	return i != null && i > 0;
    }
    
    public static boolean isValid(Double i){
    	return i != null && i > 0;
    }
    
    public static boolean isValid(Long i){
    	return i != null && i > 0;
    }
    
    public static byte boolean2Byte(boolean b){
    	
    	return b ? (byte) 0 : (byte) 1;
    }
    
    public static List<Integer> getAddIds(List<Integer> oldIdList, List<Integer> newIdList){
		
		Map<Integer, Integer> oldMenuIdMap = oldIdList.stream().collect(Collectors.toMap(i -> i, i -> i));
		
		List<Integer> r = new ArrayList<Integer>();
		
		for(Integer id : newIdList) {
			if(!oldMenuIdMap.containsKey(id)) r.add(id);
		}
		
		return r;
	}
    
    /**
     * 字符串切割返回list
     * @param s
     * @return
     */
    public static List<String> splitList(String s){
    	return splitList(s, SPLIT_COMMA);
    }
    
    /**
     * 字符串切割返回list
     * @param s
     * @param regex
     * @return
     */
    public static List<String> splitList(String s, String regex){
    	
    	List<String> r = new ArrayList<String>();
    	if(isNotBlank(s)){
    		String[] split = s.split(regex);
    		for(String string : split){
    			r.add(string);
    		}
    	}
    	
    	return r;
    }
	
    public static List<Integer> getRemoveIds(List<Integer> oldIdList, List<Integer> newIdList){
		
		Map<Integer, Integer> newMenuIdMap = newIdList.stream().collect(Collectors.toMap(i -> i, i -> i));
		
		List<Integer> r = new ArrayList<Integer>();
		
		for(Integer id : oldIdList) {
			if(!newMenuIdMap.containsKey(id)) r.add(id);
		}
		
		return r;
	}
    
    public static String find(String source, String regex){
    	
    	List<String> list = findList(source, regex);
    	
    	return list != null && !list.isEmpty() ? list.get(0) : null;
    }
    
    public static List<String> findList(String source, String regex){
    	
    	List<String> result = new ArrayList<String>();
    	
    	if(isNotBlank(source) && isNotBlank(regex)){
    		
    		Pattern compile = Pattern.compile(regex);
    		Matcher matcher = compile.matcher(source);
    		
    		while(matcher.find()){
    			result.add(matcher.group());
    		}
    	}
    	
    	return result;
    }
    
    public static List<Serializable> toLongList(String str, String splitStr){
    	
    	List<Serializable> result = null;
    	
    	if(isNumeric(str)){
    		result = new ArrayList<Serializable>();
    		result.add(Long.valueOf(str));
    	}else{
    		if(splitStr != null){
    			result = new ArrayList<Serializable>();
    			String[] split = str.split(splitStr);
    			for(String s : split){
    				if(isNumeric(s)){
    					result.add(Long.valueOf(s));
    				}
    			}
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 数据流转换成字符串
     * @param is
     * @return
     * @throws IOException
     */
    public static String convertStreamToString(InputStream is) throws IOException{
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
    	int i = -1;
    	
        while((i = is.read()) != -1) baos.write(i);
       
        return baos.toString(); 
    }
    
    /**
     * 比对字符串规则接口
     * 
     * @author chenliaohua
     * 
     */
    public interface StringFilter {
        public boolean checkStr(String s);
    }
    
    /**
     * 邮箱正则
     * @param string
     * @return
     */
    public static boolean isEmail(String string){
    	return string != null && string.matches(email_pattern);
    }
    
    public static void main(String[] args) {
    	String REDIS_HASH_KEY = "basic:${system}:data:inpage:";
    	System.out.println(FastStringUtils.$format(REDIS_HASH_KEY, "system", "1"));
	}
}