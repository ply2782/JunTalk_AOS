package com.cross.juntalk2.fourth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterImageorvideoItemBinding;
import com.cross.juntalk2.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class ImageOrVideoAdapter extends ListAdapter<String, ImageOrVideoAdapter.ImageOrVideoViewHolder> {

    private Context context;
    public ImageOrVideoViewHolder imageOrVideoViewHolder;

    protected ImageOrVideoAdapter(@NonNull @NotNull DiffUtil.ItemCallback<String> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (imageOrVideoViewHolder != null) {
                        imageOrVideoViewHolder.binding.imageVideoView.playVideo();
                        imageOrVideoViewHolder.binding.playImageView.setVisibility(View.GONE);
                    }

                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (imageOrVideoViewHolder != null) {
                        imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                        try {
                            if (getItem(firstVisibleItem).contains(".mp4")) {
                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.VISIBLE);
                            } else {
                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {

                        }


                    }

                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                }
            }
        });
    }


    @Override
    public void onViewRecycled(@NonNull @NotNull ImageOrVideoViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.binding.imageVideoView != null) {
            holder.binding.imageVideoView.releasePlayer();
        }
    }


    @NonNull
    @NotNull
    @Override
    public ImageOrVideoViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterImageorvideoItemBinding binding = AdapterImageorvideoItemBinding.inflate(inflater, parent, false);
        imageOrVideoViewHolder = new ImageOrVideoViewHolder(binding);
        return imageOrVideoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageOrVideoViewHolder holder, int position) {
        holder.onBind(getItem(position), position, getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_imageorvideo_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ImageOrVideoViewHolder extends RecyclerView.ViewHolder {
        public AdapterImageorvideoItemBinding binding;
        public View parent;

        public ImageOrVideoViewHolder(@NonNull @NotNull AdapterImageorvideoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            parent = binding.getRoot();
        }

        public void onBind(String url, int currentPosition, int sumPosition) {
            binding.setAllUrls(url);
            binding.setCurrentPosition(currentPosition);
            binding.setSumPosition(sumPosition);
            binding.playImageView.setVisibility(View.GONE);

            if (url == null) {
                binding.imageVideoView.setVisibility(View.GONE);
                binding.playImageView.setVisibility(View.GONE);
            } else {

                if (url.contains(".mp4")) {

                    if (!binding.imageVideoView.isPlaying()) {

                        binding.playImageView.setVisibility(View.VISIBLE);
                        binding.playImageView.setOnClickListener(v -> {
                            binding.imageVideoView.playVideo();
                            binding.playImageView.setVisibility(View.GONE);
                        });
                    } else {
                        binding.playImageView.setVisibility(View.GONE);
                    }

                } else {

                    binding.playImageView.setVisibility(View.GONE);
                }

                binding.imageVideoView.setVisibility(View.VISIBLE);
                binding.imageVideoView.setUrl(url);

            }
            parent.setTag(this);
            binding.executePendingBindings();
        }


    }
}
