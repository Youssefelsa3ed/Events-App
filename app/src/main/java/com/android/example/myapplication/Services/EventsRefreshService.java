package com.android.example.myapplication.Services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.example.myapplication.R;
import com.android.example.myapplication.UI.EventsList.EventsListPresenter;

import java.util.Timer;
import java.util.TimerTask;

import static com.android.example.myapplication.Utilities.App.CHANNEL_ID;

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

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getBaseContext().getResources().getString(R.string.app_name))
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .build();

            startForeground(1, notification);
        }
    }
}
