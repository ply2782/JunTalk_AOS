package com.cross.juntalk2.fifth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterLocationItemBinding;
import com.cross.juntalk2.model.KakaoMapClassUtis;

import java.util.ArrayList;
import java.util.List;

public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<KakaoMapClassUtis.Place> locationList = new ArrayList<>();
    private MapActivity.ClickLocation clickLocation;

    public LocationRecyclerViewAdapter(Context context, MapActivity.ClickLocation clickLocation) {
        this.context = context;
        this.clickLocation = clickLocation;
    }

    public void setItems(List<KakaoMapClassUtis.Place> locationList) {
        if (locationList == null) return;
        this.locationList.clear();
        this.locationList.addAll(locationList);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterLocationItemBinding binding = AdapterLocationItemBinding.inflate(inflater, parent, false);
        binding.setClickEvent(this);
        return new LocationRecyclerViewViewHolder(binding);
    }

    public void onClickEvent(View view, KakaoMapClassUtis.Place place) {
        clickLocation.location(place);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        KakaoMapClassUtis.Place location = locationList.get(position);
        if (holder instanceof LocationRecyclerViewViewHolder) {
            ((LocationRecyclerViewViewHolder) holder).onBind(location);
        }
    }

    @Override
    public int getItemCount() {
        return locationList == null ? 0 : locationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_location_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class LocationRecyclerViewViewHolder extends RecyclerView.ViewHolder {
        private AdapterLocationItemBinding binding;

        public LocationRecyclerViewViewHolder(@NonNull AdapterLocationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(KakaoMapClassUtis.Place location) {
            binding.setLocation(location);
            binding.executePendingBindings();

        }
    }
}
