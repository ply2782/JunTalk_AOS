package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

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
public class ClubModel implements Serializable {

    @Getter
    public enum ClubState {
        Y, N;

        public ClubState fromInteger(String x) {
            switch (x) {
                case "Y":
                    return Y;
                case "N":
                    return N;
            }
            return null;
        }
    }

    @SerializedName("club_Index")
    public int club_Index;
    @SerializedName("club_Uuid")
    public String club_Uuid;
    @SerializedName("userKakaoOwnNumber")
    public long userKakaoOwnNumber;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("isDelete")
    public boolean isDelete;
    @SerializedName("userId")
    public String userId;
    @SerializedName("title")
    public String title;
    @SerializedName("userNickName")
    public String userNickName;
    @SerializedName("limitJoinCount")
    public int limitJoinCount;
    @SerializedName("minAge")
    public int minAge;
    @SerializedName("maxAge")
    public int maxAge;
    @SerializedName("expectedMoney")
    public int expectedMoney;
    @SerializedName("currentSumJoinCount")
    public int currentSumJoinCount;
    @SerializedName("place")
    public String place;
    @SerializedName("clubIntroduce")
    public String clubIntroduce;
    @SerializedName("regDate")
    public String regDate;
    @SerializedName("clubListFile")
    public String clubListFile;
    @SerializedName("hashTagList")
    public String hashTagList;
    @SerializedName("allUrls")
    public List<String> allUrls;
    @SerializedName("myJoinInfo")
    public List<Map<String,Object>> myJoinInfo;
    @SerializedName("clubBlockInfo")
    public List<Map<String,Object>> clubBlockInfo;
    @SerializedName("blockReportContent")
    public String blockReportContent;



}
