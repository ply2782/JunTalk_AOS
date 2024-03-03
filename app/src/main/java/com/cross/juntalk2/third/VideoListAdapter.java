package com.cross.juntalk2.third;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterVideoItemBinding;
import com.cross.juntalk2.model.VideoModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class VideoListAdapter extends ListAdapter<VideoModel, VideoListAdapter.VideoListViewHolder> {

    private Context context;

    protected VideoListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<VideoModel> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public VideoListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterVideoItemBinding binding = AdapterVideoItemBinding.inflate(inflater, parent, false);
        binding.setVideoListAdapter(this);
        return new VideoListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VideoListViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_video_item;
    }


    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void videoItemsOnClickEvent(View view, VideoModel videoModel) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoModel" , videoModel);
        intent.putExtra("videoUrl", videoModel.folderName+ File.separator + videoModel.videoName);
        context.startActivity(intent);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class VideoListViewHolder extends RecyclerView.ViewHolder {
        private AdapterVideoItemBinding binding;

        public VideoListViewHolder(@NonNull @NotNull AdapterVideoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(VideoModel videoModelList, int position) {
            Log.e("abc","videoIndex : "+ videoModelList.video_Index);
            binding.setVideoModel(videoModelList);
            binding.setPosition(position);
            binding.executePendingBindings();
        }
    }

}
