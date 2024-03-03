package com.cross.juntalk2.first;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterRequestForAdminItemBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestForAdminAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Map<String, Object>> requestMapList = new ArrayList<>();

    public void setRequestMapList(List<Map<String, Object>> requestMapList) {
        if (requestMapList == null) return;
        this.requestMapList.addAll(requestMapList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        requestMapList.clear();
    }

    public void addItem(Map<String, Object> addItem) {
        requestMapList.add(addItem);
        notifyDataSetChanged();
    }

    public RequestForAdminAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_request_for_admin_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void clickEvent(View view, Map<String, Object> contentMap) {
        Log.e("abc","contentMap : "+ contentMap.toString());
        Intent intent = new Intent(context, RequestQuestionDetailActivity.class);
        intent.putExtra("requestQuestionMap", (Serializable) contentMap);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterRequestForAdminItemBinding binding = AdapterRequestForAdminItemBinding.inflate(inflater, parent, false);
        binding.setRequestForAdminAdapter(this);
        return new RequestForAdminViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> mapItem = requestMapList.get(position);
        if (holder instanceof RequestForAdminViewHolder) {
            ((RequestForAdminViewHolder) holder).onBind(mapItem);
        }

    }

    @Override
    public int getItemCount() {
        return requestMapList == null ? 0 : requestMapList.size();
    }

    public class RequestForAdminViewHolder extends RecyclerView.ViewHolder {
        private AdapterRequestForAdminItemBinding binding;

        public RequestForAdminViewHolder(@NonNull AdapterRequestForAdminItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(Map<String, Object> map) {
            binding.setRequestContentMap(map);
            binding.executePendingBindings();

        }
    }
}
