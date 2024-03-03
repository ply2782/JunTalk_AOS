package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.MusicModel;

import org.jetbrains.annotations.NotNull;

public class MusicFileUploadDiffUtil  extends DiffUtil.ItemCallback<MusicModel> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull MusicModel oldItem, @NonNull @NotNull MusicModel newItem) {
        return oldItem.musicName.equals(newItem.musicName);
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull MusicModel oldItem, @NonNull @NotNull MusicModel newItem) {
        return oldItem.musicName.equals(newItem.musicName);
    }
}
