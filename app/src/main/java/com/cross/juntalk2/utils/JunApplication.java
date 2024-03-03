package com.cross.juntalk2.utils;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.sdk.common.KakaoSdk;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JunApplication extends Application {

    private static JunApplication instance;
    private static SharedPreferences saveAutoLogin;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences lookStatus;
    private static SharedPreferences.Editor lookStatus_Editor;
    private static Context context;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static MyModel myModel;
    private static FirebaseAnalytics mFirebaseAnalytics;
    private static Gson gson;
    private SimpleDateFormat todayDateFormatToDate = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private boolean isBackground = false;

    public static void setMyModel(MyModel myModel) {
        if (getMyModel() == null) {
            String myModelString = saveAutoLogin.getString("myModel", null);
            String modelString = gson.toJson(myModelString, MyModel.class);
            editor.putString("myModel", modelString);
            editor.commit();
        }
        JunApplication.myModel = myModel;

    }

    public static MyModel getMyModel() {
        if (myModel == null) {
            String modelString = getSaveAutoLogin().getString("myModel", "null");
            myModel = gson.fromJson(modelString, MyModel.class);
        }
        return myModel;
    }

    public static JunApplication getGlobalApplicationContext() {
        if (instance == null) {
            throw new IllegalStateException("This Application does not inherit com.kakao.GlobalApplication");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        KakaoSDK.init(new KakaoSDKAdapter());
        KakaoSdk.init(this, "b01aaf833b224fe7a270f1899cd6ceff");
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (isBackground) {
                    isBackground = false;
                    /** 포그라운드일때*/
                    Intent firstIntent = new Intent();
                    firstIntent.setAction("MusicFragmentRefresh");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("createClub");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("FirstFragment");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("UserBulletinActivity");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("FourthFragment");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("LilsOnResume");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("WholeActivity");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("SecondFragment");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("OpenChatting");
                    sendBroadcast(firstIntent);

                    firstIntent = new Intent();
                    firstIntent.setAction("VideoFragmentRefresh");
                    sendBroadcast(firstIntent);

                    Log.e("abc", "포그라운드");
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

                if (isBackground == true) {
                    RetrofitService service = RetrofitClient.getInstance().getServerInterface();
                    service.sessionEnd(CommonString.userController, myModel.userKakaoOwnNumber).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Log.e("abc", "" + response.raw());
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
                Log.e("abc", "isBackground : " + isBackground + " , mainActivity destroyed");
                ;

            }
        });

        IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isBackground) {
                    isBackground = false;
                    /** 백그라운드 스크린 꺼져있는 상태일때 */
                    // todo
                    Log.e("abc", "백그라운드");
                }
            }
        }, screenOffFilter);

        context = this;
        myModel = new MyModel();
        gson = new GsonBuilder().create();

        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("abc", "adMob initializationStatus : " + initializationStatus.toString());
            }
        });
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        saveAutoLogin = getSharedPreferences("autoLoginList", MODE_PRIVATE);
        editor = saveAutoLogin.edit();
        lookStatus = getSharedPreferences("LookStatus", MODE_PRIVATE);
        lookStatus_Editor = lookStatus.edit();
        if (lookStatus.getString("today", null) == null) {
            lookStatus_Editor.putString("today", todayDateFormatToDate.format(new Date()));
            lookStatus_Editor.commit();
        }

        int maxCore = Runtime.getRuntime().availableProcessors();
        threadPoolExecutor = new ThreadPoolExecutor(
                1, //corePoolSize
                maxCore, //maxPoolSize
                0L, //keepAliveTime
                TimeUnit.MILLISECONDS, //unit
                new LinkedBlockingQueue<Runnable>(20), //workQueue
                new ThreadPoolExecutor.DiscardOldestPolicy() //handler
        );


        //todo : workermanager 제약사항 걸고 3시간마다 실행 시킴
        /**try {
         Constraints constraints = new Constraints.Builder()
         .setRequiredNetworkType(NetworkType.CONNECTED)
         .build();
         PeriodicWorkRequest locationWork =
         new PeriodicWorkRequest
         .Builder(Worker.class, 3, TimeUnit.HOURS)
         .addTag(Worker.class.getSimpleName())
         .setConstraints(constraints).build();
         WorkManager.getInstance(context).enqueue(locationWork);
         } catch (Exception e) {
         Log.e("abc", "error : " + e.getMessage());
         }*/


    }


    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static SharedPreferences getLookStatus() {
        return lookStatus;
    }

    public static SharedPreferences.Editor getLookStatus_Editor() {
        return lookStatus_Editor;
    }

    public static SharedPreferences getSaveAutoLogin() {
        return saveAutoLogin;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (!threadPoolExecutor.isShutdown()) {
            threadPoolExecutor.shutdown();
        }
        instance = null;
    }

    class KakaoSDKAdapter extends KakaoAdapter {

        @Override
        public ISessionConfig getSessionConfig() {
            return new ISessionConfig() {

                // 로그인 시 인증 타입 지정
                @Override
                public AuthType[] getAuthTypes() {
                    return new AuthType[]{AuthType.KAKAO_ACCOUNT};
                }

                // pause와 resume시에 타이머를 설정/ CPU의 소모를 절약 할 지의 여부를 지정
                @Override
                public boolean isUsingWebviewTimer() {
                    return false;
                }

                @Override
                public boolean isSecureMode() {
                    return false;
                }

                @Nullable
                @org.jetbrains.annotations.Nullable
                @Override
                public ApprovalType getApprovalType() {
                    return null;
                }


                // 로그인 웹뷰에서 email 입력 폼의 데이터를 저장할 지 여부를 지정
                @Override
                public boolean isSaveFormData() {
                    return true;
                }
            };
        }

        // Application이 가지고 있는 정보를 얻기 위한 인터페이스
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Context getApplicationContext() {
                    return JunApplication.getGlobalApplicationContext();
                }
            };
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(context).clearMemory();
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        /** 백그라운드인지 아닌지 알수 있는법 --> 백그라운드 일때 */
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true;
            Log.e("abc", "백그라운드");
            // todo
        }

    }
}


