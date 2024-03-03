package com.cross.juntalk2.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityMainBinding;
import com.cross.juntalk2.first.PolicyActivity;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.service.ForecdTerminationService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.kakao.auth.ApiErrorCode;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//TODO : 네이버 및 카카오톡 소셜 로그인
public class MainActivity extends CreateNewCompatActivity {
    private SessionCallback sessionCallback;
    private LoadingDialog loadingDialog;
    private ActivityMainBinding mainBinding;
    private Handler handler;
    private FirebaseAnalytics mFirebaseAnalytics;
    private CompositeDisposable compositeDisposable;
    private RetrofitService service;
    private Dialog_SelfAuthentication dialog_selfAuthentication;
    private MyModel myModel;
    private boolean isOpen = true;
    private int MY_REQUEST_CODE = 366;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private AppUpdateManager appUpdateManager;
    private String strings[] = {"optimistic", "different", "awesome", "positive", "believer"};
    private int currentIndex = 0;
    private Timer timer;
    private Typeface typeface;
    private final long DELAY_MS = 1000;           // 오토 플립용 타이머 시작 후 해당 시간에 작동(초기 웨이팅 타임) ex) 앱 로딩 후 1초 뒤 플립됨.
    private final long PERIOD_MS = 2000;          // 2초 주기로 작동
    private final int REQUEST_CODE = 100;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int thisAppVersion;

    @Override
    public void getInterfaceInfo() {
        thisAppVersion = getAppVersionCode();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        handler = new Handler(Looper.getMainLooper());
        preferences = getSharedPreferences("LookStatus", MODE_PRIVATE);
        editor = preferences.edit();

    }

    //TODO : Application 에서 미리 나의 모델 저장
    @Override
    public void getIntentInfo() {
        myModel = JunApplication.getMyModel();

        // 앱 업데이트 매니저 초기화
        // 업데이트를 체크하는데 사용되는 인텐트를 리턴한다.
        appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> { // appUpdateManager이 추가되는데 성공하면 발생하는 이벤트
            Log.e("abc", "------------------------------------------------------------------------------------------");
            Log.e("abc", "updateAvailability : " + appUpdateInfo.updateAvailability());
            Log.e("abc", "isUpdateTypeAllowed : " + appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE));
            Log.e("abc", "availableVersionCode : " + appUpdateInfo.availableVersionCode());
            Log.e("abc", "clientVersionStalenessDays : " + appUpdateInfo.clientVersionStalenessDays());
            Log.e("abc", "totalBytesToDownload : " + appUpdateInfo.totalBytesToDownload());
            Log.e("abc", "------------------------------------------------------------------------------------------");

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {

            } else {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // UpdateAvailability.UPDATE_AVAILABLE == 2 이면 앱 true
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) { // 허용된 타입의 앱 업데이트이면 실행 (AppUpdateType.IMMEDIATE || AppUpdateType.FLEXIBLE)
                    // 업데이트가 가능하고, 상위 버전 코드의 앱이 존재하면 업데이트를 실행한다.

                    requestUpdate(appUpdateInfo);
                }
            }


        });


    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    // 'getAppUpdateInfo()' 에 의해 리턴된 인텐트
                    appUpdateInfo,
                    // 'AppUpdateType.FLEXIBLE': 사용자에게 업데이트 여부를 물은 후 업데이트 실행 가능
                    // 'AppUpdateType.IMMEDIATE': 사용자가 수락해야만 하는 업데이트 창을 보여줌
                    AppUpdateType.IMMEDIATE,
                    // 현재 업데이트 요청을 만든 액티비티, 여기선 MainActivity.
                    this,
                    // onActivityResult 에서 사용될 REQUEST_CODE.
                    REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            appUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            appUpdateInfo -> {
                                if (appUpdateInfo.updateAvailability()
                                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                    // If an in-app update is already running, resume the update.
                                    try {
                                        appUpdateManager.startUpdateFlowForResult(
                                                appUpdateInfo,
                                                AppUpdateType.IMMEDIATE,
                                                this,
                                                101);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
        } catch (Exception e) {

        }

    }

    @Override
    public void init() {


        try {
            //todo : 앱 강제종료 시점 알아내는 방법
            startService(new Intent(this, ForecdTerminationService.class));
        } catch (Exception e) {

        }


        fabOpen = AnimationUtils.loadAnimation
                (context, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation
                (context, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation
                (context, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation
                (context, R.anim.rotate_backward);

        dialog_selfAuthentication = new Dialog_SelfAuthentication(context, MainActivity.this);
        dialog_selfAuthentication.getWindow().setGravity(Gravity.BOTTOM);
        compositeDisposable = new CompositeDisposable();
        mainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        mainBinding.setMainActivity(this);
        loadingDialog = new LoadingDialog(context);
        sessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(sessionCallback);
        sessionCallback.onSessionOpened();
        /*if (JunApplication.getSaveAutoLogin().getBoolean("isAutoLogin", false)) {
            //TODO: 카카오 세션 콜백함수 -> 로그인 한적이 있으면 자동 로그인 되도록 하는 부분
            Session.getCurrentSession().checkAndImplicitOpen();
        } else {

        }*/
//        Session.getCurrentSession().close();


        service.managerModeIsActivated(CommonString.userController).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainBinding.managerModeLinearLayout.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainBinding.managerModeLinearLayout.setVisibility(View.GONE);

                            }
                        });
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });

        mainBinding.mainLoginConstraintLayout.setVisibility(View.GONE);
        mainBinding.policyLinearLayout.setVisibility(View.VISIBLE);
        mainBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mainBinding.mainLoginConstraintLayout.setVisibility(View.VISIBLE);
                    mainBinding.clickLottieAnimation.performClick();
                    mainBinding.policyLinearLayout.setVisibility(View.GONE);
