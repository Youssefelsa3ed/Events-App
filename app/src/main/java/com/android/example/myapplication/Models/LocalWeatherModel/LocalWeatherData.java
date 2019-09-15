package com.android.example.myapplication.Models.LocalWeatherModel;

public class LocalWeatherData {
    private String date;
    private String weatherIcon;
    private double tempMin;
    private double tempMax;
    private double windSpeed;
    private int humidity;
    private int count;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getWindSpeed() {
        return windSpeed / count    ;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getHumidity() {
        return humidity / count;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public LocalWeatherData(String date, String weatherIcon, double tempMin, double tempMax, double windSpeed, int humidity, int count) {
        this.date = date;
        this.weatherIcon = weatherIcon;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.count = count;
    }
}
