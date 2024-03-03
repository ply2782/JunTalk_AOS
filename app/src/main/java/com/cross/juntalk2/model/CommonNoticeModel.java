package com.cross.juntalk2.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class CommonNoticeModel implements Serializable {

    @SerializedName("notice_Index")
    public int notice_Index;
    @SerializedName("notice_Content")
    public String notice_Content;
    @SerializedName("notice_FileImage")
    public String notice_FileImage;
    @SerializedName("notice_FileVideo")
    public String notice_FileVideo;
    @SerializedName("notice_RegDate")
    public String notice_RegDate;
    @SerializedName("mainPhoto")
    public String mainPhoto;
    @SerializedName("userId")
    public String userId;
    @SerializedName("isChecked")
    public boolean isChecked;

}
