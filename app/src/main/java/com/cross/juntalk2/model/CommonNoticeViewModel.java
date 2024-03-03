package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CommonNoticeViewModel extends ViewModel {

    private MutableLiveData<List<CommonNoticeModel>> commmonMutableLiveData;
    private List<CommonNoticeModel> commonNoticeModels;

    public void init() {
        if (commmonMutableLiveData == null) {
            commmonMutableLiveData = new MutableLiveData<>();
        }

    }

    public void setItems(List<CommonNoticeModel> commonNoticeModels) {
        this.commonNoticeModels = commonNoticeModels;
        commmonMutableLiveData.postValue(commonNoticeModels);
    }

    public void addItems(CommonNoticeModel commonNoticeModel) {
        commonNoticeModels.add(commonNoticeModel);
        commmonMutableLiveData.setValue(commonNoticeModels);
    }

    public void removeItems(CommonNoticeModel commonNoticeModel){
        commonNoticeModels.remove(commonNoticeModel);
        commmonMutableLiveData.setValue(commonNoticeModels);
    }

    public void removeAllItems(List<CommonNoticeModel> commonNoticeModels){
        this.commonNoticeModels.removeAll(commonNoticeModels);
        commmonMutableLiveData.setValue(this.commonNoticeModels);
    }

    public MutableLiveData<List<CommonNoticeModel>> getCommmonMutableLiveData() {
        return commmonMutableLiveData;
    }
}
