package com.android.example.myapplication.LocalDatabase;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EventsDB {
    @PrimaryKey
    @NonNull
    private String id;
    private String location;
    private String organizerEmail;
    private String eventStartDate;
    private String eventEndDate;
    private String eventStartTime;
    private String eventEndTime;
    private double temperatureMax;
    private double temperatureMin;
    private double windSpeed;
    private int humidity;
    private String weatherIcon;
    private String description;
    private String status;
    private String summary;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getLocation() {
        return location == null ? "" : location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizerEmail() {
        return organizerEmail == null ? "" : organizerEmail;
    }

    public void setOrganizerEmail(String organizerEmail) {
        this.organizerEmail = organizerEmail;
    }

    public String getEventStartDate() {
        return eventStartDate == null ? "" : eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate == null ? "" : eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getWeatherIcon() {
        return weatherIcon == null ? "" : weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary == null ? "" : summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEventStartTime() {
        return eventStartTime == null ? "" : eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime == null ? "" : eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public EventsDB(@NonNull String id, String location, String organizerEmail, String eventStartDate, String eventEndDate, double temperatureMax, double temperatureMin, double windSpeed, int humidity, String weatherIcon, String description, String status, String summary, String eventStartTime, String eventEndTime) {
        this.id = id;
        this.location = location;
        this.organizerEmail = organizerEmail;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.temperatureMax = temperatureMax;
        this.temperatureMin = temperatureMin;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.weatherIcon = weatherIcon;
        this.description = description;
        this.status = status;
        this.summary = summary;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }
}
