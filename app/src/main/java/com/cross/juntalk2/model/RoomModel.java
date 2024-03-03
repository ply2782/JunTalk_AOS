package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RoomModel implements Serializable {

    public enum RoomType {
        G, P, C, T , J;

        public RoomType fromInteger(String x) {
            switch (x) {
                case "G":
                    return G;
                case "P":
                    return P;
                case "C":
                    return C;
                case "T":
                    return T;
                case "J":
                    return J;
            }
            return null;
        }
    }

    @SerializedName("userToken")
    public String userToken;
    @SerializedName("room_Uuid")
    public String room_Uuid;
    @SerializedName("roomType")
    public RoomType roomType;
    @SerializedName("room_Index")
    public int room_Index;
    @SerializedName("room_RegDate")
    public String room_RegDate;
    @SerializedName("room_JoinCount")
    public int room_joinCount;
    @SerializedName("room_Conversation")
    public String room_Conversation;
    @SerializedName("room_JoinPeopleName")
    public String room_JoinPeopleName;
    @SerializedName("room_JoinPeopleImage")
    public String room_JoinPeopleImage;
    @SerializedName("room_Title")
    public String room_Title;
    @SerializedName("joinPeopleImageList")
    public List<String> joinPeopleImageList;
    @SerializedName("conversationTime")
    public String conversationTime;
    @SerializedName("otherUserId")
    public String otherUserId;
    @SerializedName("unreadCount")
    public int unreadCount;
    @SerializedName("alarm")
    public boolean alarm;
    @SerializedName("fromUser")
    public String fromUser;
    @SerializedName("toUser")
    public String toUser;
    @SerializedName("mainRoomPhoto")
    public String mainRoomPhoto;
    @SerializedName("roomHashTag")
    public String roomHashTag;
    @SerializedName("joinRoomContent")
    public String joinRoomContent;



}
