package com.cross.juntalk2.fourth;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.cross.juntalk2.R;
import com.cross.juntalk2.utils.CommonString;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

public class ImageVideoView extends LinearLayout {
    Context mContext;
    public CustomExoPlayerView customExoPlayerView;
    public ImageView imageView;
    private Shimmer shimmer;
    private ShimmerDrawable shimmerDrawable;

    private enum VolumeState {ON, OFF}

    VolumeState volumeState;

    public ImageVideoView(Context context) {
        super(context);
        initView(context);
    }

    public ImageVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImageVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setBackgroundColor(Color.BLACK);
        volumeState = VolumeState.OFF;

        shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setBaseColor(Color.GRAY)
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setHighlightColor(Color.WHITE)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
    }

    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
        }
    }


    public void setUrl(String url) {
        if (isVideo(url)) {
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            customExoPlayerView = new CustomExoPlayerView(mContext);
            customExoPlayerView.setUseController(false);
            customExoPlayerView.setShutterBackgroundColor(Color.TRANSPARENT);
            customExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
            customExoPlayerView.setLayoutParams(params);
            customExoPlayerView.initializePlayer(CommonString.CommonStringInterface.VideoBaseUrl + url);
            addView(customExoPlayerView);

        } else {

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(imageView);
            Glide.with(this).load(CommonString.CommonStringInterface.FileBaseUrl + url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(shimmerDrawable)
                    .into(imageView);
        }
    }


    private Boolean isVideo(String path) {
        String extension = getExtension(path);
        if (extension.equals("mp4") || extension.equals("MP4") || extension.equals("MOV") || extension.equals("mov") || extension.equals("AVI") || extension.equals("avi") ||
                extension.equals("MKV") || extension.equals("mkv") || extension.equals("WMV") || extension.equals("wmv") || extension.equals("TS") || extension.equals("ts") ||
                extension.equals("TP") || extension.equals("tp") || extension.equals("FLV") || extension.equals("flv") || extension.equals("3GP") || extension.equals("3gp") ||
                extension.equals("MPG") || extension.equals("mpg") || extension.equals("MPEG") || extension.equals("mpeg") || extension.equals("MPE") || extension.equals("mpe") ||
                extension.equals("ASF") || extension.equals("asf") || extension.equals("ASX") || extension.equals("asx") || extension.equals("DAT") || extension.equals("dat") ||
                extension.equals("RM") || extension.equals("rm")) {
            return true;
        } else {
            return false;
        }
    }

    private String getExtension(String url) {
        return url.substring(url.lastIndexOf(".") + 1, url.length());
    }

    public void releasePlayer() {
        if (customExoPlayerView != null) {
            customExoPlayerView.releasePlayer();
        }
    }

    public void playVideo() {
        if (customExoPlayerView != null) {
            customExoPlayerView.start();
        }
    }

    public void pauseVideo() {
        if (customExoPlayerView != null) {
            customExoPlayerView.pause();
            customExoPlayerView.seekTo(2);
        }
    }


    public boolean isPlaying() {
        if (customExoPlayerView != null) {
            return customExoPlayerView.isPlaying();
        }
        return false;
    }


    public void setLowVolume() {
        if (customExoPlayerView != null) {
            customExoPlayerView.player.setVolume(0f);
        }
    }

    public void setHighVolumne() {
        if (customExoPlayerView != null) {
            customExoPlayerView.player.setVolume(1f);
        }
    }


}
