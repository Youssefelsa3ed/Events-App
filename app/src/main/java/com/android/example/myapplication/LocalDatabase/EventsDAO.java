package com.android.example.myapplication.LocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventsDAO {
    //select data from database
    @Query("SELECT * FROM EventsDB")
    LiveData<List<EventsDB>> getEvents();
    // delete data from database
    @Query("DELETE FROM EventsDB")
    void deleteTable();
    //update data in database
    @Update()
    void updateEvent(EventsDB eventsDB);

    @Insert
    void insertEvents(List<EventsDB> eventsList);
}
