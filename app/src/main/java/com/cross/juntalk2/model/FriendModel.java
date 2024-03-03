package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FriendModel implements Serializable {

    public enum Gender {
        M, F
    }
    @SerializedName("firebaseToken")
    public String firebaseToken;
    @SerializedName("userMainPhoto")
    public String userMainPhoto;
    @SerializedName("user_Introduce")
    public String user_Introduce; //유저 상태메시지
    @SerializedName("user_Index")
    public int user_Index; //유저 고유 번호
    @SerializedName("userKakaoId")
    public String userKakaoId; // 유저 카카오 닉네임
    @SerializedName("userIdentity")
    public String userIdentity; // 유저 교번
    @SerializedName("userKakaoOwnNumber")
    public long userKakaoOwnNumber; // 유저 카카오 고유 넘버
    @SerializedName("userId")
    public String userId; //유저 가입당시 아이디
    @SerializedName("userName")
    public String userName; // 유저 이름
    @SerializedName("userPassword")
    public String userPassword; // 유저 비밀번호
    @SerializedName("userPhoneNumber")
    public String userPhoneNumber; // 유저 전화번호
    @SerializedName("userGender")
    public MyModel.Gender userGender; // 유저 성별
    @SerializedName("userBirthDay")
    public String userBirthDay; // 유저 생일
    @SerializedName("userJoinDate")
    public String userJoinDate; // 유저 가입 날짜
    @SerializedName("user_lastLogin")
    public String user_lastLogin; // 유저 최근 접속 날짜
    @SerializedName("userDeleteDate")
    public String userDeleteDate; // 유저 데이터 삭제날짜
    @SerializedName("isDelete")
    public boolean isDelete;
    @SerializedName("currentState")
    public String currentState;
}
