package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.NoticeModel;

import org.jetbrains.annotations.NotNull;

public class NoticeDiffUtil extends DiffUtil.ItemCallback<NoticeModel> {

    @Override
    public boolean areItemsTheSame(@NonNull @NotNull NoticeModel oldItem, @NonNull @NotNull NoticeModel newItem) {
        return oldItem.userId.equals(newItem.userId);
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull NoticeModel oldItem, @NonNull @NotNull NoticeModel newItem) {
        return oldItem.mainTitle.equals(newItem.mainTitle);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull NoticeModel oldItem, @NonNull @NotNull NoticeModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