//                    editor.putBoolean("isPolicyAgree", true);
//                    editor.commit();
                    TapTargetView.showFor(MainActivity.this,                 // `this` is an Activity
                            TapTarget.forView(findViewById(R.id.mainFloatingActionButton), "클릭해주세요 !.", "이미지를 클릭하면 카카오톡으로\n로그인 할 수 있는 이미지가 나옵니다.")
                                    // All options below are optional
                                    .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                    .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                    .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                    .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                    .titleTextColor(R.color.white)      // Specify the color of the title text
                                    .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                    .descriptionTextColor(R.color.black)  // Specify the color of the description text
                                    .textColor(R.color.black)            // Specify a color for both the title and description text
                                    .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                    .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                    .drawShadow(true)                   // Whether to draw a drop shadow or not
                                    .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                    .tintTarget(true)                   // Whether to tint the target view's color
                                    .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                                    .targetRadius(60),                  // Specify the target radius (in dp)
                            new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                @Override
                                public void onTargetClick(TapTargetView view) {
                                    super.onTargetClick(view);      // This call is optional
                                    view.dismiss(true);
//                                        editor.putBoolean("firstStartClickLoginButton", true);
//                                        editor.commit();
                                }
                            });
                } else {
                    mainBinding.clickLottieAnimation.setVisibility(View.GONE);
//                    editor.putBoolean("isPolicyAgree", false);
//                    editor.commit();
                }
            }
        });


        /*if (preferences.getBoolean("isPolicyAgree", false) == true) {
            mainBinding.mainLoginConstraintLayout.setVisibility(View.VISIBLE);
            mainBinding.clickLottieAnimation.performClick();
            mainBinding.policyLinearLayout.setVisibility(View.GONE);
            if (JunApplication.getSaveAutoLogin().getBoolean("isAutoLogin", false)) {
                //TODO: 카카오 세션 콜백함수 -> 로그인 한적이 있으면 자동 로그인 되도록 하는 부분
                Session.getCurrentSession().checkAndImplicitOpen();
            } else {

            }

        } else {
            mainBinding.mainLoginConstraintLayout.setVisibility(View.GONE);
            mainBinding.policyLinearLayout.setVisibility(View.VISIBLE);
            mainBinding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mainBinding.mainLoginConstraintLayout.setVisibility(View.VISIBLE);
                        mainBinding.clickLottieAnimation.performClick();
                        mainBinding.policyLinearLayout.setVisibility(View.GONE);
                        editor.putBoolean("isPolicyAgree", true);
                        editor.commit();
                        if (preferences.getBoolean("firstStartClickLoginButton", false) == false) {
                            TapTargetView.showFor(MainActivity.this,                 // `this` is an Activity
                                    TapTarget.forView(findViewById(R.id.mainFloatingActionButton), "클릭해주세요 !.", "이미지를 클릭하면 카카오톡으로\n로그인 할 수 있는 이미지가 나옵니다.")
                                            // All options below are optional
                                            .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                            .titleTextColor(R.color.white)      // Specify the color of the title text
                                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                                            .descriptionTextColor(R.color.black)  // Specify the color of the description text
                                            .textColor(R.color.black)            // Specify a color for both the title and description text
                                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                            .tintTarget(true)                   // Whether to tint the target view's color
                                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                                            .targetRadius(60),                  // Specify the target radius (in dp)
                                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                                        @Override
                                        public void onTargetClick(TapTargetView view) {
                                            super.onTargetClick(view);      // This call is optional
                                            view.dismiss(true);
                                            editor.putBoolean("firstStartClickLoginButton", true);
                                            editor.commit();
                                        }
                                    });

                        }
                    } else {
                        mainBinding.clickLottieAnimation.setVisibility(View.GONE);
                        editor.putBoolean("isPolicyAgree", false);
                        editor.commit();
                    }
                }
            });
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            Toast myToast = Toast.makeText(this.getApplicationContext(), "MY_REQUEST_CODE", Toast.LENGTH_SHORT);
            myToast.show();

            // 업데이트가 성공적으로 끝나지 않은 경우
            if (resultCode != RESULT_OK) {
                Log.d("abc", "Update flow failed! Result code: " + resultCode);
                // 업데이트가 취소되거나 실패하면 업데이트를 다시 요청할 수 있다.,
                // 업데이트 타입을 선택한다 (IMMEDIATE || FLEXIBLE).
                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // flexible한 업데이트를 위해서는 AppUpdateType.FLEXIBLE을 사용한다.
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // 업데이트를 다시 요청한다.
                        Log.e("abc", "appUpdateInfo : " + appUpdateInfo);
                        requestUpdate(appUpdateInfo);
                    }
                });
            }
        }

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    //TODO : 해쉬키 가져오기 ( 카카오톡 및 다른 소셜 로그인 등록시 필요 )
    private void getHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("abc", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("abc", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("abc", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }


    public void api_UpdateLoginTime(long userKakaoOwnNumber, String currentVersion) {

        service.updateLastLogin(CommonString.userController, userKakaoOwnNumber, currentVersion).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    //TODO : 액티비티 디스트로이 시 , 세션 죽이기
    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
        compositeDisposable.clear();
        compositeDisposable.dispose();
        dialog_selfAuthentication.dismiss();
        Session.getCurrentSession().removeCallback(sessionCallback);
        handler.removeCallbacksAndMessages(null);

    }


    @Override
    public void createThings() {

        //TODO : crashLytics 토큰값 가져와서 등록해주기
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //todo : fcm 구독으로 전체 fcm 보내기 위한 것
        /*FirebaseMessaging.getInstance().subscribeToTopic("chatting")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "success";
                        if (!task.isSuccessful()) {
                            msg = "fail";
                        }
                        Log.d("abc", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/

    }


    public void api_updateFireBaseToken(String userToken) {
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userToken", userToken);
        userInfoMap.put("userKakaoOwnNumber", myModel.userKakaoOwnNumber);
        service.updateToken(CommonString.fcmController, userInfoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error :  " + t.getMessage());
            }
        });
    }


    //TODO : 카카오톡 세션 콜백 리스너로 로그인 구현
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(), "세션이 만료되었습니다. 로그인을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(MeV2Response result) {
                    myModel.userKakaoOwnNumber = result.getId();
                    myModel.userKakaoId = result.getKakaoAccount().getProfile().getNickname();
                    JunApplication.setMyModel(myModel);
                    isExistOfId(result);
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //TODO : 아이디 존재 여부 판단
    public void isExistOfId(MeV2Response result) {
        Observable<MyModel> booleanResult = service.isExistOfId("userController", result.getId());
        compositeDisposable.add(booleanResult.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<MyModel>() {
                    @Override
                    public void onNext(@NotNull MyModel myModel) {
                        if (myModel == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.show();
                                    if (!isFinishing() && !dialog_selfAuthentication.isShowing()) {
                                        dialog_selfAuthentication.show();
                                    }
                                }
                            });
                        } else {

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("abc", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }

                                    if (isFinishing() || isDestroyed()) {
                                        loadingDialog.dismiss();
                                    }

                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    myModel.currentVersion = thisAppVersion;
                                    myModel.firebaseToken = token;
                                    api_updateFireBaseToken(token);
                                    api_UpdateLoginTime(myModel.userKakaoOwnNumber, String.valueOf(thisAppVersion));
                                    JunApplication.setMyModel(myModel);

                                    //todo : fcm 보내서 메인리스트 친구리스트 실시간 변경
                                    //todo : 이상한 에러로 작동이 안됌;
                                    Intent intent = new Intent(context, WholeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    Intent getIntentInfo = getIntent();
                                    //todo : fcm 채팅 눌렀을때 채팅으로 이동시키기
                                    if (getIntentInfo.getSerializableExtra("roomModel") != null) {
                                        RoomModel roomModel = (RoomModel) getIntentInfo.getSerializableExtra("roomModel");
                                        intent.putExtra("roomModel", roomModel);
                                    }
                                    startActivity(intent);
                                    finish();

                                }
                            });
                        }


                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "isExistOfId error : " + e.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing() && !dialog_selfAuthentication.isShowing()) {
                                    dialog_selfAuthentication.show();
                                }
                            }
                        });
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("abc", "isExistOfId onComplete");
                        loadingDialog.dismiss();
                        compositeDisposable.dispose();
                    }
                }));

    }

    @Override
    public void clickEvent() {

        mainBinding.lookPolicyButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, PolicyActivity.class);
            intent.putExtra("policyUrl", "http://ply2782ply2782.cafe24.com:8080/privacy.html");
            startActivity(intent);
        });


        mainBinding.loginButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String userId = mainBinding.idEditTextView.getText().toString().replaceAll(" ", "").trim();
                    String password = mainBinding.passwordEditTextView.getText().toString().replaceAll(" ", "").trim();
                    service.myModel(CommonString.userController, userId, password).enqueue(new Callback<MyModel>() {
                        @Override
                        public void onResponse(Call<MyModel> call, Response<MyModel> response) {
                            Log.e("abc", "" + response.raw());
                            if (response.isSuccessful()) {
                                MyModel myModel = response.body();
                                JunApplication.setMyModel(myModel);
                                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("abc", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        // Get new FCM registration token
                                        String token = task.getResult();
                                        myModel.currentVersion = thisAppVersion;
                                        myModel.firebaseToken = token;
                                        api_updateFireBaseToken(token);
                                        api_UpdateLoginTime(myModel.userKakaoOwnNumber, String.valueOf(thisAppVersion));
                                        JunApplication.setMyModel(myModel);
//                                            api_FcmSending(token);
                                    }
                                });

                                //todo : fcm 보내서 메인리스트 친구리스트 실시간 변경
                                //todo : 이상한 에러로 작동이 안됌;

                                Intent intent = new Intent(context, WholeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyModel> call, Throwable t) {

                        }
                    });
                }
            }
        });

    }

    public int getAppVersionCode() {
        PackageInfo packageInfo = null;         //패키지에 대한 전반적인 정보
        //PackageInfo 초기화
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return packageInfo.versionCode;
    }


    @Override
    public void getServer() {
        try {
            textSwitcherSetting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //TODO : 레트로핏 서버 통신후 남아있는 스레드 종료
        compositeDisposable.clear();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //TODO : 핸들러 종료
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.removeMessages(0);
        }
    }

    public void api_FcmSending(String fireBaseToken) {


        Log.e("abc", "fireBaseToken : " + fireBaseToken);
        Map<String, Object> fireBaseTokenMap = new HashMap<>();
        fireBaseTokenMap.put("fireBaseToken", fireBaseToken);
        service.send_noticeJoin(CommonString.fcmController, fireBaseTokenMap).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "result : " + response.body());
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseAnalytics.getInstance(context).setAnalyticsCollectionEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }

    public void deviceGroupUpstream() {
        // [START fcm_device_group_upstream]
        String to = "a_unique_key"; // the notification key
        AtomicInteger msgId = new AtomicInteger();
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(to)
                .setMessageId(String.valueOf(msgId.get()))
                .addData("hello", "world")
                .build());
        // [END fcm_device_group_upstream]
    }

    public void sendUpstream() {
        final String SENDER_ID = "YOUR_SENDER_ID";
        final int messageId = 0; // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging fm = FirebaseMessaging.getInstance();
        fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("my_message", "Hello World")
                .addData("my_action", "SAY_HELLO")
                .build());
        // [END fcm_send_upstream]
    }

    public void certify(View view) {
        getHashKey();
        Session session = Session.getCurrentSession();
        session.addCallback(new SessionCallback());
        session.open(AuthType.KAKAO_LOGIN_ALL, MainActivity.this);
    }

    public void animateFab(View view) {
        mainBinding.clickLottieAnimation.playAnimation();
        if (isOpen) {
//            mainBinding.kakaoImageButton.startAnimation(fabClose);
            mainBinding.naverImageButton.startAnimation(fabClose);
//            mainBinding.kakaoImageButton.setClickable(false);
            mainBinding.naverImageButton.setClickable(false);
            isOpen = false;
        } else {
//            mainBinding.kakaoImageButton.startAnimation(fabOpen);
            mainBinding.naverImageButton.startAnimation(fabOpen);
//            mainBinding.kakaoImageButton.setClickable(true);
            mainBinding.naverImageButton.setClickable(true);
            isOpen = true;
        }
    }

    //TODO : 텍스트 자동 수직 스크롤 되는 textViewPager
    public void textSwitcherSetting() throws Exception {
        // to keep current Index of textID array
        autoTimer();
        Animation anim_up = AnimationUtils.loadAnimation(context, R.anim.anim_up);
        anim_up.setDuration(500);
        anim_up.setInterpolator(new BounceInterpolator());
        Animation anim_down = AnimationUtils.loadAnimation(context, R.anim.anim_down);
        anim_down.setDuration(500);
        anim_down.setInterpolator(new AccelerateDecelerateInterpolator());
        mainBinding.textSwitcher.setInAnimation(anim_up);
        mainBinding.textSwitcher.setOutAnimation(anim_down);
        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked

        try {
            mainBinding.textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    //api 26이상
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        typeface = getResources().getFont(R.font.seven_b);
                    } else {
                        //api 26 이하
                        typeface = Typeface.createFromAsset(getAssets(), "fonts/seven_b.ttf"); // font 폴더내에 있는 jua.ttf 파일을 typeface로 설정
                    }
                    // TODO Auto-generated method stub
                    // create a TextView
                    TextView t = new TextView(MainActivity.this);
                    // set the gravity of text to top and center horizontal
                    t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    // set displayed text size
                    t.setTextSize(20);
                    t.setPadding(10, 10, 10, 10);
                    t.setTextColor(Color.BLACK);
                    t.setTypeface(typeface); // messsage는 TextView 변수
                    return t;
                }
            });

        } catch (Exception e) {
            typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        }
        mainBinding.textSwitcher.setCurrentText(strings[0]);
    }

    //TODO : 자동 타이머
    public void autoTimer() {
        // 세팅 후 타이머 실행
        final Runnable Update = new Runnable() {
            public void run() {
                currentIndex++;
                if (currentIndex >= strings.length) {
                    currentIndex = 0;
                }
                mainBinding.textSwitcher.setText(strings[currentIndex].toUpperCase());
            }
        };

        timer = new Timer(); // thread에 작업용 thread 추가
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }


}