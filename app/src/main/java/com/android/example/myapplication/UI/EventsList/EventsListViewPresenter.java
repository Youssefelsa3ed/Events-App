package com.android.example.myapplication.UI.EventsList;

public interface EventsListViewPresenter {
    boolean isConnected();
    void setLayout();
    void setRecycler();
    void reAssignLiveDataObserver();
}
