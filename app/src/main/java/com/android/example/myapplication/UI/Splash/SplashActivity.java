package com.android.example.myapplication.UI.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new SplashPresenter(this);
    }
}
