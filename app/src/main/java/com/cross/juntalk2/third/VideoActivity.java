package com.cross.juntalk2.third;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityVideoBinding;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.VideoModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends CreateNewCompatActivity {
    private RetrofitService service;
    private ActivityVideoBinding binding;
    private String videoUrl;
    private TrackGroupArray lastSeenTrackGroupArray;
    private AdaptiveTrackSelection.Factory videoTrackSelectionFactory;
    private SimpleExoPlayer.Builder playerBuilder;
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer player;
    private DefaultLoadControl loadControl;
    private AdaptiveTrackSelection.Factory factory;
    private RenderersFactory renderersFactory;
    private String music_BlockContent = "";
    private String userId;
    private Handler handler;
    private VideoModel videoModel;

    @Override
    public void getInterfaceInfo() {
        userId = JunApplication.getMyModel().userId;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getIntentInfo() {
        videoUrl = (String) getIntent().getStringExtra("videoUrl");
        Log.e("abc", "videoUrl : " + videoUrl);
        videoModel = (VideoModel) getIntent().getSerializableExtra("videoModel");

    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(VideoActivity.this, R.layout.activity_video);
        binding.setVideoActivity(this);
        binding.setVideoModel(videoModel);

        loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                .setPrioritizeTimeOverSizeThresholds(true).build();
        factory = new AdaptiveTrackSelection.Factory(AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS, AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS, AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION);
        renderersFactory = new DefaultRenderersFactory(context);
        trackSelector = new DefaultTrackSelector(context, factory);
        playerBuilder = new SimpleExoPlayer.Builder(context, renderersFactory);
        playerBuilder.setTrackSelector(trackSelector);
        playerBuilder.setLoadControl(loadControl);
        player = playerBuilder.build();
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setMediaSource(buildMediaSource(Uri.parse(CommonString.CommonStringInterface.PlayVideoBaseUrl + videoUrl)));
        binding.exoPlayer2.setPlayer(player);
        player.prepare();
        binding.exoPlayer2.requestFocus();
        lastSeenTrackGroupArray = null;

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {

            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("abc", "error : " + error.errorCode);

            }

            @Override
            public void onRenderedFirstFrame() {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.pause();
        player.release();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    public void createThings() {


    }

    @Override
    public void clickEvent() {

    }

    @Override
    public void getServer() {

    }


    public void blockImageButton(View view, VideoModel videoModel) {
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
        okButton.setOnClickListener(v -> api_Block(videoModel, alertDialog));
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
                    api_Block(videoModel, alertDialog);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }


    public void api_Block(VideoModel videoModel, AlertDialog alertDialog) {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        Map<String, Object> blockInfoMap = new HashMap<>();
        blockInfoMap.put("userId", userId);
        blockInfoMap.put("video_Index", videoModel.video_Index);
        blockInfoMap.put("isBlock", true);
        service.blockVideo(CommonString.musicController, blockInfoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.setAction("VideoFragmentRefresh");
                            intent.putExtra("VideoFragmentRefresh", "VideoFragmentRefresh");
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

    public MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(context, getPackageName());
        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(uri));
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            return new HlsMediaSource.Factory(new DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(uri));
        } else {
            return new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(context, userAgent))
                    .createMediaSource(MediaItem.fromUri(uri));
        }
    }


}