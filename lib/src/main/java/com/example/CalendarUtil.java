package com.example;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CalendarUtil {
    public static void main(String[] args) {

        Map<String, String> m = getCurrentWeeks(2015, 12);
        Set<Map.Entry<String, String>> allSet = m.entrySet();

        Iterator<Map.Entry<String, String>> iter = allSet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> me = iter.next();
            System.out.println(me.getKey() + " " + me.getValue());
        }

    }

    public static int getMonthSumDay(int year, int month) {
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
     * 获得输入年月的 所有周 返回这几周的开始日期和结束日期
     * 分别存在 map中 例如：2016.6月的
     * 第一周
     * 1_begin 5/29 1_end 6/4
     * 第二周
     * 2_begin 6/5 2_end 6/11
     */
    public static Map<String, String> getCurrentWeeks(int year, int month) {
        Map<String, String> map = new HashMap<String, String>();
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
        int preYear = year - 1;
        ;
        int nextYear = year + 1;
        ;
        int nextmonthNum;
        if (month == 1) {
            premonthNum = 12;
            preMonthday = getMonthSumDay(year - 1, 12);
        } else {
            preMonthday = getMonthSumDay(year, month - 1);
            premonthNum = month - 1;
        }

        if (month == 12) {
            nextmonthNum = 1;
        } else {
            nextmonthNum = month + 1;
        }


        day = getMonthSumDay(year, month);
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
        int weeknum = 1;
        boolean flag = false;
        // int beginDate = 0;
        sumdays = yearday + monthday;
        week = 1 + sumdays % 7;
        System.out.println("sun\tmon\ttue\twed\tthu\tfri\tsat");//周日是一周的开始

        if (week != 7) {
            for (int j = 0; j < week; j++) {
                int d = (preMonthday - (week - j) + 1);
                System.out.print(d + "\t");
                if (j == 0) {
                    if (premonthNum == 12) {
                        map.put("1_begin", preYear + "/" + premonthNum + "/" + d);//第一周的开始日期
                    } else {
                        map.put("1_begin", year + "/" + premonthNum + "/" + d);//第一周的开始日期
                    }

                    // System.out.println("put " + "1_begin ===" + premonthNum+"/"+d+" ");
                }
            }

            flag = true;
        }

        for (int k = 1; k <= day; k++) {
            weekday = (sumdays - 1 + k) % 7 + 1;
            if (weekday == 6) {
                System.out.print(k + "\n");
                map.put(weeknum + "_end", year + "/" + month + "/" + k);
                // System.out.println("put " + weeknum+"_end ===" + month+"/"+k +" ");

                if ((k + 1) <= day) {//本月最后一周
                    map.put((weeknum + 1) + "_begin", year + "/" + month + "/" + (k + 1));
                    //  System.out.println("monthday " +day+ "  k+1 " + (k+1) );
                    //  System.out.println("put " + (weeknum+1)+"_begin ===" +month+"/"+(k+1));
                    if ((k + 7) > day) {//最后一周的最后一天，是下个月的几号
                        if (month == 12) {
                            map.put((weeknum + 1) + "_end", nextYear + "/" + nextmonthNum + "/" + ((k + 7) - day));
                        } else {
                            map.put((weeknum + 1) + "_end", year + "/" + nextmonthNum + "/" + ((k + 7) - day));
                        }

                        //     System.out.print("put " + (weeknum+1)+"_end ===" + nextmonthNum+"/"+((k+7)-day) +" ");;
                    }
                }

                weeknum++;
            } else {
                if (!flag && k == 1) {
                    map.put("1_begin",year +"/"+ month + "/" + 1);
                    //  System.out.println("put " + "1_begin ===" +month+"/"+1+" ");//第一周的开始日期
                }

                System.out.print(k + "\t");

            }
        }

        return map;
    }
}