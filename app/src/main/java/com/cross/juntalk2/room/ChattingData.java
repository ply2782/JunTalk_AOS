package com.cross.juntalk2.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cross.juntalk2.model.ChattingModel;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
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
@Entity(tableName = "chatting_conversation")
public class ChattingData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chatting_Index")
    @SerializedName("chatting_Index")
    public int chatting_Index; //유저 고유 번호

    @ColumnInfo(name = "room_Uuid")
    @SerializedName("room_Uuid")
    public String room_Uuid;

    @ColumnInfo(name = "room_Index")
    @SerializedName("room_Index")
    public int room_Index;

    @ColumnInfo(name = "userId")
    @SerializedName("userId")
    public String userId;


    @ColumnInfo(name = "userImage")
    @SerializedName("userImage")
    public String userImage;

    @ColumnInfo(name = "userConversation")
    @SerializedName("userConversation")
    public String userConversation;


    @ColumnInfo(name = "userJoinCount")
    @SerializedName("userJoinCount")
    public int userJoinCount;


    @ColumnInfo(name = "uploadUrl")
    @SerializedName("uploadUrl")
    public String uploadUrl;


    @ColumnInfo(name = "userConversationTime")
    @SerializedName("userConversationTime")
    public String userConversationTime;


    @ColumnInfo(name = "chatting_VideoFile")
    @SerializedName("chatting_VideoFile")
    public String chatting_VideoFile;

    @ColumnInfo(name = "chatting_ImageFile")
    @SerializedName("chatting_ImageFile")
    public String chatting_ImageFile;

    @ColumnInfo(name = "userState")
    @SerializedName("userState")
    public ChattingModel.UserState userState;

    @ColumnInfo(name = "userToken")
    @SerializedName("userToken")
    public String userToken;

    @ColumnInfo(name = "currentState")
    @SerializedName("currentState")
    public String currentState;

    @ColumnInfo(name = "currentActualTime")
    @SerializedName("currentActualTime")
    public String currentActualTime;

    @ColumnInfo(name = "userMessageType")
    @SerializedName("userMessageType")
    public ChattingModel.MessageType userMessageType;


    @ColumnInfo(name = "actualTime")
    @SerializedName("actualTime")
    public long actualTime;

    @ColumnInfo(name = "unReadCount")
    @SerializedName("unReadCount")
    public int unReadCount;

    @ColumnInfo(name = "isDelete")
    @SerializedName("isDelete")
    public boolean isDelete;


    public JSONObject jsonObject(ChattingData chattingData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("chatting_Index", chattingData.chatting_Index);
            jsonObject.put("room_Uuid", chattingData.room_Uuid);
            jsonObject.put("room_Index", chattingData.room_Index);
            jsonObject.put("userId", chattingData.userId);
            jsonObject.put("userImage", chattingData.userImage);
            jsonObject.put("userConversation", chattingData.userConversation);
            jsonObject.put("userJoinCount", chattingData.userJoinCount);
            jsonObject.put("uploadUrl", chattingData.uploadUrl);
            jsonObject.put("userConversationTime", chattingData.userConversationTime);
            jsonObject.put("chatting_VideoFile", chattingData.chatting_VideoFile);
            jsonObject.put("chatting_ImageFile", chattingData.chatting_ImageFile);
            jsonObject.put("userState", chattingData.userState);
            jsonObject.put("userToken", chattingData.userToken);
            jsonObject.put("currentState", chattingData.currentState);
            jsonObject.put("currentActualTime", chattingData.currentActualTime);
            jsonObject.put("userMessageType", chattingData.userMessageType);
            jsonObject.put("actualTime", chattingData.actualTime);
            jsonObject.put("unReadCount", chattingData.unReadCount);
            jsonObject.put("isDelete", chattingData.isDelete);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public JSONObject jsonObject_ChattingModel(ChattingModel chattingModel ) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("room_Uuid", chattingModel.room_Uuid);
            jsonObject.put("room_Index", chattingModel.room_Index);
            jsonObject.put("userId", chattingModel.userId);
            jsonObject.put("userImage", chattingModel.userImage);
            jsonObject.put("userConversation", chattingModel.userConversation);
            jsonObject.put("userJoinCount", chattingModel.userJoinCount);
            jsonObject.put("uploadUrl", chattingModel.uploadUrl);
            jsonObject.put("userConversationTime", chattingModel.userConversationTime);
            jsonObject.put("chatting_VideoFile", chattingModel.chatting_VideoFile);
            jsonObject.put("chatting_ImageFile", chattingModel.chatting_ImageFile);
            jsonObject.put("userState", chattingModel.userState);
            jsonObject.put("userToken", chattingModel.userToken);
            jsonObject.put("currentState", chattingModel.currentState);
            jsonObject.put("currentActualTime", chattingModel.currentActualTime);
            jsonObject.put("userMessageType", chattingModel.userMessageType);
            jsonObject.put("actualTime", chattingModel.actualTime);
            jsonObject.put("unReadCount", chattingModel.unReadCount);
            jsonObject.put("isDelete", chattingModel.isDelete);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
