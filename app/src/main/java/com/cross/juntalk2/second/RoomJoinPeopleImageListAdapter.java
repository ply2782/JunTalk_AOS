package com.cross.juntalk2.second;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.RoomjoinpeopleimageItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RoomJoinPeopleImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<String> imageUrlList = new ArrayList<>();

    public RoomJoinPeopleImageListAdapter(Context context) {
        this.context = context;
    }

    public void setItems(List<String> imageUrlList){
        if(imageUrlList ==null)return;
        this.imageUrlList.clear();
        this.imageUrlList.addAll(imageUrlList);
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RoomjoinpeopleimageItemBinding binding = RoomjoinpeopleimageItemBinding.inflate(inflater, parent, false);
        return new RoomJoinPeopleImageListAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        String imageUrl = imageUrlList.get(position);
        if (holder instanceof RoomJoinPeopleImageListAdapterViewHolder) {
            ((RoomJoinPeopleImageListAdapterViewHolder) holder).onBind(imageUrl);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrlList == null ? 0 : imageUrlList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.roomjoinpeopleimage_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RoomJoinPeopleImageListAdapterViewHolder extends RecyclerView.ViewHolder {
        private RoomjoinpeopleimageItemBinding binding;

        public RoomJoinPeopleImageListAdapterViewHolder(@NonNull @NotNull RoomjoinpeopleimageItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(String imageUrl) {
            binding.setImageUrl(imageUrl);
            binding.executePendingBindings();
        }
    }
}
