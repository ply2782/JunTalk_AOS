package com.cross.juntalk2.diffutil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.CommentModel;

import java.util.List;

public class CommentDiffUtil extends DiffUtil.Callback {

    private List<CommentModel> oldList;
    private List<CommentModel> newList;

    public CommentDiffUtil(List<CommentModel> oldList, List<CommentModel> newList) {
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
        CommentModel oldModel = oldList.get(oldItemPosition);
        CommentModel newModel = newList.get(newItemPosition);
        return oldModel.userId.equals(newModel.userId);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        CommentModel oldModel = oldList.get(oldItemPosition);
        CommentModel newModel = newList.get(newItemPosition);
        return oldModel.userComment.equals(newModel.userComment);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}