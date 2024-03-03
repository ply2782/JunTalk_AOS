package com.cross.juntalk2.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.cross.juntalk2.R;
import com.cross.juntalk2.model.BillingStateModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.PayInfoModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BillingManager implements PurchasesUpdatedListener {


    String TAG = "abc";
    BillingClient mBillingClient;
    Activity mActivity;
    public List<SkuDetails> day_mSkuDetails;
    public List<SkuDetails> month_mSkuDetails;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private Handler handler;
    private PurchaseHistoryResponseListener purchaseHistoryResponseListener;
    private RetrofitService service;
    private int user_Index;
    private String userId;
    private MyModel myModel;

    public enum connectStatusTypes {waiting, connected, fail, disconnected}

    private Map<String, Object> resultMap;
    private String purchaseType = "";
    private Context context;
    public connectStatusTypes connectStatus = connectStatusTypes.waiting;
    private ConsumeResponseListener mConsumResListnere;
    String dayPay = "juntalk_merchandise_01";
    String monthPay = "juntalk_merchandise_month_01";


    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

            } else {
            }
        }
    }

    public void errorCodeMethod(int errorCode) {
        switch (errorCode) {
            case BillingClient.BillingResponseCode.OK:
                Log.e("abc", "ok");
                break;
            case BillingClient.BillingResponseCode.USER_CANCELED:
                Log.e("abc", "USER_CANCELED");
                break;
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE:
                Log.e("abc", "SERVICE_UNAVAILABLE");
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE:
                Log.e("abc", "BILLING_UNAVAILABLE");
                break;
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE:
                Log.e("abc", "ITEM_UNAVAILABLE");
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR:
                Log.e("abc", "DEVELOPER_ERROR");
                break;
            case BillingClient.BillingResponseCode.ERROR:
                Log.e("abc", "ERROR");
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED:
                Log.e("abc", "ITEM_ALREADY_OWNED");
                break;
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED:
                Log.e("abc", "ITEM_NOT_OWNED");
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED:
                Log.e("abc", "SERVICE_DISCONNECTED");
                break;
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED:
                Log.e("abc", "FEATURE_NOT_SUPPORTED");
                break;
            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT:
                Log.e("abc", "SERVICE_TIMEOUT");
                break;
        }
    }


    public void setUser_Index(int user_Index) {
        this.user_Index = user_Index;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BillingManager(Activity _activity) {
        mActivity = _activity;
        myModel = JunApplication.getMyModel();
        myModel.myPayInfo = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
        mBillingClient = BillingClient.newBuilder(mActivity)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        context = _activity.getApplicationContext();

        purchaseHistoryResponseListener = new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> list) {
                errorCodeMethod(billingResult.getResponseCode());
                if (list.size() > 0) {
                    PurchaseHistoryRecord purchaseHistoryRecord = list.get(0);
                    Gson gson = new Gson();
                    BillingStateModel bsVO = gson.fromJson(purchaseHistoryRecord.getOriginalJson(), BillingStateModel.class);
                    String itemID = purchaseHistoryRecord.getSkus().toString();
                    boolean isPurchase = itemID.equals(bsVO.packageName) && itemID.equals(bsVO.productId);

                    if (isPurchase) {
                        Log.e("abc", "정기 구독");
                        // 정기 구독한 사람
                        long diff = System.currentTimeMillis() - bsVO.purchaseTime;
                        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
                        Log.e("abc", "bsVO.purchaseState : " + bsVO.purchaseState);

                        // 구독이 끝나도 30일간 사용 가능
                        if (diffDays > 30) {

                        } else {

                        }

                        switch (Integer.parseInt(bsVO.purchaseState)) {
                            //구독중 ...
                            case Purchase.PurchaseState.PURCHASED:
                                Log.e("abc", "구독중..");
                                break;

                            // 결제 수단문제로 구매 보류가 이뤄졌을때 ...
                            case Purchase.PurchaseState.PENDING:
                                Log.e("abc", "보류중..");
                                break;
                        }


                    } else {
                        // 정기구독 구입한적이 없는 사람
                        Log.e("abc", "정기구독 구입한적이 없는 사람");
                    }


                } else {
                    //앱스토어에서 정기결제 한번도 한적 없는 사람
                    Log.e("abc", "앱스토어에서 정기결제 한번도 한적 없는 사람");
                }

            }
        };
        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                errorCodeMethod(billingResult.getResponseCode());
            }
        };

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.e(TAG, "respCode=" + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    connectStatus = connectStatusTypes.connected;
                    day_getSkuDetailList();
                    month_getSkuDetailList();
                    Log.e(TAG, "connected...");
                } else {
                    connectStatus = connectStatusTypes.fail;
                    Log.e(TAG, "connected... fail ");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                connectStatus = connectStatusTypes.disconnected;
                Log.e(TAG, "disconnected ");
            }
        });

        mConsumResListnere = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "사용끝 + " + purchaseToken);

                    return;
                } else {
                    Log.e(TAG, "소모에 실패 " + billingResult.getResponseCode() + " 대상 상품 " + purchaseToken);
                    return;
                }
            }
        };
    }

    public int purchase(SkuDetails skuDetails) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        return mBillingClient.launchBillingFlow(mActivity, flowParams).getResponseCode();
    }


    public void day_getSkuDetailList() {
        List<String> skuIdList = new ArrayList<>();
        skuIdList.add(dayPay);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuIdList).setType(BillingClient.SkuType.INAPP); // INAPP 가 인앱결제라는 구분임.
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "detail respCode=" + billingResult.getResponseCode());
                    return;
                }
                if (skuDetailsList == null) {
                    Toast.makeText(mActivity, "상품이 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e(TAG, "listCount=" + skuDetailsList.size());
                day_mSkuDetails = skuDetailsList;

                /*for (SkuDetails skuDetails : skuDetailsList) {
                    Log.e(TAG, "\n" + skuDetails.getSku()
                            + "\n" + skuDetails.getTitle()
                            + "\n" + skuDetails.getPrice()
                            + "\n" + skuDetails.getDescription()
                            + "\n" + skuDetails.getFreeTrialPeriod()
                            + "\n" + skuDetails.getIconUrl()
                            + "\n" + skuDetails.getIntroductoryPrice()
                            + "\n" + skuDetails.getIntroductoryPriceAmountMicros()
                            + "\n" + skuDetails.getOriginalPrice()
                            + "\n" + skuDetails.getPriceCurrencyCode());
                }*/

            }
        });
    }

    public void month_getSkuDetailList() {
        List<String> skuIdList = new ArrayList<>();
        skuIdList.add(monthPay);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuIdList).setType(BillingClient.SkuType.SUBS); // INAPP 가 인앱결제라는 구분임.
        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "detail respCode=" + billingResult.getResponseCode());
                    return;
                }
                if (skuDetailsList == null) {
                    Toast.makeText(mActivity, "상품이 없습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.e(TAG, "listCount=" + skuDetailsList.size());
                month_mSkuDetails = skuDetailsList;

                /*for (SkuDetails skuDetails : skuDetailsList) {
                    Log.e(TAG, "\n" + skuDetails.getSku()
                            + "\n" + skuDetails.getTitle()
                            + "\n" + skuDetails.getPrice()
                            + "\n" + skuDetails.getDescription()
                            + "\n" + skuDetails.getFreeTrialPeriod()
                            + "\n" + skuDetails.getIconUrl()
                            + "\n" + skuDetails.getIntroductoryPrice()
                            + "\n" + skuDetails.getIntroductoryPriceAmountMicros()
                            + "\n" + skuDetails.getOriginalPrice()
                            + "\n" + skuDetails.getPriceCurrencyCode());
                }*/

            }
        });
    }

    public void isPurchasedItem_InApp() {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, purchaseHistoryResponseListener);
    }

    public void isPurchasedItem_SubScribe() {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, purchaseHistoryResponseListener);
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.e(TAG, "구매 성공>>>" + billingResult.getDebugMessage());
            JSONObject object = null;
            String pID = "";
            long pDate = 0;
            for (Purchase purchase : purchases) {
                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    Log.e(TAG, "성공값=" + purchase.getPurchaseToken());
                    Log.e(TAG, "성공값=" + purchase.getOriginalJson());
                    try {
                        object = new JSONObject(purchase.getOriginalJson());
                        resultMap = new HashMap<>();
                        resultMap.put("user_Index", user_Index);
                        resultMap.put("userId", userId);
                        resultMap.put("payType", purchaseType);
                        resultMap.put("payInfo", object.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        object = new JSONObject(purchase.getOriginalJson());
                        pID = object.getString("purchaseToken");
                        pDate = object.getLong("purchaseTime"); // 날자를 구하기 위해서
                        String orderId = object.getString("orderId");
                        String productId = object.getString("productId");

                        Log.e(TAG, "token=" + pID + "" + pDate);

                        ConsumeParams params = ConsumeParams.newBuilder()
                                .setPurchaseToken(pID)
                                .build();

                        if (purchaseType.equals("INAPP")) {
                            mBillingClient.consumeAsync(params, mConsumResListnere);
                        } else {
                            handlePurchase(purchase);
                        }
                        PayInfoModel payInfoModel = new PayInfoModel();
                        payInfoModel.user_Index = user_Index;
                        payInfoModel.userId = userId;
                        payInfoModel.orderId = orderId;
                        payInfoModel.productId = productId;
                        payInfoModel.purchaseTime = String.valueOf(pDate);
                        payInfoModel.purchaseToken = pID;
                        myModel.myPayInfo.add(payInfoModel);
                        JunApplication.setMyModel(myModel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            service.savePay(CommonString.userController, resultMap).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("abc", "" + response.raw());
                    if (response.isSuccessful()) {
                        Intent intent = new Intent();
                        intent.setAction("FirstFragment");
                        intent.putExtra("FirstFragment", "FirstFragment");
                        context.sendBroadcast(intent);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("abc", "error : " + t.getMessage());
                }
            });


        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            errorCodeMethod(billingResult.getResponseCode());
        } else {
            errorCodeMethod(billingResult.getResponseCode());
        }
    }
}
