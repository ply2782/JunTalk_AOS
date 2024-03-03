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
public class LilsReplyModel {

    @SerializedName("friendModel")
    public FriendModel friendModel;
    @SerializedName("isBlocked")
    public boolean isBlocked;
    @SerializedName("replyMessage")
    public String replyMessage;
    @SerializedName("imoticonUrl")
    public String imoticonUrl;
    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("userId")
    public String userId;
    @SerializedName("lils_Uuid")
    public String lils_Uuid;
    @SerializedName("lils_Index")
    public int lils_Index;
    @SerializedName("regDate")
    public String regDate;


}