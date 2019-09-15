package com.android.example.myapplication.UI.EventsList;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.android.example.myapplication.GoogleCalenderViewModel.GoogleCalenderViewModel;
import com.android.example.myapplication.LocalDatabase.EventsDB;
import com.android.example.myapplication.R;
import com.android.example.myapplication.UI.Adapters.EventsAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class EventsListPresenter implements EventsListViewPresenter {
    static final int RC_AUTHORIZE_CALENDER = 2;
    static final int RC_REAUTHORIZE = 0;
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static EventListActivity context;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static GoogleCalenderViewModel calenderViewModel;
    private EventsAdapter eventsAdapter;

    EventsListPresenter(EventListActivity context) {
        EventsListPresenter.context = context;
        setLayout();
        setRecycler();
    }

    @Override
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void setLayout() {
        context.setSupportActionBar(context.toolbar);
        context.toolbar.setTitle(context.getTitle());
        if (context.findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        } else {
            mTwoPane = false;
            context.eventLayout = context.findViewById(R.id.eventLayout);
            context.bottomSheetBehavior = BottomSheetBehavior.from(context.eventLayout);
            context.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void setRecycler() {
        eventsAdapter = new EventsAdapter();
        context.rvEvents.setAdapter(eventsAdapter);
        Scope SCOPE_CALENDER = new Scope("https://www.googleapis.com/auth/calendar.events");
        Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), SCOPE_CALENDER, SCOPE_EMAIL))
            GoogleSignIn.requestPermissions(context, RC_AUTHORIZE_CALENDER, GoogleSignIn.getLastSignedInAccount(context), SCOPE_CALENDER, SCOPE_EMAIL);
        else {
            calenderViewModel = ViewModelProviders.of(context).get(GoogleCalenderViewModel.class);
            calenderViewModel.getAllEvents().observe(context, events -> eventsAdapter.submitList(events));
        }
        eventsAdapter.setOnChipClickListener((event, position, action) -> {
            if (isConnected()) {
                event.setStatus(action);
                calenderViewModel.updateEvent(event);
                if (!action.equals("cancelled"))
                    eventsAdapter.notifyItemChanged(position);

            }
        });
        eventsAdapter.setOnItemClickListener(event -> {
            if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(EventDetailFragment.ARG_ITEM_ID, event.getId());
                    EventDetailFragment fragment = new EventDetailFragment();
                    fragment.setArguments(arguments);
                    context.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                }
                else
                    context.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    @Override
    public void reAssignLiveDataObserver() {
        calenderViewModel = ViewModelProviders.of(context).get(GoogleCalenderViewModel.class);
        calenderViewModel.getAllEvents().observe(context, events -> eventsAdapter.submitList(events));
    }

    public static class GetEventsTask extends AsyncTask<Void, Void, List<Event>> {

        @Override
        protected List<Event> doInBackground(Void... params) {
            List<Event> result = new ArrayList<>();
            try {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(context, Collections.singleton("https://www.googleapis.com/auth/calendar.events"));
                credential.setSelectedAccount(Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)).getAccount());
                Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("CalenderTask")
                        .build();

                String pageToken = null;
                do {
                    Events connectionsResponse = service.events()
                            .list("primary")
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .setPageToken(pageToken)
                            .execute();
                    result.addAll(connectionsResponse.getItems());
                    pageToken = connectionsResponse.getNextPageToken();
                }
                while (pageToken != null);
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                // Explain to the user again why you need these OAuth permissions
                // And prompt the resolution to the user again:
                context.startActivityForResult(userRecoverableException.getIntent(), RC_REAUTHORIZE);
            } catch (IOException ignored) {
                // Other non-recoverable exceptions.
            }

            return result;
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            if (events.size() == 0)
                return;
            List<EventsDB> eventsDBList = new ArrayList<>();
            for (Event e : events) {
                eventsDBList.add(new EventsDB(e.getId(), e.getLocation(), e.getCreator().getEmail(), e.getStart().getDateTime().toString(), e.getEnd().getDateTime().toString(), "", "", "", e.getDescription(), e.getStatus()));
            }
            calenderViewModel.insertEvents(eventsDBList);
        }
    }

    public static class UpdateAnEvent extends AsyncTask<Void, Void, Event> {
        private EventsDB mEvent;

        public UpdateAnEvent(EventsDB mEvent) {
            this.mEvent = mEvent;
        }

        @Override
        protected Event doInBackground(Void... voids) {
            Event result = null;
            try {
                GoogleAccountCredential credential =
                        GoogleAccountCredential.usingOAuth2(context, Collections.singleton("https://www.googleapis.com/auth/calendar.events"));
                credential.setSelectedAccount(Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)).getAccount());
                Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                        .setApplicationName("CalenderTask")
                        .build();
                // Retrieve the event from the API
                Event event = service.events().get("primary", mEvent.getId()).execute();
                // Make a change
                Event updatedEvent = event.setStatus(mEvent.getStatus());
                // Update the event
                result = service.events().update("primary", mEvent.getId(), updatedEvent).execute();
            } catch (UserRecoverableAuthIOException userRecoverableException) {
                // Explain to the user again why you need these OAuth permissions
                // And prompt the resolution to the user again:
                context.startActivityForResult(userRecoverableException.getIntent(), RC_REAUTHORIZE);
            } catch (IOException ignored) {
                // Other non-recoverable exceptions.
            }

            return result;
        }
    }
}
