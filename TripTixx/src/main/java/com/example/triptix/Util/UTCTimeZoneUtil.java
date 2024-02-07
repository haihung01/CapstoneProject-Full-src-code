package com.example.triptix.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTCTimeZoneUtil {
    public static String getTimeNow(){
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(utcTimeZone);
        return dateFormat.format(new Date());
    }
    public static Date getTimeNowDate(){
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(utcTimeZone);
        try {
            String formattedDate = dateFormat.format(new Date());
            return dateFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Xử lý ngoại lệ nếu cần thiết
            return null;
        }
    }



    public static String getTimeGTMplus7VN(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 7);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    public static Date getTimeGTMplus7VN_utildate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 7);
        return calendar.getTime();
    }

    public static Long convertFormatddMMyyyyHHmmssToLong(String timeStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = dateFormat.parse(timeStr);
        return date.getTime() / 1000;
    }

    public static String getTimeFormatUTC(Date timeComes) {
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(utcTimeZone);
        return dateFormat.format(timeComes);
    }

    public static void main(String[] args) {
        System.out.println(getTimeGTMplus7VN());
    }
}
