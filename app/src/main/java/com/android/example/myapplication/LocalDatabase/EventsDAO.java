package com.android.example.myapplication.LocalDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventsDAO {
    //select data from table
    @Query("SELECT * FROM EventsDB")
    LiveData<List<EventsDB>> getEvents();
    //clear all data from table
    @Query("DELETE FROM EventsDB")
    void deleteTable();
    //update data in table
    @Update()
    void updateEvent(EventsDB eventsDB);
    //insert data into table
    @Insert
    void insertEvents(List<EventsDB> eventsList);
    //delete data from table
    @Query("DELETE FROM EventsDB WHERE id = :id")
    void deleteEvent(String id);
}
