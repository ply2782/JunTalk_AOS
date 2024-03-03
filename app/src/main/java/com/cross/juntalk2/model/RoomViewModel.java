package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class RoomViewModel extends ViewModel {
    private MutableLiveData<List<RoomModel>> roomModelMutableLiveData;
    private List<RoomModel> roomModels;


    public void init() {
        if (roomModelMutableLiveData == null) {
            roomModelMutableLiveData = new MutableLiveData<>();
        }
        roomModelMutableLiveData.setValue(roomModels);
    }

    public MutableLiveData<List<RoomModel>> roomModelMutableLiveData() {
        return roomModelMutableLiveData;
    }

    public void setItems(List<RoomModel> roomModels) {
        this.roomModels = roomModels;
        roomModelMutableLiveData.postValue(roomModels);
    }

    public void clearAllItem() {
        if (roomModels != null) {
            roomModels.clear();
            roomModelMutableLiveData.postValue(roomModels);
        }

    }

    public void addItems(RoomModel roomModel) {
        if(roomModels !=null){
            roomModels.add(roomModel);
            roomModelMutableLiveData.postValue(roomModels);
        }

    }
}
