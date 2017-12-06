package com.esp.weather.utils;

import java.text.DecimalFormat;

public class WeatherUtils {

    public static float getCelsius(Double kelvin) {
        float celsius = kelvin.floatValue() - 273.5f;
        DecimalFormat format = new DecimalFormat("##");
        return Math.round(celsius);
    }

    public static float getHourValue(String dateTime) {
        return Float.parseFloat(dateTime.substring(11,13));
    }
}
