package com.cross.juntalk2.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cross.juntalk2.model.MyModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(tableName = "user")
public class MainRoom implements Serializable {
    public enum Gender {
        M, F
    }
    @ColumnInfo(name = "firebaseToken")
    @SerializedName("firebaseToken")
    public String firebaseToken;

    @ColumnInfo(name = "userMainPhoto")
    @SerializedName("userMainPhoto")
    public String userMainPhoto;

    @ColumnInfo(name = "user_Introduce")
    @SerializedName("user_Introduce")
    public String user_Introduce; //유저 상태메시지

    @PrimaryKey(autoGenerate = true)
    @SerializedName("user_Index")
    public int user_Index; //유저 고유 번호

    @ColumnInfo(name = "userKakaoId")
    @SerializedName("userKakaoId")
    public String userKakaoId; // 유저 카카오 닉네임

    @ColumnInfo(name = "userIdentity")
    @SerializedName("userIdentity")
    public String userIdentity; // 유저 교번

    @ColumnInfo(name = "userKakaoOwnNumber")
    @SerializedName("userKakaoOwnNumber")
    public long userKakaoOwnNumber; // 유저 카카오 고유 넘버

    @ColumnInfo(name = "userId")
    @SerializedName("userId")
    public String userId; //유저 가입당시 아이디

    @ColumnInfo(name = "userName")
    @SerializedName("userName")
    public String userName; // 유저 이름

    @ColumnInfo(name = "userPassword")
    @SerializedName("userPassword")
    public String userPassword; // 유저 비밀번호

    @ColumnInfo(name = "userPhoneNumber")
    @SerializedName("userPhoneNumber")
    public String userPhoneNumber; // 유저 전화번호

    @ColumnInfo(name = "userGender")
    @SerializedName("userGender")
    public MyModel.Gender userGender; // 유저 성별

    @ColumnInfo(name = "userBirthDay")
    @SerializedName("userBirthDay")
    public String userBirthDay; // 유저 생일

    @ColumnInfo(name = "userJoinDate")
    @SerializedName("userJoinDate")
    public String userJoinDate; // 유저 가입 날짜

    @ColumnInfo(name = "user_lastLogin")
    @SerializedName("user_lastLogin")
    public String user_lastLogin; // 유저 최근 접속 날짜

    @ColumnInfo(name = "userDeleteDate")
    @SerializedName("userDeleteDate")
    public String userDeleteDate; // 유저 데이터 삭제날짜

    @ColumnInfo(name = "isDelete")
    @SerializedName("isDelete")
    public boolean isDelete;

}
