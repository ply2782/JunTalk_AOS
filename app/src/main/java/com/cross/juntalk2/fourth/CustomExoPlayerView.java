package com.cross.juntalk2.fourth;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;


public class CustomExoPlayerView extends PlayerView {
    private static final String TAG = "CustomExoPlayerView";
    public DataSource.Factory mediaDataSourceFactory;
    public TrackGroupArray lastSeenTrackGroupArray;
    private DataSource.Factory dataSourceFactory;
    public AdaptiveTrackSelection.Factory videoTrackSelectionFactory;
    public SimpleExoPlayer.Builder playerBuilder;
    public DefaultTrackSelector trackSelector;
    public SimpleExoPlayer player;
    public DefaultLoadControl loadControl;
    public AdaptiveTrackSelection.Factory factory;
    public RenderersFactory renderersFactory;

    public CustomExoPlayerView(Context context) {
        super(context);
    }

    public CustomExoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomExoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "blackJin");
        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {
            return new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(uri));
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            return new HlsMediaSource.Factory(new DefaultHttpDataSource.Factory())
                    .createMediaSource(MediaItem.fromUri(uri));
        } else {
            return new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(getContext(), userAgent))
                    .createMediaSource(MediaItem.fromUri(uri));
        }
    }

    public void initializePlayer(String url) {


//        loadControl = new DefaultLoadControl.Builder()
//                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
//                .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
//                .setPrioritizeTimeOverSizeThresholds(true).build();
//        factory = new AdaptiveTrackSelection.Factory(AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS, AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS, AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION);
//        renderersFactory = new DefaultRenderersFactory(getContext());
//        trackSelector = new DefaultTrackSelector(getContext(), factory);
//        playerBuilder = new SimpleExoPlayer.Builder(getContext(), renderersFactory);
//        playerBuilder.setTrackSelector(new DefaultTrackSelector(getContext()));
//        playerBuilder.setLoadControl(new DefaultLoadControl());
//        player = playerBuilder.build();
//        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
//        player.setRepeatMode(Player.REPEAT_MODE_ONE);


        loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(DefaultLoadControl.DEFAULT_MIN_BUFFER_MS, DefaultLoadControl.DEFAULT_MAX_BUFFER_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS)
                .setPrioritizeTimeOverSizeThresholds(true).build();
        dataSourceFactory = new DefaultHttpDataSource.Factory();
        MediaSourceFactory mediaSourceFactory =
                new DefaultMediaSourceFactory(dataSourceFactory);
        renderersFactory = new DefaultRenderersFactory(getContext());
        factory = new AdaptiveTrackSelection.Factory(AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS, AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS, AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS, AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION);
        trackSelector = new DefaultTrackSelector(getContext(), factory);
        player =
                new SimpleExoPlayer.Builder(/* context= */ getContext(), renderersFactory)
                        .setMediaSourceFactory(mediaSourceFactory)
                        .setLoadControl(loadControl)
                        .setTrackSelector(trackSelector)
                        .build();
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setMediaSource(buildMediaSource(Uri.parse(url)));
        player.prepare();

        player.addListener(new Player.Listener() {

            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e("abc", "media error : " + error.getMessage());
            }


            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    //로딩중..
                    case Player.STATE_BUFFERING:
                        setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING);
                        break;
                    case Player.STATE_IDLE:
                        player.release();
                        /*player.setMediaSource(buildMediaSource(Uri.parse(url)));
                        player.prepare();*/
                    case Player.STATE_ENDED:
                    case Player.STATE_READY:
                    default:
                        break;
                }
            }
        });


        setShutterBackgroundColor(Color.TRANSPARENT);
        setPlayer(player);
        requestFocus();


//        lastSeenTrackGroupArray = null;

    }


    //현재 동영상의 시간
    public int getCurrentPosition() {
        return (int) player.getCurrentPosition();
    }

    //동영상이 실행되고있는지 확인
    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    //동영상 정지
    public void pause() {
        player.setPlayWhenReady(false);
    }

    //동영상 재생
    public void start() {
        player.setPlayWhenReady(true);
    }

    //동영상 시간 설정
    public void seekTo(int position) {
        player.seekTo(position);
    }

    public int playerState() {
        return player.getPlaybackState();
    }

    @Override
    public void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
    }


    //동영상 해제
    public void releasePlayer() {
        player.release();
//        trackSelector = null;
    }
}

