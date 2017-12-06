package com.esp.weather.data.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    private Double temp;
    private Double pressure;
    private Integer humidity;

    @SerializedName("temp_min")
    @Expose
    private Double tempMin;

    @SerializedName("temp_max")
    @Expose
    private Double tempMax;

    @SerializedName("sea_level")
    @Expose
    private Double seaLevel;

    @SerializedName("grnd_level")
    private Double groundLevel;

    public Double getTemp() {
        return temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public Double getSeaLevel() {
        return seaLevel;
    }

    public Double getGroundLevel() {
        return groundLevel;
    }
}
