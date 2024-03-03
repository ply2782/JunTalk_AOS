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
public class NoticeModel implements Serializable {

    @SerializedName("mainPhoto")
    public String mainPhoto;
    @SerializedName("mainTitle")
    public String mainTitle;
    @SerializedName("subContent")
    public String subContent;
    @SerializedName("regDate")
    public String regDate;
    @SerializedName("userId")
    public String userId;
    @SerializedName("room_Index")
    public int room_Index;

}
