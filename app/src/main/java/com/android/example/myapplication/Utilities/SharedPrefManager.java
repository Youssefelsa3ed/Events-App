package com.android.example.myapplication.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.example.myapplication.Models.UserModel.User;
import com.google.gson.Gson;

public class SharedPrefManager {
    final static String SHARED_PREF_NAME = "calender_task";
    final static String LOGIN_STATUS = "login_status";
    final static String USER_DATA = "user_data";
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

    public void setFirstTime(Boolean status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_TIME, status);
        editor.apply();
    }

    public User getUserData(){
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(USER_DATA, "");
        return gson.fromJson(json, User.class) ;
    }

    public void setUserData(User data){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(USER_DATA, json);
        editor.apply();
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
