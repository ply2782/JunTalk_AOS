package com.cross.juntalk2.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> commentMutableLiveData;


    public MutableLiveData<List<CommentModel>> getCommentMutableLiveData() {
        if (commentMutableLiveData == null) {
            commentMutableLiveData = new MutableLiveData<>();
        }
        return commentMutableLiveData;
    }
}
