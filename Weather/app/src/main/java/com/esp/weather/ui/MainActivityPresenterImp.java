package com.esp.weather.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.esp.weather.R;
import com.esp.weather.data.network.models.HourForecast;
import com.esp.weather.data.network.responses.CurrentWeatherResponse;
import com.esp.weather.data.network.responses.HourForecastResponse;
import com.esp.weather.receivers.AlertReceiver;
import com.esp.weather.utils.APIUtils;
import com.esp.weather.utils.TimeUtils;
import com.esp.weather.utils.WeatherUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.roger.catloadinglibrary.CatLoadingView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivityPresenterImp implements MainActivityPresenter, LocationListener {

    private Context context;
    private MainActivityView view;
    private Location location;

    private double longitude;
    private double latitude;

    private int isFinished = 0;

    private List<HourForecast> list;

    public MainActivityPresenterImp(Context context, MainActivityView view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void initLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isNetworkAvailable) {
            Toast.makeText(context, context.getString(R.string.enable_wifi), Toast.LENGTH_SHORT).show();
            WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            if (!wifiManager.isWifiEnabled()) {
                try {
                    Method method = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                    method.setAccessible(true);
                    method.invoke(connectivityManager);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled || isNetworkEnabled) {
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
            }
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
            if (isGPSEnabled) {
                if (location != null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
            initData();
        }
    }

    private void initData() {
        view.showProgressDialog();
        isFinished = 0;
        Map<String, String> query = new HashMap<>();
        query.put("lat", String.valueOf(latitude));
        query.put("lon", String.valueOf(longitude));
        query.put("appid", context.getString(R.string.app_id));
        query.put("lang", "vi");

        APIUtils.getResponseService().getCurrentWeather(query).enqueue(new retrofit2.Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                CurrentWeatherResponse body = response.body();
                view.onSuccess(body);
                isFinished += 1;
                if (isFinished == 2) {
                    view.cancelProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                view.onFailed();
                isFinished += 1;
                if (isFinished == 2) {
                    view.cancelProgressDialog();
                }
            }
        });

        APIUtils.getResponseService().getHourForecast(query).enqueue(new retrofit2.Callback<HourForecastResponse>() {
            @Override
            public void onResponse(Call<HourForecastResponse> call, Response<HourForecastResponse> response) {
                HourForecastResponse body = response.body();
                List<HourForecast> list = body.getList();
                List<Entry> temps = new ArrayList<>();
                List<Entry> winds = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    HourForecast element = list.get(i);
                    temps.add(new Entry(i, WeatherUtils.getCelsius(element.getMain().getTemp())));
                    winds.add(new Entry(i, element.getWind().getSpeed().floatValue()));
                }
                final float offset = WeatherUtils.getHourValue(list.get(0).getDateTime());

                LineDataSet tempDataSet = new LineDataSet(temps, context.getString(R.string.temp_label));
                LineDataSet windDataSet = new LineDataSet(winds, context.getString(R.string.wind_speed));
                tempDataSet.setValueTextSize(12);
                windDataSet.setValueTextSize(11);
                tempDataSet.setLineWidth(2);
                tempDataSet.setCircleRadius(6);
                tempDataSet.setCircleColor(Color.WHITE);
                tempDataSet.setCircleColorHole(Color.RED);
                tempDataSet.setColor(Color.RED);
                tempDataSet.setDrawCircles(true);
                tempDataSet.setDrawCircleHole(true);
                tempDataSet.setDrawFilled(false);
                tempDataSet.setDrawHorizontalHighlightIndicator(false);
                tempDataSet.setDrawHighlightIndicators(false);
                windDataSet.setLineWidth(2);
                windDataSet.setCircleRadius(6);
                windDataSet.setCircleColor(Color.WHITE);
                windDataSet.setCircleColorHole(Color.parseColor("#049e8c"));
                windDataSet.setColor(Color.parseColor("#049e8c"));
                windDataSet.setDrawCircles(true);
                windDataSet.setDrawCircleHole(true);
                windDataSet.setDrawFilled(false);
                windDataSet.setDrawHorizontalHighlightIndicator(false);
                windDataSet.setDrawHighlightIndicators(false);
                LineData tempLineData = new LineData(tempDataSet);
                tempLineData.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        DecimalFormat format = new DecimalFormat("##");
                        return format.format(value);
                    }
                });

                LineData windLineData = new LineData(windDataSet);
                windLineData.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        DecimalFormat format = new DecimalFormat("#.#");
                        return format.format(value);
                    }
                });
                view.updateChart(tempLineData, windLineData, offset);
                view.updateFiveDay(body.getList());
                isFinished += 1;
                if (isFinished == 2) {
                    view.cancelProgressDialog();
                }
                AlertReceiver.scheduleAlert(context, list);
            }

            @Override
            public void onFailure(Call<HourForecastResponse> call, Throwable t) {
                view.onFailed();
                isFinished += 1;
                if (isFinished == 2) {
                    view.cancelProgressDialog();
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void search(double lat, double lon) {
        latitude = lat;
        longitude = lon;
        initData();
    }

    @Override
    public void prepareDay(List<HourForecast> list, int[] dateTime) {
        List<HourForecast> day = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            HourForecast element = list.get(i);
            if (TimeUtils.equalsDate(element.getDateTime(), dateTime)) {
                day.add(element);
            }
        }
        if (day.size() != 0) {
            view.showDaySheet(day);
        }
    }
}
