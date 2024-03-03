package com.cross.juntalk2.third;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogMusiclistItemBinding;
import com.cross.juntalk2.model.MusicModel;

import java.util.List;

public class DialogMusicList extends Dialog {

    private Context context;
    private DialogMusiclistItemBinding binding;
    private List<MusicModel> musicModelList;
    private DialogMusicListAdapter adapter;
    private FileUploadActivity.MusicUploadListInterface musicUploadListInterface;

    public interface ClickItemInterface {
        void getItems(MusicModel musicModel , int position);
    }

    public ClickItemInterface clickItemInterface;

    public DialogMusicList(@NonNull Context context, List<MusicModel> musicModelList, FileUploadActivity.MusicUploadListInterface musicUploadListInterface) {
        super(context);
        this.context = context;
        this.musicModelList = musicModelList;
        this.musicUploadListInterface = musicUploadListInterface;
        if (context instanceof ClickItemInterface) {
            clickItemInterface = (ClickItemInterface) context;
        }
        clickItemInterface = new ClickItemInterface() {
            @Override
            public void getItems(MusicModel musicModel, int position) {
                musicUploadListInterface.chooseMusic(musicModel, position);
                dismiss();
            }
        };
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_musiclist_item, null, false);
        setContentView(binding.getRoot());
        init();
    }

    public void init() {
        adapter = new DialogMusicListAdapter(context, clickItemInterface);
        binding.musicUploadListRecyclerView.setAdapter(adapter);
        binding.setMusicModelList(musicModelList);
        //TODO : 다이얼로그 넓이와 높이 동적 조정
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

    }
}
