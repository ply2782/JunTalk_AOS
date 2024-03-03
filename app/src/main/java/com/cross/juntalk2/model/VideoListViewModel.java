package com.cross.juntalk2.model;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.exoplayer2.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class VideoListViewModel extends ViewModel {
    private MutableLiveData<List<VideoListModel>> videoModelMutableLiveData;
    private List<VideoListModel> videoListModels;

    public void init() {
        if (videoModelMutableLiveData == null) {
            videoModelMutableLiveData = new MutableLiveData<>();
        }
        if (videoListModels == null) {
            videoListModels = new ArrayList<>();
        }
    }

    public List<VideoListModel> getVideoListModels() {
        return videoListModels;
    }

    public MutableLiveData<List<VideoListModel>> getVideoListMutableLiveData() {
        return videoModelMutableLiveData;
    }

    public void setItems(List<VideoListModel> videoListModels) {
        this.videoListModels = videoListModels;
        videoModelMutableLiveData.postValue(videoListModels);
    }

    public void setListItems(List<VideoListModel> videoListModels) {
        this.videoListModels.addAll(videoListModels);
        videoModelMutableLiveData.postValue(videoListModels);
    }

    public void clearAllItems() {
        videoListModels.clear();
        videoModelMutableLiveData.postValue(videoListModels);
    }

    public void removeItems(VideoListModel videoListModel) {
        videoListModels.remove(videoListModel);
        videoModelMutableLiveData.postValue(videoListModels);
    }

    public void addItems(VideoListModel videoListModel) {
        videoListModels.add(videoListModel);
        videoModelMutableLiveData.postValue(videoListModels);
    }
}
