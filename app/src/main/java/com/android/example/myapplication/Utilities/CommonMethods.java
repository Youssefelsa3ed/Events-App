package com.android.example.myapplication.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.example.myapplication.R;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CommonMethods {
    public static void handleException(Context context, Throwable t) {
        if (t instanceof SocketTimeoutException || t instanceof UnknownHostException || t instanceof ConnectException)
            makeToast(context, context.getResources().getString(R.string.check_connection));
        else
            makeToast(context, t.getLocalizedMessage());

    }

    private static void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
