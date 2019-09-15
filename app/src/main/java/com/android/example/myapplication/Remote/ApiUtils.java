package com.android.example.myapplication.Remote;

public class ApiUtils {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    public static WeatherService getWeatherService()
    {
        return RetrofitClient.getClient(BASE_URL).create(WeatherService.class);
    }
}
