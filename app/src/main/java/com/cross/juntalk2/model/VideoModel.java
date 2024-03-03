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
public class VideoModel implements Serializable {




    @SerializedName("videoUrl")
    public String videoUrl;
    @SerializedName("videoLength")
    public long videoLength;
    @SerializedName("userId")
    public String userId;
    @SerializedName("video_Index")
    public int video_Index;
    @SerializedName("userKakaoOwnNumber")
    public long userKakaoOwnNumber;
    @SerializedName("userNaverOwnNumber")
    public String userNaverOwnNumber;
    @SerializedName("folderName")
    public String folderName;
    @SerializedName("videoName")
    public String videoName;
    @SerializedName("regDate")
    public String regDate;






}
