package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UnReadCountModel {

    @SerializedName("userId")
    public String userId;

    @SerializedName("user_Index")
    public int user_Index;

    @SerializedName("room_Index")
    public int room_Index;

    @SerializedName("room_Conversation")
    public String room_Conversation;

    @SerializedName("room_Uuid")
    public String room_Uuid;

    @SerializedName("conversationTime")
    public String conversationTime;

    @SerializedName("outUserName")
    public String outUserName;

    @SerializedName("unReadCount")
    public int unReadCount;


}
