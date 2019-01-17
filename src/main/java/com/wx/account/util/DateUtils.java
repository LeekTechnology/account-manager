package com.wx.account.util;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 处理日期和时间转换函数的工具类
 *
 * @author changjinlin
 * @version 2.0.0 2016-11-08
 */
public class DateUtils {

    public static String DATE_MONTH = "yyyy.MM";
    public static String DATE_REQ_PATTERNs = "yyyyMM";
    public static String DATE_REQ_PATTERN = "yyyyMMdd";
    public static String DATE_DB_PATTERN = "yyyy-MM-dd";
    public static String TIME_REQ_PATTERN = "yyyyMMddHHmmss";
    public static String TIME_DB_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String START_STAT_DATE_TIME = "00:00:00";   //统计起始时间
    public static final String END_STAT_DATE_TIME = "23:59:59";       //统计结束时间

    public static final String SEPERATE_LINE = "-";
    public static final String SEPERATE_COLON = ":";
    public static final String SEPERATE_DOT = ",";

    /**
     * 获取数据库存储格式的时间,主要用于存储actiontime和createtime例:20161108111111
     *
     * @return
     */
    public static Long getDBTime() {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(TIME_REQ_PATTERN);

        return Long.parseLong(dateFormat.format(date));
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static Long getDBDate() {

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_REQ_PATTERN);

        return Long.parseLong(dateFormat.format(date));
    }

    /**
     * 获取指定格式的日期.
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDate(Date date, String format) {

        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date now() {
        return new Date();
    }

    public static String formatDateToDB(String dateStr, String reqPattern, String dbPattern) {

        SimpleDateFormat df = new SimpleDateFormat(reqPattern);

        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            return dateStr;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dbPattern);

        return sdf.format(date);
    }

    public static Date formatStr2Date(String dateStr, String format) {

        SimpleDateFormat df = new SimpleDateFormat(format);

        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            return date;
        }
        return date;
    }

    public static String formatTimeToDB(String dateStr) {
        return formatDateToDB(dateStr, TIME_REQ_PATTERN, TIME_DB_PATTERN);
    }

    public static String formatTimeToClient(Long timestamp, String format) {

        Date dt = new Date(timestamp);
        DateFormat df = new SimpleDateFormat(format);

        return df.format(dt);
    }

    /**
     * 格式化数据
     *
     * @param dateStr
     * @return
     */
    public static String formatDateToDB(String dateStr) {

        return formatDateToDB(dateStr, DATE_REQ_PATTERN, DATE_DB_PATTERN);

    }

