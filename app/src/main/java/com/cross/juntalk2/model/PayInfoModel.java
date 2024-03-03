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
public class PayInfoModel implements Serializable {

    @SerializedName("user_Index")
    public int user_Index;
    @SerializedName("userId")
    public String userId;
    @SerializedName("orderId")
    public String orderId;
    @SerializedName("packageName")
    public String packageName;
    @SerializedName("productId")
    public String productId;
    @SerializedName("purchaseTime")
    public String purchaseTime;//long
    @SerializedName("purchaseState")
    public String purchaseState;//int
    @SerializedName("purchaseToken")
    public String purchaseToken;
    @SerializedName("autoRenewing")
    public boolean autoRenewing;//boolean
    @SerializedName("acknowledged")
    public boolean acknowledged;//boolean
}
