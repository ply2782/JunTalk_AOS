package com.cross.juntalk2.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.model.CommonNoticeModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private RetrofitService service;
    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private final String url = "https://play.google.com/store/apps/details?id=com.cross.JunTalk2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler(Looper.getMainLooper());
        if (service == null)
            service = RetrofitClient.getInstance().getServerInterface();

        loadingDialog = new LoadingDialog(SplashActivity.this);
        compositeDisposable = new CompositeDisposable();
        int thisAppVersion = getAppVersionCode();
        Observable<Map<String, Object>> getCurrentVersion = service.getCurrentVersion(CommonString.userController);
        boolean result = isNetworkConnected();
        if (result) {
            compositeDisposable.add(getCurrentVersion.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                    .subscribeWith(new DisposableObserver<Map<String, Object>>() {
                        @Override
                        public void onNext(@NonNull Map<String, Object> map) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.show();
                                    Map<String, Object> result = map;
                                    Log.e("abc", "result " + result.toString());
                                    int updateVersion = Integer.parseInt((String) result.get("android"));
                                    double isForceUpdate = (double) result.get("isForceUpdate");
                                    int isForceUpdateInteger = Integer.parseInt(String.valueOf(isForceUpdate).replaceAll(".0", ""));
                                    if (updateVersion > thisAppVersion) {
                                        updateShowDialog(isForceUpdateInteger);
                                    } else {
                                        moveMain(1);
                                    }
                                }
                            });

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.e("abc", "error : " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }));
        } else {
            connectionNetworkDialog();
        }
    }

    public void connectionNetworkDialog() {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(SplashActivity.this);
        View cautionView = LayoutInflater.from(SplashActivity.this).inflate(R.layout.dialog_networkconnection, null, false);
        cautionAlert.setView(cautionView);
        Button disconnectionCloseButton = cautionView.findViewById(R.id.disconnectionCloseButton);
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setGravity(Gravity.CENTER);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
                alertDialog.setCancelable(false);
            }
        });
        disconnectionCloseButton.setOnClickListener(v -> {
            finish();
        });
    }

    public void updateShowDialog(int isForceUpdateInteger) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(SplashActivity.this);
        View cautionView = LayoutInflater.from(SplashActivity.this).inflate(R.layout.dialog_updatenotification, null, false);
        cautionAlert.setView(cautionView);
        Button okButton = cautionView.findViewById(R.id.okButton);
        TextView subTextView = cautionView.findViewById(R.id.subTextView);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        if (isForceUpdateInteger == 1) {
            subTextView.setText("이번 업데이트는 기존 버전을 사용할 경우 \n내부 오류 및 기능 추가로 인하여 \n모든 데이터에 영향이 있을 수 있으므로 \n업데이트가 필요합니다.\n많은 관심을 가져주셔서 감사합니다.");
            cancelButton.setVisibility(View.GONE);
        } else {
            cancelButton.setVisibility(View.VISIBLE);
        }
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setGravity(Gravity.CENTER);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
                alertDialog.setCancelable(false);
            }
        });
        okButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            finish();
        });


        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            moveMain(1);
        });
    }


    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        LinkProperties linkProperties = connectivityManager.getLinkProperties(currentNetwork);
        if (caps == null) {
            Toast.makeText(SplashActivity.this, "인터넷을 연결해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            boolean LTEMobile = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            boolean WIFI = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            if (LTEMobile) {
                return true;
            } else if (WIFI) {
                return true;
            } else {
                Toast.makeText(SplashActivity.this, "인터넷을 연결해주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    //앱버전 코드
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


    private void moveMain(int sec) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                //new Intent(현재 context, 이동할 activity)
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);    //intent 에 명시된 액티비티로 이동
                finish();    //현재 액티비티 종료
            }
        }, 1000 * sec); // sec초 정도 딜레이를 준 후 시작
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.dismiss();
        handler.removeCallbacksAndMessages(null);
        compositeDisposable.dispose();
        compositeDisposable.clear();
    }
}