package com.esp.weather.ui;

import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.data.network.responses.CurrentWeatherResponse;
import com.esp.weather.data.network.responses.HourForecastResponse;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public interface MainActivityView {

    void onSuccess(CurrentWeatherResponse body);

    void onFailed();

    void updateFiveDay(List<HourForecast> list);

    void updateChart(LineData tempLineData, LineData windLineData, float offset);

    void showProgressDialog();

    void cancelProgressDialog();

    void changeBackground(int resId);

    void showDaySheet(List<HourForecast> list);
}
