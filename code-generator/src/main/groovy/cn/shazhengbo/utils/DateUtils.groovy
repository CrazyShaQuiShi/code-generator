package cn.shazhengbo.utils

import java.text.SimpleDateFormat

/**
 * @author:CrazyShaQiuShi
 * @email:3105334046 @ q q . c o m
 * @descript:
 * @version:1.0.0
 */
class DateUtils {
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static def format(Date date) {
        return format(date, DATE_PATTERN);
    }

    static def format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }
}
