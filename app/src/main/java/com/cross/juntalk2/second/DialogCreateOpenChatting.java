package com.cross.juntalk2.second;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogOpenchattingBinding;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogCreateOpenChatting extends Dialog {
    private OpenChattingActivity.Refresh refreshInterface;
    private DialogOpenchattingBinding openChatting;
    private Context context;
    private ActivityResultLauncher<Intent> fileResultLauncher;
    private RetrofitService service;
    private Handler handler;
    private StringBuilder builder;
    private List<MultipartBody.Part> videoFileList;
    private List<MultipartBody.Part> imageFileList;
    private SimpleDateFormat todayRegDate = new SimpleDateFormat("yyyy-MM-dd (E) aa HH시 mm분");

    public DialogCreateOpenChatting(@NonNull Context context, ActivityResultLauncher<Intent> fileResultLauncher) {
        super(context);
        this.fileResultLauncher = fileResultLauncher;
        this.context = context;
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        handler = new Handler(Looper.getMainLooper());
        builder = new StringBuilder();

    }

    public void setRefreshInterface(OpenChattingActivity.Refresh refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    public void setVideoFileList(List<MultipartBody.Part> videoFileList) {
        this.videoFileList = videoFileList;
    }

    public void setImageFileList(List<MultipartBody.Part> imageFileList) {
        this.imageFileList = imageFileList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        openChatting = DataBindingUtil.inflate(inflater, R.layout.dialog_openchatting, null, false);
        setContentView(openChatting.getRoot());
        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point deviceSize = new Point();
        display.getSize(deviceSize);
        getWindow().getAttributes().width = deviceSize.x;
        getWindow().getAttributes().gravity = Gravity.BOTTOM;

        clickEvent();

    }

    public void setBitmapImage(Bitmap bitmap) {
        openChatting.openChattingMainPhotoImageView.setImageBitmap(bitmap);
    }

    public void clickEvent() {

        openChatting.openChattingMainPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        openChatting.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openChatting.clubCategoryTextInputEditTextView == null || openChatting.clubCategoryTextInputEditTextView.getText().toString().equals("")) {

                } else {
                    addHashTag(openChatting.clubCategoryTextInputEditTextView.getText().toString());
                    openChatting.clubCategoryTextInputEditTextView.setText("");
                }

            }
        });

        openChatting.transitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatting.transitionButton.startAnimation();
                createOpenChatting();
            }
        });
    }

    public Map<String, RequestBody> multiPartMap() {
        Map<String, RequestBody> map = new HashMap<>();
        RequestBody room_Title = RequestBody.create(openChatting.idTextInputEditTextView.getText().toString(), MediaType.parse("text/plain"));
        map.put("room_Title", room_Title);
        RequestBody room_RegDate = RequestBody.create(todayRegDate.format(new Date().getTime()).toString(), MediaType.parse("text/plain"));
        map.put("room_RegDate", room_RegDate);
        RequestBody hashTagList = RequestBody.create(builder.toString(), MediaType.parse("text/plain"));
        map.put("hashTagList", hashTagList);
        RequestBody room_Uuid = RequestBody.create(UUID.randomUUID().toString(), MediaType.parse("text/plain"));
        map.put("room_Uuid", room_Uuid);
        RequestBody roomType = RequestBody.create(RoomModel.RoomType.C.toString(), MediaType.parse("text/plain"));
        map.put("roomType", roomType);
        RequestBody userId = RequestBody.create(JunApplication.getMyModel().userId, MediaType.parse("text/plain"));
        map.put("userId", userId);
        RequestBody user_Index = RequestBody.create(String.valueOf(JunApplication.getMyModel().user_Index), MediaType.parse("text/plain"));
        map.put("user_Index", user_Index);
        return map;
    }

    public void createOpenChatting() {
        Map<String, RequestBody> openChattingInfo = multiPartMap();
        service.createOpenChatting(CommonString.roomController, openChattingInfo, imageFileList, videoFileList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", String.valueOf(response.raw()));
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            openChatting.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    refreshInterface.refresh();
                                    dismiss();
                                }
                            });
                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            openChatting.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void addHashTag(String text) {
        builder.append("#" + text);
        builder.append(",");
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
        chip.setText("#" + text);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        openChatting.chipgroup.addView(chip, layoutParams);

    }


    //TODO : 저장소 저장하기 권한 묻기
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getOwnerActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                context.startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setType("image/* video/*");
                                intent.setAction(Intent.ACTION_PICK);
                                fileResultLauncher.launch(intent);
                                dismiss();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        } else {
            Intent intent = new Intent();
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_PICK);
            final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
            fileResultLauncher.launch(chooserIntent);

        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacksAndMessages(null);
    }
}
