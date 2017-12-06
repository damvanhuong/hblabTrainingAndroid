package com.esp.weather.ui;


import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.data.network.responses.CurrentWeatherResponse;

import java.util.List;

public interface MainActivityPresenter {

    void initLocation();

    void search(double lat, double lon);

    void prepareDay(List<HourForecast> list, int[] dateTime);

}
