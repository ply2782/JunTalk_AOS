package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CommentModel implements Serializable {

    @SerializedName("userId")
    public String userId;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("userComment")
    public String userComment;
    @SerializedName("music_Index")
    public int music_Index;
    @SerializedName("music_Name")
    public String music_Name;
    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("userImoticonUrl")
    public String userImoticonUrl;
    @SerializedName("userCommentRegDate")
    public String userCommentRegDate;
    @SerializedName("dateDiff")
    public int dateDiff;
    @SerializedName("actualTime")
    public long actualTime;

}
