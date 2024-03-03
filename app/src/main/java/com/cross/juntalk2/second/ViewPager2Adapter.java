package com.cross.juntalk2.second;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterChattingnoticeItemBinding;
import com.cross.juntalk2.model.NoticeModel;

import org.jetbrains.annotations.NotNull;

public class ViewPager2Adapter extends ListAdapter<NoticeModel, ViewPager2Adapter.ViewPager2ViewHolder> {
    private Context context;
    private Activity activity;


    protected ViewPager2Adapter(@NonNull @NotNull DiffUtil.ItemCallback<NoticeModel> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @NotNull
    @Override
    public ViewPager2ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterChattingnoticeItemBinding binding = AdapterChattingnoticeItemBinding.inflate(inflater, parent, false);
        return new ViewPager2ViewHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_chattingnotice_item;
    }

    @Override
    protected NoticeModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewPager2ViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public class ViewPager2ViewHolder extends RecyclerView.ViewHolder {
        private AdapterChattingnoticeItemBinding binding;

        public ViewPager2ViewHolder(@NonNull @NotNull AdapterChattingnoticeItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(NoticeModel noticeModel) {
            binding.setNoticeModel(noticeModel);
            binding.executePendingBindings();

        }
    }
}
