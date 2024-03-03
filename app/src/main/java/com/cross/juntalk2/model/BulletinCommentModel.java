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
public class BulletinCommentModel implements Serializable {

    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("bulletin_Uuid")
    public String bulletin_Uuid;
    @SerializedName("userId")
    public String userId;
    @SerializedName("userComment")
    public String userComment;
    @SerializedName("userCommentRegDate")
    public String userCommentRegDate;
    @SerializedName("viewType")
    public int viewType;


}
