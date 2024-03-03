package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FileUploadViewModel extends ViewModel {

    private MutableLiveData<FileUploadModel> fileUploadModelMutableLiveData;

    public MutableLiveData<FileUploadModel> getFileUploadModelMutableLiveData() {
        if(fileUploadModelMutableLiveData ==null){
            fileUploadModelMutableLiveData = new MutableLiveData<>();
        }
        return fileUploadModelMutableLiveData;
    }
}
