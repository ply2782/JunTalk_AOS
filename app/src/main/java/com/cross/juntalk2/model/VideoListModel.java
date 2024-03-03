package com.cross.juntalk2.model;

import com.google.android.exoplayer2.MediaItem;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VideoListModel implements Serializable {

    @SerializedName("lils_Uuid")
    public String lils_Uuid;
    @SerializedName("lils_Index")
    public int lils_Index;
    @SerializedName("userId")
    public String userId;
    @SerializedName("fromUser_Index")
    public int fromUser_Index;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("lils_LikeClick")
    public boolean lils_LikeClick;
    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("regDate")
    public String regDate;
    @SerializedName("content")
    public String content;
    @SerializedName("hashTagList")
    public String hashTagList;
    @SerializedName("likeCount")
    public int likeCount;
    @SerializedName("replyCount")
    public int replyCount;
    @SerializedName("videoUrl")
    public String videoUrl;
    @SerializedName("lils_videoUrl")
    public String lils_videoUrl;
    @SerializedName("isDelete")
    public boolean isDelete;
    @SerializedName("isBlock")
    public boolean isBlock;
    @SerializedName("replyList")
    public List<String> replyList;
    @SerializedName("heartList")
    public List<String> heartList;
    @SerializedName("lilsReplyModels")
    public List<LilsReplyModel> lilsReplyModels;


}
