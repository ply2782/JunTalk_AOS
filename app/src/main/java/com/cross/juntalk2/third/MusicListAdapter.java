package com.cross.juntalk2.third;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterMusicfragmentItemBinding;
import com.cross.juntalk2.model.MusicModel;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MusicListAdapter extends ListAdapter<MusicModel, MusicListAdapter.MusicViewHolder> {
    private Context context;
    private Activity activity;

    public MusicListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<MusicModel> diffCallback, Context context, Activity activity) {
        super(diffCallback);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_musicfragment_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected MusicModel getItem(int position) {
        return super.getItem(position);
    }

    public void imageViewClickEvent(View view, MusicModel musicModel, int position) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra("musicModel", (Serializable) musicModel);
        intent.putExtra("position", musicModel.music_Index);
        context.startActivity(intent);
    }

    @NonNull
    @NotNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterMusicfragmentItemBinding binding = AdapterMusicfragmentItemBinding.inflate(inflater, parent, false);
        binding.setMusicListAdapter(this);
        return new MusicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MusicViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private AdapterMusicfragmentItemBinding binding;

        public MusicViewHolder(@NonNull @NotNull AdapterMusicfragmentItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(MusicModel musicModelList, int position) {
            Log.e("abc", "musicModelList index : " + musicModelList.music_Index);
            AnimatedVectorDrawable vector = (AnimatedVectorDrawable) ContextCompat.getDrawable(context, R.drawable.play_animation);
            binding.musicTitleImageView.setImageDrawable(vector);
            vector.start();
            binding.setMusicModelList(musicModelList);
            binding.setPosition(position);
            binding.executePendingBindings();


        }
    }
}
