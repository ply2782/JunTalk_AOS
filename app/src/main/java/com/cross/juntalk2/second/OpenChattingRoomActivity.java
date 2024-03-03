package com.cross.juntalk2.second;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.transition.TransitionValues;
import androidx.transition.Visibility;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityChattingRoomBinding;
import com.cross.juntalk2.databinding.ActivityOpenChattingBinding;
import com.cross.juntalk2.databinding.ActivityOpenChattingRoomBinding;
import com.cross.juntalk2.diffutil.NoticeDiffUtil;
import com.cross.juntalk2.first.CustomPopUpMenuAdapter;
import com.cross.juntalk2.model.ChattingModel;
import com.cross.juntalk2.model.ChattingViewModel;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.NoticeViewModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.room.ChattingData;
import com.cross.juntalk2.room.ChattingDataDB;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.CompletableTransformer;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class OpenChattingRoomActivity extends CreateNewCompatActivity {

    private Window window;
    private Animation rotateForward, rotateBackward;
    private Handler handler;
    private List<ChattingModel> chattingModelList;
    private ActivityOpenChattingRoomBinding binding;
    private RoomModel roomModel;
    private OpenChattingRoomAdapter adapter;
    private StompClient stompClient;
    private Gson sendEnterAndExitGson, gson;
    private MyModel myModel;
    private Type type;
    private boolean isOpen = false, isImoticonBoxOpen = false;
    private int rootHeight = -1;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RetrofitService service;
    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private SimpleDateFormat simpleDateFormat;
    private String imageUrl;
    private String uploadUrl;
    private String videoUrl;
    private List<String> imageNameList, videoNameList;
    private List<Map<String, Object>> conversationList;
    private List<MultipartBody.Part> videoFileList;
    private List<MultipartBody.Part> imageFileList;
    private ViewPager2Adapter viewPager2Adapter;
    private NoticeViewModel noticeViewModel;
    private ChattingViewModel chattingViewModel;
    private ViewModelFactory viewModelFactory;
    private SimpleDateFormat todayFormat = new SimpleDateFormat("aa HH:mm");
    private SimpleDateFormat notice_TodayFormat = new SimpleDateFormat("yy/MM/dd (E) aa HH:mm");
    private SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Timer timer;
    private TimerTask timerTask;
    private int pageCurrent = 0;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private PowerMenu powerMenu;
    private Observer<List<ChattingModel>> chattingObserver;
    private CustomPowerMenu friendListPopupMenu;
    private List<Bitmap> thumbNailBitmap;
    private boolean otherPersonEnter = false;
    private List<FriendModel> currentPeopleJoinList;
    private ChattingDataDB chattingDataDB;
    private ChattingData chattingData;
    private Type typeTokenOfChattingData = new TypeToken<ChattingData>() {
    }.getType();

    private OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            powerMenu.setSelectedPosition(position); // change selected item
            switch (item.getTitle().toString()) {
                case "# 방 나가기":
                    Log.e("abc", "방가나기");
                    sendExitMessage();
                    break;

                case "# 초대하기":
                    friendListDialog();
                    break;


            }
            powerMenu.dismiss();
        }
    };
    private OnMenuItemClickListener<FriendModel> onFriendListClickListener = new OnMenuItemClickListener<FriendModel>() {
        @Override
        public void onItemClick(int position, FriendModel item) {
            addFriendInRoom(roomModel.roomType, myModel.userMainPhoto, item.userId, item.firebaseToken, myModel.userId, item.userId, ChattingModel.UserState.NULL, roomModel.room_Index, myModel.user_Index, myModel.userId, item.userKakaoOwnNumber, item.userMainPhoto, roomModel.room_Uuid, notice_TodayFormat.format(new Date().getTime()));
            friendListPopupMenu.dismiss();
        }
    };

    private ChattingService chattingService;
    private boolean isService = false;
    private ServiceConnection conn;

    public void friendListDialog() {
        api_RoomJoinPeopleNameList(roomModel.room_Uuid, myModel.userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("CheckResult")
    public void connectionSocket() {
        String url = "ws://ply2782ply2782.cafe24.com:8080/chatting/websocket";
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        stompClient.connect();
        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.e("abc", "OPENED");
                            //TODO : 채팅은 리시브 부터 먼저 받아야 함
                            receiveMessage();
                            receiveMessageOfInAndOut();
                            sendEnterMessage();
                            sendCurrentState_in();

                            break;
                        case ERROR:
                            Log.e("abc", "Stomp connection error", lifecycleEvent.getException());

                            break;
                        case CLOSED:
                            Log.e("abc", "Stomp connection closed");


                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            break;
                    }
                });
        Log.e("abc", "connectionSocket() 실행..");
    }

    //TODO  : 입장시 메시지 보내기
    @SuppressLint("CheckResult")
    public void sendEnterMessage() {

        try {
            ChattingModel chattingModel = new ChattingModel();
            chattingModel.userToken = myModel.firebaseToken;
            if (myModel == null || myModel.userId == null) {
                chattingModel.userId = roomModel.room_JoinPeopleName;
            } else {
                chattingModel.userId = myModel.userId;
            }
            chattingModel.room_Uuid = roomModel.room_Uuid;
            chattingModel.room_Index = roomModel.room_Index;
            chattingModel.userMessageType = ChattingModel.MessageType.valueOf("ENTER");
            chattingModel.userConversationTime = todayFormat.format(new Date().getTime());
            if (myModel == null || myModel.userId == null) {
                chattingModel.userImage = roomModel.room_JoinPeopleImage;
            } else {
                chattingModel.userImage = myModel.userMainPhoto;
            }
            chattingModel.currentActualTime = today.format(new Date().getTime());
            chattingModel.actualTime = new Date().getTime();
            stompClient.send("/comingIn/messageShare", sendEnterAndExitGson.toJson(chattingModel))
                    .compose(applySchedulers())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                    .subscribe(() -> {
                        Log.e("abc", "REST echo send successfully");
                    }, throwable -> {
                        Log.e("abc", "Error send REST echo", throwable);
                    });
        } catch (Exception e) {

        }
    }

    //TODO  : 방 나갈때 메시지 보내기
    @SuppressLint("CheckResult")
    public void sendExitMessage() {
        //TODO : receive Message 에서 chattingListInfoModel 자체를 상대방의 것으로 변환하기 때문에 나갈때는 자기의 것으로 다시한번 변환해주어야 함.
        ChattingModel chattingModel = new ChattingModel();
        chattingModel.userToken = myModel.firebaseToken;
        chattingModel.room_Uuid = roomModel.room_Uuid;
        chattingModel.userMessageType = ChattingModel.MessageType.valueOf("EXIT");
        chattingModel.room_Index = roomModel.room_Index;

        if (myModel == null || myModel.userId == null) {
            chattingModel.userId = roomModel.room_JoinPeopleName;
        } else {
            chattingModel.userId = myModel.userId;
        }
        chattingModel.userState = ChattingModel.UserState.OUT;
        if (myModel == null || myModel.userId == null) {
            chattingModel.userImage = roomModel.room_JoinPeopleImage;
        } else {
            chattingModel.userImage = myModel.userMainPhoto;
        }
        chattingModel.currentActualTime = today.format(new Date().getTime());
        chattingModel.userConversationTime = todayFormat.format(new Date().getTime());
        stompClient.send("/comingIn/messageShare", sendEnterAndExitGson.toJson(chattingModel))
                .compose(applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(() -> {
                    Log.e("abc", "REST echo send successfully");
                    chattingDataDB.chattingDao().removeAllConversation(chattingModel.room_Uuid);
                    refreshActivity();
                }, throwable -> {
                    Log.e("abc", "Error send REST echo", throwable);
                });
    }


    //TODO  : 현재 방에 있는지 없는지 확인
    @SuppressLint("CheckResult")
    public void sendCurrentState_in() {

        try {
            //TODO : receive Message 에서 chattingListInfoModel 자체를 상대방의 것으로 변환하기 때문에 나갈때는 자기의 것으로 다시한번 변환해주어야 함.
            ChattingModel chattingModel = new ChattingModel();
            chattingModel.room_Uuid = roomModel.room_Uuid;
            chattingModel.room_Index = roomModel.room_Index;

            if (myModel == null || myModel.userId == null) {
                chattingModel.userId = roomModel.room_JoinPeopleName;
            } else {
                chattingModel.userId = myModel.userId;
            }
            chattingModel.currentState = "IN";
            chattingModel.actualTime = new Date().getTime();
            chattingModel.currentActualTime = today.format(new Date().getTime());
            stompClient.send("/comingIn/updateCurrentState", sendEnterAndExitGson.toJson(chattingModel))
                    .compose(applySchedulers())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                    .subscribe(() -> {
                        Log.e("abc", "updateCurrentState in successfully");

                    }, throwable -> {
                        Log.e("abc", "Error send REST echo", throwable);
                    });
        } catch (Exception e) {

        }
    }


    //TODO  : 현재 방에 있는지 없는지 확인
    @SuppressLint("CheckResult")
    public void sendCurrentState_out() {
        try {
            //TODO : receive Message 에서 chattingListInfoModel 자체를 상대방의 것으로 변환하기 때문에 나갈때는 자기의 것으로 다시한번 변환해주어야 함.
            ChattingModel chattingModel = new ChattingModel();
            chattingModel.room_Uuid = roomModel.room_Uuid;
            chattingModel.room_Index = roomModel.room_Index;
            if (myModel == null || myModel.userId == null) {
                chattingModel.userId = roomModel.room_JoinPeopleName;
            } else {
                chattingModel.userId = myModel.userId;
            }
            chattingModel.currentState = "OUT";
            chattingModel.actualTime = new Date().getTime();
            chattingModel.currentActualTime = today.format(new Date().getTime());
            stompClient.send("/comingIn/updateCurrentState", sendEnterAndExitGson.toJson(chattingModel))
                    .compose(applySchedulers())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                    .subscribe(() -> {
                        Log.e("abc", "updateCurrentState out successfully");
                        if (stompClient != null) {

                            stompClient.disconnect();
                            stompClient = null;
                            service = null;
                        }
                    }, throwable -> {
                        Log.e("abc", "Error send REST echo", throwable);
                    });
        } catch (Exception e) {

        }
    }


    public void api_ControlAlarm(String room_Uuid, String userId, boolean currentState) {

        service.chattingAlarmUpdate(CommonString.roomController, room_Uuid, userId, currentState).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (currentState) {
                                Toast.makeText(context, "알람 ON", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "알람 OFF", Toast.LENGTH_SHORT).show();
                            }

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

    public void refreshActivity() {

        Intent fifthIntent = new Intent();
        fifthIntent.setAction("createClub");
        fifthIntent.putExtra("createClub", "createClub");
        sendBroadcast(fifthIntent);

        finish();
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        sendCurrentState_out();
        Intent intent = new Intent();
        intent.setAction("OpenChatting");
        intent.putExtra("OpenChatting", "OpenChatting");
        sendBroadcast(intent);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    //TODO : 메시지 받기
    @SuppressLint("CheckResult")
    public void receiveMessage() {
        // Receive greetings
        stompClient.topic("/send/chatting/" + roomModel.room_Index)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(topicMessage -> {
                    Log.e("abc", "Received " + topicMessage.getPayload());
                    if (topicMessage == null || topicMessage.getPayload().equals("")) {

                    } else {

                        try {
                            ChattingModel chattingModel = gson.fromJson(topicMessage.getPayload(), type);
                            chattingModelList.add(chattingModel);
                            addChat(chattingModel);
                        } catch (Exception e) {

                        }

                    }

                }, throwable -> {
                    Log.e("abc", "Error on subscribe topic", throwable);
                });
        Log.e("abc", "receiveMessage() 실행..");
    }


    //TODO : 들어오고 나갈 때의 신호 받기
    @SuppressLint("CheckResult")
    public void receiveMessageOfInAndOut() {

        // Receive greetings
        stompClient.topic("/send/inAndOut/" + roomModel.room_Index)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(topicMessage -> {
                    if (topicMessage == null || topicMessage.getPayload().equals("")) {

                    } else {

                        Type typeToken = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map<String, String> resultMap = gson.fromJson(topicMessage.getPayload(), typeToken);
                        String userId = resultMap.get("userId");
                        String in = resultMap.get("result");
                        if (in.equals("IN")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    api_loadChattingConversation();
                                    if (!userId.equals(myModel.userId)) {
                                        otherPersonEnter = true;
                                    }

                                }
                            });
                        }
                    }

                }, throwable -> {
                    Log.e("abc", "Error on subscribe topic", throwable);
                });
        Log.e("abc", "receiveMessage() 실행..");
    }


    //TODO : list 에서 add 할 때 같은 객체이면 관련된 객체 내용이 전부 업데이트 되기 때문에 새로운 객체 생성하여 보내줘야함
    @SuppressLint("CheckResult")
    public void sendMyMessage(String imageUrl, String videoUrl, String uploadUrl) {
        String conversation = binding.editTextView.getText().toString();
        ChattingModel chattingModel = new ChattingModel();
        chattingModel.userToken = myModel.firebaseToken;
        chattingModel.room_Index = roomModel.room_Index;

        if (myModel == null || myModel.userId == null) {
            chattingModel.userImage = roomModel.room_JoinPeopleImage;
        } else {
            chattingModel.userImage = myModel.userMainPhoto;
        }

        if (myModel == null || myModel.userId == null) {
            chattingModel.userId = roomModel.room_JoinPeopleName;
        } else {
            chattingModel.userId = myModel.userId;
        }
        chattingModel.userConversation = conversation;
        chattingModel.chatting_ImageFile = imageUrl;
        chattingModel.chatting_VideoFile = videoUrl;
        chattingModel.uploadUrl = uploadUrl;
        chattingModel.userJoinCount = roomModel.room_joinCount;
        chattingModel.userMessageType = ChattingModel.MessageType.valueOf("CONVERSATION");
        chattingModel.userConversationTime = todayFormat.format(new Date().getTime());
        chattingModel.actualTime = new Date().getTime();
        chattingModel.currentActualTime = today.format(new Date().getTime());
        chattingModel.room_Uuid = roomModel.room_Uuid;
        //TODO : json 에서 개행처리 할때 gson.toJason으로 치환해준 다음 넣어줘야 함

        stompClient.send("/comingIn/messageShare", gson.toJson(chattingModel))
                .compose(applySchedulers())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(() -> {
                    Log.d("abc", "REST echo send successfully");
                }, throwable -> {
                    Log.e("abc", "Error send REST echo", throwable);
                });
        binding.editTextView.getText().clear();
        this.imageUrl = "null";
        this.uploadUrl = "null";
        this.videoUrl = "null";
    }


    @Override
    public void onBackPressed() {
        if (isOpen) {
            binding.menuImageButton.performClick();
            if (binding.imoticonBoxRelativeLayout.getVisibility() == View.VISIBLE) {
                binding.imoticonBoxRelativeLayout.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    //TODO : 채팅리스트에 채팅 내용 입력해주기
    public void addChat(ChattingModel chattingModel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.setChattingModelList(chattingModel);
            }
        });
    }


    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()));
    }


    @Override
    public void getInterfaceInfo() {

        chattingDataDB = ChattingDataDB.getInstance(context);
        chattingData = new ChattingData();

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
                    } else {


                        if (data.getClipData() != null) {
                            // TODO : 이미지 선택이 여러장일 경우
                            ClipData clipData = data.getClipData();

                            if (clipData.getItemCount() > 10) {
                                Snackbar.make(getCurrentFocus(), "사진선택은 10장까지만 가능합니다.", Snackbar.LENGTH_SHORT).show();

                            } else {

                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    uriList.add(uri);
                                    Bitmap bitmap = null;
                                    try {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                            } catch (Exception e) {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }
                                        } else {
                                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                        }
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    try {
                                        Log.e("abc", "uri : " + uri.toString());
                                        if (uri.toString().contains("video")) {
                                            String videoPath = getRealPathFromURI(uri); // path 경로
//                                            String videoPath = getPath(context, uri);
//                                            thumbNailBitmap.add(createThumbnail(context, videoPath));
                                            getVideoFile(videoPath, i);

                                        } else {
                                            String imagePath = getRealPathFromURI(uri);
                                            getImageFile(imagePath, i);
                                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                            saveBitmaptoJpeg(bmRotated, "abc", simpleDateFormat.format(new Date()) + "_" + i + "_JunTalk");

                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {


                            ClipData clipData = data.getClipData();
                            if (data.getClipData() == null) {
                                //TODO : 이미지 선택이 하나일 경우
                                Uri uri = result.getData().getData();
                                uriList.add(uri);

                                Bitmap bitmap = null;
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                        try {
                                            bitmap = ImageDecoder.decodeBitmap(source);
                                        } catch (Exception e) {
                                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                        }
                                    } else {
                                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                    }
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                try {

                                    if (uri.toString().contains("video")) {
                                        String videoPath = getRealPathFromURI(uri); // path 경로
//                                        thumbNailBitmap.add(createThumbnail(context, videoPath));
//                                    String videoPath = getPath(context, uri);
                                        getVideoFile(videoPath, -1);

                                    } else {
                                        String imagePath = getRealPathFromURI(uri);
                                        getImageFile(imagePath, -1);
                                        Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                        saveBitmaptoJpeg(bmRotated, "abc", simpleDateFormat.format(new Date()) + "_JunTalk");

                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    uriList.add(uri);
                                    Bitmap bitmap = null;
                                    try {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                            } catch (Exception e) {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }

                                        } else {
                                            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                        }
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    try {
                                        Log.e("abc", "uri : " + uri.toString());
                                        if (uri.toString().contains("video")) {
                                            String videoPath = getRealPathFromURI(uri); // path 경로
//                                            String videoPath = getPath(context, uri);
                                            getVideoFile(videoPath, i);

                                        } else {
                                            String imagePath = getRealPathFromURI(uri);
                                            getImageFile(imagePath, i);
                                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                            saveBitmaptoJpeg(bmRotated, "abc", simpleDateFormat.format(new Date()) + "_" + i + "_JunTalk");

                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }//data == null or
                    saveFiles();
                }
            }
        });
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

    //TODO : 동영상 섬네일 만들기
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


    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {

        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();
        String folder_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + folder_name;
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
    }


    //TODO : 비디오 파일 서버로 전송
    public void getVideoFile(String filePath, int count) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String videoFileName;
        if (count != 0) {
            videoFileName = simpleDateFormat.format(new Date()) + "_" + count + "_JunTalk.mp4";
        } else {
            videoFileName = simpleDateFormat.format(new Date()) + "_JunTalk.mp4";
        }
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
        videoFileList.add(uploadFile);
        String fileName = videoFileName.replace(" ", "%20");
        videoNameList.add(fileName);
        imageNameList.add(fileName);
    }

    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath, int count) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String imageFileName;
        if (count != -1) {
            imageFileName = simpleDateFormat.format(new Date()) + "_" + count + "_JunTalk.jpg";
        } else {
            imageFileName = simpleDateFormat.format(new Date()) + "_JunTalk.jpg";
        }
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile);
        imageFileList.add(uploadFile);
        String fileName = imageFileName.replace(" ", "%20");
        imageNameList.add(fileName);
    }


    //todo : 비트맵을 바이트로 변환
    public byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //todo : 바이트를 비트맵으로 변환
    public Bitmap byteArrayToBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }


    //TODO : 안드로이드 패키지 안에 있는 files 경로에 있는 파일들을 보내는 부분
    public void getFileList() {
        File fileDir = getFilesDir();
        String path = fileDir.getPath();
        String string_path = path + File.separator + "abc" + File.separator;
        File file = new File(string_path);
        File[] files = file.listFiles();
        for (File myFile : files) {
            RequestBody requestFile = RequestBody.create(myFile, MediaType.parse("multipart/form-data"));
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files", myFile.getName(), requestFile);
            imageFileList.add(uploadFile);
        }
    }


    //TODO : 이미지 회전 시키기
    public Bitmap rotateBitmap(Uri uri, Bitmap bitmap) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(inputStream);
        inputStream.close();
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        Log.e("abc", "orientation : " + orientation);
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

    @Override
    public void getIntentInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        myModel = JunApplication.getMyModel();
        roomModel = (RoomModel) getIntent().getSerializableExtra("roomModel");
        Log.e("abc", "roomUuid : " + roomModel.room_Uuid);

        //todo : 서비스 바인드 연결 부분 ( 서비스에서의 데이터를 가져와서 이 액티비티에 쓸 수 있도록 해줌 )
        conn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                // 서비스와 연결되었을 때 호출되는 메서드
                // 서비스 객체를 전역변수로 저장
                ChattingService.MyBinder mb = (ChattingService.MyBinder) service;
                chattingService = mb.getService(); // 서비스가 제공하는 메소드 호출하여
                // 서비스쪽 객체를 전달받을수 있슴
                isService = true;
                Log.e("abc", "binding sevice");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    }
                });

            }

            public void onServiceDisconnected(ComponentName name) {
                // 서비스와 연결이 끊겼을 때 호출되는 메서드
                Log.e("abc", "name : " + name);
                isService = false;
            }
        };

        //todo : fcm 구독으로 전체 fcm 보내기 위한 것
        /*FirebaseMessaging.getInstance().subscribeToTopic(roomModel.room_Uuid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "success";
                        if (!task.isSuccessful()) {
                            msg = "fail";
                        }
                        Log.e("abc", "구독 완료  : "+msg);
                        Toast.makeText(OpenChattingRoomActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });*/

        Map<String, Object> map = new HashMap<>();
        map.put("userId", myModel.userId);
        map.put("room_Uuid", roomModel.room_Uuid);
        map.put("user_Index", myModel.user_Index);
        map.put("userMainPhoto", myModel.userMainPhoto);
        service.insertOpenChattingList(CommonString.roomController, map).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });

    }

    @Override
    public void init() {
        checkPermission();

        timer = new Timer();
        currentPeopleJoinList = new ArrayList<>();
        thumbNailBitmap = new ArrayList<>();
        conversationList = new ArrayList<>();
        chattingModelList = new ArrayList<>();
        imageFileList = new ArrayList<>();
        videoFileList = new ArrayList<>();
        imageNameList = new ArrayList<>();
        videoNameList = new ArrayList<>();

        sendEnterAndExitGson = new Gson();
        gson = new Gson();
        type = new TypeToken<ChattingModel>() {
        }.getType();

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        loadingDialog = new LoadingDialog(context);
        window = getWindow();
        compositeDisposable = new CompositeDisposable();
        rotateForward = AnimationUtils.loadAnimation
                (this, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation
                (this, R.anim.rotate_backward);
        handler = new Handler(Looper.getMainLooper());
        binding = DataBindingUtil.setContentView(OpenChattingRoomActivity.this, R.layout.activity_open_chatting_room);
        setSupportActionBar(binding.collapsingToolBarLayout);
        adapter = new OpenChattingRoomAdapter(context, OpenChattingRoomActivity.this);

        PhItemAnimator phItemAnimator = new PhItemAnimator(context);

        binding.conversationListRecyclerView.setAdapter(adapter);
        binding.conversationListRecyclerView.setItemAnimator(phItemAnimator);
        viewModelFactory = new ViewModelFactory();
        noticeViewModel = new ViewModelProvider(this, viewModelFactory).get(NoticeViewModel.class);
        chattingViewModel = new ViewModelProvider(this, viewModelFactory).get(ChattingViewModel.class);
        chattingViewModel.init();

        viewPager2Adapter = new ViewPager2Adapter(new NoticeDiffUtil());
        binding.bannerViewPager2.setAdapter(viewPager2Adapter);
        binding.bannerViewPager2.setPageTransformer(new DepthPageTransformer());
        binding.indicator.setViewPager(binding.bannerViewPager2);
        viewPager2Adapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());

        chattingObserver = new Observer<List<ChattingModel>>() {
            @Override
            public void onChanged(List<ChattingModel> chattingModels) {
                try {
                    adapter.settingConversation(chattingModels);
                    adapter.notifyDataSetChanged();
                    if (!otherPersonEnter) {
                        binding.conversationListRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }

                } catch (Exception e) {

                }
            }
        };

        chattingViewModel.getChattingMutableLiveData().observe(this, chattingObserver);
//        api_loadChattingConversation();

    }


    @Override
    public void createThings() {
        connectionSocket();


        List<PowerMenuItem> powerMenuItems = new ArrayList<>();
//        powerMenuItems.add(new PowerMenuItem("# 초대하기", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 방 나가기", R.drawable.juntalk_logo, false));

        powerMenu = new PowerMenu.Builder(context)
                .addItemList(powerMenuItems) // list has "Novel", "Poerty", "Art"
//                .addItem(new PowerMenuItem("Journals", false)) // add an item.
//                .addItem(new PowerMenuItem("Travel", false)) // aad an item list.
                .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setPadding(30)
                .setTextColor(ContextCompat.getColor(context, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setWidth(800)
                .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                .setSelectedMenuColor(ContextCompat.getColor(context, R.color.teal_200))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();
    }


    @Override
    public void clickEvent() {

        if (roomModel.alarm) {
            binding.toggleButtonGroup.check(R.id.cattingAlarmON);
            binding.toggleButtonGroup.uncheck(R.id.cattingAlarmOFF);
        } else {
            binding.toggleButtonGroup.uncheck(R.id.cattingAlarmON);
            binding.toggleButtonGroup.check(R.id.cattingAlarmOFF);
        }

        binding.toggleButtonGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (R.id.cattingAlarmON == checkedId) {
                    if (isChecked) {
                        api_ControlAlarm(roomModel.room_Uuid, myModel.userId, true);
                    } else {
                        api_ControlAlarm(roomModel.room_Uuid, myModel.userId, false);
                    }
                }

            }
        });

        binding.optionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerMenu.showAtCenter(binding.getRoot());
            }
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                pageCurrent++;
                if (pageCurrent > viewPager2Adapter.getItemCount()) {
                    pageCurrent = 0;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.bannerViewPager2.setCurrentItem(pageCurrent);
                    }
                });
            }
        };
        timer.schedule(timerTask, 2000, 3000);

        binding.bannerViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        binding.closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("업로드");
                builder.setIcon(R.drawable.juntalk_logo);
                RadioGroup radioGroup = new RadioGroup(context);
                radioGroup.setGravity(Gravity.CENTER);
                RadioButton videoRadioButton = new RadioButton(context);
                videoRadioButton.setText("비디오");
                videoRadioButton.setTextSize(10.0f);
                videoRadioButton.setId(1);
                radioGroup.addView(videoRadioButton);
                RadioButton imageRadioButton = new RadioButton(context);
                imageRadioButton.setText("사진");
                imageRadioButton.setTextSize(10.0f);
                imageRadioButton.setId(2);
                radioGroup.addView(imageRadioButton);
                builder.setView(radioGroup);
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        Intent intent;
                        switch (checkedId) {
                            case 1:
                                intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_PICK);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                activityResultLauncher.launch(intent);
                                alertDialog.dismiss();
                                break;
                            case 2:
                                intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                activityResultLauncher.launch(intent);
                                alertDialog.dismiss();
                                break;
                        }
                    }
                });
            }
        });


        binding.openSingImageButton.bringToFront();
        binding.openSingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
                scaleAnimation.setDuration(600);
                scaleAnimation.setFillAfter(true);
                binding.noticeRelativeLayout.startAnimation(scaleAnimation);
                binding.noticeRelativeLayout.animate().alpha(1.0f).setDuration(500);
                binding.noticeRelativeLayout.setVisibility(View.VISIBLE);
                binding.openSingImageButton.setVisibility(View.GONE);



                /*TranslateAnimation animate = new TranslateAnimation(binding.noticeRelativeLayout.getWidth(), 0, 0, 0);
                animate.setDuration(600);
                animate.setInterpolator(new AnticipateOvershootInterpolator());
                animate.setFillAfter(true);*/
