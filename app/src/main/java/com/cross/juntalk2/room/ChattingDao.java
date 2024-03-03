package com.cross.juntalk2.room;


import static androidx.room.OnConflictStrategy.ABORT;
import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ChattingDao {

    @Insert(onConflict = REPLACE)
    void insert(ChattingData chattingData);

    @Delete
    void delete(ChattingData chattingData);

    @Delete
    void reset(List<ChattingData> chattingDataList);

    @Query("SELECT * FROM chatting_conversation where room_Uuid IN (:room_Uuid)")
    List<ChattingData> getAll(String room_Uuid);

    @Query("Delete from chatting_conversation where room_Uuid IN (:room_Uuid)")
    void removeAllConversation(String room_Uuid);

    @Query("Delete from chatting_conversation")
    void removeAllDataInConversation();

    @Query("update chatting_conversation set userId = :changeUserId where userId = :currentUserId ")
    void updateUserId(String currentUserId, String changeUserId);

    @Query("update chatting_conversation set userImage = :changeUserMainPhoto where userId = :currentUserId ")
    void updateUserMainPhoto(String currentUserId, String changeUserMainPhoto);
}
