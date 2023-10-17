package com.santint.toolutils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DateFormatter {

    private static final DateFormat y_formater = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static final DateFormat d_formater = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    private static final String LOCK = "DateFormatterClassLOCK";
    final static int[] sizeTable = {9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE};
    private static final List<String> monthDayList2 = new ArrayList<String>();
    private static SimpleDateFormat sdf;
    private static final DateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static HashMap<Integer,List<String>> mYearHashMap = new HashMap<Integer,List<String>>();
    public static HashMap<Integer,List<String>> mMonthHashMap = new HashMap<Integer,List<String>>();

    public static Date getCurrDate() {
        long currTime = System.currentTimeMillis();
        Date date = new Date(currTime);
        return date;
    }

    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        return date.after(begin) && date.before(end);
    }

    ///比较两个date大小
    public static boolean isWan(Date nowTime, Date betweenTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar between = Calendar.getInstance();
        between.setTime(betweenTime);
        return date.after(between);
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    public static Date string2Date(String value) throws Exception {
        Date date = null;
        if (value != null && !value.trim().equals("")) {
            synchronized (LOCK) {
                try {
                    date = d_formater.parse(value);
                } catch (Exception e) {
                    throw e;
                }
            }
        }
        return date;
    }

    public static Date string2Date(String value, String format) {
        Date date = null;
        if (value != null) {
            try {
                synchronized (LOCK) {
                    SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
                    date = df.parse(value);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return date;
    }

    public static String date2String(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public static String date2String(Date date) {
        if (date != null) {
            synchronized (LOCK) {
                return d_formater.format(date);
            }
        }
        return "";
    }

    public static String date2YearString(Date date) {
        if (date != null) {
            synchronized (LOCK) {
                return y_formater.format(date);
            }
        }
        return "";
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneBigger(String str1, String str2, DateFormat sdf) {
        boolean isBigger = false;
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
            if (dt1.getTime() > dt2.getTime()) {
                isBigger = true;
            } else if (dt1.getTime() < dt2.getTime()) {
                isBigger = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isBigger;
    }

//    public static String bytesToString(com.google.protobuf.ByteString src, String charSet) {
//        if (TextUtils.isEmpty(charSet)) {
//            charSet = "GB2312";
//        }
//        return bytesToString(src.toByteArray(), charSet);
//    }

    public static String bytesToString(byte[] input, String charSet) {
        //if(ArrayUtils.isEmpty(input)) {
        //return StringUtils.EMPTY;
        if (input == null && input.length == 0) {
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(input.length);
        buffer.put(input);
        buffer.flip();

        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;

        try {
            charset = Charset.forName(charSet);
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());

            return charBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<Date> test(Date date) {
        Date start = dayStartDate(date);//转换为天的起始date
        Date nextDayDate = dayEndDate(date);//下一天的date

        List<Date> result = new ArrayList<Date>();
        while (start.compareTo(nextDayDate) < 0) {
            result.add(start);
            //日期加5分钟
            start = addFiveMin(start, 20);
        }

        return result;
    }

    private static Date addFiveMin(Date start, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.MINUTE, offset);
        return c.getTime();
    }

    private static Date addOneDay(Date start, int offset) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DAY_OF_WEEK, offset);
        return c.getTime();
    }

    private static Date nextDay(Date start) {
        Calendar c = Calendar.getInstance();
        c.setTime(start);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private static Date dayStartDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 10);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private static Date dayStartDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, 1);
        return c.getTime();
    }

    private static Date dayEndDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, 7);
        return c.getTime();
    }

    private static Date dayEndDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 19);
        c.set(Calendar.MINUTE, 50);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * date是为则默认今天日期、可自行设置“2013-06-03”格式的日期
     *
     * @return 返回1是星期日、2是星期一、3是星期二、4是星期三、5是星期四、6是星期五、7是星期六
     */

    public static int getDayofweek(String date) {
        Calendar cal = Calendar.getInstance();
//   cal.setTime(new Date(System.currentTimeMillis()));
        if (date.equals("")) {
            cal.setTime(DateFormatter.getCurrDate());
        } else {
            cal.setTime(new Date(getDateByStr2(date).getTime()));
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * .Description://根据字符日期返回星期几
     * .Author:麦克劳林
     * .@Date: 2018/12/29
     */
    public static String getWeek(String dateTime) {
        String week = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dateTime);
            SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
            week = dateFm.format(date);
            week = week.replaceAll("星期", "周");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return week;
    }

    /**
     * 获取将来第几天的日期
     *
     * @param past
     * @return
     */
    public static String getFutureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }


    public static Date getDateByStr2(String dd) {

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date;
        try {
            date = sd.parse(dd);
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return date;
    }

    public static List<String> getMonthDay(int month) {
        List<String> monthDayList = new ArrayList<String>();
        if (monthDayList2 != null && monthDayList2.size() > 0) {
            monthDayList2.clear();
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        // int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

//        builder.append("今日");
//        monthDayList.add(builder.toString());
//        builder.delete(0, builder.length());

        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        for (int i = day; i < day + 3; i++) {
            // 判断大小月及是否闰年,用来确定"日"的数据
            builder2.append(year).append("-");
            if (list_big
                    .contains(String.valueOf(month))) {
                if (i > 31) {
                    int bigDay = i - 31;
                    int bigMonth = month + 1;
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(bigMonth).append("月").append(bigDay).append("日");
                    }
                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(bigMonth) == 1) {
                        builder2.append(0).append(bigMonth).append("-");
                    } else {
                        builder2.append(bigMonth).append("-");
                    }
                    if (sizeOfInt(bigDay) == 1) {
                        builder2.append(0).append(bigDay);
                    } else {
                        builder2.append(bigDay);
                    }

                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                } else {
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(month).append("月").append(i).append("日");
                    }

                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(month) == 1) {
                        builder2.append(0).append(month).append("-");
                    } else {
                        builder2.append(month).append("-");
                    }
                    if (sizeOfInt(i) == 1) {
                        builder2.append(0).append(i);
                    } else {
                        builder2.append(i);
                    }
                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                }

            } else if (list_little.contains(String.valueOf(month))) {
                if (i > 30) {
                    int bigDay = i - 30;
                    int bigMonth = month + 1;
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(bigMonth).append("月").append(bigDay).append("日");
                    }
                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(bigMonth) == 1) {
                        builder2.append(0).append(bigMonth).append("-");
                    } else {
                        builder2.append(bigMonth).append("-");
                    }
                    if (sizeOfInt(bigDay) == 1) {
                        builder2.append(0).append(bigDay);
                    } else {
                        builder2.append(bigDay);
                    }
                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                } else {
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(month).append("月").append(i).append("日");
                    }

                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(month) == 1) {
                        builder2.append(0).append(month).append("-");
                    } else {
                        builder2.append(month).append("-");
                    }
                    if (sizeOfInt(i) == 1) {
                        builder2.append(0).append(i);
                    } else {
                        builder2.append(i);
                    }
                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                }
            } else {
                if ((year % 4 == 0 && year % 100 != 0)
                        || year % 400 == 0)
                    if (i > 29) {
                        int bigDay = i - 29;
                        int bigMonth = month + 1;
                        if (i == day) {
                            builder.append("今日");
                        } else {
                            builder.append(bigMonth).append("月").append(bigDay).append("日");
                        }
                        monthDayList.add(builder.toString());
                        builder.delete(0, builder.length());

                        if (sizeOfInt(bigMonth) == 1) {
                            builder2.append(0).append(bigMonth).append("-");
                        } else {
                            builder2.append(bigMonth).append("-");
                        }
                        if (sizeOfInt(bigDay) == 1) {
                            builder2.append(0).append(bigDay);
                        } else {
                            builder2.append(bigDay);
                        }
                        monthDayList2.add(builder2.toString());
                        builder2.delete(0, builder2.length());
                    } else {
                        if (i == day) {
                            builder.append("今日");
                        } else {
                            builder.append(month).append("月").append(i).append("日");
                        }
                        monthDayList.add(builder.toString());
                        builder.delete(0, builder.length());

                        if (sizeOfInt(month) == 1) {
                            builder2.append(0).append(month).append("-");
                        } else {
                            builder2.append(month).append("-");
                        }
                        if (sizeOfInt(i) == 1) {
                            builder2.append(0).append(i);
                        } else {
                            builder2.append(i);
                        }
                        monthDayList2.add(builder2.toString());
                        builder2.delete(0, builder2.length());
                    }

                else if (i > 28) {
                    int bigDay = i - 28;
                    int bigMonth = month + 1;
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(bigMonth).append("月").append(bigDay).append("日");
                    }
                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(bigMonth) == 1) {
                        builder2.append(0).append(bigMonth).append("-");
                    } else {
                        builder2.append(bigMonth).append("-");
                    }
                    if (sizeOfInt(bigDay) == 1) {
                        builder2.append(0).append(bigDay);
                    } else {
                        builder2.append(bigDay);
                    }
                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                } else {
                    if (i == day) {
                        builder.append("今日");
                    } else {
                        builder.append(month).append("月").append(i).append("日");
                    }
                    monthDayList.add(builder.toString());
                    builder.delete(0, builder.length());

                    if (sizeOfInt(month) == 1) {
                        builder2.append(0).append(month).append("-");
                    } else {
                        builder2.append(month).append("-");
                    }
                    if (sizeOfInt(i) == 1) {
                        builder2.append(0).append(i);
                    } else {
                        builder2.append(i);
                    }
                    monthDayList2.add(builder2.toString());
                    builder2.delete(0, builder2.length());
                }

            }
        }
        return monthDayList;
    }

    static List<String> list_year = new ArrayList<String>();
    static List<String> list_month = new ArrayList<String>();
    static List<String> list_day = new ArrayList<String>();
    static List<String> list_hour = new ArrayList<String>();
    static List<String> list_minute = new ArrayList<String>();

    public static List<String> getListYear() {
        return list_year;
    }

    public static List<String> getListMonth() {
        return list_month;
    }

    public static List<String> getListDay() {
        return list_day;
    }

    public static List<String> getListHour() {
        return list_hour;
    }

    public static List<String> getListMinute() {
        return list_minute;
    }

    public static void getMonthDayBookOrder() {
        if (list_year != null && list_year.size() > 0) {
            list_year.clear();
        }
        if (list_month != null && list_month.size() > 0) {
            list_month.clear();
        }
        if (list_day != null && list_day.size() > 0) {
            list_day.clear();
        }
        if (list_hour != null && list_hour.size() > 0) {
            list_hour.clear();
        }
        if (list_minute != null && list_minute.size() > 0) {
            list_minute.clear();
        }
        if(mYearHashMap!=null && mYearHashMap.size()>0){
            mYearHashMap.clear();
        }
        if(mMonthHashMap!=null && mMonthHashMap.size()>0){
            mMonthHashMap.clear();
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

//        builder.append("今日");
//        monthDayList.add(builder.toString());
//        builder.delete(0, builder.length());

        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        for(int j=0;j<60;j+=10){
            String minute = String.valueOf(j);
            list_minute.add(minute+"分");
        }
        for(int k=0;k<24;k++){
            String hour = String.valueOf(k);
            list_hour.add(hour+"时");
        }
        int count = 0;

        for (int i = day; i < day + 3; i++) {

            // 判断大小月及是否闰年,用来确定"日"的数据
            builder2.append(year).append("-");
            if (list_big
                    .contains(String.valueOf(month))) {
                if (i > 31) {
                    int bigDay = i - 31;
                    int bigMonth = month + 1;
                    if (bigMonth > 12) {
                        judgeYear(count,year+1);
                        bigMonth = 1;
                    }else{
                        judgeYear(count,year);
                    }

                    String months = String.valueOf(bigMonth);
                    String days = String.valueOf(bigDay);
                    judgeMonth(count,bigMonth);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(bigMonth).append("月").append(bigDay).append("日");
//                    }
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());

//                    if (sizeOfInt(bigMonth) == 1) {
//                        builder2.append(0).append(bigMonth).append("-");
//                    } else {
//                        builder2.append(bigMonth).append("-");
//                    }
//                    if (sizeOfInt(bigDay) == 1) {
//                        builder2.append(0).append(bigDay);
//                    } else {
//                        builder2.append(bigDay);
//                    }
//
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                } else {
                    judgeYear(count,year);
                    String months = String.valueOf(month);
                    String days = String.valueOf(i);
                   judgeMonth(count,month);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(month).append("月").append(i).append("日");
//                    }
//
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());
//
//                    if (sizeOfInt(month) == 1) {
//                        builder2.append(0).append(month).append("-");
//                    } else {
//                        builder2.append(month).append("-");
//                    }
//                    if (sizeOfInt(i) == 1) {
//                        builder2.append(0).append(i);
//                    } else {
//                        builder2.append(i);
//                    }
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                }

            } else if (list_little.contains(String.valueOf(month))) {
                judgeYear(count,year);
                if (i > 30) {
                    int bigDay = i - 30;
                    int bigMonth = month + 1;
                    String months = String.valueOf(bigMonth);
                    String days = String.valueOf(bigDay);
                    judgeMonth(count,bigMonth);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(bigMonth).append("月").append(bigDay).append("日");
//                    }
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());
//
//                    if (sizeOfInt(bigMonth) == 1) {
//                        builder2.append(0).append(bigMonth).append("-");
//                    } else {
//                        builder2.append(bigMonth).append("-");
//                    }
//                    if (sizeOfInt(bigDay) == 1) {
//                        builder2.append(0).append(bigDay);
//                    } else {
//                        builder2.append(bigDay);
//                    }
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                } else {
                    String months = String.valueOf(month);
                    String days = String.valueOf(i);
                    judgeMonth(count,month);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(month).append("月").append(i).append("日");
//                    }
//
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());
//
//                    if (sizeOfInt(month) == 1) {
//                        builder2.append(0).append(month).append("-");
//                    } else {
//                        builder2.append(month).append("-");
//                    }
//                    if (sizeOfInt(i) == 1) {
//                        builder2.append(0).append(i);
//                    } else {
//                        builder2.append(i);
//                    }
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                }
            } else {
                judgeYear(count,year);
                if ((year % 4 == 0 && year % 100 != 0)
                        || year % 400 == 0)
                    if (i > 29) {
                        int bigDay = i - 29;
                        int bigMonth = month + 1;
                        String months = String.valueOf(bigMonth);
                        String days = String.valueOf(bigDay);
                        judgeMonth(count,bigMonth);
                        if (!list_day.contains(days)) {
                            list_day.add(days+"日");
                        }
//                        if (i == day) {
//                            builder.append("今日");
//                        } else {
//                            builder.append(bigMonth).append("月").append(bigDay).append("日");
//                        }
//                        monthDayList.add(builder.toString());
//                        builder.delete(0, builder.length());
//
//                        if (sizeOfInt(bigMonth) == 1) {
//                            builder2.append(0).append(bigMonth).append("-");
//                        } else {
//                            builder2.append(bigMonth).append("-");
//                        }
//                        if (sizeOfInt(bigDay) == 1) {
//                            builder2.append(0).append(bigDay);
//                        } else {
//                            builder2.append(bigDay);
//                        }
//                        monthDayList2.add(builder2.toString());
//                        builder2.delete(0, builder2.length());
                    } else {
                        String months = String.valueOf(month);
                        String days = String.valueOf(i);
                        judgeMonth(count,month);
                        if (!list_day.contains(days)) {
                            list_day.add(days+"日");
                        }
//                        if (i == day) {
//                            builder.append("今日");
//                        } else {
//                            builder.append(month).append("月").append(i).append("日");
//                        }
//                        monthDayList.add(builder.toString());
//                        builder.delete(0, builder.length());
//
//                        if (sizeOfInt(month) == 1) {
//                            builder2.append(0).append(month).append("-");
//                        } else {
//                            builder2.append(month).append("-");
//                        }
//                        if (sizeOfInt(i) == 1) {
//                            builder2.append(0).append(i);
//                        } else {
//                            builder2.append(i);
//                        }
//                        monthDayList2.add(builder2.toString());
//                        builder2.delete(0, builder2.length());
                    }

                else if (i > 28) {
                    int bigDay = i - 28;
                    int bigMonth = month + 1;
                    String months = String.valueOf(bigMonth);
                    String days = String.valueOf(bigDay);
                    judgeMonth(count,bigMonth);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(bigMonth).append("月").append(bigDay).append("日");
//                    }
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());
//
//                    if (sizeOfInt(bigMonth) == 1) {
//                        builder2.append(0).append(bigMonth).append("-");
//                    } else {
//                        builder2.append(bigMonth).append("-");
//                    }
//                    if (sizeOfInt(bigDay) == 1) {
//                        builder2.append(0).append(bigDay);
//                    } else {
//                        builder2.append(bigDay);
//                    }
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                } else {
                    String months = String.valueOf(month);
                    String days = String.valueOf(i);
                    judgeMonth(count,month);
                    if (!list_day.contains(days)) {
                        list_day.add(days+"日");
                    }
//                    if (i == day) {
//                        builder.append("今日");
//                    } else {
//                        builder.append(month).append("月").append(i).append("日");
//                    }
//                    monthDayList.add(builder.toString());
//                    builder.delete(0, builder.length());
//
//                    if (sizeOfInt(month) == 1) {
//                        builder2.append(0).append(month).append("-");
//                    } else {
//                        builder2.append(month).append("-");
//                    }
//                    if (sizeOfInt(i) == 1) {
//                        builder2.append(0).append(i);
//                    } else {
//                        builder2.append(i);
//                    }
//                    monthDayList2.add(builder2.toString());
//                    builder2.delete(0, builder2.length());
                }

            }
            count++;
        }
    }
    private static void judgeYear(int mDay,int mYear){
        list_year = new ArrayList<String>();
        if (!list_year.contains(mYear)) {
            if(list_year!=null && list_year.size()>0){
                list_year.clear();
            }
            list_year.add(mYear+"年");
        }
        mYearHashMap.put(mDay,list_year);
    }
    private static void judgeMonth(int mDay,int mMonth){
        list_month = new ArrayList<String>();
        if (!list_month.contains(mMonth)) {
            if(list_month!=null && list_month.size()>0){
                list_month.clear();
            }
            list_month.add(mMonth+"月");
        }
        mMonthHashMap.put(mDay,list_month);
    }

    static int sizeOfInt(int x) {
        for (int i = 0; ; i++)
            if (x <= sizeTable[i])
                return i + 1;
    }


    public static List<String> getDayWeekList() {
        int dayNumer = DateFormatter.getDayofweek("");
        List<Integer> dayNumList = new ArrayList<Integer>();
//        if(dayNumer >= 6 || dayNumer == 1){
//            for (int k = dayNumer; k < dayNumer + 2; k++) {
//                dayNumList.add(k);
//            }
//        }else{
//            for (int k = dayNumer; k < dayNumer + 1; k++) {
//                dayNumList.add(k);
//            }
//        }
        for (int k = dayNumer; k < dayNumer + 3; k++) {
            dayNumList.add(k);
        }

        List<String> dayWeekList = new ArrayList<String>();
        for (int l = 0; l < dayNumList.size(); l++) {
            int m = dayNumList.get(l);
            if (m > 7) {
                m = m - 7;
                dayNumList.set(l, m);
            }

            if (m == 2) {
                dayWeekList.add("（周一）");
            } else if (m == 3) {
                dayWeekList.add("（周二）");
            } else if (m == 4) {
                dayWeekList.add("（周三）");
            } else if (m == 5) {
                dayWeekList.add("（周四）");
            } else if (m == 6) {
                dayWeekList.add("（周五）");
            } else if (m == 7) {
                dayWeekList.add("（周六）");
            } else if (m == 1) {
                dayWeekList.add("（周日）");
            }
        }
        return dayWeekList;
    }

    public static List<String> getDayWeekList2() {
        int dayNumer = DateFormatter.getDayofweek("");
        List<Integer> dayNumList = new ArrayList<Integer>();
//        if(dayNumer >= 6 || dayNumer == 1){
//            for (int k = dayNumer; k < dayNumer + 2; k++) {
//                dayNumList.add(k);
//            }
//        }else{
//            for (int k = dayNumer; k < dayNumer + 1; k++) {
//                dayNumList.add(k);
//            }
//        }
        for (int k = dayNumer; k < dayNumer + 6; k++) {
            dayNumList.add(k);
        }

        List<String> dayWeekList = new ArrayList<String>();
        for (int l = 0; l < dayNumList.size(); l++) {
            int m = dayNumList.get(l);
            if (m > 7) {
                m = m - 7;
                dayNumList.set(l, m);
            }

            if (m == 2) {
                dayWeekList.add("（周一）");
            } else if (m == 3) {
                dayWeekList.add("（周二）");
            } else if (m == 4) {
                dayWeekList.add("（周三）");
            } else if (m == 5) {
                dayWeekList.add("（周四）");
            } else if (m == 6) {
                dayWeekList.add("（周五）");
            } else if (m == 7) {
                dayWeekList.add("（周六）");
            } else if (m == 1) {
                dayWeekList.add("（周日）");
            }
        }
        return dayWeekList;
    }

    public static boolean isFourDay() {
        int dayNumer = DateFormatter.getDayofweek("");
        return dayNumer == 5;
    }

    public static boolean isFiveDay() {
        int dayNumer = DateFormatter.getDayofweek("");
        return dayNumer == 6;
    }

    public static boolean isSixDay() {
        int dayNumer = DateFormatter.getDayofweek("");
        return dayNumer == 7;
    }

    public static boolean isSevenDay() {
        int dayNumer = DateFormatter.getDayofweek("");
        return dayNumer == 1;
    }


}