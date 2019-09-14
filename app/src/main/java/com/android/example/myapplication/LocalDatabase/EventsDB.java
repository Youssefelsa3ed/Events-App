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
    private String creatorEmail;
    private String eventStartDate;
    private String eventEndDate;
    private String temperature;
    private String humidity;
    private String weatherIcon;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public EventsDB(@NonNull String id, String location, String creatorEmail, String eventStartDate, String eventEndDate, String temperature, String humidity, String weatherIcon) {
        this.id = id;
        this.location = location;
        this.creatorEmail = creatorEmail;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherIcon = weatherIcon;
    }
}
