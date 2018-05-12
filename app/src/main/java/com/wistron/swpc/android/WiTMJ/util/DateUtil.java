package com.wistron.swpc.android.WiTMJ.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by WH1604025 on 2016/6/8.
 */
public class DateUtil {
    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式："2016-06-07T14:38:21"
     * @param str2 时间参数 2 格式："2016-06-07T14:58:21"
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(day==0){
            if(hour==0){
                return min + "mins";
            }else if(min ==0){
                return hour +"hrs";
            }
            return hour + " hrs" + min + " mins";
        }else if(day==1){
            if(min==0){
                return 24 + " hrs";
            }
            return 24 + " hrs" + min + " mins";
        }
        return day + " day " + hour + " hrs " + min + " mins";
    }

    /**
     *
     * 计算一个时间经过多少毫秒之后的的时间
     * @param str1  起始时间 时间参数 格式："2016-06-07T14:38:21"
     * @param duration 经过多长时间
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getEndDate(String str1, long duration) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date one;
        try {
            one = df.parse(str1);
            long time = one.getTime();

            long diff ;

            diff = time + duration;

            one.setTime(diff);

            return df.format(one);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 比较日期的较早，true 代表已结束，false代表为未开始
     * @param aDate
     * @param bDate
     * @return
     */
    public static boolean compareDate(String aDate,String bDate){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date one;
        Date two;

        try {
            one = df.parse(aDate);
            two = df.parse(bDate);

            long time1 = one.getTime();
            long time2 = two.getTime();

            if(time1>time2){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param date 格式为  2016-06-07
     * @param time 格式为  14:58:21
     * @return
     */
    public static String subDateAndTime(String date,String time){
        String temp = date + "T" + time +".000" + getZoon();
        return temp;
    }

    public static String getNowDate (){
        String time="";
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");//可以方便地修改日期格式
        time = dateFormat.format( now );
        return time;
    }


    public static String getZoon(){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date = new Date();
        String s = format1.format(date);
        return s.substring(20);
    }

    public static String convertServerDate(String dtc){
        if(TextUtils.isEmpty(dtc)) return "";
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = readDate.parse(dtc);
            SimpleDateFormat writeDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            String s = writeDate.format(date);
            return s;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return "";
    }
}