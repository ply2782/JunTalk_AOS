package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    public MutableLiveData<MyModel> myModelMutableLiveData;

    public MutableLiveData<MyModel> getMyModelMutableLiveData() {
        if (myModelMutableLiveData == null) {
            myModelMutableLiveData = new MutableLiveData<>();
        }
        return myModelMutableLiveData;
    }

}
