package com.android.example.myapplication.UI.Splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import com.android.example.myapplication.ItemListActivity;
import com.android.example.myapplication.UI.Login.LoginActivity;
import com.android.example.myapplication.Utilities.SharedPrefManager;

import static android.content.Context.MODE_PRIVATE;

public class SplashPresenter implements SplashViewPresenter {

    private SplashActivity context ;

    SplashPresenter(SplashActivity context) {
        this.context = context;
        loading();
    }

    @Override
    public void loading() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent intent;
                if (SharedPrefManager.getInstance(context).getLoginStatus())
                    intent = new Intent(context, ItemListActivity.class);
                else
                    intent = new Intent(context, LoginActivity.class);

                context.startActivity(intent);
                context.finish();
            }
        }.start();
    }
}
