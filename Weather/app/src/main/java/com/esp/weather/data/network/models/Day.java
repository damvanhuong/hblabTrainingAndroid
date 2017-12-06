package com.esp.weather.data.network.models;

import java.util.List;

public class Day {

    private Integer dt;
    private Temperature temp;
    private Double pressure;
    private Integer humidity;
    private List<Weather> weather;
    private Double speed;
    private Double deg;
    private Double clouds;

    public Integer getDt() {
        return dt;
    }

    public Temperature getTemp() {
        return temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public Double getSpeed() {
        return speed;
    }

    public Double getDeg() {
        return deg;
    }

    public Double getClouds() {
        return clouds;
    }
}
