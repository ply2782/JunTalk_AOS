package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;

public class CategoryListDiffUtil extends DiffUtil.ItemCallback<String> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
        return oldItem.equals(newItem);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
