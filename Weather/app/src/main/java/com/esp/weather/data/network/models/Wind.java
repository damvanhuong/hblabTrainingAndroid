package com.esp.weather.data.network.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    private Double speed;

    @SerializedName("deg")
    @Expose
    private Double degrees;

    public Double getSpeed() {
        return speed;
    }

    public Double getDegrees() {
        return degrees;
    }
}