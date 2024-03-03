package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class NoticeViewModel extends ViewModel {

    private MutableLiveData<List<NoticeModel>> noticeMutableLiveData;
    private List<NoticeModel> noticeModels;

    public MutableLiveData<List<NoticeModel>> getNoticeMutableLiveData() {
        return noticeMutableLiveData;
    }

    public NoticeViewModel() {
        init();
    }

    public void init() {
        if (noticeMutableLiveData == null) {
            noticeMutableLiveData = new MutableLiveData<>();
        }
        noticeMutableLiveData.setValue(noticeModels);
    }

    public void setItems(List<NoticeModel> noticeModels) {
        if (noticeModels == null) return;
        this.noticeModels = noticeModels;
    }

    public void addItems(NoticeModel noticeModel) {
        noticeModels.add(noticeModel);
        noticeMutableLiveData.setValue(noticeModels);
    }

    public void removeItems(NoticeModel noticeModel) {
        noticeModels.remove(noticeModel);
        noticeMutableLiveData.setValue(noticeModels);
    }
}
