package com.candao.spas.flow.redis.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateFormatThreadLocal {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT2 = "yyyy-MM-dd HH:mm";
	
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();
    private static ThreadLocal<DateFormat> threadLocal2 = new ThreadLocal<DateFormat>();
    
    // 第一次调用get将返回null
    // 获取线程的变量副本，如果不覆盖initialValue，第一次get返回null，
    // 故需要初始化一个SimpleDateFormat，并set到threadLocal中
    public static DateFormat getDateFormat() {
        DateFormat df = (DateFormat) threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_FORMAT);  
            threadLocal.set(df);
        }
        return df;
    }
    
    public static DateFormat getDateFormat2(){
    	DateFormat df = (DateFormat) threadLocal2.get();
        if (df == null) {
            df = new SimpleDateFormat(DATE_FORMAT2);  
            threadLocal2.set(df);
        }
        return df;
    }
    public void removeDateFormat() {
        threadLocal.remove();
    }
    public void removeDateFormat2() {
        threadLocal2.remove();
    }
}