package com.cross.juntalk2.diffutil;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.cross.juntalk2.model.BulletinCommentModel;

import java.util.List;

public class BulletinCommentDillUtil extends DiffUtil.Callback {

    private List<BulletinCommentModel> oldItemList;
    private List<BulletinCommentModel> newItemList;

    public BulletinCommentDillUtil(List<BulletinCommentModel> oldItemList, List<BulletinCommentModel> newItemList) {
        this.oldItemList = oldItemList;
        this.newItemList = newItemList;
    }


    @Override
    public int getOldListSize() {
        return oldItemList.size();
    }

    @Override
    public int getNewListSize() {
        return newItemList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        BulletinCommentModel bulletinCommentModel_old = oldItemList.get(oldItemPosition);
        BulletinCommentModel bulletinCommentModel_new = newItemList.get(newItemPosition);
        return bulletinCommentModel_old.user_Index == bulletinCommentModel_new.user_Index;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        BulletinCommentModel bulletinCommentModel_old = oldItemList.get(oldItemPosition);
        BulletinCommentModel bulletinCommentModel_new = newItemList.get(newItemPosition);
        return bulletinCommentModel_old.userComment.equals(bulletinCommentModel_new.userComment);
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
