package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by WH1603043 on 20/6/2016.
 */
public class JTest {
        public static int getMonthSumDay(int year , int month ){
            int day = 0;
            boolean isRn = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
            if (month == 1 || month == 3 || month == 5 || month == 7
                    || month == 8 || month == 10 || month == 12) {
                day = 31;
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                day = 30;
            } else if (month == 2 && isRn == true) {
                day = 29;
            } else {
                day = 28;
            }
            return day;
        }
        /**
         *
         * 获得输入年月的 所有周 返回这几周的开始日期和结束日期
         * 分别存在 map中 例如：2016.6月的
         * 第一周
         * 1_begin 5/29 1_end 6/4
         * 第二周
         * 2_begin 6/5 2_end 6/11
         *
         * */
        public static Map<String,String> getCurrentWeeks(int year,int month){
            Map<String,String> map = new HashMap<String,String>();
            //  int year = 0;
            // int month = 0;
            int day = 0;
            int sumdays = 0;
            int yearday = 0;//到1900年1月1日的天数
            int monthday = 0;//到当年1.1日的天数
            int week = 0;//该月第一天是星期几
            int weekday = 0;//当天是星期几
            //计算闰年
            boolean isRn = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
            int preMonthday;
            int premonthNum;
            int nextmonthNum;
            if(month==1){
                premonthNum = 12;
                preMonthday =getMonthSumDay(year-1,12);
            }else{
                preMonthday =getMonthSumDay(year,month-1);
                premonthNum = month-1;
            }

            if(month==12){
                nextmonthNum = 1;
            }else{
                nextmonthNum = month+1;
            }


            day = getMonthSumDay(year,month);
            for (int i = 1900; i < year; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    yearday = yearday + 366;
                } else {
                    yearday = yearday + 365;
                }
            }
            for (int n = 1; n < month; n++) {
                if (n == 1 || n == 3 || n == 5 || n == 7 || n == 8 || n == 10
                        || n == 12) {
                    monthday = monthday + 31;
                } else if (n == 4 || n == 6 || n == 9 || n == 11) {
                    monthday = monthday + 30;
                } else if (n == 2 && isRn == true) {
                    monthday = monthday + 29;
                } else {
                    monthday = monthday + 28;
                }
            }
            int weeknum=1;
            boolean flag = false;
            // int beginDate = 0;
            sumdays = yearday + monthday;
            week = 1 + sumdays%7;
            System.out.println("sun\tmon\ttue\twed\tthu\tfri\tsat");//周日是一周的开始

            if (week != 7) {
                for (int j = 0; j < week; j++) {
                    int d = (preMonthday - (week - j) +1);
                    System.out.print(d+"\t");
                    if(j==0){
                        if (String.valueOf(d).length()<2){
                            String dStr ="0"+String.valueOf(d);
                            map.put("1_begin",premonthNum+"/"+dStr) ;//第一周的开始日期
                        }else{
                            map.put("1_begin",premonthNum+"/"+d) ;//第一周的开始日期
                            // System.out.println("put " + "1_begin ===" + premonthNum+"/"+d+" ");
                        }
                    }
                }

                flag = true;
            }

            for (int k = 1; k <= day; k++) {
                weekday = (sumdays - 1 + k) % 7 + 1;
                if (weekday == 6) {
                    System.out.print(k + "\n");
                    if (String.valueOf(month).length()<2){
                        String mStr ="0"+String.valueOf(month);
                        month = Integer.valueOf(mStr);
                    }
                    map.put(weeknum+"_end",month+"/"+k);
                    // System.out.println("put " + weeknum+"_end ===" + month+"/"+k +" ");

                    if((k+1)<=day){//本月最后一周
                        map.put((weeknum+1)+"_begin",month+"/"+(k+1));
                        //  System.out.println("monthday " +day+ "  k+1 " + (k+1) );
                        //  System.out.println("put " + (weeknum+1)+"_begin ===" +month+"/"+(k+1));
                        if((k+7)>day){//最后一周的最后一天，是下个月的几号
                            map.put((weeknum+1)+"_end",nextmonthNum+"/"+ ((k+7)-day));
                            //     System.out.print("put " + (weeknum+1)+"_end ===" + nextmonthNum+"/"+((k+7)-day) +" ");;
                        }
                    }

                    weeknum++;
                } else {
                    if(!flag && k==1){
                        map.put("1_begin",month+"/"+1);
                        //  System.out.println("put " + "1_begin ===" +month+"/"+1+" ");//第一周的开始日期
                    }

                    System.out.print(k + "\t");
/*
 打印-------万年历
                    System.out.print(k + "\t");
*/
                }
            }

