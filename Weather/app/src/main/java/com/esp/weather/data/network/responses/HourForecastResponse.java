package com.esp.weather.data.network.responses;

import com.esp.weather.data.network.models.HourForecast;

import java.util.List;

public class HourForecastResponse {

    private String cod;
    private Double message;
    private Integer cnt;
    private List<HourForecast> list = null;

    public String getCod() {
        return cod;
    }

    public Double getMessage() {
        return message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public List<HourForecast> getList() {
        return list;
    }
}
