package com.esp.weather.data.network.responses;

import com.esp.weather.data.network.models.Main;
import com.esp.weather.data.network.models.Sys;
import com.esp.weather.data.network.models.Weather;
import com.esp.weather.data.network.models.Wind;
import com.esp.weather.data.network.models.Clouds;
import com.esp.weather.data.network.models.Coord;

import java.util.List;

public class CurrentWeatherResponse {

    private Coord coord;
    private List<Weather> weather = null;
    private String base;
    private Main main;
    private Integer visibility;
    private Wind wind;
    private Clouds clouds;
    private Long dt;
    private Sys sys;
    private Integer id;
    private String name;
    private Integer cod;

    public Coord getCoord() {
        return coord;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public String getBase() {
        return base;
    }

    public Main getMain() {
        return main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public Wind getWind() {
        return wind;
    }

    public Clouds getClouds() {
        return clouds;
    }

    public Long getDt() {
        return dt;
    }

    public Sys getSys() {
        return sys;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCod() {
        return cod;
    }
}
