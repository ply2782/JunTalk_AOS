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
public class BlockBulletinModel implements Serializable {

    @SerializedName("category")
    public String category;
    @SerializedName("userId")
    public String userId;
    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("bulletin_Uuid")
    public String bulletin_Uuid;
    @SerializedName("isBlock")
    public boolean isBlock;


}
