package com.android.example.myapplication.GoogleCalenderViewModel;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.example.myapplication.ItemListActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleCalenderRepository {
    public static final int RC_AUTHORIZE_CALENDER = 2;
    public static final int RC_REAUTHORIZE = 0;
    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private Context context;
    private GoogleSignInAccount account;
    private static MutableLiveData<List<Event>> allEvents;

    GoogleCalenderRepository(Context context) {
        this.context = context;
        if(allEvents == null)
            allEvents = new MutableLiveData<>();
        account = GoogleSignIn.getLastSignedInAccount(context);

        Scope SCOPE_CALENDER = new Scope("https://www.googleapis.com/auth/calendar.events");
        Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);

        if (!GoogleSignIn.hasPermissions(account, SCOPE_CALENDER, SCOPE_EMAIL))
            GoogleSignIn.requestPermissions((Activity) context, RC_AUTHORIZE_CALENDER, account, SCOPE_CALENDER, SCOPE_EMAIL);
        else
            getEvents();

    }

    private class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            List<Event> result = null;
            try {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                context,
                                Collections.singleton(
                                        "https://www.googleapis.com/auth/calendar.events")
                        );
                credential.setSelectedAccount(account.getAccount());
                Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("CalenderTask")
                        .build();
                Events connectionsResponse = service.events()
                        .list("primary")
                        .execute();
                result = connectionsResponse.getItems();
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                Toast.makeText(context, "Can't get your events unless you give the application an access to your Google account!", Toast.LENGTH_SHORT).show();
                ((Activity)context).startActivityForResult(userRecoverableException.getIntent(),RC_REAUTHORIZE);
            } catch (IOException e) {
                // Other non-recoverable exceptions.
            }

            return result;
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            Log.i("events", events.size() + "");
            if(events.size() > 0)
                Log.i("events", events.get(0).getStatus() + "");
            allEvents.postValue(events);
        }
    }

    private class UpdateAnEvent extends AsyncTask<Void, Void, Event> {
        private Event mEvent;

        UpdateAnEvent(Event mEvent) {
            this.mEvent = mEvent;
        }

        @Override
        protected Event doInBackground(Void... voids) {
            Event result = null;
            try {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(
                                context,
                                Collections.singleton("https://www.googleapis.com/auth/calendar.events")
                        );
                credential.setSelectedAccount(account.getAccount());
                Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("CalenderTask")
                        .build();
                result = service.events()
                        .update("primary", mEvent.getId(), mEvent)
                        .execute();
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                Toast.makeText(context, "Can't get your events unless you give the application an access to your Google account!", Toast.LENGTH_SHORT).show();
                ((Activity)context).startActivityForResult(userRecoverableException.getIntent(),RC_REAUTHORIZE);
            } catch (IOException e) {
                // Other non-recoverable exceptions.
            }

            return result;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            getEvents();
        }
    }

    private void getEvents() {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            GetEventsTask task = new GetEventsTask();
            task.execute();
        }
    }

    MutableLiveData<List<Event>> getAllEvents(){
        return allEvents;
    }

    void updateEvent(Event event) {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            UpdateAnEvent task = new UpdateAnEvent(event);
            task.execute();
        }
    }

    Event getEvent(String id){
        for (Event event: Objects.requireNonNull(allEvents.getValue())) {
            if(event.getId().equals(id))
                return event;
        }
        return null;
    }

}
