package com.cross.juntalk2.fifth;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.model.CalendarModel;
import com.cross.juntalk2.model.ClubModel;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarFragmentAdapter extends RecyclerView.Adapter<CalendarFragmentAdapter.CalendarViewHolder> {
    private Context context;
    private Map<Integer, List<CalendarModel>> calendarMap;
    private Handler handler;
    private CalendarViewHolder calendarViewHolder;
    private LinkedHashMap<String, List<ClubModel>> clubModelMap = new LinkedHashMap<>();

    public CalendarFragmentAdapter(Context context, Map<Integer, List<CalendarModel>> calendarMap) {
        this.context = context;
        this.calendarMap = calendarMap;
        handler = new Handler(Looper.getMainLooper());
    }

    public void setItems(LinkedHashMap<String, List<ClubModel>> clubModelMap) {
        if (clubModelMap == null) return;
        this.clubModelMap.clear();
        this.clubModelMap.putAll(clubModelMap);
        notifyDataSetChanged();
    }


    @NonNull
    @NotNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        calendarViewHolder = new CalendarViewHolder(view);
        return calendarViewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.calendar_item_list;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalendarViewHolder holder, int position) {
        List<CalendarModel> modelList = calendarMap.get(position);
        holder.recyclerViewGridView.setAdapter(new CalendarRecyclerViewAdapter(context, modelList, clubModelMap));
    }


    @Override
    public int getItemCount() {
        return calendarMap.size();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        private View parent;
        public RecyclerView recyclerViewGridView;


        public CalendarViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            parent = itemView;
            parent.setTag(this);
            recyclerViewGridView = itemView.findViewById(R.id.recyclerViewGridView);

        }

    }
}

