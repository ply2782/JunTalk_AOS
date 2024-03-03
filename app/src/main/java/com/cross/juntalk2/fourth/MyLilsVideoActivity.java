package com.cross.juntalk2.fourth;

import static com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityMyLilsVideoBinding;
import com.cross.juntalk2.diffutil.LilsVideoListDiffUtil;
import com.cross.juntalk2.first.ProfileActivity;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.model.VideoListModel;
import com.cross.juntalk2.model.VideoListViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.datatransport.Priority;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLilsVideoActivity extends CreateNewCompatActivity {

    private ActivityMyLilsVideoBinding binding;
    private MyLilsVideoAdapter myLilsVideoAdapter;
    private RetrofitService service;
    private VideoListViewModel videoListViewModel;
    private ViewModelFactory viewModelFactory;
    private Observer<List<VideoListModel>> observer;
    private List<MediaItem> mediaItems;
    private DataSource.Factory dataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private DefaultLoadControl loadControl;
    private AdaptiveTrackSelection.Factory factory;
    private RenderersFactory renderersFactory;
    private boolean startAutoPlay = true;
    public SimpleExoPlayer player;
    private List<MediaSource> mediaSources;
    private String userId, userMainPhoto;
    private int user_Index;
    private int page = 0;
    private HandlerThread handlerThread;
    private boolean serverPaging = false;
    private Handler mainHandler, subHandler;
    private String blockContent = "";
    private MakeMediaItemThread makeMediaItemThread;
    private String myId;
    private int my_Index;
    private BroadcastReceiver broadcastReceiver;

    public interface RefreshInterface {
        void reFresh();

        void block(VideoListModel videoListModel, int position);

        void report(VideoListModel videoListModel, int position);

        void remove(VideoListModel videoListModel);

    }

    private RefreshInterface refreshInterface;

    @Override
    public void getInterfaceInfo() {
        handlerThread = new HandlerThread("subHandlerThread");
        handlerThread.start();
        subHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {

                    List<VideoListModel> videoListModels = (List<VideoListModel>) msg.obj;
                    mainHandler.post(() -> {
                        player.setMediaSources(mediaSources);
                        player.prepare();
                        player.setVolume(0f);
                        videoListViewModel.setListItems(videoListModels);
                        myLilsVideoAdapter.setItems(player);
                    });

                } else if (msg.what == 2) {

                    mainHandler.post(() -> {

                        if (videoListViewModel.getVideoListModels().size() > 0) {
                            binding.makeVideoListImageButton.setVisibility(View.GONE);
                            binding.addTextView.setVisibility(View.GONE);

                        } else {

                            if (user_Index == my_Index) {
                                binding.makeVideoListImageButton.setVisibility(View.VISIBLE);
                                binding.addTextView.setVisibility(View.VISIBLE);

                            } else {
                                binding.emptyTextView.setVisibility(View.VISIBLE);
                                binding.makeVideoListImageButton.setVisibility(View.GONE);
                                binding.addTextView.setVisibility(View.GONE);
                            }

                        }
                    });
                }
            }
        };
        mediaSources = new ArrayList<>();
        myId = JunApplication.getMyModel().userId;
        my_Index = JunApplication.getMyModel().user_Index;
        userId = getIntent().getStringExtra("userId");
        user_Index = getIntent().getIntExtra("user_Index", 0);
        userMainPhoto = getIntent().getStringExtra("userMainPhoto");
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        if (context instanceof RefreshInterface) {
            refreshInterface = (RefreshInterface) context;
        }

    }

    @Override
    public void getIntentInfo() {

        refreshInterface = new RefreshInterface() {
            @Override
            public void reFresh() {
                page = 0;
                getVideoList(page);

            }

            @Override
            public void block(VideoListModel videoListModel, int position) {
                lilsVideoDeleteBlock(videoListModel, position);

            }

            @Override
            public void report(VideoListModel videoListModel, int position) {
                reportDialog(videoListModel, position);
            }


            @Override
            public void remove(VideoListModel videoListModel) {
                removeLilsVideo(videoListModel);
            }

        };
    }

    public void reportDialog(VideoListModel videoListModel, int position) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_blockreport, null, false);
        cautionAlert.setView(cautionView);
        RadioGroup radioGroup = cautionView.findViewById(R.id.radioGroupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.firstReportRadioButton:
                        blockContent = "부적절한 사진 및 동영상";
                        break;
                    case R.id.secondReportRadioButton:
                        blockContent = "선정적 / 폭력적 사진 및 동영상";
                        break;
                    case R.id.thirdReportRadioButton:
                        blockContent = "불쾌감을 자극하는 잔인한 사진 및 동영상";
                        break;
                    case R.id.fourthReportRadioButton:
                        blockContent = "기타 부적절한 사진 및 동영상";
                        break;
                }
            }
        });
        Button okButton = cautionView.findViewById(R.id.reportButton);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        AlertDialog alertDialog = cautionAlert.create();
        mainHandler.post(() -> {
            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
            alertDialog.show();
        });
        okButton.setOnClickListener(v -> {

            if (blockContent.replace(" ", "").trim().equals("")) {
                Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();

            } else {
                alertDialog.dismiss();
                try {
                    reportJunesApi(blockContent, videoListModel, position);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    public void reportJunesApi(String blockReportUser, VideoListModel videoListModel, int position) {
        service.blockReportJunes(
                CommonString.bulletinBoardController,
                user_Index,
                videoListModel.lils_Uuid,
                videoListModel.user_Index,
                videoListModel.lils_Index,
                videoListModel.userId,
                blockReportUser
        ).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    mainHandler.post(() -> {
                        Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                        player.removeMediaItem(position);
                        player.seekTo(1);
                        myLilsVideoAdapter.setItems(player);
//                            videoListAdapter.notifyItemChanged(position);
                        videoListViewModel.removeItems(videoListModel);
                        Intent intent = new Intent();
                        intent.setAction("LilsOnResume");
                        sendBroadcast(intent);
                    });
                } else {
                    mainHandler.post(() -> Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
                mainHandler.post(() -> Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("LilsOnResume");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mediaSources.clear();
                videoListViewModel.clearAllItems();
                page = 0;
                getVideoList(page);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        if (handlerThread == null) {
            handlerThread = new HandlerThread("subThread");
            handlerThread.start();
        }

        if (subHandler == null) {
            subHandler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        List<VideoListModel> videoListModels = (List<VideoListModel>) msg.obj;
                        mainHandler.post(() -> {
                            player.setMediaSources(mediaSources);
                            player.prepare();
                            player.setVolume(0f);
                            videoListViewModel.setListItems(videoListModels);
                            myLilsVideoAdapter.setItems(player);
                        });
                    } else if (msg.what == 2) {

                        mainHandler.post(() -> {

                            if (videoListViewModel.getVideoListModels().size() > 0) {
                                binding.makeVideoListImageButton.setVisibility(View.GONE);
                                binding.addTextView.setVisibility(View.GONE);

                            } else {
                                binding.makeVideoListImageButton.setVisibility(View.VISIBLE);
                                binding.addTextView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            };
        }
    }

    @Override
    public void init() {

        binding = DataBindingUtil.setContentView(MyLilsVideoActivity.this, R.layout.activity_my_lils_video);
        mediaItems = new ArrayList<>();
        mainHandler = new Handler(Looper.getMainLooper());
        viewModelFactory = new ViewModelFactory();
        videoListViewModel = new ViewModelProvider(this, viewModelFactory).get(VideoListViewModel.class);
        videoListViewModel.init();
        myLilsVideoAdapter = new MyLilsVideoAdapter(new LilsVideoListDiffUtil(), context, MyLilsVideoActivity.this);
        myLilsVideoAdapter.setHasStableIds(true);
        myLilsVideoAdapter.setRefreshInterface(refreshInterface);
        binding.videoListRecyclerView.setAdapter(myLilsVideoAdapter);

        RecyclerView.ItemAnimator animator = binding.videoListRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        if (binding.videoListRecyclerView.getOnFlingListener() == null)
            pagerSnapHelper.attachToRecyclerView(binding.videoListRecyclerView);
        initializePlayer();

    }


    @Override
    public void createThings() {
        observer = new Observer<List<VideoListModel>>() {
            @Override
            public void onChanged(List<VideoListModel> videoListModels) {


                myLilsVideoAdapter.submitList(videoListViewModel.getVideoListModels());
                myLilsVideoAdapter.notifyDataSetChanged();

                mainHandler.post(() -> {
                    if (myId.equals(userId) && my_Index == user_Index) {

                        if (myLilsVideoAdapter == null || myLilsVideoAdapter.getCurrentList().isEmpty()) {
                            if (myLilsVideoAdapter == null || myLilsVideoAdapter.getItemCount() == 0) {
                                binding.makeVideoListImageButton.setVisibility(View.VISIBLE);
                                binding.addTextView.setVisibility(View.VISIBLE);
                            } else {
                                binding.emptyTextView.setVisibility(View.GONE);
                                binding.makeVideoListImageButton.setVisibility(View.GONE);
                                binding.addTextView.setVisibility(View.GONE);
                            }
                        } else {
                            binding.makeVideoListImageButton.setVisibility(View.GONE);
                            binding.addTextView.setVisibility(View.GONE);
                        }

                    } else {

                        if (myLilsVideoAdapter == null || myLilsVideoAdapter.getCurrentList().isEmpty()) {
                            if (myLilsVideoAdapter == null || myLilsVideoAdapter.getItemCount() == 0) {
                                binding.emptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                binding.emptyTextView.setVisibility(View.GONE);
                            }
                        } else {
                            binding.emptyTextView.setVisibility(View.GONE);
                        }
                        binding.makeVideoListImageButton.setVisibility(View.GONE);
                        binding.addTextView.setVisibility(View.GONE);
                    }
                });
            }
        };
        videoListViewModel.getVideoListMutableLiveData().observe(this, observer);


    }


    public void removeLilsVideo(VideoListModel videoListModel) {
        service.removeLilsVideoList(CommonString.clubController, videoListModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                videoListViewModel.removeItems(videoListModel);
                            }
                        });
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 연결이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void clickEvent() {
        binding.makeVideoListImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateLilsVideoListActivity.class);
            context.startActivity(intent);
        });

        binding.videoListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (serverPaging && lastItem == videoListViewModel.getVideoListMutableLiveData().getValue().size() - 1) {
                    page++;
                    getVideoList(page);
                    serverPaging = false;
                }
            }
        });


        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mediaSources.clear();
                videoListViewModel.clearAllItems();
                page = 0;
                getVideoList(page);
                if (binding.refreshLayout.isRefreshing()) {
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void getServer() {
        page = 0;
        getVideoList(page);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing())
            overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        handlerThread.quit();

        if (makeMediaItemThread != null) {
            if (makeMediaItemThread.isInterrupted()) {
                makeMediaItemThread.interrupt();
            }
        }


        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            subHandler.removeCallbacksAndMessages(null);
        }
    }

    public void getVideoList(int page) {
        service.myLilsVideoList(CommonString.bulletinBoardController, page, userId, user_Index).enqueue(new Callback<List<VideoListModel>>() {
            @Override
            public void onResponse(Call<List<VideoListModel>> call, Response<List<VideoListModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    List<VideoListModel> videoListModels = response.body();

                    makeMediaItemThread = new MakeMediaItemThread(videoListModels);
                    if (!makeMediaItemThread.isInterrupted()) {
                        makeMediaItemThread.interrupt();
                    }
                    makeMediaItemThread.start();

                    if (videoListModels != null && videoListModels.size() > 0) {
                        serverPaging = true;
                    } else {
                        serverPaging = false;
                    }


                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<List<VideoListModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    private class MakeMediaItemThread extends Thread {
        private List<VideoListModel> videoListModels;

        public MakeMediaItemThread(List<VideoListModel> videoListModels) {
            this.videoListModels = videoListModels;
        }

        @Override
        public void run() {
            super.run();
            if (videoListModels.size() > 0) {
                for (VideoListModel videoListModel : videoListModels) {
                    MediaSource mediaSource = buildMediaSource(Uri.parse(CommonString.CommonStringInterface.VideoBaseUrl + videoListModel.lils_videoUrl));
                    mediaSources.add(mediaSource);
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setUri(Uri.parse(CommonString.CommonStringInterface.VideoBaseUrl + videoListModel.lils_videoUrl))
                            .build();
                    mediaItems.add(mediaItem);
                }
                Message message = subHandler.obtainMessage();
                message.what = 1;
                message.obj = videoListModels;
                subHandler.sendMessage(message);
            } else {
                Message message = subHandler.obtainMessage();
                message.what = 2;
                message.obj = videoListModels;
                subHandler.sendMessage(message);
            }


        }
    }


    public MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(context, "blackJin");
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


    public void lilsVideoDeleteBlock(VideoListModel videoListModel, int position) {
        service.removeLilsVideoList(CommonString.bulletinBoardController, videoListModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        mainHandler.post(() -> {
                            videoListViewModel.removeItems(videoListModel);
                            player.removeMediaItem(position);
                            player.seekTo(1);
                            myLilsVideoAdapter.setItems(player);
                            myLilsVideoAdapter.notifyItemChanged(position);

                            Intent intent = new Intent();
                            intent.setAction("LilsOnResume");
                            sendBroadcast(intent);
                        });
                    } else {
                        mainHandler.post(() -> Toast.makeText(context, "서버와의 연결이 원할하지 않습니다.", Toast.LENGTH_SHORT).show());
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void initializePlayer() {

        if (player == null) {
            loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                    .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                    .setPrioritizeTimeOverSizeThresholds(true).build();
            dataSourceFactory = new DefaultHttpDataSource.Factory();
            MediaSourceFactory mediaSourceFactory =
                    new DefaultMediaSourceFactory(dataSourceFactory);
            renderersFactory = new DefaultRenderersFactory(context);
            factory = new AdaptiveTrackSelection.Factory(AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS, AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS, AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION);
            trackSelector = new DefaultTrackSelector(context, factory);
            player =
                    new SimpleExoPlayer.Builder(/* context= */ context, renderersFactory)
                            .setMediaSourceFactory(mediaSourceFactory)
                            .setLoadControl(loadControl)
                            .setTrackSelector(trackSelector)
                            .build();
            player.setPlayWhenReady(startAutoPlay);
            player.setRepeatMode(Player.REPEAT_MODE_ONE);

        }
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    //로딩중..
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_IDLE:
                    case Player.STATE_ENDED:
                    case Player.STATE_READY:
                    default:
                        break;
                }
            }
        });
        /*StyledPlayerControlView controller = playerView.findViewById(R.id.exo_controller);
        View customLayoutView = LayoutInflater.from(context).inflate(R.layout.custom_exoplayer_controller, null, false);
        controller.removeAllViews();
        controller.addView(customLayoutView);
        ImageButton startButton = customLayoutView.findViewById(R.id.exo_play);
        ImageButton stopButton = customLayoutView.findViewById(R.id.exo_pause);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerView.getPlayer().play();
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                playerView.getPlayer().pause();
            }
        });
        playerView.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        playerView.requestFocus();
        playerView.setPlayer(player);
        if (!Objects.requireNonNull(playerView.getPlayer()).isPlaying()) {
            startButton.setVisibility(View.VISIBLE);
        }*/
    }
}