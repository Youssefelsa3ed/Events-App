package com.android.example.myapplication.UI.EventsList;

import java.util.Date;

public interface EventsListViewPresenter {
    void setLayout();
    void setRecycler();
    void askForReschedule(String suggestion, String organizerEmail, String title);
    void sendEmail(String suggestion, String organizerEmail, String title);
    long getDifference(String second, String first);
    void reAssignLiveDataObserver();
    void logout();
}
