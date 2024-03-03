package com.cross.juntalk2.second;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterChattingItemBinding;
import com.cross.juntalk2.databinding.AdapterJoinchattingItemBinding;
import com.cross.juntalk2.fifth.JoinClubPeopleListActivity;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.model.UnReadCountModel;
import com.cross.juntalk2.utils.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class ChattingListAdapter extends ListAdapter<RoomModel, RecyclerView.ViewHolder> {

    private Context context;
    private SimpleDateFormat stringToDate = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    public ChattingListAdapter(@NonNull @NotNull DiffUtil.ItemCallback<RoomModel> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case R.layout.adapter_chatting_item:
                AdapterChattingItemBinding binding = AdapterChattingItemBinding.inflate(inflater, parent, false);
                binding.setChattingListAdapter(this);
                return new ChattingListViewHolder(binding);
        }
        return null;
    }

    public void moveToChattingRoom(View view, RoomModel roomModel) {
        Intent intent = new Intent(context, ChattingRoomActivity.class);
        intent.putExtra("roomModel", roomModel);
        context.startActivity(intent);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChattingListViewHolder) {
            ((ChattingListViewHolder) holder).onBind(getItem(position));
        }
    }


    @Override
    protected RoomModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_chatting_item;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ChattingListViewHolder extends RecyclerView.ViewHolder {
        private AdapterChattingItemBinding binding;

        public ChattingListViewHolder(@NonNull @NotNull AdapterChattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(RoomModel roomModel) {
            binding.setRoomModel(roomModel);
            RoomJoinPeopleImageListAdapter adapter = new RoomJoinPeopleImageListAdapter(context);
            binding.joinPeopleImageListRecyclerView.setAdapter(adapter);
            binding.executePendingBindings();

            try {
                if (roomModel.conversationTime != null && !roomModel.conversationTime.equals("null")) {
                    binding.lastestConversationTime.setText(calculateTime(stringToDate.parse(roomModel.conversationTime)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


        public String calculateTime(Date date) {
            long curTime = System.currentTimeMillis();
            long regTime = date.getTime();
            long diffTime = (curTime - regTime) / 1000;
            String msg = null;
            if (diffTime < Utils.TIME_MAXIMUM.SEC) {

                // sec
                msg = diffTime + "초전";

            } else if ((diffTime /= Utils.TIME_MAXIMUM.SEC) < Utils.TIME_MAXIMUM.MIN) {
                // min
                System.out.println(diffTime);
                msg = diffTime + "분전";

            } else if ((diffTime /= Utils.TIME_MAXIMUM.MIN) < Utils.TIME_MAXIMUM.HOUR) {
                // hour
                msg = (diffTime) + "시간전";

            } else if ((diffTime /= Utils.TIME_MAXIMUM.HOUR) < Utils.TIME_MAXIMUM.DAY) {
                // day
                msg = (diffTime) + "일전";

            } else if ((diffTime /= Utils.TIME_MAXIMUM.DAY) < Utils.TIME_MAXIMUM.MONTH) {
                // day
                msg = (diffTime) + "달전";

            } else {
                msg = (diffTime) + "년전";

            }
            return msg;
        }

    }
}
