package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.BulletinBoardModel;

import org.jetbrains.annotations.NotNull;

public class BulletinDiffUtil extends DiffUtil.ItemCallback<BulletinBoardModel> {


    @Override
    public boolean areItemsTheSame(@NonNull @NotNull BulletinBoardModel oldItem, @NonNull @NotNull BulletinBoardModel newItem) {
        return oldItem.hashCode() == newItem.hashCode();
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull BulletinBoardModel oldItem, @NonNull @NotNull BulletinBoardModel newItem) {
        return oldItem.bulletin_Content.equals(newItem.bulletin_Content);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull BulletinBoardModel oldItem, @NonNull @NotNull BulletinBoardModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
