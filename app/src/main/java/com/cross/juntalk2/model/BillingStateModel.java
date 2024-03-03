package com.cross.juntalk2.model;


import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BillingStateModel {


    @SerializedName("orderId")
    public String orderId;
    @SerializedName("packageName")
    public String packageName;
    @SerializedName("productId")
    public String productId;
    @SerializedName("purchaseTime")
    public long purchaseTime;//long
    @SerializedName("purchaseState")
    public String purchaseState;//int
    @SerializedName("purchaseToken")
    public String purchaseToken;
    @SerializedName("autoRenewing")
    public boolean autoRenewing;//boolean
    @SerializedName("acknowledged")
    public boolean acknowledged;//boolean
}
