package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.VideoListModel;
import com.cross.juntalk2.model.VideoModel;

import org.jetbrains.annotations.NotNull;

public class LilsVideoListDiffUtil extends DiffUtil.ItemCallback<VideoListModel> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull VideoListModel oldItem, @NonNull @NotNull VideoListModel newItem) {

        return oldItem.userId.equals(newItem.userId);
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull VideoListModel oldItem, @NonNull @NotNull VideoListModel newItem) {
        return oldItem.content.equals(newItem.content);
    }
}
