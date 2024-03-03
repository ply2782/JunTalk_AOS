package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.RoomModel;

import org.jetbrains.annotations.NotNull;

public class RoomDiffUtil extends DiffUtil.ItemCallback<RoomModel> {


    @Override
    public boolean areItemsTheSame(@NonNull @NotNull RoomModel oldItem, @NonNull @NotNull RoomModel newItem) {
        return oldItem.room_Index == newItem.room_Index;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull RoomModel oldItem, @NonNull @NotNull RoomModel newItem) {
        return oldItem.room_Conversation.equals(newItem.room_Conversation);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull RoomModel oldItem, @NonNull @NotNull RoomModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
