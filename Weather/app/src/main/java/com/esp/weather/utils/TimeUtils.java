package com.esp.weather.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static int[] getDate(String dt) {
        String[] date = dt.substring(0, 10).split("-");
        int[] d = new int[3];
        for (int i = 0; i < date.length; i++) {
            d[i] = Integer.parseInt(date[i]);
        }
        return d;
    }

    public static int[] getTime(String dt) {
        String[] time = dt.substring(11).split(":");
        int[] t = new int[3];
        for (int i = 0; i < 3; i++) {
            t[i] = Integer.parseInt(time[i]);
        }
        return t;
    }

    public static String getDateFromTimestamp(long timestamp) {
        Date date = new Date(timestamp*1000);
        Format format = new SimpleDateFormat("dd/MM");
        return format.format(date);
    }

    public static String getTimeFromTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return getTimeFromTimestamp(calendar.getTime().getTime());
    }

    public static boolean equalsDate(int[] day1, int[] day2) {
        return (day1[0] == day2[0]) && (day1[1] == day2[1]) && (day1[2] == day2[2]);
    }

    public static boolean equalsDate(String source, int[] destination) {
        int[] day1 = getDate(source);
        return (day1[0] == destination[0]) && (day1[1] == destination[1]) && (day1[2] == destination[2]);
    }

}
