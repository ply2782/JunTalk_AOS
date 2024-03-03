package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class BulletinCommentViewModel extends ViewModel {
    private MutableLiveData<List<BulletinCommentModel>> bulletinCommentMutableLiveData;
    private List<BulletinCommentModel> bulletinCommentModels;


    public void init() {
        if (bulletinCommentMutableLiveData == null) {
            bulletinCommentMutableLiveData = new MutableLiveData<>();
        }
        bulletinCommentMutableLiveData.setValue(bulletinCommentModels);
    }

    public MutableLiveData<List<BulletinCommentModel>> getBulletinCommentMutableLiveData() {
        return bulletinCommentMutableLiveData;
    }

    public void setItems(List<BulletinCommentModel> bulletinCommentModels) {
        if (bulletinCommentModels == null) return;
        this.bulletinCommentModels = bulletinCommentModels;
    }

    public void addItems(BulletinCommentModel bulletinCommentModel) {
        bulletinCommentModels.add(bulletinCommentModel);
        bulletinCommentMutableLiveData.setValue(bulletinCommentModels);
    }
}
