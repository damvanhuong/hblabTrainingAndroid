package com.esp.weather.data.network;

import com.esp.weather.data.network.responses.CurrentWeatherResponse;
import com.esp.weather.data.network.responses.DayForecastResponse;
import com.esp.weather.data.network.responses.HourForecastResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ResponseService {

    @GET("data/2.5/weather")
    Call<CurrentWeatherResponse> getCurrentWeather(@QueryMap Map<String, String> query);

    @GET("data/2.5/forecast")
    Call<HourForecastResponse> getHourForecast(@QueryMap Map<String, String> query);

    @GET("data/2.5/forecast/daily")
    Call<DayForecastResponse> getDayForecast(@QueryMap Map<String, String> query);

}
