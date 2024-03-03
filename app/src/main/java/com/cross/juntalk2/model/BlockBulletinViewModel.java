package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class BlockBulletinViewModel extends ViewModel {
    private MutableLiveData<List<BlockBulletinModel>> blockBulletinBoardMutableLiveData;
    private List<BlockBulletinModel> blockBulletinBoardModelList;

    public void init() {
        if (blockBulletinBoardMutableLiveData == null) {
            blockBulletinBoardMutableLiveData = new MutableLiveData<>();
        }
    }

    public MutableLiveData<List<BlockBulletinModel>> getblockBulletinBoardMutableLiveData() {
        return blockBulletinBoardMutableLiveData;
    }

    public void setItems(List<BlockBulletinModel> blockBulletinBoardModelList) {
        if (blockBulletinBoardModelList == null) return;
        this.blockBulletinBoardModelList = blockBulletinBoardModelList;
        blockBulletinBoardMutableLiveData.postValue(this.blockBulletinBoardModelList);
    }

    public void addItems(BlockBulletinModel blockBulletinModel){
        blockBulletinBoardModelList.add(blockBulletinModel);
        blockBulletinBoardMutableLiveData.setValue(blockBulletinBoardModelList);
    }

    public void removeItems(BlockBulletinModel blockBulletinModel){
        blockBulletinBoardModelList.remove(blockBulletinModel);
        blockBulletinBoardMutableLiveData.setValue(blockBulletinBoardModelList);
    }
}
