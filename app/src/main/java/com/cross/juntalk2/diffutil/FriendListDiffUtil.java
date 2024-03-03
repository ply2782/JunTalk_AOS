package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.FriendModel;

import org.jetbrains.annotations.NotNull;

public class FriendListDiffUtil extends DiffUtil.ItemCallback<FriendModel> {


    @Override
    public boolean areItemsTheSame(@NonNull @NotNull FriendModel oldItem, @NonNull @NotNull FriendModel newItem) {
        return oldItem.userKakaoOwnNumber == newItem.userKakaoOwnNumber;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull FriendModel oldItem, @NonNull @NotNull FriendModel newItem) {
        return oldItem.userId.equals(newItem.userId);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull FriendModel oldItem, @NonNull @NotNull FriendModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
