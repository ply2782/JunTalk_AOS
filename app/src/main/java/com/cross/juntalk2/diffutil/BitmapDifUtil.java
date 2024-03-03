package com.cross.juntalk2.diffutil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import org.jetbrains.annotations.NotNull;

public class BitmapDifUtil extends DiffUtil.ItemCallback<Bitmap> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Bitmap oldItem, @NonNull @NotNull Bitmap newItem) {
        return oldItem.hashCode() == newItem.hashCode();
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Bitmap oldItem, @NonNull @NotNull Bitmap newItem) {
        return oldItem.getByteCount() == newItem.getByteCount();
    }
}
