package com.android.example.myapplication.Services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.android.example.myapplication.UI.EventsList.EventsListPresenter;

import java.util.Timer;
import java.util.TimerTask;

public class EventsRefreshService extends IntentService {
    private static final String TAG = "EventsRefreshService";
    public EventsRefreshService() {
        super("EventsRefreshService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new EventsListPresenter.GetEventsTask().execute();
            }
        }, 30000, 30000);
    }
}
