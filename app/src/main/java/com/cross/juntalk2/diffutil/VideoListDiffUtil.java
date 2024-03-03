package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.VideoModel;

import org.jetbrains.annotations.NotNull;

public class VideoListDiffUtil extends DiffUtil.ItemCallback<VideoModel>{


    @Override
    public boolean areItemsTheSame(@NonNull @NotNull VideoModel oldItem, @NonNull @NotNull VideoModel newItem) {
        return oldItem.toString().equals(newItem.toString());
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull VideoModel oldItem, @NonNull @NotNull VideoModel newItem) {
        return oldItem.videoLength == newItem.videoLength;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull VideoModel oldItem, @NonNull @NotNull VideoModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}