package com.esp.weather.data.network.responses;

import com.esp.weather.data.network.models.Day;

import java.util.List;

public class DayForecastResponse {

    private Integer cod;
    private Double message;
    private List<Day> list;

    public Integer getCod() {
        return cod;
    }

    public Double getMessage() {
        return message;
    }

    public List<Day> getList() {
        return list;
    }
}