//                Transition transition = new CircularRevealTransition();
//                transition.setDuration(600);
//                transition.addTarget(R.id.noticeRelativeLayout);
//                TransitionManager.beginDelayedTransition(binding.noticeRelativeLayout, transition);
//                binding.noticeRelativeLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.closeSignImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f);
                scaleAnimation.setDuration(600);
                scaleAnimation.setFillAfter(true);
                binding.noticeRelativeLayout.startAnimation(scaleAnimation);
                binding.noticeRelativeLayout.animate().alpha(0.0f).setDuration(500);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.noticeRelativeLayout.setVisibility(View.GONE);
                        binding.openSingImageButton.bringToFront();
                        binding.openSingImageButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                /*TranslateAnimation animate = new TranslateAnimation(0, binding.noticeRelativeLayout.getWidth(), 0, 0);
                animate.setDuration(600);
                animate.setInterpolator(new AnticipateOvershootInterpolator());
                animate.setFillAfter(true);*/
//                Transition transition = new Fade();
//                Transition transition = new CircularRevealTransition();
//                transition.setDuration(600);
//                transition.addTarget(R.id.noticeRelativeLayout);
//                TransitionManager.beginDelayedTransition(binding.noticeRelativeLayout, transition);
//                binding.noticeRelativeLayout.setVisibility(View.GONE);


            }
        });

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editTextView.getText() == null || binding.editTextView.getText().toString().replace(" ", "").equals("")) {

                } else {
                    sendMyMessage("null", "null", "null");
                }
            }
        });

        binding.menuImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation;
                if (isOpen) {
                    animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    binding.menuImageButton.startAnimation(rotateBackward);
                    binding.editBoxLinearLayout.setVisibility(View.INVISIBLE);
                    binding.editBoxLinearLayout.setAnimation(animation);
                    isOpen = false;
                } else {
                    animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    binding.menuImageButton.startAnimation(rotateForward);
                    binding.editBoxLinearLayout.setVisibility(View.VISIBLE);
                    binding.editBoxLinearLayout.setAnimation(animation);
                    isOpen = true;
                }
            }
        });

        binding.imoticonImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                if (isImoticonBoxOpen) {
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    inputMethodManager.showSoftInput(binding.editTextView, 0);
                    binding.imoticonBoxRelativeLayout.setVisibility(View.GONE);
                    binding.imoticonBoxRelativeLayout.setAnimation(animation);
                    isImoticonBoxOpen = false;
                } else {
                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    inputMethodManager.hideSoftInputFromWindow(binding.editTextView.getWindowToken(), 0);
                    binding.imoticonBoxRelativeLayout.setVisibility(View.VISIBLE);
                    binding.imoticonBoxRelativeLayout.setAnimation(animation);
                    isImoticonBoxOpen = true;
                }
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            }
        });

        //TODO : 이모티콘 컨테이너 ( 키보드 숨기고 보여주기 )
        /*binding.imoticonBoxRelativeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rootHeight == -1)
                    rootHeight = binding.imoticonBoxRelativeLayout.getHeight(); // 매번 호출되기 때문에, 처음 한 번만 값을 할당해준다.
                Rect visibleFrameSize = new Rect();
                binding.imoticonBoxRelativeLayout.getWindowVisibleDisplayFrame(visibleFrameSize);
                int heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top;
                // 키보드를 제외한 높이가 디바이스 root_view보다 높거나 같다면, 키보드가 올라왔을 때가 아니므로 거른다.
                if (heightExceptKeyboard < rootHeight) {
                    // 키보드 높이
                    int keyboardHeight = rootHeight - heightExceptKeyboard;
                    Log.e("abc", "keyboardHeight :" + keyboardHeight);
                }
            }
        });*/

        binding.imoticonInfoImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] array = getResources().getStringArray(R.array.imoticonLicense);
                List<String> licenseItem = Arrays.asList(array);
                CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder<>(context, new CustomPopUpMenuAdapter())
                        .setHeaderView(R.layout.dialog_popupmenu_header) // header used for title
                        .setFooterView(R.layout.dialog_popupmenu_footer) // footer used for Read More and Close buttons
                        // this is body
                        .setWidth(1000)
                        .addItemList(licenseItem)
                        .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                        .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                        .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                        .build();
                customPowerMenu.showAtCenter(binding.getRoot());
            }
        });

        binding.love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.love.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });

        binding.pretty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.pretty.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });

        binding.shocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.shocked.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });

        binding.happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.happy.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });

        binding.cute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.cute.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });

        binding.tiger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 url 이모티콘
                imageUrl = binding.tiger.getTag() + ".png";
                sendMyMessage(imageUrl, "null", "null");
            }
        });


        binding.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImoticonBoxOpen) {
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    binding.imoticonBoxRelativeLayout.setVisibility(View.GONE);
                    binding.imoticonBoxRelativeLayout.setAnimation(animation);
                } else {

                }
            }
        });


        binding.editTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImoticonBoxOpen) {
                    Animation animation = new AlphaAnimation(1, 0);
                    animation.setDuration(1000);
                    binding.imoticonBoxRelativeLayout.setVisibility(View.GONE);
                    binding.imoticonBoxRelativeLayout.setAnimation(animation);
                } else {

                }
            }
        });

    }

    @Override
    public void getServer() {
    }

    public void saveFiles() {
        if (!isFinishing() && !loadingDialog.isShowing())
            loadingDialog.show();

        Observable<String> result = service.saveFiles(CommonString.fileController, imageFileList, videoFileList);
        compositeDisposable.add(result.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NotNull String s) {
                        Log.e("abc", "s : " + s);

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error : " + e.getMessage());
                        loadingDialog.dismiss();
                        imageFileList.clear();
                        imageNameList.clear();
                        videoFileList.clear();
                        videoNameList.clear();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("abc", "onComplete");
                        if (imageFileList != null && imageFileList.size() >= 0) {
                            for (String name : imageNameList) {
                                Log.e("abc", "name : " + name);
                                sendMyMessage(name, "null", "null");

                            }
                        }
                        imageFileList.clear();
                        imageNameList.clear();
                        if (videoFileList != null && videoFileList.size() >= 0) {
                            for (String name : videoNameList) {
                                sendMyMessage("null", name, "null");
                            }
                        }
                        videoFileList.clear();
                        videoNameList.clear();

                        loadingDialog.dismiss();
                    }
                }));
    }

    //TODO : 이모티콘 삽입
    /*public void insertImoticon(String imoticon) {
        int st_index = binding.editTextView.getSelectionStart();
        binding.editTextView.getText().insert(st_index, "\n" + imoticon + "\n");
        int et_index = binding.editTextView.getSelectionEnd();
        Spannable span = binding.editTextView.getText();
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.jorudi);
        span.setSpan(new ImageSpan(context, bm), st_index, et_index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }*/


    //TODO : 이미지 리사이즈
    public Bitmap getResizedBitmap(Resources resources, int id, int size, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        Bitmap src = BitmapFactory.decodeResource(resources, id, options);
        Bitmap resized = Bitmap.createScaledBitmap(src, width, height, true);
        return resized;
    }


    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
