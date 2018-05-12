package com.example;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by WH1604025 on 2016/6/15.
 */
public class Test {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static void main(String[] args) throws Exception {
        double duration = 70000.000 / 1000;

        if(duration / 60 >0){
            System.out.println("min = " + duration % 60);
        }

        System.out.println("duration = " + duration +  " duration / 60  = "+(int)duration / 60);

        String time = "05" + ":" +"30";

        Date d = sdf.parse(time);

        System.out.println(d.getTime());

        String s0 = "2016-06-10T08:41:00.000Z";

        System.out.println(s0.split("T").length);

        if(s0.split("T").length>1){
            System.out.println(s0.split("T")[0]);
            System.out.println(s0.split("T")[1].substring(0,8));
        }


        String s1 = "dsadasd|30.456524,114.41641299999999";

        String sss  = s1.split("\\|")[1];

        System.out.println(sss.split(",")[0]);

        System.out.println(s1);
        System.out.println(s1.split("|")[1].split(",")[0]);
//        System.out.println(s1.split("|")[1].split(",")[1]);

//        Double s  = Double.parseDouble(s1.split("|")[1].split(",")[0]);
//        Double e  = Double.parseDouble(s1.split("|")[1].split(",")[1]);
//        System.out.println(s +""+ e);

        System.out.println(s1.split("\\|").length);

        StringBuilder sb = new StringBuilder();
        sb.append("fafd");
        sb.append("132132");
        System.out.println(sb.toString());

        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat();
//        Calendar cal = Calendar.getInstance();
//
//        TimeZone timeZone = cal.getTimeZone();
//        System.out.println(timeZone.toString());

        String s3 = "2016-06-16 16:21:01"+ " +0800";

        String s4 = "2016-06-16 16:21:01 +0800";

        System.out.println(s4);
        System.out.println(s4.split(" ")[0]);
        System.out.println(s4.split(" ")[1]);
        System.out.println(s4.split(" ")[2]);

        System.out.println(s3 +"\n" );

        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        String s5 = format1.format(date);
        System.out.println(s5+ "\n" +s5.substring(20));


        String dtc = "2016-07-07T20:26:00.000Z";

        String start_date = "2016-06-15T07:11:00.000Z";
        String end_date = "2016-06-15T08:13:00.000Z";

        System.out.println(convertServerDate(dtc));

        System.out.println(getDistanceTime(convertServerDate(start_date),convertServerDate(end_date)));
    }

    /**
     * 转换服务器数据  如  "2016-06-21T01:48:50.596Z";
     * 转换结果格式为      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
     * 返回数据格式如       2016-06-21T09:48:50.596+0800
     *
     * @param dtc
     * @return
     */
    public static String convertServerDate(String dtc){
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
        return day + " day" + hour + " hrs" + min + " mins";
    }
}
