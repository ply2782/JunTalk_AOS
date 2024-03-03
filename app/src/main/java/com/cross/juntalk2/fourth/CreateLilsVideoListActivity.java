package com.cross.juntalk2.fourth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityCreateLilsVideoListBinding;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.VideoTrimmerActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateLilsVideoListActivity extends CreateNewCompatActivity {

    private ActivityCreateLilsVideoListBinding binding;
    private StringBuilder builder;
    private Handler handler;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private RetrofitService service;
    private ActivityResultLauncher<Intent> chooserLauncer;
    private ActivityResultLauncher<Intent> trimVideoResultLauncher;
    private boolean isVideoExists = false;
    private MultipartBody.Part videoFile;
    private Bitmap thumbNailBitmap;
    private MyModel myModel;
    RequestBody userId, user_Index, contents, hashTagList, lils_Uuid, userMainPhoto;

    @Override
    public void getInterfaceInfo() {
        myModel = JunApplication.getMyModel();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        chooserLauncer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            String imagePath = getRealPathFromURI(uri);
                            Intent intent = new Intent(context, VideoTrimmerActivity.class);
                            intent.putExtra("videoUri", imagePath);
                            trimVideoResultLauncher.launch(intent);
                        } else {

                        }
                    } else {
                        Snackbar.make(getCurrentFocus(), "현재 선택된 사진이 없습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });

        trimVideoResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.e("abc", "result : " + result);
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String trimmedVideoURi = data.getStringExtra("trimmedVideoURi");
                        Bitmap videoThumbNail = createThumbnail(context, trimmedVideoURi);
                        thumbNailBitmap = videoThumbNail;
                        binding.uploadFileImageFilterButton.setImageBitmap(thumbNailBitmap);
                        File file = new File(trimmedVideoURi);
                        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                        String videoFileName = UUID.randomUUID().toString() + "_JunTalk.mp4";
                        MultipartBody.Part part = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
                        videoFile = part;
                        isVideoExists = true;
                    }
                }
            }
        });
    }

    public Bitmap createThumbnail(Context activity, String path) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(activity, Uri.parse(path));
            //timeUs는 마이크로 초 이므로 1000000초 곱해줘야 초단위다.
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    @Override
    public void getIntentInfo() {

    }

    //TODO : uri 로부터 사진 절대위치 구하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(CreateLilsVideoListActivity.this, R.layout.activity_create_lils_video_list);
        handler = new Handler(Looper.getMainLooper());
        builder = new StringBuilder();
        preferences = getSharedPreferences("LookStatus", MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public void createThings() {

    }

    @Override
    public void clickEvent() {
        binding.plusButton.setOnClickListener(v -> {
            if (binding.clubCategoryTextInputEditTextView == null || binding.clubCategoryTextInputEditTextView.getText().toString().equals("")) {

            } else {
                addHashTag(binding.clubCategoryTextInputEditTextView.getText().toString());
                binding.clubCategoryTextInputEditTextView.setText("");
            }

        });

        binding.uploadFileImageFilterButton.setOnClickListener(v -> {


            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_PICK);
            chooserLauncer.launch(intent);
        });

        binding.transitionButton.setOnClickListener(v -> {
            if (checkNull()) {
                binding.transitionButton.startAnimation();
                createLilsVideo();
            } else {

            }


        });
    }

    @Override
    public void getServer() {
        if (preferences.getBoolean("createLilsVideo", false) == false) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.uploadFileImageFilterButton), "파일을 업로드 해주세요.", "당신의 일상을 공유해 보아요.")
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
                            editor.putBoolean("createLilsVideo", true);
                            editor.commit();
                        }
                    });

        }
    }

    public void addHashTag(String text) {
        builder.append("#" + text);
        builder.append(",");
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
        chip.setText("#" + text);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.chipgroup.addView(chip, layoutParams);
        chip.setOnClickListener(v -> {
            builder.delete(builder.lastIndexOf("#" + text + ","), (builder.lastIndexOf("#" + text + ",") + (text.length() + 2)));
            binding.chipgroup.removeView(v);
        });
    }

    public boolean checkNull() {
        Log.e("abc", "videoFile : " + videoFile);
        lils_Uuid = RequestBody.create(UUID.randomUUID().toString(), MediaType.parse("text/plain"));
        userId = RequestBody.create(myModel.userId, MediaType.parse("text/plain"));
        userMainPhoto = RequestBody.create(myModel.userMainPhoto, MediaType.parse("text/plain"));
        user_Index = RequestBody.create(String.valueOf(myModel.user_Index), MediaType.parse("text/plain"));
        contents = RequestBody.create(binding.clubContentsEditTextView.getText().toString(), MediaType.parse("text/plain"));
        hashTagList = RequestBody.create(builder.toString(), MediaType.parse("text/plain"));
        if (contents == null || contents.equals("")) {
            Toast.makeText(context, "클럽 내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (builder == null || builder.length() == 0) {
            Toast.makeText(context, "클럽 태그들을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (isVideoExists == false) {
            Toast.makeText(context, "영상을 넣어 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void createLilsVideo() {

        service.createLilsVideoList(
                CommonString.bulletinBoardController,
                videoFile,
                lils_Uuid,
                userId,
                userMainPhoto,
                user_Index,
                contents,
                hashTagList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    Intent intent = new Intent();
                                    intent.setAction("LilsOnResume");
                                    sendBroadcast(intent);
                                    finish();
                                }
                            });
                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }
}