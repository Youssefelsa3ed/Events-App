package com.android.example.myapplication.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.example.myapplication.Models.LocalWeatherModel.LocalWeatherData;
import com.google.gson.Gson;

public class SharedPrefManager {
    private final static String SHARED_PREF_NAME = "calender_task";
    private final static String SHARED_WEATHER_DATA = "weather_data";
    private final static String LOGIN_STATUS = "login_status";
    private final static String FIRST_DATE = "first_data";
    final static String FIRST_TIME = "shared_first_time";
    private static Context mContext;
    private static SharedPrefManager mInstance ;

    public static synchronized SharedPrefManager getInstance(Context context) {
        mContext = context ;
        if (mInstance == null) {
            mInstance = new SharedPrefManager();
        }
        return mInstance;
    }

    public Boolean getLoginStatus() {
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                SHARED_PREF_NAME, 0);
        return sharedPreferences.getBoolean(LOGIN_STATUS, false);
    }

    public void setLoginStatus(Boolean status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN_STATUS, status);
        editor.apply();
    }

    public LocalWeatherData getWeatherData(String date){
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_WEATHER_DATA, Context.MODE_PRIVATE);
        String json = prefs.getString(date, "");
        if(json == null || json.equals(""))
            return null;

        Gson gson = new Gson();
        return gson.fromJson(json, LocalWeatherData.class) ;
    }

    public void saveWeatherData(LocalWeatherData data, String date){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHARED_WEATHER_DATA, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(date, json);
        editor.apply();
    }

    public String getFirstWeatherData(){
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_WEATHER_DATA, Context.MODE_PRIVATE);
        return prefs.getString(FIRST_DATE, "");
    }

    public void setFirstWeatherDate(String date){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHARED_WEATHER_DATA, Context.MODE_PRIVATE).edit();
        editor.putString(FIRST_DATE, date);
        editor.apply();
    }

    public void clearSavedWeatherData(){
        mContext.getSharedPreferences(SHARED_WEATHER_DATA, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * this method is responsible for user logout and clearing cache
     */
    public void Logout() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
