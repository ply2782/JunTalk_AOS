package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.CommonNoticeModel;

import org.jetbrains.annotations.NotNull;

public class CommonNoticeDiffUtil extends DiffUtil.ItemCallback<CommonNoticeModel> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull CommonNoticeModel oldItem, @NonNull @NotNull CommonNoticeModel newItem) {
        return oldItem.userId.equals(newItem.userId);
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull CommonNoticeModel oldItem, @NonNull @NotNull CommonNoticeModel newItem) {
        return oldItem.notice_Content.equals(newItem.notice_Content);
    }
}
