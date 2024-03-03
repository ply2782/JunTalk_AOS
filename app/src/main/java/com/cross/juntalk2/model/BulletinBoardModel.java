package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BulletinBoardModel implements Serializable {


    @SerializedName("category")
    public String category;
    @SerializedName("userId")
    public String userId;
    @SerializedName("bulletin_isUserLike")
    public boolean bulletin_isUserLike;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("bulletin_Content")
    public String bulletin_Content;
    @SerializedName("allUrls")
    public List<String> allUrls;
    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("bulletin_Uuid")
    public String bulletin_Uuid;
    @SerializedName("bulletin_LikeCount")
    public int bulletin_LikeCount;
    @SerializedName("bulletin_CommentCount")
    public int bulletin_CommentCount;
    @SerializedName("bulletin_RegDate")
    public String bulletin_RegDate;
    @SerializedName("isBlock")
    public boolean isBlock;

}
