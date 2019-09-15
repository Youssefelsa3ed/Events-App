package com.android.example.myapplication.Models.WeatherForecast;

import com.google.gson.annotations.SerializedName;

public class Wind{

	@SerializedName("deg")
	private double deg;

	@SerializedName("speed")
	private double speed;

	public void setDeg(double deg){
		this.deg = deg;
	}

	public double getDeg(){
		return deg;
	}

	public void setSpeed(double speed){
		this.speed = speed;
	}

	public double getSpeed(){
		return speed;
	}
}