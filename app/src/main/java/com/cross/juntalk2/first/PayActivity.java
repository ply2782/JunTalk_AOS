package com.cross.juntalk2.first;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.billingclient.api.SkuDetails;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityPayBinding;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.BillingManager;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends CreateNewCompatActivity {

    private BillingManager billingManager;
    private ActivityPayBinding binding;
    private String purchaseType="";
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing())
            overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
    }

    @Override
    public void getInterfaceInfo() {

    }

    @Override
    public void getIntentInfo() {

    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(PayActivity.this, R.layout.activity_pay);
        billingManager = new BillingManager(PayActivity.this);
        String userId = JunApplication.getMyModel().userId;
        int user_Index = JunApplication.getMyModel().user_Index;
        billingManager.setUser_Index(user_Index);
        billingManager.setUserId(userId);

    }

    @Override
    public void createThings() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
        adView.setAdUnitId("ca-app-pub-9868504639371547/1253062251");
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    @Override
    public void clickEvent() {

        binding.dayPayCardView.setOnClickListener(v -> {
            purchaseType = "INAPP";
            billingManager.setPurchaseType(purchaseType);
            int result = billingManager.purchase(billingManager.day_mSkuDetails.get(0));
            Log.e("abc", "result : " + result);
            billingManager.isPurchasedItem_InApp();

        });

        binding.monthPayCardView.setOnClickListener(v -> {
            purchaseType = "SUBSCRIBE";
            billingManager.setPurchaseType(purchaseType);
            int result = billingManager.purchase(billingManager.month_mSkuDetails.get(0));
            Log.e("abc", "result : " + result);
            billingManager.isPurchasedItem_SubScribe();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void getServer() {

    }
}