package com.cross.juntalk2.third;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityMusicBinding;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.CommentModel;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.MusicViewModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.service.NotificationService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicActivity extends CreateNewCompatActivity {
    private LoadingDialog loadingDialog;
    private CompositeDisposable compositeDisposable;
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    private RetrofitService service;
    private ActivityMusicBinding binding;
    private MusicModel musicModel;
    private MediaPlayer mediaPlayer;
    private ResponseAdapter adapter;
    private String musicDuration, stringSecond;
    private int commonCurrentPosision = 0;
    private List<CommentModel> commentModels;
    private List<CommentModel> mainCommentModels;
    private MyModel myModel;
    private int position;
    private SimpleDateFormat today = new SimpleDateFormat("aa HH:mm");
    private SimpleDateFormat todayDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Intent intent;
    private NotificationManager notificationManager;
    private NotificationService notificationService; // 서비스 객체
    private boolean isService = false; // 서비스 중인 확인용
    private String music_BlockContent = "";
    public String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public int REQUEST_EXTERNAL_STORAGE = 1;
    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            NotificationService.MyBinder mb = (NotificationService.MyBinder) service;
            notificationService = mb.getService(); // 서비스가 제공하는 메소드 호출하여

            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
            Log.e("abc", "binding sevice");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    createNotification("http://ply2782ply2782.cafe24.com:8080/musicController/musicPlay?name=" + musicModel.musicName.substring(0, musicModel.musicName.lastIndexOf(".")) + "/" + musicModel.musicName);
                }
            });
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            Log.e("abc", "name : " + name);
            isService = false;
        }
    };

    public interface ClickInterface {
        void report(CommentModel commentModel, String blockContent, AlertDialog alertDialog);

        void block(CommentModel commentModel, AlertDialog alertDialog);
    }

    private ClickInterface clickInterface;

    @Override
    public void getInterfaceInfo() {
        sendService();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        mainCommentModels = new ArrayList<>();
        clickInterface = new ClickInterface() {
            @Override
            public void report(CommentModel commentModel, String blockContent, AlertDialog alertDialog) {
                reportLilsReplyList(commentModel, blockContent, alertDialog);
            }

            @Override
            public void block(CommentModel commentModel, AlertDialog alertDialog) {
                blockLilsReplyList(commentModel, "null", alertDialog);
            }
        };
    }

    public void reportLilsReplyList(CommentModel commentModel, String blockContent, AlertDialog alertDialog) {
        Map<String, Object> blockCommentMap = new HashMap<>();
        blockCommentMap.put("user_Index", commentModel.user_Index);
        blockCommentMap.put("userId", commentModel.userId);
        blockCommentMap.put("music_Index", commentModel.music_Index);
        blockCommentMap.put("userComment", commentModel.userComment);
        blockCommentMap.put("fromUser_Index", myModel.user_Index);
        blockCommentMap.put("fromUser", myModel.userId);
        blockCommentMap.put("blockReportContent", blockContent);
        service.insertBlockComment(CommonString.musicController, blockCommentMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                Iterator<CommentModel> iterator = mainCommentModels.listIterator();
                                while (iterator.hasNext()) {
                                    CommentModel mainComment = iterator.next();
                                    if (mainComment.userId.equals(commentModel.userId)) {
                                        iterator.remove();
                                    }
                                }
                                adapter = new ResponseAdapter(context, mainCommentModels);
                                adapter.setClickInterface(clickInterface);
                                binding.responseRecyclerView.setAdapter(adapter);
                                binding.responseRecyclerView.setItemAnimator(new DefaultItemAnimator());

                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void blockLilsReplyList(CommentModel commentModel, String blockContent, AlertDialog alertDialog) {
        Map<String, Object> blockCommentMap = new HashMap<>();
        blockCommentMap.put("user_Index", commentModel.user_Index);
        blockCommentMap.put("userId", commentModel.userId);
        blockCommentMap.put("music_Index", commentModel.music_Index);
        blockCommentMap.put("userComment", commentModel.userComment);
        blockCommentMap.put("fromUser_Index", myModel.user_Index);
        blockCommentMap.put("fromUser", myModel.userId);
        blockCommentMap.put("blockReportContent", blockContent);

        service.insertBlockComment(CommonString.musicController, blockCommentMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "차단되었습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                Iterator<CommentModel> iterator = mainCommentModels.listIterator();
                                while (iterator.hasNext()) {
                                    CommentModel mainComment = iterator.next();
                                    if (mainComment.userId.equals(commentModel.userId)) {
                                        iterator.remove();
                                    }
                                }
                                adapter = new ResponseAdapter(context, mainCommentModels);
                                adapter.setClickInterface(clickInterface);
                                binding.responseRecyclerView.setAdapter(adapter);
                                binding.responseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    @Override
    public void getIntentInfo() {
        musicModel = (MusicModel) getIntent().getSerializableExtra("musicModel");
        position = getIntent().getIntExtra("position", 0);
        myModel = JunApplication.getMyModel();
    }

    @Override
    public void init() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.setCancelable(true);
        compositeDisposable = new CompositeDisposable();
        commentModels = new ArrayList<>();
        handler = new Handler(Looper.myLooper());
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(seekBarMoveRunnable());
            }
        };
        binding = DataBindingUtil.setContentView(MusicActivity.this, R.layout.activity_music);
        binding.setMusicActivity(this);
        binding.setMusicModel(musicModel);
        configureFABReveal(binding.fabRevealLayout);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (commentModels != null && commentModels.size() > 0) {
                insertComment();
            }
            if (isFinishing()) {
                timer.cancel();
                timerTask.cancel();
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
            }
            compositeDisposable.dispose();
            notificationManager.cancel(0);
            unbindService(conn);

            if(handler !=null){
                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {

        }

    }


    public void blockImageButton(View view, MusicModel musicModel) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
        cautionAlert.setView(cautionView);
        Button okButton = cautionView.findViewById(R.id.okButton);
        Button reportButton = cautionView.findViewById(R.id.reportButton);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(v -> api_Block(musicModel, alertDialog));
        reportButton.setOnClickListener(v -> reportDialog());
        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }


    public void reportDialog() {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_blockreport, null, false);
        cautionAlert.setView(cautionView);
        RadioGroup radioGroup = cautionView.findViewById(R.id.radioGroupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.firstReportRadioButton:
                        music_BlockContent = "부적절한 사진 및 동영상";
                        break;
                    case R.id.secondReportRadioButton:
                        music_BlockContent = "선정적 / 폭력적 사진 및 동영상";
                        break;
                    case R.id.thirdReportRadioButton:
                        music_BlockContent = "불쾌감을 자극하는 잔인한 사진 및 동영상";
                        break;
                    case R.id.fourthReportRadioButton:
                        music_BlockContent = "기타 부적절한 사진 및 동영상";
                        break;
                }
            }
        });
        Button okButton = cautionView.findViewById(R.id.reportButton);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(v -> {
            if (music_BlockContent.replace(" ", "").trim().equals("")) {
                Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            } else {

                try {
                    api_Block(musicModel, alertDialog);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    public void api_Block(MusicModel musicModel, AlertDialog alertDialog) {

        Map<String, Object> blockInfoMap = new HashMap<>();
        blockInfoMap.put("userId", myModel.userId);
        blockInfoMap.put("music_Index", musicModel.music_Index);
        blockInfoMap.put("isBlock", true);
        service.blockMusic(CommonString.musicController, blockInfoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction("MusicFragmentRefresh");
                            intent.putExtra("MusicFragmentRefresh", "MusicFragmentRefresh");
                            sendBroadcast(intent);
                            finish();
                            alertDialog.dismiss();
                        }
                    });
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


    @Override
    public void createThings() {
        try {
            String encodeStr = URLEncoder.encode(musicModel.musicName.substring(0, musicModel.musicName.lastIndexOf(".")) + "/" + musicModel.musicName, "UTF-8");
            StartMusic("http://ply2782ply2782.cafe24.com:8080/musicController/musicPlay?name=" + encodeStr);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public void sendService() {
        intent = new Intent(context, NotificationService.class);
        startService(intent);
        bindService(intent, // intent 객체
                conn, // 서비스와 연결에 대한 정의
                Context.BIND_AUTO_CREATE);
    }


    public void createNotification(String url) {
        int NOTIFICATION_ID = 0;
        String channelId = getPackageName();
        // 알림 표시
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("채널에 대한 설명.");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{0});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }
        Intent notificationIntent = new Intent(getApplicationContext(), MusicActivity.class);
        notificationIntent.putExtra("musicModel", (Serializable) musicModel);
        notificationIntent.putExtra("position", position);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Intent start = new Intent(context, NotificationService.class);
        start.setAction("Play");
        start.putExtra("musicName", url);
        PendingIntent startPending = PendingIntent.getService(context, 0, start, PendingIntent.FLAG_IMMUTABLE);

        Intent stop = new Intent(context, NotificationService.class);
        stop.setAction("Stop");
        start.putExtra("musicName", url);
        PendingIntent stopPending = PendingIntent.getService(context, 0, stop, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L});
        Notification noti = builder
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.image_floatmainbutton))
                .setSmallIcon(R.drawable.juntalk_logo)
                .setContentTitle(musicModel.musicName)
                .addAction(new NotificationCompat.Action(R.drawable.exo_icon_play, "start", startPending))
                .addAction(new NotificationCompat.Action(R.drawable.exo_icon_stop, "stop", stopPending))
                .setContentText(musicModel.musicName)
                .setContentIntent(pendingIntent)
                .build();
        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle(builder);
        mediaStyle.setMediaSession(androidx.media.app.NotificationCompat.MediaStyle.getMediaSession(noti));
        mediaStyle.setShowActionsInCompactView(0, 1);
        builder.setStyle(mediaStyle);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void clickEvent() {

        binding.musicTitleTextView.setSelected(true);      // 선택하기
        binding.closeImageButton.setOnClickListener(v -> binding.fabRevealLayout.revealMainView());

        binding.fileDownImageButton.setOnClickListener(v -> {
            if (verifyStoragePermissions(MusicActivity.this)) {
                downLoadMusic(musicModel.musicName.substring(0, musicModel.musicName.lastIndexOf(".")) + "/" + musicModel.musicName);
            } else {

            }

        });

        binding.sendImageButton.setOnClickListener(v -> {

            CommentModel commentModel = new CommentModel();
            commentModel.userComment = binding.commentEditTextView.getText().toString();
            commentModel.user_Index = myModel.user_Index;
            commentModel.userId = myModel.userId;
            commentModel.music_Name = musicModel.musicName;
            commentModel.music_Index = musicModel.music_Index;
            commentModel.dateDiff = 0;
            commentModel.userMainPhoto = myModel.userMainPhoto;
            commentModel.actualTime = System.currentTimeMillis();
            commentModel.userCommentRegDate = todayDate.format(new Date().getTime());
            commentModels.add(commentModel);
            adapter.setItems(commentModel);
            binding.responseRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            binding.commentEditTextView.getText().clear();

        });


        binding.leftPlayImageButton.setOnClickListener(v -> {
            commonCurrentPosision = (commonCurrentPosision - 10000);
            mediaPlayer.seekTo(commonCurrentPosision);
            binding.musicSeekBar.setProgress(commonCurrentPosision);
        });

        binding.rightPlayImageButton.setOnClickListener(v -> {
            commonCurrentPosision = (commonCurrentPosision + 10000);
            mediaPlayer.seekTo(commonCurrentPosision);
            binding.musicSeekBar.setProgress(commonCurrentPosision);
        });

        binding.stopImageButton.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        });

        binding.playImageButton.setOnClickListener(v -> mediaPlayer.start());


        binding.musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                commonCurrentPosision = progress;
                if (fromUser) {
                    // 사용자가 시크바를 움직이면
                    mediaPlayer.seekTo(commonCurrentPosision);
                }
                binding.musicSeekBar.setProgress(commonCurrentPosision);

                //미디어 플레이어 시간 구하기
                int currentPosition = mediaPlayer.getCurrentPosition();
                int timeMs = currentPosition;
                int totalSeconds = timeMs / 1000;
                int seconds = totalSeconds % 60;
                int minutes = (totalSeconds / 60) % 60;
                int hours = totalSeconds / 3600;
                if (seconds < 10) {
                    stringSecond = "0" + seconds;
                } else {
                    stringSecond = String.valueOf(seconds);
                }
                binding.timeTextView.setText(minutes + ":" + stringSecond + " / " + musicDuration);
            }
        });
    }

    public String StringReplace(String str) {
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str = str.replaceAll(match, "");
        return str;
    }

    public Runnable seekBarMoveRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isFinishing() && mediaPlayer.isPlaying()) {
                        commonCurrentPosision = mediaPlayer.getCurrentPosition();
                        binding.musicSeekBar.setProgress(commonCurrentPosision);
                    }
                } catch (IllegalStateException e) {

                }
            }
        };
        return runnable;
    }


    public void insertComment() {

        service.insertComment(CommonString.musicController, commentModels).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response result : " + response.toString());
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

    @Override
    public void getServer() {

        service.comment(CommonString.musicController, position, myModel.user_Index).enqueue(new Callback<List<CommentModel>>() {
            @Override
            public void onResponse(Call<List<CommentModel>> call, Response<List<CommentModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainCommentModels.clear();
                            mainCommentModels.addAll(response.body());
                            adapter = new ResponseAdapter(context, response.body());
                            adapter.setClickInterface(clickInterface);
                            binding.responseRecyclerView.setAdapter(adapter);
                            binding.responseRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            notificationService.setMediaPlayer(mediaPlayer);
                        }
                    });
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<List<CommentModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });


    }

    public void StartMusic(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    int duration = mediaPlayer.getDuration();
                    int timeMs = duration;
                    int totalSeconds = timeMs / 1000;
                    int seconds = totalSeconds % 60;
                    int minutes = (totalSeconds / 60) % 60;
                    binding.musicSeekBar.setMax(duration);
                    String secondString = "";
                    if (seconds < 10) {
                        secondString = "0" + seconds;
                    } else {
                        secondString = "" + seconds;
                    }
                    binding.timeTextView.setText("0:00 / " + minutes + ":" + secondString);
                    musicDuration = minutes + ":" + seconds;
                    timer.schedule(timerTask, 1000, 1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    public void downLoadMusic(String musicName) {
        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        Observable<ResponseBody> downloadMusic = service.musicDownLoad(CommonString.musicController, musicName);
        compositeDisposable.add(downloadMusic.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(@NotNull ResponseBody responseBody) {
                        boolean writtenToDisk = writeResponseBodyToDisk(responseBody);
                        if (writtenToDisk) {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(notificationService, "다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(notificationService, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        if (isFinishing()) {
                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));
    }


    //파일 다운로드
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File file = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //삼성 및 lg 외부 경로
//                file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "music.zip");
                //삼성 핸드폰 내부 음악 경로
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "music.zip");
                Log.e("abc", "file : " + file.getPath());
            }
//            File futureStudioIconFile = new File(Environment.getStorageDirectory() + File.separator + "imoticon.zip");
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                //내부 저장소
//                outputStream = context.openFileOutput("music.zip", Context.MODE_PRIVATE);
                //외부 저장소
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("abc", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                Log.e("abc", "e : " + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.e("abc", "e : " + e.getMessage());
            return false;
        }
    }

    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }


    private void configureFABReveal(FABRevealLayout fabRevealLayout) {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {

            }
        });
    }




    /*private void showMainViewItems() {
        scale(albumTitleText, 50);
        scale(artistNameText, 150);
    }

    private void showSecondaryViewItems() {
        scale(songProgress, 0);
        animateSeekBar(songProgress);
        scale(songTitleText, 100);
        scale(prev, 150);
        scale(stop, 100);
        scale(next, 200);
    }*/

    private void scale(View view, long delay) {
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void animateSeekBar(SeekBar seekBar) {
        seekBar.setProgress(15);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(seekBar, "progress", 15, 0);
        progressAnimator.setDuration(300);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }

}