package com.android.example.myapplication.GoogleCalenderViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.example.myapplication.LocalDatabase.EventsDB;

import java.util.List;

public class GoogleCalenderViewModel extends AndroidViewModel {

    private GoogleCalenderRepository repository;
    private LiveData<List<EventsDB>> allNotes;
    public GoogleCalenderViewModel(@NonNull Application application) {
        super(application);
        repository = new GoogleCalenderRepository(application);
        allNotes = repository.getAllEvents();
    }

    public void insertEvents(List<EventsDB> eventsDBS){
        repository.insertEvents(eventsDBS);
    }

    public void updateEvent(EventsDB event){
        repository.updateEvent(event);
    }

    public LiveData<List<EventsDB>> getAllEvents() {
        return allNotes;
    }

    public EventsDB getEvent(String id){
        return repository.getEvent(id);
    }
}
