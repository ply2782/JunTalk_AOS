package com.cross.juntalk2.third;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogAdapterMusicuploadlistItemBinding;
import com.cross.juntalk2.model.MusicModel;

import org.jetbrains.annotations.NotNull;

public class MusicFileUploadAdapter extends ListAdapter<MusicModel, MusicFileUploadAdapter.MusicFileUploadViewHolder> {

    protected MusicFileUploadAdapter(@NonNull @NotNull DiffUtil.ItemCallback<MusicModel> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @NotNull
    @Override
    public MusicFileUploadViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DialogAdapterMusicuploadlistItemBinding binding = DialogAdapterMusicuploadlistItemBinding.inflate(inflater, parent, false);
        return new MusicFileUploadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MusicFileUploadViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.dialog_adapter_musicuploadlist_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MusicFileUploadViewHolder extends RecyclerView.ViewHolder {
        private DialogAdapterMusicuploadlistItemBinding binding;

        public MusicFileUploadViewHolder(@NonNull @NotNull DialogAdapterMusicuploadlistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(MusicModel musicModel, int position) {
            binding.setMusicModel(musicModel);
            binding.setPosition(position);
            binding.executePendingBindings();
        }
    }
}
