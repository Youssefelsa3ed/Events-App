package com.android.example.myapplication.GoogleCalenderViewModel;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.android.example.myapplication.LocalDatabase.EventsDAO;
import com.android.example.myapplication.LocalDatabase.EventsDB;
import com.android.example.myapplication.LocalDatabase.EventsRoomDatabase;
import com.android.example.myapplication.UI.EventsList.EventsListPresenter;
import com.android.example.myapplication.Utilities.CommonMethods;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;
import java.util.Objects;

class GoogleCalenderRepository {

    private Context context;
    private GoogleSignInAccount account;
    private LiveData<List<EventsDB>> allEvents;
    private EventsDAO eventsDAO;

    GoogleCalenderRepository(Context context) {
        this.context = context;
        EventsRoomDatabase db = EventsRoomDatabase.getDatabase(context);
        eventsDAO = db.eventsDAO();
        allEvents = eventsDAO.getEvents();
        account = GoogleSignIn.getLastSignedInAccount(context);

    }

    private static class UpdateEventLocal extends AsyncTask<Void, Void, Void> {
        private EventsDB event;
        private EventsDAO eventsDAO;

        UpdateEventLocal(EventsDAO eventsDAO, EventsDB event) {
            this.event = event;
            this.eventsDAO = eventsDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            eventsDAO.updateEvent(event);
            return null;
        }
    }

    private static class InsertEventsLocal extends AsyncTask<Void, Void, Void> {
        private List<EventsDB> events;
        private EventsDAO eventsDAO;

        InsertEventsLocal(EventsDAO eventsDAO, List<EventsDB> events) {
            this.events = events;
            this.eventsDAO = eventsDAO;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            eventsDAO.deleteTable();
            eventsDAO.insertEvents(events);
            return null;
        }
    }

    private void getEvents() {
        account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null)
            new EventsListPresenter.GetEventsTask().execute();
    }

    void insertEvents(List<EventsDB> eventsDBS){
        new InsertEventsLocal(eventsDAO, eventsDBS).execute();
    }

    LiveData<List<EventsDB>> getAllEvents() {
        if (CommonMethods.isConnected(context))
            getEvents();
        return allEvents;
    }

    void updateEvent(EventsDB event) {
        if (account != null)
            new EventsListPresenter.UpdateAnEvent(event).execute();
        new UpdateEventLocal(eventsDAO, event).execute();
    }

    EventsDB getEvent(String id) {
        for (EventsDB event : Objects.requireNonNull(allEvents.getValue())) {
            if (event.getId().equals(id))
                return event;
        }
        return null;
    }

}
