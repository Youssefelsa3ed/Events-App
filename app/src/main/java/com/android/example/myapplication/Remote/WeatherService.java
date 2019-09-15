package com.android.example.myapplication.Remote;

import com.android.example.myapplication.Models.WeatherForecast.WeatherForecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("forecast")
    Call<WeatherForecast> getWeatherForecast(
            @Query("lat") double lat,
            @Query("lon") double lng,
            @Query("units") String units,
            @Query("appid") String appId
    );
}
