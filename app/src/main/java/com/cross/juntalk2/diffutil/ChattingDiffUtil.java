package com.cross.juntalk2.diffutil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.ChattingModel;

import java.util.List;

public class ChattingDiffUtil extends DiffUtil.Callback {

    private List<ChattingModel> oldList;
    private List<ChattingModel> newList;

    public ChattingDiffUtil(List<ChattingModel> oldList, List<ChattingModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ChattingModel oldModel = oldList.get(oldItemPosition);
        ChattingModel newModel = newList.get(newItemPosition);
        return oldModel.userId.equals(newModel.userId);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChattingModel oldModel = oldList.get(oldItemPosition);
        ChattingModel newModel = newList.get(newItemPosition);
        return oldModel.hashCode() == newModel.hashCode();

    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}