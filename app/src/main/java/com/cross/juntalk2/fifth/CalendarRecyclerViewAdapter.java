package com.cross.juntalk2.fifth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.model.CalendarModel;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.utils.JunApplication;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    public List<CalendarModel> calendarList;
    private SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private Date now;
    private LinkedHashMap<String, List<ClubModel>> clubListMap;
    private String myId;

    public CalendarRecyclerViewAdapter(Context context, List<CalendarModel> calendarList, LinkedHashMap<String, List<ClubModel>> clubListMap) {
        this.context = context;
        this.calendarList = calendarList;
        now = new Date();
        this.clubListMap = clubListMap;
        myId = JunApplication.getMyModel().userId;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        Object getItem = calendarList.get(position);

        if (holder instanceof CalendarViewHolder) {
            ((CalendarViewHolder) holder).onBind(getItem, clubListMap);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.calendar_date_item_list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return calendarList == null ? 0 : calendarList.size();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        private TextView dayText, todayTextView, clubListCountTextView;
        private AppCompatImageView myImageView;
        public View parent;
        private LinearLayout backgroundColorChangeLayout;

        public CalendarViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            todayTextView = itemView.findViewById(R.id.todayTextView);
            clubListCountTextView = itemView.findViewById(R.id.clubListCountTextView);
            backgroundColorChangeLayout = itemView.findViewById(R.id.backgroundColorChangeLayout);
            myImageView = itemView.findViewById(R.id.myImageView);
            parent = itemView;
        }


        public void onBind(Object getItem, LinkedHashMap<String, List<ClubModel>> clubModelList) {

            if (getItem instanceof CalendarModel) {
                CalendarModel calendar = (CalendarModel) getItem;
                clubListCountTextView.setVisibility(View.GONE);
                dayText.setText("" + dayFormat.format(calendar.getGregorianCalendar().getTime()));
                if (calendar.getType() == 1) {

                    if (clubListMap.get(defaultFormat.format(calendar.getGregorianCalendar().getTime())) != null) {
                        List<ClubModel> clubModels = clubListMap.get(defaultFormat.format(calendar.getGregorianCalendar().getTime()));
                        clubListCountTextView.setText("" + clubModels.size());
                        clubListCountTextView.setVisibility(View.VISIBLE);

                        myImageView.setVisibility(View.GONE);
                        for (ClubModel clubModel : clubModels) {
                            for (Map<String, Object> mapInfo : clubModel.myJoinInfo) {
                                if (mapInfo.get("userId").equals(myId)) {
                                    if (mapInfo.get("requestResult") != null) {
                                        if (mapInfo.get("requestResult").equals("R")) {
                                            myImageView.setVisibility(View.GONE);
                                        } else {
                                            myImageView.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        myImageView.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    }

                    dayText.setTextColor(Color.BLACK);
                    if (calendar.getGregorianCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        dayText.setTextColor(Color.RED);
                    } else if (calendar.getGregorianCalendar().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        dayText.setTextColor(Color.BLUE);
                    }

                    if (defaultFormat.format(calendar.getGregorianCalendar().getTime()).equals(defaultFormat.format(now))) {
                        todayTextView.setVisibility(View.VISIBLE);
                    } else {
                        todayTextView.setVisibility(View.GONE);
                    }

                    String currentDateTime = defaultFormat.format(calendar.getGregorianCalendar().getTime());
                    moveDetailOfClubList(clubModelList.get(currentDateTime), currentDateTime);
                } else {
                    dayText.setTextColor(Color.GRAY);
                }
                parent.setTag(this);
            }
        }

        public void moveDetailOfClubList(List<ClubModel> clubModelList, String currentDate) {
            backgroundColorChangeLayout.setOnClickListener(v -> {
                if (clubModelList != null && !clubModelList.isEmpty()) {
                    Intent intent = new Intent(context, ClubListActivity.class);
//                    intent.putExtra("clubModel", (Serializable) clubModelList);
                    intent.putExtra("currentDate", currentDate);
                    context.startActivity(intent);
                }
            });
        }
    }
}
