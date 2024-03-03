package com.cross.juntalk2.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ChattingData.class}, version = 1, exportSchema = false)
public abstract class ChattingDataDB extends RoomDatabase {
    private static ChattingDataDB database;

    private static String DATABASE_NAME = "database";

    public synchronized static ChattingDataDB getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(), ChattingDataDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;
    }

    public abstract ChattingDao chattingDao();

}
