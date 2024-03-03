package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.MusicModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MusicListDiffUtil extends DiffUtil.ItemCallback<MusicModel>{


    @Override
    public boolean areItemsTheSame(@NonNull @NotNull MusicModel oldItem, @NonNull @NotNull MusicModel newItem) {
        return oldItem.music_Index == newItem.music_Index;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull MusicModel oldItem, @NonNull @NotNull MusicModel newItem) {
        return oldItem.musicName.equals(newItem.musicName);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull MusicModel oldItem, @NonNull @NotNull MusicModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}