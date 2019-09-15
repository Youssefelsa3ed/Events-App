package com.android.example.myapplication.LocalDatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {EventsDB.class},version = 1, exportSchema = false)
public abstract class EventsRoomDatabase extends RoomDatabase {

    public abstract EventsDAO eventsDAO();
    private static EventsRoomDatabase INSTANCE;
    public static EventsRoomDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (EventsDB.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                            EventsRoomDatabase.class,"events")
                            .fallbackToDestructiveMigration()
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

}