    public static Long getCurrentDateTimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hour);
        return formatDateTime(calendar.getTime(), TIME_REQ_PATTERN);
    }

    public static Long getDateTimeByDay(Long baseTime, String baseTimePattern,
                                        int day, String pattern) {
        try {
            Date date = new SimpleDateFormat(baseTimePattern).parse(baseTime.toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, day);
            return formatDateTime(calendar.getTime(), pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static Long formatDateTime(Date date, String pattern) {
        return Long.parseLong(new SimpleDateFormat(pattern).format(date));
    }

    public static Long getDateTimeByDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return formatDateTime(calendar.getTime(), DATE_REQ_PATTERN);
    }

    public static Long getDateTimeByDay(Long baseTime, int day) {
        try {
            Date date = new SimpleDateFormat(TIME_REQ_PATTERN).parse(String.valueOf(baseTime));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, day);
            return formatDateTime(calendar.getTime(), DATE_REQ_PATTERN);
        } catch (Exception e) {
            return null;
        }

    }

    public static String timestamp2String(Timestamp timestamp, String format) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String result = null;
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            result = sdf.format(ts);
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static Integer calWorkAge(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_REQ_PATTERN);
        Calendar calendar = Calendar.getInstance();
        long nowDate = calendar.getTime().getTime();
        try {
            long specialDate = sdf.parse(dateString).getTime();
            Long days = (nowDate - specialDate) / (1000 * 60 * 60 * 24 * 365);
            return days.intValue();

        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取指定天的最小时间.
     *
     * @param calendar
     * @return
     */
    public static Date minTimeInDay(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        Date ret = calendar.getTime();
        return ret;
    }

    /**
     * 获取指定天的最大时间.
     *
     * @param calendar
     * @return
     */
    public static Date maxTimeInDay(Calendar calendar) {
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 59);
        Date ret = calendar.getTime();
        return ret;
    }

    /**
     * 把时间段信息转换成long型日期格式.
     *
     * @param currentDate 当前日期
     * @param periodTime
     * @return
     */
    public static List<Long> parsePeriodTime(Long currentDate, String periodTime) {
        //13:09-15:00
        List<Long> result = new ArrayList<Long>();
        String[] arr = periodTime.split(SEPERATE_LINE);
        String[] oneArr = arr[0].split(SEPERATE_COLON);
        String[] twoArr = arr[1].split(SEPERATE_COLON);

        String dateStr = currentDate.toString();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, CommonUtils.string2Int(dateStr.substring(0, 4)));
        calendar.set(Calendar.MONTH, CommonUtils.string2Int(dateStr.substring(4, 6)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, CommonUtils.string2Int(dateStr.substring(6, 8)));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(oneArr[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(oneArr[1]));
        calendar.set(Calendar.SECOND, 0);
        result.add(DateUtils.formatDateTime(calendar.getTime(), DateUtils.TIME_REQ_PATTERN));

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(twoArr[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(twoArr[1]));
        calendar.set(Calendar.SECOND, 0);
        result.add(DateUtils.formatDateTime(calendar.getTime(), DateUtils.TIME_REQ_PATTERN));

        return result;
    }

    /**
     * 从long型日期中摘取小时分钟时间.
     *
     * @param datetime
     * @return
     */
    public static String parseperiodTime(Long datetime) {
        String str = datetime.toString();
        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(8, 10));
        builder.append(SEPERATE_COLON);
        builder.append(str.substring(10, 12));
        return builder.toString();
    }

    /**
     * 计算两个日期时间相差的分钟数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Integer calDiffMinutes(Long startTime, Long endTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatStr2Date(startTime.toString(), TIME_REQ_PATTERN));
        Long min = calendar.getTimeInMillis();

        calendar.setTime(formatStr2Date(endTime.toString(), TIME_REQ_PATTERN));
        Long max = calendar.getTimeInMillis();
        return (int) ((max - min) / (1000 * 60));
    }

    public static Long parseMinTimeForDate(Long longDate) {
        String dateStr = longDate.toString() + "000000";
        return Long.parseLong(dateStr);
    }

    public static Long parseMaxTimeForDate(Long longDate) {
        String dateStr = longDate.toString() + "235959";
        return Long.parseLong(dateStr);
    }

    /**
     * 按照最小时间和最大时间搜索考勤记录.
     *
     * @param checkTimeList
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<Long> searchCheckTime(List<Long> checkTimeList, Long startTime, Long endTime) {
        List<Long> result = new ArrayList<Long>();
        checkTimeList.forEach(deviceRealtime -> {
            if (deviceRealtime >= startTime && deviceRealtime <= endTime) {
                result.add(deviceRealtime);
            }
        });
        return result;
    }

    /**
     * 把日数字转成long型的日期返回.
     *
     * @param day
     * @param format
     * @return
     */
    public static Long formatDayToLongDate(int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return formatDateTime(calendar.getTime(), format);
    }

    /**
     * 设置年月信息.
     *
     * @param calendar
     * @param queryDate
     * @return
     */
    public static Calendar editYearMonth(Calendar calendar, Long queryDate) {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        int year = CommonUtils.string2Int(queryDate.toString().substring(0, 4));
        int month = CommonUtils.string2Int(queryDate.toString().substring(4, 6));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar;
    }

    /**
     * 根据天数计算工龄.
     *
     * @return
     */
    public static Integer calWorkAgeByDays(Long workDays) {
        return Long.valueOf(workDays / ConstantUtils.ANNUAL_DAYS).intValue();
    }

    /**
     * 计算两个时间段相差的天数.
     *
     * @return
     */
    public static Long calDiffDays(Long startDate, Long endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatStr2Date(startDate.toString(), DATE_REQ_PATTERN));
        Long startTime = calendar.getTimeInMillis();

        calendar.setTime(formatStr2Date(endDate.toString(), DATE_REQ_PATTERN));
        Long endTime = calendar.getTimeInMillis();

        return (endTime - startTime) / (3600 * 24 * 1000);
    }

    /**
     * 计算月份相差月数
     *
     * @param startMonth
     * @param endMonth
     * @return
     */
    public static Integer calDiffMonth(Long startMonth, Long endMonth) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(formatStr2Date(startMonth.toString()+"01", DATE_REQ_PATTERN));
        c2.setTime(formatStr2Date(endMonth.toString()+"01", DATE_REQ_PATTERN));
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
         // 获取年的差值 
        int yearInterval = year1 - year2;
         // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2)
        yearInterval--;
         // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2)
            monthInterval--;
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    /**
     * 取出指定月份的最后一天日期.
     *
     * @param date
     * @return
     */
    public static Long getMonthLastDate(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatStr2Date(date.toString(), DATE_REQ_PATTERN));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return formatDateTime(calendar.getTime(), DATE_REQ_PATTERN);
    }
}
