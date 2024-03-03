package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class VideoViewModel extends ViewModel {
    private MutableLiveData<List<VideoModel>> videoMutableLiveData;
    private List<VideoModel> videoModels;

    public void init() {
        if (videoMutableLiveData == null) {
            videoMutableLiveData = new MutableLiveData<>();
        }
    }

    public void setItems(List<VideoModel> videoModels) {
        if (videoModels == null) return;
        this.videoModels = videoModels;
        videoMutableLiveData.postValue(this.videoModels);
    }

    public void addItems(VideoModel videoModel) {
        videoModels.add(videoModel);
        videoMutableLiveData.setValue(videoModels);
    }

    public MutableLiveData<List<VideoModel>> getVideoMutableLiveData() {

        return videoMutableLiveData;
    }
}
