package com.android.example.myapplication.UI.EventsList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.android.example.myapplication.GoogleCalenderViewModel.GoogleCalenderViewModel;
import com.android.example.myapplication.LocalDatabase.EventsDB;
import com.android.example.myapplication.Models.LocalWeatherModel.LocalWeatherData;
import com.android.example.myapplication.R;
import com.android.example.myapplication.UI.Adapters.EventsAdapter;
import com.android.example.myapplication.UI.Splash.SplashActivity;
import com.android.example.myapplication.Utilities.CommonMethods;
import com.android.example.myapplication.Utilities.SharedPrefManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    public void setLayout() {
        context.setSupportActionBar(context.toolbar);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toolbarLayout = inflater.inflate(R.layout.main_header, context.toolbar);
        ImageView menu = toolbarLayout.findViewById(R.id.imgOpenNavDrawer);
        TextView txtTitle = toolbarLayout.findViewById(R.id.txtTitle);
        menu.setOnClickListener(v -> {
            if (context.drawerLayout.isDrawerOpen(GravityCompat.START))
                context.drawerLayout.closeDrawers(); //CLOSE Nav Drawer!
            else
                context.drawerLayout.openDrawer(GravityCompat.START, true); //OPEN Nav Drawer!
        });
        txtTitle.setText(context.getTitle());

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
        context.navView.setNavigationItemSelectedListener(context);
        View header = context.navView.getHeaderView(0);
        SimpleDraweeView userImage = header.findViewById(R.id.imageView);
        TextView userName = header.findViewById(R.id.txtUserName);
        TextView userEmail = header.findViewById(R.id.txtUserEmail);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        userImage.setImageURI(Objects.requireNonNull(account).getPhotoUrl());
        userName.setText(Objects.requireNonNull(account).getDisplayName());
        userEmail.setText(Objects.requireNonNull(account).getEmail());
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
            if (CommonMethods.isConnected(context)) {
                event.setStatus(action);
                calenderViewModel.updateEvent(event);
                if (!action.equals("declined"))
                    eventsAdapter.notifyDataSetChanged();

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
            } else {
                SimpleDateFormat format1 = new SimpleDateFormat("EEEE, MMM d", Locale.US);
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                context.txtCreator.setText(event.getCreatorEmail());
                try {
                    if(event.getEventEndDate().equals(event.getEventStartDate())) {
                        context.txtEventDate.setText(String.format("%s • %s - %s",
                                format1.format(format2.parse(event.getEventStartDate())),
                                event.getEventStartTime().substring(0, 5), event.getEventEndTime().substring(0, 5)));
                    }
                    else
                        context.txtEventDate.setText(String.format("%s • %s \n%s • %s",
                                format1.format(format2.parse(event.getEventStartDate())), event.getEventStartTime().substring(0, 5),
                                format1.format(format2.parse(event.getEventEndDate())), event.getEventEndTime().substring(0, 5)));
                }
                catch (ParseException e) {
                    if(event.getEventEndDate().equals(event.getEventStartDate())) {
                        context.txtEventDate.setText(String.format("%s • %s - %s",
                                (event.getEventStartDate()),
                                event.getEventStartTime().substring(0, 5), event.getEventEndTime().substring(0, 5)));
                    }
                    else
                        context.txtEventDate.setText(String.format("%s • %s \n%s • %s",
                                event.getEventStartDate(), event.getEventStartTime().substring(0, 5),
                                event.getEventEndDate(), event.getEventEndTime().substring(0, 5)));
                }
                context.txtLocation.setText(event.getLocation());
                context.txtStatus.setText(event.getStatus().replace("accepted","Accepted")
                        .replace("tentative", "Pending").replace("needsAction", "Pending"));
                context.txtSummary.setText(event.getSummary());
                context.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void reAssignLiveDataObserver() {
        calenderViewModel = ViewModelProviders.of(context).get(GoogleCalenderViewModel.class);
        calenderViewModel.getAllEvents().observe(context, events -> eventsAdapter.submitList(events));
    }

    @Override
    public void logout() {
        SharedPrefManager.getInstance(context).Logout();
        SharedPrefManager.getInstance(context).clearSavedWeatherData();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mGoogleSignInClient.revokeAccess();
        mGoogleSignInClient.signOut();
        context.startActivity(new Intent(context, SplashActivity.class));
        context.finishAffinity();
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
                String status = "";
                if (e.getAttendees() != null)
                    for (EventAttendee attendee : e.getAttendees()) {
                        if (attendee.getEmail().equals(Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)).getEmail())) {
                            status = attendee.getResponseStatus();
                            break;
                        }
                    }
                if (status.equals("declined"))
                    continue;
                String startDate = e.getStart().getDate() == null ? e.getStart().getDateTime().toString().split("T")[0] : e.getStart().getDate().toString();
                String endDate = e.getEnd().getDate() == null ? e.getEnd().getDateTime().toString().split("T")[0] : e.getEnd().getDate().toString();
                String startTime = e.getStart().getDateTime() == null ? "" : e.getStart().getDateTime().toString().split("T")[1];
                String endTime = e.getEnd().getDateTime() == null ? "" : e.getEnd().getDateTime().toString().split("T")[1];
                LocalWeatherData weatherData = SharedPrefManager.getInstance(context).getWeatherData(startDate);
                if (weatherData != null)
                    eventsDBList.add(new EventsDB(e.getId(), e.getLocation(), e.getCreator().getEmail(), startDate, endDate, weatherData.getTempMax(), weatherData.getTempMin(), weatherData.getWindSpeed(), weatherData.getHumidity(), weatherData.getWeatherIcon(), e.getDescription(), status, e.getSummary(), startTime, endTime));
                else
                    eventsDBList.add(new EventsDB(e.getId(), e.getLocation(), e.getCreator().getEmail(), startDate, endDate, 0, 0, 0, 0, "", e.getDescription(), status, e.getSummary(), startTime, endTime));
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
                if (event.getAttendees() != null) {
                    for (EventAttendee attendee : event.getAttendees()) {
                        if (attendee.getEmail().equals(Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(context)).getEmail())) {
                            attendee.setResponseStatus(mEvent.getStatus());
                            break;
                        }
                    }
                }
                // Update the event
                result = service.events().update("primary", mEvent.getId(), event).execute();
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