            return map;
        }

    /*public static void main(String[] args) {

*//*        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int numOfWeeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
        int whichWeek = c.get(Calendar.WEEK_OF_MONTH);
        System.out.println(" actual "+numOfWeeks+" which week "+whichWeek);*//*

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int numOfWeeks = c.getActualMaximum(Calendar.WEEK_OF_MONTH);

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        System.out.print("year= "+year+" month= "+month);
        int day = c.get(Calendar.DAY_OF_MONTH);
        System.out.println(" day= "+day);
        Map<String,String> m =   getCurrentWeeks(year,month);
        Set<Map.Entry<String, String>> allSet=m.entrySet();

        Iterator<Map.Entry<String, String>> iter=allSet.iterator();
        //            String week1bgn,week1end ,week2bgn ,week2end,week3bgn,week3end,week4bgn,week4end;
        String week1bgn = "";
        String week1end = "";
        String week2bgn = "";
        String week2end = "";
        String week3bgn = "";
        String week3end = "";
        String week4bgn = "";
        String week4end = "";
        String week5bgn = "";
        String week5end = "";

        while(iter.hasNext()){
            Map.Entry<String, String> me=iter.next();
            System.out.println(me.getKey()+ " "+me.getValue());
            if (me.getKey().equals("1_begin")){
                week1bgn = me.getValue();
            }else if (me.getKey().equals("1_end")){
                week1end = "~\n" +me.getValue();
            }else if (me.getKey().equals("2_begin")){
                week2bgn = "" +me.getValue();
            }else if (me.getKey().equals("2_end")){
                week2end = "~\n" +me.getValue();
            }else if (me.getKey().equals("3_begin")){
                week3bgn = "" +me.getValue();
            }else if (me.getKey().equals("3_end")){
                week3end = "~\n" +me.getValue();
            }else if (me.getKey().equals("4_begin")){
                week4bgn = "" +me.getValue();
            }else if (me.getKey().equals("4_end")){
                week4end = "~\n" +me.getValue();
            }
            if (numOfWeeks>4){
                 if (me.getKey().equals("5_begin")){
                    week5bgn = "" +me.getValue();
                }else if (me.getKey().equals("5_end")){
                    week5end = "~\n" +me.getValue();
                }
            }


        }
        System.out.println(week1bgn +""+week1end+"\n");
        System.out.println(week2bgn +""+week2end+"\n");
        System.out.println(week3bgn +""+week3end+"\n");
        System.out.println(week4bgn +""+week4end+"\n");
        if (!"".equals(week5bgn))
        System.out.println(week5bgn +""+week5end+"\n");

    }*/

    public static String getServerDateFormat(Date date){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String s = format1.format(date);
        return s;
    }

    public static Date getBeforeStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy/MM/dd");
            System.out.println("readDate=="+readDate);
            date = readDate.parse(str);
            System.out.println("date=="+date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static void main(String[] args) {

        Date endDate =getStrToDate("2016-06-24T06:08:13.146Z");
        String endTimeStr = getDateToStr(endDate);
        System.out.println("==endTimeStr=="+endTimeStr);


/*
        Date d = getBeforeStrToDate("2016/06/22");
        String s = getServerDateFormat(d);
        System.out.println("==d==" + d);
        System.out.println("==s==" + s);
        Calendar c = Calendar.getInstance();
        //        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone==" + tz.getDisplayName(false, TimeZone.SHORT) + "TimeZone id==" + tz.getID());
*/



    }
    public static Date getStrToDate(String str){
        Date date =null ;
        try {
            SimpleDateFormat readDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            readDate.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = readDate.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /*将Date类型的时间转换为 physicalfragment 要求的时间格式 */
    public static String getDateToStr(Date date){
        java.text.DateFormat format1 = new java.text.SimpleDateFormat("MM/dd HH:mm");
        String s = format1.format(date);
        return s;
    }

    }