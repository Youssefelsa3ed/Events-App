package com.android.example.myapplication.GoogleCalenderViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.api.services.calendar.model.Event;

import java.util.List;

public class GoogleCalenderViewModel extends AndroidViewModel {

    private GoogleCalenderRepository repository;
    private LiveData<List<Event>> allNotes;
    public GoogleCalenderViewModel(@NonNull Application application) {
        super(application);
        repository = new GoogleCalenderRepository(application);
        allNotes = repository.getAllEvents();
    }

    public void updateEvent(Event event){
        repository.updateEvent(event);
    }

    public LiveData<List<Event>> getAllEvents() {
        return allNotes;
    }

    public Event getEvent(String id){
        return repository.getEvent(id);
    }
}
