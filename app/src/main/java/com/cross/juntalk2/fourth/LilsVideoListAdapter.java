package com.cross.juntalk2.fourth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterVideolistItemListBinding;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.model.VideoListModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.ImageControlActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.exoplayer2.IllegalSeekPositionException;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.video.VideoSize;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LilsVideoListAdapter extends ListAdapter<VideoListModel, LilsVideoListAdapter.LilsVideoListAdapterViewHolder> {

    private Context context;
    public Player player;
    private DialogLilsReplyList dialogLilsReplyList;
    private RetrofitService retrofitService;
    private String userId, userMainPhoto;
    private int user_Index;
    private Activity activity;

    private enum VolumeState {ON, OFF}

    private int width, height;
    private LilsVideoListActivity.RefreshInterface refreshInterface;
    private VolumeState volumeState;
    private Handler handler;

    public interface ReplyClickInterface {
        void reFresh(int size, int position);
    }

    private ReplyClickInterface replyClickInterface;

    public void setRefreshInterface(LilsVideoListActivity.RefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    protected LilsVideoListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<VideoListModel> diffCallback, Context context, Activity activity) {
        super(diffCallback);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
        volumeState = VolumeState.OFF;
        if (context instanceof ReplyClickInterface) {
            replyClickInterface = (ReplyClickInterface) context;
        }
        if (retrofitService == null) {
            retrofitService = RetrofitClient.getInstance().getServerInterface();
        }
        this.activity = activity;
        userId = JunApplication.getMyModel().userId;
        user_Index = JunApplication.getMyModel().user_Index;
        userMainPhoto = JunApplication.getMyModel().userMainPhoto;
    }

    public void setItems(Player player) {
        this.player = player;
    }

    @NonNull
    @NotNull
    @Override
    public LilsVideoListAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterVideolistItemListBinding binding = AdapterVideolistItemListBinding.inflate(inflater, parent, false);
        binding.setLilsVideoListAdapter(this);
        return new LilsVideoListAdapterViewHolder(binding);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    View childView = recyclerView.getChildAt(0);
                    if (childView != null) {
                        LilsVideoListAdapter.LilsVideoListAdapterViewHolder viewHolder = (LilsVideoListAdapter.LilsVideoListAdapterViewHolder) childView.getTag();
                        if (viewHolder != null) {
                            viewHolder.binding.styledPlayerView.setPlayer(null);
                            try {
                                int position = viewHolder.getAbsoluteAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    player.seekTo(position, 1);
                                }
                                viewHolder.binding.styledPlayerView.setPlayer(player);
                                viewHolder.binding.styledPlayerView.getVideoSurfaceView().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        toggleVolume(viewHolder.binding.volumeImageView);
                                    }
                                });

                            } catch (IllegalSeekPositionException e) {
                                Log.e("abc", "error : " + e.getMessage());
                            } catch (Exception e) {
                                Log.e("abc", "error : " + e.getMessage());
                            }
                        }
                    }

                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                    if (player != null) {
                        player.addListener(new Player.Listener() {
                            @Override
                            public void onRenderedFirstFrame() {
                                View childView = recyclerView.getChildAt(0);
                                LilsVideoListAdapterViewHolder viewHolder = (LilsVideoListAdapterViewHolder) childView.getTag();
                                if (viewHolder != null) {
                                    viewHolder.binding.thumbNailImageView.setVisibility(View.GONE);
                                }
                            }
                        });
                    }

                }
            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull LilsVideoListAdapterViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getAbsoluteAdapterPosition() == 0) {
            holder.binding.styledPlayerView.setPlayer(null);
            player.seekTo(0, 0);
            holder.binding.styledPlayerView.setPlayer(player);
            player.play();
            holder.binding.styledPlayerView.getVideoSurfaceView().setOnClickListener(v -> toggleVolume(holder.binding.volumeImageView));
            holder.binding.thumbNailImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull LilsVideoListAdapterViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.binding.thumbNailImageView.setVisibility(View.VISIBLE);

    }


    private void toggleVolume(AppCompatImageView volumeImageView) {
        if (volumeState == LilsVideoListAdapter.VolumeState.OFF) {
            setVolumeControl(LilsVideoListAdapter.VolumeState.ON, volumeImageView);
        } else if (volumeState == LilsVideoListAdapter.VolumeState.ON) {
            setVolumeControl(LilsVideoListAdapter.VolumeState.OFF, volumeImageView);
        }
    }

    private void setVolumeControl(LilsVideoListAdapter.VolumeState state, AppCompatImageView volumeImageView) {
        volumeState = state;
        if (state == LilsVideoListAdapter.VolumeState.OFF) {
            player.setVolume(0f);
            animateVolumeControl(volumeImageView);
        } else if (state == LilsVideoListAdapter.VolumeState.ON) {
            player.setVolume(1f);
            animateVolumeControl(volumeImageView);
        }
    }

    private RequestManager initGlide() {
        // 로드된 이미지를 받을 Target을 생성한다.

        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setBaseColor(Color.GRAY)
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setHighlightColor(Color.WHITE)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .disallowHardwareConfig()
                .error(R.drawable.btn_x)
                .placeholder(shimmerDrawable);
        return Glide.with(context)
                .setDefaultRequestOptions(requestOptions);
    }


    private void animateVolumeControl(AppCompatImageView volumeImageView) {
        if (volumeImageView != null) {
            volumeImageView.bringToFront();
            if (volumeState == LilsVideoListAdapter.VolumeState.OFF) {
                initGlide().load(R.drawable.volume_down)
                        .into(volumeImageView);
            } else if (volumeState == LilsVideoListAdapter.VolumeState.ON) {
                initGlide().load(R.drawable.volume_up)
                        .into(volumeImageView);
            }
            volumeImageView.animate().cancel();
            volumeImageView.setAlpha(1f);
            volumeImageView.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }

    public void clickLike(View view, VideoListModel videoListModel, int position) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("lils_Uuid", videoListModel.lils_Uuid);
        infoMap.put("userMainPhoto", userMainPhoto);
        infoMap.put("userId", userId);
        infoMap.put("user_Index", user_Index);
        infoMap.put("lils_Index", videoListModel.lils_Index);

        retrofitService.lilsLike(CommonString.bulletinBoardController, infoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (result == false) {
                                getItem(position).likeCount = (videoListModel.likeCount + 1);
                            } else {
                                getItem(position).likeCount = (videoListModel.likeCount - 1);
                            }
                            notifyItemChanged(position);
                            notifyDataSetChanged();
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


    public void clickReply(View view, VideoListModel videoListModel, int poisition) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("lils_Uuid", videoListModel.lils_Uuid);
        infoMap.put("user_Index", user_Index);
        retrofitService.enterLilsReply(CommonString.bulletinBoardController, infoMap).enqueue(new Callback<List<LilsReplyModel>>() {
            @Override
            public void onResponse(Call<List<LilsReplyModel>> call, Response<List<LilsReplyModel>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    List<LilsReplyModel> lilsReplyModels = response.body();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialogLilsReplyList = new DialogLilsReplyList(context);
                            dialogLilsReplyList.setReplyClickInterface(replyClickInterface);
                            dialogLilsReplyList.setLils_Index(videoListModel.lils_Index);
                            dialogLilsReplyList.setLils_Uuid(videoListModel.lils_Uuid);
                            dialogLilsReplyList.setLilsReplyModels(lilsReplyModels);
                            dialogLilsReplyList.setCurrentPosition(poisition);
                            dialogLilsReplyList.show();
                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<LilsReplyModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });

    }

    public void makeVideo(View view) {
        Intent intent = new Intent(context, CreateLilsVideoListActivity.class);
        context.startActivity(intent);
    }

    public void profile(View view, VideoListModel videoListModel) {
        Intent intent = new Intent(context, ImageControlActivity.class);
        intent.putExtra("imageUrl", videoListModel.userMainPhoto);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_in);
    }

    public void blockLilsVideo(View view, VideoListModel videoListModel, int position) {
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

        reportButton.setOnClickListener(v -> handler.post(new Runnable() {
            @Override
            public void run() {
                refreshInterface.report(videoListModel, position);
                alertDialog.dismiss();

            }
        }));

        okButton.setOnClickListener(v -> handler.post(new Runnable() {
            @Override
            public void run() {
                refreshInterface.block(videoListModel, position);
                /*ViewGroup.LayoutParams layoutParams = binding.playerViewFrameLayout.getLayoutParams();
                layoutParams.width = 500;
                layoutParams.height = 500;
                binding.playerViewFrameLayout.setLayoutParams(layoutParams);*/
                alertDialog.dismiss();
            }
        }));

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull LilsVideoListAdapterViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }


    public int getDp(int a) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, context.getResources().getDisplayMetrics());
        return dp;
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_videolist_item_list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class LilsVideoListAdapterViewHolder extends RecyclerView.ViewHolder {
        public AdapterVideolistItemListBinding binding;
        public View parent;
        private String videoUrl;

        public LilsVideoListAdapterViewHolder(@NonNull @NotNull AdapterVideolistItemListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            parent = binding.getRoot();
        }

        public void onBind(VideoListModel videoListModel) {
            videoUrl = CommonString.CommonStringInterface.VideoThumbNail + videoListModel.lils_videoUrl;
            binding.styledPlayerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
            Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                    .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                    .setBaseAlpha(0.7f) //the alpha of the underlying children
                    .setBaseColor(Color.GRAY)
                    .setHighlightAlpha(0.7f) // the shimmer alpha amount
                    .setHighlightColor(Color.WHITE)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build();
            ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
            shimmerDrawable.setShimmer(shimmer);
            RequestOptions requestOptions = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .disallowHardwareConfig()
                    .error(R.drawable.btn_x)
                    .placeholder(shimmerDrawable);

            player.addListener(new Player.Listener() {
                @Override
                public void onVideoSizeChanged(VideoSize videoSize) {
                    View view = binding.styledPlayerView.getVideoSurfaceView();
                    ViewGroup.LayoutParams params = binding.thumbNailImageView.getLayoutParams();
                    params.width = view.getWidth();
                    params.height = view.getHeight();

                }
            });

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(videoUrl)
                    .optionalCenterCrop()
                    .centerCrop()
                    .thumbnail(0.3f)
                    .into(binding.thumbNailImageView);


            binding.setLilsVideoListModel(videoListModel);
            binding.setPosition(getAbsoluteAdapterPosition());
            binding.executePendingBindings();
            parent.setTag(this);

            replyClickInterface = new ReplyClickInterface() {
                @Override
                public void reFresh(int size, int position) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getItem(position).replyCount = size;
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                        }
                    });

                }
            };
        }


        private class PlayerErrorMessageProvider implements ErrorMessageProvider<PlaybackException> {

            @Override
            public Pair<Integer, String> getErrorMessage(PlaybackException e) {
                String errorString = context.getString(R.string.error_generic);
                Throwable cause = e.getCause();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = context.getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    context.getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    context.getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                context.getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo.name);
                    }
                }
                return Pair.create(0, errorString);
            }
        }

    }

}
