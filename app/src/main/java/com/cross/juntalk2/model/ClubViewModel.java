package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ClubViewModel extends ViewModel {
    private MutableLiveData<List<ClubModel>> clubMutableData;
    private List<ClubModel> clubModelList;

    public void init() {
        if (clubMutableData == null) {
            clubMutableData = new MutableLiveData<>();
        }
        clubModelList = new ArrayList<>();
    }


    public List<ClubModel> getClubModelList() {
        return clubModelList;
    }

    public MutableLiveData<List<ClubModel>> getClubMutableData() {
        return clubMutableData;
    }

    public void setItems(List<ClubModel> clubModelList) {
        if (clubModelList == null) return;
        this.clubModelList = clubModelList;
        clubMutableData.postValue(this.clubModelList);
    }



    public void addItems(ClubModel clubModel) {
        clubModelList.add(clubModel);
        clubMutableData.setValue(clubModelList);
    }

    public void removeItems(ClubModel clubModel) {
        clubModelList.remove(clubModel);
        clubMutableData.setValue(clubModelList);
    }
}
