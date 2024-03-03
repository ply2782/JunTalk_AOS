package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChattingModel implements Serializable {


    @Getter
    public enum UserState{
        IN,OUT, CONVERSATION, NULL , INSIDE,OUTSIDE

    }
    @Getter
    public enum MessageType {
        ENTER, EXIT, CONVERSATION , DATE;

        public MessageType fromInteger(String x) {
            switch (x) {
                case "ENTER":
                    return ENTER;
                case "EXIT":
                    return EXIT;
                case "CONVERSATION":
                    return CONVERSATION;
                case "DATE":
                    return DATE;

            }
            return null;
        }
    }


    @SerializedName("userToken")
    public String userToken;
    @SerializedName("room_Uuid")
    public String room_Uuid;
    @SerializedName("today")
    public String today;
    @SerializedName("room_Index")
    public int room_Index;
    @SerializedName("userId")
    public String userId;
    @SerializedName("currentState")
    public String currentState;
    @SerializedName("userImage")
    public String userImage;
    @SerializedName("userConversation")
    public String userConversation;
    @SerializedName("room_JoinPeopleName")
    public String room_JoinPeopleName;
    @SerializedName("room_JoinPeopleImage")
    public List<Map<String, Object>> room_JoinPeopleImage;
    @SerializedName("userJoinCount")
    public int userJoinCount;
    @SerializedName("imageUrl")
    public String imageUrl;
    @SerializedName("videoUrl")
    public String videoUrl;
    @SerializedName("uploadUrl")
    public String uploadUrl;
    @SerializedName("userState")
    public UserState userState;
    @SerializedName("userConversationTime")
    public String userConversationTime;
    @SerializedName("currentActualTime")
    public String currentActualTime;
    @SerializedName("userMessageType")
    public MessageType userMessageType;
    @SerializedName("chatting_VideoFile")
    public String chatting_VideoFile;
    @SerializedName("chatting_ImageFile")
    public String chatting_ImageFile;
    @SerializedName("actualTime")
    public long actualTime;
    @SerializedName("unReadCount")
    public int unReadCount;
    @SerializedName("isDelete")
    public boolean isDelete;



}
