package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MusicViewModel extends ViewModel {
    private MutableLiveData<List<MusicModel>> musicMutableLiveData;
    private List<MusicModel> musicModels;


    public void init() {
        if (musicMutableLiveData == null) {
            musicMutableLiveData = new MutableLiveData<>();
        }

    }

    public MutableLiveData<List<MusicModel>> getMusicMutableLiveData() {
        return musicMutableLiveData;
    }

    public void setItems(List<MusicModel> musicModels) {
        this.musicModels = musicModels;
        musicMutableLiveData.postValue(this.musicModels);
    }
}
