package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ChattingViewModel extends ViewModel {
    private MutableLiveData<List<ChattingModel>> chattingMutableLiveData;
    private List<ChattingModel> chattingModels;

    public void init(){
        if (chattingMutableLiveData == null) {
            chattingMutableLiveData = new MutableLiveData<>();
        }
    }

    public void removeAllItems(){
        chattingModels.clear();
        chattingMutableLiveData.postValue(chattingModels);
    }
    public void setItems(List<ChattingModel> chattingModels){
        this.chattingModels = chattingModels;
        chattingMutableLiveData.postValue(this.chattingModels);
    }
    public MutableLiveData<List<ChattingModel>> getChattingMutableLiveData() {
        return chattingMutableLiveData;
    }

    public void addItems(ChattingModel chattingModel){
        chattingModels.add(chattingModel);
        chattingMutableLiveData.setValue(chattingModels);
    }
}
