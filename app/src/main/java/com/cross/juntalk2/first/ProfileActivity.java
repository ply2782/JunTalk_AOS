package com.cross.juntalk2.first;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityProfileBinding;
import com.cross.juntalk2.fourth.MyLilsVideoActivity;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.room.ChattingDataDB;
import com.cross.juntalk2.second.ChattingRoomActivity;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.ImageControlActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends CreateNewCompatActivity {

    private SimpleDateFormat todayRegDate = new SimpleDateFormat("yyyy-MM-dd (E) aa HH시 mm분");
    private ActivityProfileBinding binding;
    // 플로팅버튼 상태
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean isOpen = false;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SimpleDateFormat simpleDateFormat;
    private boolean isExistOfPicture = false, isChangedMyNicKName = false, isChangedMyState = false;
    private String type, imageFileName;
    private Handler handler;
    private RetrofitService service;
    private MultipartBody.Part userMainPhoto;
    private CompositeDisposable compositeDisposable;
    private int user_Index;
    private FriendModel friendModel;
    private CustomPowerMenu customPowerMenu;
    private RoomModel roomModel;
    // 파이어베이스 데이터베이스 연동
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private MyModel myModel;
    private UUID uuid;
    private String intentImageUrl;
    private ChattingDataDB chattingDataDB;
    private View.OnClickListener thirdCardViewOnClickEvent, firstCardViewOnClickEvent, secondCardViewOnClickEvent, fourthCardViewClickEvent;

    @Override
    public void getInterfaceInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        type = getIntent().getStringExtra("type");
        uuid = UUID.randomUUID();
        chattingDataDB = ChattingDataDB.getInstance(context);
    }

    @Override
    public void getIntentInfo() {
        myModel = JunApplication.getMyModel();
        handler = new Handler(Looper.getMainLooper());

        secondCardViewOnClickEvent = v -> {
            Intent intent = new Intent(context, UserBulletinActivity.class);
            if (type.equals("me")) {
                intent.putExtra("userId", myModel.userId);
                intent.putExtra("user_Index", myModel.user_Index);
                intent.putExtra("myIndex", myModel.user_Index);
                intent.putExtra("myId", myModel.userId);

            } else {
                intent.putExtra("userId", friendModel.userId);
                intent.putExtra("user_Index", friendModel.user_Index);
                intent.putExtra("myIndex", myModel.user_Index);
                intent.putExtra("myId", myModel.userId);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
        };

        fourthCardViewClickEvent = v -> {
            Intent intent = new Intent(context, MyLilsVideoActivity.class);
            if (type.equals("me")) {
                if (myModel != null) {
                    intent.putExtra("flag", "Me");
                    intent.putExtra("userId", myModel.userId);
                    intent.putExtra("user_Index", myModel.user_Index);
                    intent.putExtra("userMainPhoto", myModel.userMainPhoto);
                }

            } else {

                if (friendModel != null) {
                    intent.putExtra("flag", "You");
                    intent.putExtra("userId", friendModel.userId);
                    intent.putExtra("user_Index", friendModel.user_Index);
                    intent.putExtra("userMainPhoto", friendModel.userMainPhoto);
                }
            }
            startActivity(intent);
            overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
        };

        firstCardViewOnClickEvent = v -> {
            if (type.equals("me")) {
                if (binding.firstCardView.isChecked()) {

                    binding.profileImageChangeCardView.setVisibility(View.GONE);
                    binding.nickNameCarcView.setVisibility(View.GONE);
                    binding.myStateIntroduceCardView.setVisibility(View.GONE);
                    binding.firstCardView.setChecked(false);

                } else {

                    binding.profileImageChangeCardView.setVisibility(View.VISIBLE);
                    binding.nickNameCarcView.setVisibility(View.VISIBLE);
                    binding.myStateIntroduceCardView.setVisibility(View.VISIBLE);
                    binding.firstCardView.setChecked(true);

                }

            } else {

                customPowerMenu = new CustomPowerMenu.Builder<>(context, new CustomDialogMenuAdapter())
                        .setHeaderView(R.layout.dialog_popupmenu_header) // header used for title
                        .setFooterView(R.layout.dialog_popupmenu_footer) // footer used for Read More and Close buttons
                        // this is body
                        .setWidth(800)
                        .addItem(friendModel)
                        .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                        .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                        .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                        .build();
                customPowerMenu.showAtCenter(binding.getRoot());
            }
        };

        thirdCardViewOnClickEvent = v -> {
            if (type.equals("me")) {
                customPowerMenu = new CustomPowerMenu.Builder<>(context, new CustomDialogMenuAdapter())
                        .setHeaderView(R.layout.dialog_popupmenu_header) // header used for title
                        .setFooterView(R.layout.dialog_popupmenu_footer) // footer used for Read More and Close buttons
                        // this is body
                        .setWidth(800)
                        .addItem(myModel)
                        .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                        .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                        .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                        .build();
                customPowerMenu.showAtCenter(binding.getRoot());


            } else {

                if (binding.thirdCardView.isChecked()) {
                    binding.thirdCardView.setChecked(false);
                } else {

                    //방생성
                    roomModel = new RoomModel();
                    roomModel.room_Uuid = uuid.toString();
                    roomModel.room_JoinPeopleName = myModel.userId;
                    roomModel.room_JoinPeopleImage = myModel.userMainPhoto;
                    roomModel.room_RegDate = todayRegDate.format(new Date().getTime());
                    roomModel.otherUserId = friendModel.userId;
                    roomModel.userToken = friendModel.firebaseToken;
                    roomModel.fromUser = myModel.userId;
                    roomModel.toUser = friendModel.userId;
                    createRoomList(RoomModel.RoomType.P, roomModel);
                    binding.thirdCardView.setChecked(true);
                    /**api_FcmSending(roomModel);*/
                }
            }
        };

    }

    public void createRoomList(RoomModel.RoomType type, RoomModel roomModel) {


        Map<String, Object> roomInfo = new HashMap<>();
        roomInfo.put("roomType", type.fromInteger("P"));
        roomInfo.put("room_JoinPeopleName", roomModel.room_JoinPeopleName);
        roomInfo.put("room_RegDate", roomModel.room_RegDate);
        roomInfo.put("room_Uuid", roomModel.room_Uuid);
        roomInfo.put("otherUserId", roomModel.otherUserId);
        roomInfo.put("fromUser", roomModel.fromUser);
        roomInfo.put("toUser", roomModel.toUser);
        service.createChattingRoomList(CommonString.roomController, roomInfo).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Log.e("abc", "response" + response.toString());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction("SecondFragment");
                            intent.putExtra("SecondFragment", "SecondFragment");
                            sendBroadcast(intent);
                            Map<String, String> result = response.body();
                            roomModel.room_Index = Integer.parseInt(result.get("room_Index"));
                            if (result.get("room_Uuid") != null) {
                                roomModel.room_Uuid = result.get("room_Uuid");
                            }
                            intent = new Intent(context, ChattingRoomActivity.class);
                            intent.putExtra("roomModel", roomModel);
                            startActivity(intent);
                            finish();

                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("abc", "error  : " + t.getMessage());
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        if (isChangedMyNicKName || isChangedMyState || isExistOfPicture) {
            saveFiles();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void init() {
        compositeDisposable = new CompositeDisposable();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        binding = DataBindingUtil.setContentView(ProfileActivity.this, R.layout.activity_profile);
        binding.setProfileActivity(this);


        if (type.equals("me")) {

            user_Index = myModel.user_Index;
            binding.setMyModel(myModel);
            if (!isDestroyed() && !isFinishing()) {

                if (myModel.userMainPhoto.contains(".jpg")) {
                    glide().load(CommonString.CommonStringInterface.FileBaseUrl + myModel.userMainPhoto.replace(" ", "%20")).thumbnail(0.3f).optionalCenterCrop().into(binding.personImageView);
                    intentImageUrl = myModel.userMainPhoto.replace(" ", "%20");
                } else {
                    glide().load(CommonString.CommonStringInterface.FileBaseUrl + myModel.userMainPhoto.replace(" ", "%20") + ".jpg").thumbnail(0.3f).optionalCenterCrop().into(binding.personImageView);
                    intentImageUrl = myModel.userMainPhoto.replace(" ", "%20") + ".jpg";
                }

            }
            if (myModel.user_Introduce == null || myModel.user_Introduce.equals("") || myModel.user_Introduce.equals("null")) {
                binding.myStateIntroduceTextView.setText("상태메시지를 입력해주세요.");
            } else {
                binding.myStateIntroduceTextView.setText(myModel.user_Introduce);
            }

            binding.nickNameTextView.setText(myModel.userId);
            binding.personImageView.setOnClickListener(v -> {
                if (isExistOfPicture == false) {
                    Intent intent = new Intent(context, ImageControlActivity.class);
                    intent.putExtra("imageUrl", intentImageUrl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
                } else {
                    Toast.makeText(context, "사진 변경 확정 전에는 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }


            });

        } else {

            friendModel = (FriendModel) getIntent().getSerializableExtra("friendModel");
            binding.setFriendModel(friendModel);
            if (!isDestroyed() && !isFinishing()) {
                if (friendModel.userMainPhoto.contains(".jpg")) {
                    glide().load(CommonString.CommonStringInterface.FileBaseUrl + friendModel.userMainPhoto.replace(" ", "%20")).thumbnail(0.3f).optionalCenterCrop().into(binding.personImageView);
                    intentImageUrl = friendModel.userMainPhoto.replace(" ", "%20");

                } else {
                    glide().load(CommonString.CommonStringInterface.FileBaseUrl + friendModel.userMainPhoto.replace(" ", "%20") + ".jpg").thumbnail(0.3f).optionalCenterCrop().into(binding.personImageView);
                    intentImageUrl = friendModel.userMainPhoto.replace(" ", "%20") + ".jpg";
                }
            }
            if (friendModel.user_Introduce == null || friendModel.user_Introduce.equals("") || friendModel.user_Introduce.equals("null")) {
                binding.myStateIntroduceTextView.setText("상태메시지를 입력해주세요.");
            } else {
                binding.myStateIntroduceTextView.setText(friendModel.user_Introduce);
            }
            binding.nickNameTextView.setText(friendModel.userId);
            binding.personImageView.setOnClickListener(v -> {
                if (isExistOfPicture == false) {
                    Intent intent = new Intent(context, ImageControlActivity.class);
                    intent.putExtra("imageUrl", intentImageUrl);
                    startActivity(intent);
                    overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
                } else {
                    Toast.makeText(context, "사진 변경 확정 전에는 확인할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }


    //TODO : 글라이드
    public RequestManager glide() {
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .disallowHardwareConfig()
                .error(R.drawable.image_question_mark)
                .placeholder(R.drawable.juntalk_logo);
        return Glide.with(context)
                .setDefaultRequestOptions(requestOptions);
    }


    @Override
    public void createThings() {

        fabOpen = AnimationUtils.loadAnimation
                (this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation
                (this, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation
                (this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation
                (this, R.anim.rotate_backward);


        if (type.equals("me")) {
            binding.thirdTextView.setText("# 정보보기");
            binding.firstCardTextView.setText("# 정보변경");
        } else {
            binding.thirdTextView.setText("# 채팅");
            binding.firstCardTextView.setText("# 정보");
        }

    }


    @Override
    public void clickEvent() {

        binding.secondCardView.setChecked(false);
        binding.secondCardView.setOnClickListener(secondCardViewOnClickEvent);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                List<Uri> uriList = new ArrayList<>();
                //TODO : result 값이 성공할 경우
                if (result.getResultCode() == RESULT_OK) {
                    if (data == null) {
                        //TODO : 이미지 선택이 없을 경우
                        Snackbar.make(getCurrentFocus(), "현재 선택된 사진이 없습니다.", Snackbar.LENGTH_SHORT).show();
                        Log.e("abc", "data is null");
                    } else {
                        //TODO : 이미지 선택이 하나일 경우
                        Uri uri = result.getData().getData();
                        uriList.add(uri);
                        Bitmap bitmap = null;
                        try {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                            }
                            isExistOfPicture = true;

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String imagePath = getRealPathFromURI(uri);
                        Log.e("abc", "imagePath : " + imagePath);
                        getImageFile(imagePath);
                        saveBitmaptoJpeg(bitmap, "abc", simpleDateFormat.format(new Date()) + "_JunTalk");
                        try {
                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
                            Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                            if (!isDestroyed() && !isFinishing()) {
                                glide()
                                        .load(bmp_Copy)
                                        .optionalCenterCrop()
                                        .thumbnail(0.3f)
                                        .into(binding.personImageView);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        binding.thirdCardView.setChecked(false);
        binding.thirdCardView.setOnClickListener(thirdCardViewOnClickEvent);


        binding.firstCardView.setChecked(false);
        binding.firstCardView.setOnClickListener(firstCardViewOnClickEvent);


        binding.fourthCardView.setChecked(false);
        binding.fourthCardView.setOnClickListener(fourthCardViewClickEvent);


        binding.closeImageButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        });


        binding.nickNameImageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.juntalk_logo);
            builder.setTitle("닉네임 변경");
            AppCompatEditText nickNameEditText = new AppCompatEditText(context);
            nickNameEditText.setHint("변경할 닉네임을 입력해주세요.");
            Typeface typeface;
            //api 26이상
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = getResources().getFont(R.font.seven_l);
            } else {
                //api 26 이하
                typeface = Typeface.createFromAsset(getAssets(), "font/seven_l.ttf"); // font 폴더내에 있는 jua.ttf 파일을 typeface로 설정
            }
            nickNameEditText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            nickNameEditText.setTextSize(15);
            nickNameEditText.setTextColor(Color.BLACK);
            nickNameEditText.setTypeface(typeface);
            nickNameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            builder.setView(nickNameEditText);
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nickName = nickNameEditText.getText().toString();
                    binding.nickNameTextView.setText(nickName);
                    isChangedMyNicKName = true;
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });
        });

        binding.myStateIntroduceImageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.juntalk_logo);
            builder.setTitle("상태메시지 변경");
            AppCompatEditText myStateEditText = new AppCompatEditText(context);
            myStateEditText.setHint("변경할 상태메시지를 입력해주세요.");
            Typeface typeface;
            //api 26이상
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = getResources().getFont(R.font.seven_l);
            } else {
                //api 26 이하
                typeface = Typeface.createFromAsset(getAssets(), "font/seven_l.ttf"); // font 폴더내에 있는 jua.ttf 파일을 typeface로 설정
            }
            myStateEditText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            myStateEditText.setTextSize(15);
            myStateEditText.setTextColor(Color.BLACK);
            myStateEditText.setTypeface(typeface);
            myStateEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            builder.setView(myStateEditText);
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String myStateIntroduce = myStateEditText.getText().toString();
                    binding.myStateIntroduceTextView.setText(myStateIntroduce);
                    isChangedMyState = true;
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });
        });

        binding.profileImageChangeImageButton.setOnClickListener(v -> {
            checkPermission();

            AlertDialog.Builder builder;
            AlertDialog alertDialog;
            if (isExistOfPicture == false) {
                builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.juntalk_logo);
                builder.setTitle("알림");
                builder.setMessage("사진을 새로 추가하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                        activityResultLauncher.launch(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = builder.create();
            } else {

                builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.juntalk_logo);
                builder.setTitle("알림");
                builder.setMessage("사진을 변경 하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        activityResultLauncher.launch(intent);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = builder.create();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });
        });
    }


    public String getDateDay(String date, String dateType) throws Exception {

        String day = "";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
        Date nDate = dateFormat.parse(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }

        return day;
    }


    //TODO : 이미지 회전
    public Bitmap rotateBitmap(Uri uri, Bitmap bitmap) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(inputStream);
        inputStream.close();
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<MultipartBody.Part> getFileList() {
        List<MultipartBody.Part> fileList = new ArrayList<>();
        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath();*/
        File fileDir = getFilesDir();
        String path = fileDir.getPath();
        String string_path = path + "/abc";
        File file = new File(string_path);
        File[] files = file.listFiles();
        for (File myFile : files) {
            RequestBody requestFile = RequestBody.create(myFile, MediaType.parse("multipart/form-data"));
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
            fileList.add(uploadFile);
        }
        return fileList;
    }

    public void myInfoSetting(MyModel myModel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (myModel != null) {

                    if (myModel.userMainPhoto != null && !myModel.userMainPhoto.replace(" ", "").trim().equals("")) {
                        if (!isDestroyed() && !isFinishing()) {
                            glide().load(CommonString.CommonStringInterface.FileBaseUrl + myModel.userMainPhoto.replace(" ", "%20") + ".jpg").thumbnail(0.3f).optionalCenterCrop().into(binding.personImageView);
                        }
                    }

                    if (myModel.user_Introduce != null && !myModel.user_Introduce.replace(" ", "").trim().equals("")) {
                        if (binding.myStateIntroduceTextView != null) {
                            binding.myStateIntroduceTextView.setText(myModel.user_Introduce);
                        }

                    }

                    if (myModel.userId != null && !myModel.userId.replace(" ", "").trim().equals("")) {
                        if (binding.nickNameTextView != null) {
                            binding.nickNameTextView.setText(myModel.userId);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void getServer() {
        sharedPreferences = getSharedPreferences("LookStatus", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("profileListLook", false) == false) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.clickImageButton), "정보를 확인해 보세요 !", "버튼을 클릭하여 채팅 및 게시물들을 확인해 보세요 !")
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
                            editor.putBoolean("profileListLook", true);
                            editor.commit();
                        }
                    });

        }

    }

    public void animateFab(View view) {
        if (isOpen) {
            binding.clickImageButton.startAnimation(rotateBackward);
            binding.firstCardView.startAnimation(fabClose);
            binding.secondCardView.startAnimation(fabClose);
            binding.thirdCardView.startAnimation(fabClose);
            binding.fourthCardView.startAnimation(fabClose);
            binding.firstCardView.setClickable(false);
            binding.secondCardView.setClickable(false);
            binding.thirdCardView.setClickable(false);
            binding.fourthCardView.setClickable(false);
            binding.firstCardView.setOnClickListener(null);
            binding.secondCardView.setOnClickListener(null);
            binding.thirdCardView.setOnClickListener(null);
            binding.fourthCardView.setOnClickListener(null);
            isOpen = false;
        } else {
            binding.clickImageButton.startAnimation(rotateForward);
            binding.firstCardView.startAnimation(fabOpen);
            binding.secondCardView.startAnimation(fabOpen);
            binding.thirdCardView.startAnimation(fabOpen);
            binding.fourthCardView.startAnimation(fabOpen);
            binding.firstCardView.setClickable(true);
            binding.secondCardView.setClickable(true);
            binding.thirdCardView.setClickable(true);
            binding.fourthCardView.setClickable(true);
            binding.firstCardView.setOnClickListener(firstCardViewOnClickEvent);
            binding.secondCardView.setOnClickListener(secondCardViewOnClickEvent);
            binding.thirdCardView.setOnClickListener(thirdCardViewOnClickEvent);
            binding.fourthCardView.setOnClickListener(fourthCardViewClickEvent);
            isOpen = true;
        }
    }

    //TODO : 저장소 저장하기 권한 묻기
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        }
    }

    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {

        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();

        String foler_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + foler_name;
        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        getFileList();
    }


    public Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        imageFileName = UUID.randomUUID().toString() + "_JunTalk.jpg";
        userMainPhoto = MultipartBody.Part.createFormData("userMainPhoto", imageFileName, requestFile);

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

    public void saveFiles() {

        if (isChangedMyNicKName) {
            String currentUserId = myModel.userId;
            myModel.userId = String.valueOf(binding.nickNameTextView.getText());
            JunApplication.setMyModel(myModel);
            String changeUserId = String.valueOf(binding.nickNameTextView.getText());
            chattingDataDB.chattingDao().updateUserId(currentUserId, changeUserId);
        }

        if (isExistOfPicture) {
            @NonNull String currentId;
            if (isChangedMyNicKName) {
                currentId = String.valueOf(binding.nickNameTextView.getText());
            } else {
                currentId = myModel.userId;
            }
            chattingDataDB.chattingDao().updateUserMainPhoto(currentId, imageFileName);
        }

        RequestBody nickNameBody = RequestBody.create(binding.nickNameTextView.getText().toString().trim(), MediaType.parse("text/plain"));
        RequestBody myStateContentBody = RequestBody.create(binding.myStateIntroduceTextView.getText().toString().trim(), MediaType.parse("text/plain"));
        RequestBody user_IndexBody = RequestBody.create(String.valueOf(user_Index), MediaType.parse("text/plain"));
        Call<MyModel> result = service.saveMyDataFile(CommonString.userController, userMainPhoto, nickNameBody, user_IndexBody, myStateContentBody);
        result.enqueue(new Callback<MyModel>() {
            @Override
            public void onResponse(Call<MyModel> call, Response<MyModel> response) {
                Log.e("abc", "result : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            Intent FirstFragment = new Intent();
                            FirstFragment.setAction("FirstFragment");
                            FirstFragment.putExtra("FirstFragment", "FirstFragment");
                            sendBroadcast(FirstFragment);

                            Intent SecondFragment = new Intent();
                            SecondFragment.setAction("SecondFragment");
                            SecondFragment.putExtra("SecondFragment", "SecondFragment");
                            sendBroadcast(SecondFragment);

                            Intent FourthFragment = new Intent();
                            FourthFragment.setAction("FourthFragment");
                            FourthFragment.putExtra("FourthFragment", "FourthFragment");
                            sendBroadcast(FourthFragment);

                            Intent intent = new Intent();
                            intent.setAction("WholeActivity");
                            intent.putExtra("WholeActivity", "WholeActivity");
                            sendBroadcast(intent);

                            Toast.makeText(context, "저장 완료", Toast.LENGTH_SHORT).show();
                        }
                    });
                    userMainPhoto = null;
                    MyModel myModel = response.body();
                    JunApplication.setMyModel(myModel);
                    myInfoSetting(myModel);
                } else {
                    Log.e("abc", "fail");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MyModel> call, Throwable t) {
                Log.e("abc", "error  :" + t.getMessage());
            }
        });
    }

    public void api_FcmSending(RoomModel roomModel) {

        service.send_NotificationFcm(CommonString.fcmController, roomModel, "friendRequest").enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "result : " + response.body());
                    Log.e("abc", "success");
                    finish();
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


    public int getDp(int a) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, context.getResources().getDisplayMetrics());
        return dp;
    }

}