// This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
// Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
// Fade the page out.
                view.setAlpha(1 - position);

// Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

// Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
// This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    public class CircularRevealTransition extends Visibility {

        @Override
        public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            int startRadius = 0;
            int endRadius = (int) Math.hypot(view.getWidth(), view.getHeight());
            Animator reveal = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, startRadius, endRadius);
            //make view invisible until animation actually starts
            view.setAlpha(0);
            reveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    view.setAlpha(1);
                }
            });
            return reveal;
        }

        @Override
        public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            int endRadius = 0;
            int startRadius = (int) Math.hypot(view.getWidth(), view.getHeight());
            Animator reveal = ViewAnimationUtils.createCircularReveal(view, view.getWidth() / 2, view.getHeight() / 2, startRadius, endRadius);
            return reveal;
        }
    }

    public class PhItemAnimator extends SimpleItemAnimator {

        private Context mContext;

        public PhItemAnimator(Context a_context) {

            mContext = a_context;
        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder a_holder) {
            return false;
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder a_holder) {
            Animator animator = AnimatorInflater.loadAnimator(mContext, R.animator.zoom_in);
            animator.setInterpolator(new BounceInterpolator());
            animator.setTarget(a_holder.itemView);
            animator.start();

            return true;
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder a_holder, int a_fromX, int a_fromY, int a_toX, int a_toY) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder a_oldHolder, RecyclerView.ViewHolder a_newHolder, int a_fromLeft, int a_fromTop, int a_toLeft, int a_toTop) {
            return false;
        }

        @Override
        public void runPendingAnimations() {

        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder a_item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
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


    public void addFriendInRoom(
            RoomModel.RoomType roomType,
            String myMainPhoto,
            String userId,
            String fireBaseToken,
            String fromUser,
            String toUSer,
            ChattingModel.UserState userState,
            int room_Index,
            int my_Index, String friendId,
            long userKakaoOwnNumber,
            String userMainPhoto,
            String room_Uuid,
            String notice_RegDate) {

        service.addFriendInRoom(CommonString.roomController, roomType.toString(), myMainPhoto, userId, fireBaseToken, userState, room_Index, my_Index, friendId, userKakaoOwnNumber, userMainPhoto, room_Uuid, notice_RegDate, fromUser, toUSer).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "초대를 보냈습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void api_loadChattingConversation() {

        service.loadChattingConversation(CommonString.roomController, roomModel.room_Uuid, myModel.userId).enqueue(new Callback<List<ChattingModel>>() {
            @Override
            public void onResponse(Call<List<ChattingModel>> call, Response<List<ChattingModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {

                    List<ChattingData> chattingDataList = chattingDataDB.chattingDao().getAll(roomModel.room_Uuid);
                    List<ChattingModel> chattingModels = response.body();

                    if (chattingDataList == null || chattingDataList.isEmpty()) {
                        handler.post(() -> {
                            try {
                                chattingViewModel.setItems(chattingModels);
                                for (ChattingModel chattingModel : chattingModels) {
                                    JSONObject object = chattingData.jsonObject_ChattingModel(chattingModel);
                                    ChattingData clubModelList = gson.fromJson(object.toString(), typeTokenOfChattingData);
                                    chattingDataDB.chattingDao().insert(clubModelList);
                                }
                            } catch (Exception e) {

                            }
                        });


                    } else {


                        handler.post(() -> {
                            List<ChattingModel> copyList = new ArrayList<>();

                            for (ChattingData data : chattingDataList) {
                                if (roomModel.room_Uuid.equals(data.room_Uuid)) {
                                    JSONObject object = chattingData.jsonObject(data);
                                    ChattingModel clubModelList = gson.fromJson(object.toString(), type);
                                    copyList.add(clubModelList);
                                }
                            }

                            assert chattingModels != null;
                            ListIterator<ChattingModel> iterator = chattingModels.listIterator();
                            while (iterator.hasNext()) {
                                ChattingModel chattingModel = iterator.next();
                                for (ChattingModel copyModel : copyList) {
                                    if (chattingModel.userConversation.equals(copyModel.userConversation)) {
                                        if (chattingModel.toString().equals(copyModel.toString())) {
                                            iterator.remove();
                                            break;
                                        }
                                    }
                                }
                            }

                            if (!chattingModels.isEmpty()) {
                                chattingDataDB.chattingDao().removeAllConversation(roomModel.room_Uuid);
                                for (ChattingModel chattingModel : chattingModels) {
                                    JSONObject object = chattingData.jsonObject_ChattingModel(chattingModel);
                                    ChattingData clubModelList = gson.fromJson(object.toString(), typeTokenOfChattingData);
                                    chattingDataDB.chattingDao().insert(clubModelList);
                                }
                                copyList.addAll(chattingModels);
                            }
                            chattingViewModel.setItems(copyList);
                        });
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<ChattingModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void api_RoomJoinPeopleNameList(String room_Uuid, String userId) {

        service.currentRoomJoinPeopleList(CommonString.roomController, room_Uuid, userId).enqueue(new Callback<List<FriendModel>>() {
            @Override
            public void onResponse(Call<List<FriendModel>> call, Response<List<FriendModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "response result : " + response.body());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            currentPeopleJoinList.clear();
                            currentPeopleJoinList.addAll(response.body());

                            if (!currentPeopleJoinList.isEmpty() && currentPeopleJoinList.size() > 0) {
                                friendListPopupMenu = new CustomPowerMenu.Builder<>(context, new FriendListAdapter())
                                        .addItemList(currentPeopleJoinList)
                                        .setOnMenuItemClickListener(onFriendListClickListener)
                                        .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                                        .setAnimation(MenuAnimation.ELASTIC_CENTER)
                                        .setWidth(800)
                                        .setHeight(1200)
                                        .setMenuRadius(10f)
                                        .setMenuShadow(10f)
                                        .build();
                                friendListPopupMenu.showAtCenter(binding.getRoot());
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "현재 모든 친구들이 입장중입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }
                    });


                } else {

                }
            }

            @Override
            public void onFailure(Call<List<FriendModel>> call, Throwable t) {

            }
        });
    }

}