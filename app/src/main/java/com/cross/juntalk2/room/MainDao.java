package com.cross.juntalk2.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

import com.cross.juntalk2.model.MyModel;

@Dao
public interface MainDao {
    @Insert(onConflict = REPLACE)
    void insert(MainRoom mainData);

    @Delete
    void delete(MainRoom mainData);

    @Delete
    void reset(List<MainRoom> mainData);

    @Query("UPDATE user SET userId = :sText WHERE user_Index = :sID")
    void update(int sID, String sText);

    @Query("SELECT * FROM user")
    List<MainRoom> getAll();
}