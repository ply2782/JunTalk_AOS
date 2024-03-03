package com.cross.juntalk2.retrofit;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cross.juntalk2.model.BlockBulletinViewModel;
import com.cross.juntalk2.model.BulletinBoardViewModel;
import com.cross.juntalk2.model.BulletinCommentViewModel;
import com.cross.juntalk2.model.ChattingViewModel;
import com.cross.juntalk2.model.ClubViewModel;
import com.cross.juntalk2.model.CommentViewModel;
import com.cross.juntalk2.model.CommonNoticeViewModel;
import com.cross.juntalk2.model.FileUploadViewModel;
import com.cross.juntalk2.model.FriendViewModel;
import com.cross.juntalk2.model.MusicViewModel;
import com.cross.juntalk2.model.MyViewModel;
import com.cross.juntalk2.model.NoticeViewModel;
import com.cross.juntalk2.model.RoomViewModel;
import com.cross.juntalk2.model.VideoListViewModel;
import com.cross.juntalk2.model.VideoViewModel;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class ViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FriendViewModel.class)) {
            return (T) new FriendViewModel();
        } else if (modelClass.isAssignableFrom(MyViewModel.class)) {
            return (T) new MyViewModel();
        } else if (modelClass.isAssignableFrom(RoomViewModel.class)) {
            return (T) new RoomViewModel();
        } else if (modelClass.isAssignableFrom(ChattingViewModel.class)) {
            return (T) new ChattingViewModel();
        } else if (modelClass.isAssignableFrom(MusicViewModel.class)) {
            return (T) new MusicViewModel();
        } else if (modelClass.isAssignableFrom(CommentViewModel.class)) {
            return (T) new CommentViewModel();
        } else if (modelClass.isAssignableFrom(BulletinBoardViewModel.class)) {
            return (T) new BulletinBoardViewModel();
        } else if (modelClass.isAssignableFrom(BulletinCommentViewModel.class)) {
            return (T) new BulletinCommentViewModel();
        }else if(modelClass.isAssignableFrom(NoticeViewModel.class)){
            return (T) new NoticeViewModel();
        }else if(modelClass.isAssignableFrom(FileUploadViewModel.class)){
            return (T) new FileUploadViewModel();
        }else if(modelClass.isAssignableFrom(CommonNoticeViewModel.class)){
            return (T) new CommonNoticeViewModel();
        }else if(modelClass.isAssignableFrom(VideoViewModel.class)){
            return (T) new VideoViewModel();
        }else if(modelClass.isAssignableFrom(BlockBulletinViewModel.class)){
            return (T) new BlockBulletinViewModel();
        }else if(modelClass.isAssignableFrom(VideoListViewModel.class)){
            return (T) new VideoListViewModel();
        }else if(modelClass.isAssignableFrom(ClubViewModel.class)){
            return (T) new ClubViewModel();
        }
        throw new IllegalArgumentException("Not found ViewModel class.");
    }
}

