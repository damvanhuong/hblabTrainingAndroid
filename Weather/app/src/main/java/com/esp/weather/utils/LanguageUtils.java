package com.esp.weather.utils;

import android.content.Context;

import com.esp.weather.R;

public class LanguageUtils {

    public static String getCountry(Context context, String raw) {
        switch (raw) {
            case "VN": return context.getString(R.string.vietnam);
            case "CN": return context.getString(R.string.chinese);
            case "JP": return context.getString(R.string.japan);
            case "KR": return context.getString(R.string.korean);
            default: return raw;
        }
    }

    public static String getCity(String raw) {
        switch (raw) {
            case "Ha Noi": return "Hà Nội";
            case "Bac Giang": return "Bắc Giang";
            case "Bac Ninh": return "Bắc Ninh";
            case "Ha Nam": return "Hà Nam";
            case "Nam Dinh": return "Nam Định";
            case "Thai Binh": return "Thái Bình";
            default: return raw;
        }
    }

    public static String getAddress(Context context, String city, String country) {
        return getCity(city) + ", " + getCountry(context, country);
    }
}
