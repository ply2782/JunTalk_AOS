package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class FriendViewModel extends ViewModel {
    private MutableLiveData<List<FriendModel>> friendListMutableLiveData;
    private List<FriendModel> friendModels;

    public void init() {
        if (friendListMutableLiveData == null) {
            friendListMutableLiveData = new MutableLiveData<>();
        }

    }

    public void setItems(List<FriendModel> friendModels) {
        if (friendModels == null) return;
        this.friendModels = friendModels;
        friendListMutableLiveData.postValue(this.friendModels);
    }

    public void addItems(FriendModel friendModel) {
        friendModels.add(friendModel);
        friendListMutableLiveData.setValue(this.friendModels);
    }

    public void removeItems(FriendModel friendModel) {
        friendModels.remove(friendModel);
        friendListMutableLiveData.setValue(this.friendModels);
    }

    public MutableLiveData<List<FriendModel>> getFriendListMutableLiveData() {

        return friendListMutableLiveData;
    }

}
