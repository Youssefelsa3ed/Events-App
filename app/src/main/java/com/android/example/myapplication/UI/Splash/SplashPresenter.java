package com.android.example.myapplication.UI.Splash;

import android.content.Intent;
import android.os.CountDownTimer;

import com.android.example.myapplication.UI.EventsList.EventListActivity;
import com.android.example.myapplication.UI.Login.LoginActivity;
import com.android.example.myapplication.Utilities.SharedPrefManager;

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
                    intent = new Intent(context, EventListActivity.class);
                else
                    intent = new Intent(context, LoginActivity.class);

                context.startActivity(intent);
                context.finish();
            }
        }.start();
    }
}
