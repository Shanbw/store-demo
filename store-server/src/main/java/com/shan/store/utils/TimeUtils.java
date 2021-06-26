package com.shan.store.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间工具类
 **/
public class TimeUtils {

    /**
     * 获取GMT格式的时间字符串
     */
    public static String getGMT() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(new Date());
    }

    /**
     * 以当前时间错增加指定的时间戳
     */
    public static Date expireDateTime(Long expireInSeconds) {
        return Date.from(Instant.now().plusSeconds(expireInSeconds));
    }

    public static String formatDay(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     *  时间格式转化为ISO8601格林威治天文台的标准时间
     */
    public static String getISO8601Timestamp(Date date){
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

    public static String getISO8601TimeWithoutSplit(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

}
