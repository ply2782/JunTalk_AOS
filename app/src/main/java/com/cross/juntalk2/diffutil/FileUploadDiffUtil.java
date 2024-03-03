package com.cross.juntalk2.diffutil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.FileUploadViewModel;

import org.jetbrains.annotations.NotNull;

public class FileUploadDiffUtil extends DiffUtil.ItemCallback<FileUploadViewModel> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull FileUploadViewModel oldItem, @NonNull @NotNull FileUploadViewModel newItem) {
        return oldItem.getFileUploadModelMutableLiveData().getValue().fileLength == newItem.getFileUploadModelMutableLiveData().getValue().fileLength;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull FileUploadViewModel oldItem, @NonNull @NotNull FileUploadViewModel newItem) {
        return oldItem.getFileUploadModelMutableLiveData().getValue().fileName.equals(newItem.getFileUploadModelMutableLiveData().getValue().fileName);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(@NonNull @NotNull FileUploadViewModel oldItem, @NonNull @NotNull FileUploadViewModel newItem) {
        return super.getChangePayload(oldItem, newItem);
    }
}
