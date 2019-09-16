package com.android.example.myapplication.UI.Splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.example.myapplication.Models.LocalWeatherModel.LocalWeatherData;
import com.android.example.myapplication.Models.WeatherForecast.ListItem;
import com.android.example.myapplication.Models.WeatherForecast.WeatherForecast;
import com.android.example.myapplication.R;
import com.android.example.myapplication.Remote.ApiUtils;
import com.android.example.myapplication.UI.EventsList.EventListActivity;
import com.android.example.myapplication.UI.Login.LoginActivity;
import com.android.example.myapplication.Utilities.CommonMethods;
import com.android.example.myapplication.Utilities.CurrentLocation;
import com.android.example.myapplication.Utilities.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashPresenter implements SplashViewPresenter {

    private SplashActivity context ;
    private CurrentLocation currentLocation;
    private boolean checkedPermission;
    static final int GPS_ENABLED = 5;
    private LocationManager mLocationManager;
    private Dialog noGpsDialog;
    private Location location;
    SplashPresenter(SplashActivity context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        checkedPermission = false;
        currentLocation = new CurrentLocation(context);
        String currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        if(SharedPrefManager.getInstance(context).getWeatherData(currentDay) == null) {
            checkPermissions();
        }
        else if(SharedPrefManager.getInstance(context).getFirstWeatherData().equals(currentDay))
            proceedToNextActivity();
        else{
            SharedPrefManager.getInstance(context).clearSavedWeatherData();
            checkPermissions();
        }
    }

    @Override
    public void checkPermissions() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(noGpsDialog == null) {
                buildAlertMessageNoGps();
            }
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context
                , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, CurrentLocation.LOCATION_PERMISSION);
            return ;
        }

        location = currentLocation.getCurrentLocation();

        if(!context.animationView.isAnimating()) {
            context.animationView.playAnimation();
            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                    if (location != null) {
                        if (!checkedPermission) {
                            getWeatherData();
                            this.cancel();
                        }
                    }
                    else
                        location = currentLocation.getCurrentLocation();
                }

                public void onFinish() {
                    if(location != null)
                        getWeatherData();
                    else
                        checkPermissions();

                }
            }.start();
        }

    }

    private void buildAlertMessageNoGps() {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        noGpsDialog = new Dialog(context);
        noGpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        noGpsDialog.setContentView(R.layout.no_gps_dialog);
        Objects.requireNonNull(noGpsDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        Objects.requireNonNull(noGpsDialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        noGpsDialog.setCancelable(false);
        Button btnClose = noGpsDialog.findViewById(R.id.btnClose);
        Button btnOpen = noGpsDialog.findViewById(R.id.btnOpen);
        btnClose.setOnClickListener(v-> {
            noGpsDialog.cancel();
        });
        btnOpen.setOnClickListener(v-> {
            noGpsDialog.cancel();
            context.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), GPS_ENABLED);
        });
        noGpsDialog.show();
    }

    @Override
    public void getWeatherData() {
        checkedPermission = true;
        ApiUtils.getWeatherService().getWeatherForecast(location.getLatitude(),location.getLongitude(), "metric", context.getResources().getString(R.string.weath_api_key)).enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecast> call, @NonNull Response<WeatherForecast> response) {
                if(response.isSuccessful()){
                    if (response.body() != null && response.body().getCod().equals("200")) {
                        saveWeatherForecast(response.body().getList());
                    }
                }
                else
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();

                proceedToNextActivity();
            }

            @Override
            public void onFailure(@NonNull Call<WeatherForecast> call, @NonNull Throwable t) {
                CommonMethods.handleException(context, t);
                proceedToNextActivity();
            }
        });
    }

    private void saveWeatherForecast(List<ListItem> list) {
        String date = "";
        double tempMin = 0;
        double tempMax = 100;
        int count = 0, humidity = 0;
        double windSpeed = 0;
        for (int i = 0; i < list.size(); i++) {
            ListItem item = list.get(i);
            if(item.getDtTxt().split(" ")[0].equals(date)) {
                count++;
                if(tempMin > item.getMain().getTempMin())
                    tempMin = item.getMain().getTempMin();

                if(tempMax < item.getMain().getTempMax())
                    tempMax = item.getMain().getTempMax();
            }
            else {
                count = 0;
                windSpeed = 0;
                humidity = 0;
                tempMin = item.getMain().getTempMin();
                tempMax = item.getMain().getTempMax();
            }

            date = item.getDtTxt().split(" ")[0];
            String weatherIcon = "https://openweathermap.org/img/wn/"+ item.getWeather().get(0).getIcon()+".png";
            windSpeed += item.getWind().getSpeed();
            humidity += item.getMain().getHumidity();
            SharedPrefManager.getInstance(context).saveWeatherData(new LocalWeatherData(date, weatherIcon, tempMin, tempMax, windSpeed, humidity, count), date);
        }
        if(list.size() > 0)
            SharedPrefManager.getInstance(context).setFirstWeatherDate(list.get(0).getDtTxt().split(" ")[0]);
    }

    @Override
    public void proceedToNextActivity() {
        Intent intent;
        if (SharedPrefManager.getInstance(context).getLoginStatus())
            intent = new Intent(context, EventListActivity.class);
        else
            intent = new Intent(context, LoginActivity.class);

        context.startActivity(intent);
        context.finish();
    }


}
