package com.candao.spas.flow.redis.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
//	public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 日期格式化对象 格式为yyyy/MM/dd HH:mm
     */
    private static ThreadLocal<SimpleDateFormat> DATE_FORMATER_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    /**
     * 一天的毫秒数
     */
    public static final long DAY_MS = 1000 * 24 * 60 * 60L;
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String pattern1 = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd
     */
    public static final String pattern2 = "yyyy-MM-dd";
    public static final String pattern4 = "yyyyMMddHHmm";
    public static final String BEGIN_TIME = "yyyy-MM-dd 00:00:00";
    public static final String END_TIME = "yyyy-MM-dd 23:59:59";
    public static final String pattern5 = "HH:mm";
    /**
     * yyyy-MM-dd的正则表达式
     */
    private static final String yyyy_MM_dd_EL = "^(?:(?!0000)[0-9]{4}([-/.]?)(?:(?:0?[1-9]|1[0-2])([-/.]?)(?:0?[1-9]|1[0-9]|2[0-8])|(?:0?[13-9]|1[0-2])([-/.]?)(?:29|30)|(?:0?[13578]|1[02])([-/.]?)31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)([-/.]?)0?2([-/.]?)29)$";
    /**
     * HH:mm的正则表达式
     */
    private static final String HH_MM_EL = "^([01]\\d|2[01234]):([0-5]\\d|60)$";
    /**
     * 获取当前时间的指定格式字符串
     *
     * @param pattern-如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurTimeStr(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }
    public static void remove(){
        DATE_FORMATER_LOCAL.remove();
    }
    /**
     * 获取当前时间的默认字符串形式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getDefaultCurTimeStr() {
        return DateFormatThreadLocal.getDateFormat().format(new Date());
    }
    /**
     * 返回指定时间格式的毫秒数
     *
     * @param pattern-如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getTime(String timeStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(timeStr);
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
     * @param pattern 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getDate(String timeStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 获取今天为星期几,星期天为0；星期一为1；星期六为6
     *
     * @return
     */
    public static int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
    /**
     * 获取指定日期是星期几,星期天为0；星期一为1；星期六为6
     *
     * @return
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;
    }
    /**
     * 获取指定的日期为星期几,星期天为0；星期一为1；星期六为6
     *
     * @param timeStr-时间串          如：2014-10-20
     * @param pattern-如：yyyy-MM-dd
     * @return
     */
    public static int getDayOfWeek(String timeStr, String pattern) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(timeStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 判断当前时间是否在有效期内（注：时间必须为yyyy-MM-dd HH:mm:ss格式）
     *
     * @param startTime-有效期起始时间
     * @param endTime-有效期结束时间
     * @return
     */
    public static boolean isInActivityTime(String startTime, String endTime) {
        DateTime start = DateTime.parse(startTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        DateTime end = DateTime.parse(endTime, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        return start.isBeforeNow() && end.isAfterNow();
    }
    /**
     * 判断当前时间是否再两个时间段中
     * @param start
     * @param end
     * @param time
     * @return
     */
    public static boolean isInTime(Date start, Date end, Date time) {
        if (start == null || end == null || time == null) {
            return false;
        }
        long startTime = start.getTime();
        long endTime = end.getTime();
        long nowTime = time.getTime();
        return nowTime <= endTime && nowTime >= startTime;
    }
    public static boolean isInTime(String startTime, String endTime) {
        try {
            if (!StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                Calendar now = Calendar.getInstance();
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(df.parse(startTime));
                end.setTime(df.parse(endTime));
                start.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                end.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                if (end.before(start)) {
                    if (now.before(end)) {
                        start.add(Calendar.DATE, -1);
                    } else {
                        end.add(Calendar.DATE, 1);
                    }
                }
                if (now.after(start) && now.before(end)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 判断指定时间是否在营业时间
     *
     * @param startTime 时间格式HH:mm
     * @param endTime   时间格式HH:mm
     * @param timeStr   指定时间字符串 yyyy-MM-dd HH:mm
     * @return
     */
    public static boolean isInTime(String startTime, String endTime, String timeStr) {
        try {
            if (!StringUtils.isBlank(startTime) && !StringUtils.isBlank(endTime)) {
                DateFormat df = new SimpleDateFormat("HH:mm");
                Calendar now = Calendar.getInstance();
                long dateMil = DateUtil.getTime(timeStr, "yyyy-MM-dd HH:mm");
                now.setTimeInMillis(dateMil);
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                start.setTime(df.parse(startTime));
                end.setTime(df.parse(endTime));
                start.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                end.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                if (end.before(start)) {
                    if (now.before(end)) {
                        start.add(Calendar.DATE, -1);
                    } else {
                        end.add(Calendar.DATE, 1);
                    }
                }
//				start.add(Calendar.MINUTE, -1);
//				end.add(Calendar.MINUTE, 1);
                if ((now.equals(start) || now.equals(end)) || (now.after(start) && now.before(end))) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 将时间转换成最终格式
     *
     * @param dateString
     * @param pattern    比如：yyyy-MM-dd HH:mm:ss / yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static String toString(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(DateFormatThreadLocal.getDateFormat().parse(dateString));
    }
    /**
     * 获取间隔指定天数后的时间毫秒数
     *
     * @param days 间隔的天数，正数为往后，负数为往前
     * @return
     */
    public static long getTimeFrom(int days) {
        DateTime dt = new DateTime();
        dt = dt.plusDays(days).hourOfDay().setCopy(0).millisOfDay().setCopy(0).secondOfDay().setCopy(0);
        return dt.getMillis();
    }
    /**
     * 获取间隔指定天数后的时间字符串
     *
     * @param days
     * @param parttern
     * @return
     */
    public static String getDateBefore(int days, String parttern) {
        DateTime dt = new DateTime(getTimeFrom(days));
        return dt.toString(parttern);
    }
    public static String getDateBefore(int days) {
        return getDateBefore(days, "yyyy-MM-dd");
    }
    /**
     * 获取当前时间到凌晨的秒数
     *
     * @return
     */
    public static int getSecondUntilTomorrow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        // 第二天凌晨至当前时间的秒数
        int validTime = (int) ((cal.getTime().getTime() - new Date().getTime()) / 1000);
        return validTime;
    }
    /**
     * 获取今日日期
     *
     * @return
     */
    public static String getTodayDate() {
        Date date = new Date();
        String today = DateFormatThreadLocal.getDateFormat().format(date);
        String temp = "";
        if (!StringUtils.isBlank(today)) {
            temp = today.split(" ")[0];
        }
        return temp;
    }
    /**
     * 时间转换
     *
     * @param time yyyy-MM-dd HH:mm:ss:fff、yyyy-MM-dd /yyyy-M-dd、HH:mm
     * @param day  yyyy-MM-dd
     * @return yyyy-MM-dd HH:mm:ss
     * @author Guoden
     */
    public static String converterTime(String time, String day) {
        if (time != null) {
            try {
                if (time.length() > 19) { // yyyy-mm-dd HH:mm:ss:fff
                    time = DateFormatThreadLocal.getDateFormat().format(DateFormatThreadLocal.getDateFormat().parse(time));
                } else if (time.length() == 16 || time.length() == 15 || time.length() == 14 || time.length() == 13) {// yyyy-mm-dd HH:mm /yyyy-m-dd
                    // HH:mm/yyyy-m-d HH:mm
                    time = DateFormatThreadLocal.getDateFormat().format(DateFormatThreadLocal.getDateFormat2().parse(time));
                } else if (time.length() == 5) { // HH:mm
                    if (!StringUtils.isBlank(day)) {
                        time = day + " " + time + ":00";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return time;
    }
    /**
     * 校验时间格式
     *
     * @param time  -时间
     * @param regex - 正则表达式
     * @return
     */
    public static boolean checkTimeFormat(String time, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(time);
        return m.matches();
    }
    /**
     * 日期格式校验
     *
     * @param str    - 字符串
     * @param format - 格式 如 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static boolean isValidDate(String str, String format) {
        boolean convertSuccess = true;
        try {
            DateTime.parse(str, DateTimeFormat.forPattern(format));
        } catch (Exception e) {
            // e.printStackTrace();
            convertSuccess = false;
        }
        return convertSuccess;
    }
    /**
     * 获取当前月的第一天
     *
     * @param date
     * @return
     */
    public static String getFirstDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        String first = DATE_FORMATER_LOCAL.get().format(c.getTime());
        return first;
    }
    /**
     * 获取当前月的最后一天
     *
     * @param date
     * @return
     */
    public static String getLastDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        String last = DATE_FORMATER_LOCAL.get().format(c.getTime());
        return last;
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
     * 比较现有时间和字符串时间("17:30"格式，用于定时器)
     *
     * @param timeStr
     * @param timeRange
     * @return
     */
    public static boolean checkTime(String timeStr, int timeRange) {
        boolean flag = false;
        if (StringUtils.isBlank(timeStr)) {
            return false;
        }
        // 1.获取现在时间分钟数
        Calendar calendar = Calendar.getInstance();
        int currentMinute = calendar.get(Calendar.MINUTE);// 分
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);// 小时
        int currentTimeMinute = currentHour * 60 + currentMinute;
        // 2.获取比较时间分钟数
        String[] timeStrTemp = timeStr.split(":");
        if (!(timeStrTemp.length == 2)) {
            return false;
        }
        int hour = Integer.valueOf(timeStrTemp[0]);
        int minute = Integer.valueOf(timeStrTemp[1]);
        int recordTimeMinute = hour * 60 + minute;
        // 3.进行时差比较
        if (Math.abs(currentTimeMinute - recordTimeMinute) < timeRange) {
            flag = true;
        }
        return flag;
    }
    /**
     * 比较现有时间和字符串时间("17:30"格式，用于定时器) 默认时差为5分钟,能满足大部分的定时器需求
     *
     * @param timeStr
     * @return
     */
    public static boolean checkTimeDefault(String timeStr) {
        return checkTime(timeStr, 5);
    }
    /**
     * 判断结束日期-起始日期之间是否小于指定天数
     *
     * @param startTime "yyyy-MM-dd HH:mm:ss"
     * @param endTime   "yyyy-MM-dd HH:mm:ss"
     * @param day       时间跨度天数
     * @return
     * @author Guoden 2017年1月1日
     */
    public static boolean judgDay(String startTime, String endTime, int day) {
        DateTime start = DateTimeFormat.forPattern(DateUtil.pattern1).parseDateTime(startTime);
        DateTime end = DateTimeFormat.forPattern(DateUtil.pattern1).parseDateTime(endTime);
        double time = (double)(end.getMillis() - start.getMillis())/(24 * 60 * 60 * 1000);
        if (time > (double)day) {
            return false;
        }
        return true;
    }
    /**
     * 获取两个时间相差的天数 晚的时间减去早的时间 如 2017-06-01 早 2017-06-03 晚
     *
     * @param before 早的时间
     * @param after  晚的时间
     **/
    public static int getIntervalDays(DateTime before, DateTime after) {
        int beforeYear = before.getYear();
        int afterYear = after.getYear();
        int day = 0;
        for (int i = beforeYear; i < afterYear; i++) {
            if (beforeYear % 4 == 0) {
                day = day + 366;
                continue;
            }
            day = day + 365;
        }
        int count = after.getDayOfYear() - before.getDayOfYear();
        if (count < 0 || (beforeYear < afterYear && day > 0)) {
            count = count + day;
        }
        return count;
    }
    public static String format(Date createTime, String parttern) {
        DateTime dt = new DateTime(createTime);
        return dt.toString(parttern);
    }
    /**
     * 日期格式转换
     *
     * @param dateTime
     * @param patternFrom - dateTime的格式
     * @param patternTo   - 返回数据的格式
     * @return
     * @author davis
     */
    public static String parse(String dateTime, String patternFrom, String patternTo) {
        String date = dateTime;
        try {
            date = DateTime.parse(dateTime, DateTimeFormat.forPattern(patternFrom)).toString(patternTo);
        } catch (Exception e) {
            e.printStackTrace();
            return date;
        }
        return date;
    }
    /**
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDifferentMinute(Date startDate, Date endDate) {
        long a = endDate.getTime();
        long b = startDate.getTime();
        int c = Math.round((a - b) / (1000 * 60F));
        return c;
    }
    /**
     * 允许两个日期的最大跨度时间
     * @param begin
     * @param end
     * @param maxScope
     * @return
     */
    public static boolean maxScopeDay(String begin,String end,int maxScope){
        begin = begin.replaceAll("\\s.*","");
        end = end.replaceAll("\\s.*","");
        LocalDate beginDay = LocalDate.parse(begin,DateTimeFormat.forPattern(DateUtil.pattern2));
        LocalDate endDay = LocalDate.parse(end,DateTimeFormat.forPattern(DateUtil.pattern2));
        LocalDate maxBeginDay = beginDay.plusDays(maxScope-1);
        if ((maxBeginDay.isAfter(endDay)||maxBeginDay.equals(endDay))&&(beginDay.isBefore(endDay)||beginDay.equals(endDay))){
            return true;
        }
        return false;
    }
    /**
     * 分钟转秒数四舍五入
     * @param minutes 必填
     * @return
     */
    public static int minutesToSeconds(double minutes) {
    	BigDecimal seconds = new BigDecimal(String.valueOf(minutes))
                .multiply(new BigDecimal(60))
                .setScale(0, BigDecimal.ROUND_HALF_UP);
    	return seconds.intValue();
    }
    
    public static void main(String[] args) {
    	System.out.println(DateUtil.isValidDate("2019-07-08 23:59", pattern5));
    	System.out.println(DateUtil.isValidDate("23:59", pattern5));
    }
}