package com.cross.juntalk2.first;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterBirthdayBinding;
import com.cross.juntalk2.model.FriendModel;

import org.jetbrains.annotations.NotNull;

public class BirthdayAdapter extends ListAdapter<FriendModel, BirthdayAdapter.BirthdayViewHolder> {


    protected BirthdayAdapter(@NonNull @NotNull DiffUtil.ItemCallback<FriendModel> diffCallback) {
        super(diffCallback);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_birthday;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected FriendModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    @NonNull
    @NotNull
    @Override
    public BirthdayViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterBirthdayBinding binding = AdapterBirthdayBinding.inflate(inflater, parent, false);
        return new BirthdayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BirthdayViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    public class BirthdayViewHolder extends RecyclerView.ViewHolder {
        private AdapterBirthdayBinding binding;

        public BirthdayViewHolder(@NonNull @NotNull AdapterBirthdayBinding binding) {
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
