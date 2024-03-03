package com.cross.juntalk2.first;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterPrayrequestItemBinding;
import com.cross.juntalk2.model.FriendModel;

import org.jetbrains.annotations.NotNull;

public class PrayRequestAdapter extends ListAdapter<FriendModel, PrayRequestAdapter.PrayRequestViewHolder> {


    protected PrayRequestAdapter(@NonNull @NotNull DiffUtil.ItemCallback<FriendModel> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @NotNull
    @Override
    public PrayRequestViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterPrayrequestItemBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new PrayRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PrayRequestViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_prayrequest_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class PrayRequestViewHolder extends RecyclerView.ViewHolder {
        private AdapterPrayrequestItemBinding binding;

        public PrayRequestViewHolder(@NonNull @NotNull AdapterPrayrequestItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(FriendModel friendModel, int position) {
            binding.setFriendModel(friendModel);
            binding.setPosition(position);
            binding.executePendingBindings();

        }
    }
}
