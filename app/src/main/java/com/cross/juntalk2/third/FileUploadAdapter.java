package com.cross.juntalk2.third;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterFileuploadItemBinding;

import org.jetbrains.annotations.NotNull;

public class FileUploadAdapter extends ListAdapter<Bitmap, FileUploadAdapter.FileUploadViewHolder> {

    protected FileUploadAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Bitmap> diffCallback) {
        super(diffCallback);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_fileupload_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @NotNull
    @Override
    public FileUploadViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterFileuploadItemBinding binding = AdapterFileuploadItemBinding.inflate(inflater, parent, false);
        return new FileUploadViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FileUploadViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public class FileUploadViewHolder extends RecyclerView.ViewHolder {
        private AdapterFileuploadItemBinding binding;

        public FileUploadViewHolder(@NonNull @NotNull AdapterFileuploadItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(Bitmap bitmap) {
            binding.setBitmap(bitmap);
            binding.executePendingBindings();
        }
    }
}

