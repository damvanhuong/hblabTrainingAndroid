package com.esp.weather.utils;

import com.esp.weather.data.network.ResponseService;
import com.esp.weather.data.network.RetrofitClient;

public class APIUtils {

    public static ResponseService getResponseService() {
        return RetrofitClient.getClient().create(ResponseService.class);
    }
}
