package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class BulletinBoardViewModel extends ViewModel {
    private MutableLiveData<List<BulletinBoardModel>> bulletinBoardMutableLiveData;
    private List<BulletinBoardModel> bulletinBoardModelList;

    public void init() {
        if (bulletinBoardMutableLiveData == null) {
            bulletinBoardMutableLiveData = new MutableLiveData<>();
        }
        bulletinBoardMutableLiveData.setValue(bulletinBoardModelList);
    }

    public MutableLiveData<List<BulletinBoardModel>> getBulletinBoardMutableLiveData() {
        return bulletinBoardMutableLiveData;
    }

    public List<BulletinBoardModel> getBulletinBoardModelList() {
        return bulletinBoardModelList;
    }

    public void setItems(List<BulletinBoardModel> bulletinBoardModelList) {
        if (bulletinBoardModelList == null) return;
        this.bulletinBoardModelList = bulletinBoardModelList;
        bulletinBoardMutableLiveData.postValue(this.bulletinBoardModelList);
    }

    public void addItems(BulletinBoardModel bulletinBoardModel){
        bulletinBoardModelList.add(bulletinBoardModel);
        bulletinBoardMutableLiveData.setValue(bulletinBoardModelList);
    }

    public void removeItems(BulletinBoardModel bulletinBoardModel){
        bulletinBoardModelList.remove(bulletinBoardModel);
        bulletinBoardMutableLiveData.postValue(bulletinBoardModelList);
    }
}
