package com.cross.juntalk2.second;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterChattingItemBinding;
import com.cross.juntalk2.databinding.AdapterJoinchattingItemBinding;
import com.cross.juntalk2.databinding.AdapterOpenchattingItemBinding;
import com.cross.juntalk2.model.RoomModel;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.StringTokenizer;

public class OpenChattingListAdapter extends ListAdapter<RoomModel, OpenChattingListAdapter.JoinChattingListViewHolder> {

    private Context context;
    private Handler handler;

    public OpenChattingListAdapter(@NonNull DiffUtil.ItemCallback<RoomModel> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
        handler = new Handler(Looper.getMainLooper());
    }



    @NonNull
    @Override
    public JoinChattingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterJoinchattingItemBinding binding1 = AdapterJoinchattingItemBinding.inflate(inflater, parent, false);
        binding1.setChattingListAdapter(this);
        return new JoinChattingListViewHolder(binding1);

    }

    public void moveToJoinChattingRoom(View view, RoomModel roomModel) {
        Intent intent = new Intent(context, OpenChattingRoomActivity.class);
        intent.putExtra("roomModel", roomModel);
        context.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_joinchatting_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull JoinChattingListViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }

    public class JoinChattingListViewHolder extends RecyclerView.ViewHolder {

        private AdapterJoinchattingItemBinding binding;

        public JoinChattingListViewHolder(@NonNull AdapterJoinchattingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(RoomModel roomModel) {
            try {
                binding.setRoomModel(roomModel);
                binding.executePendingBindings();
                binding.chipgroup.removeAllViews();
                StringTokenizer tokenizer = new StringTokenizer(roomModel.roomHashTag, ",");
                StringBuilder builder = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    String tokenText = tokenizer.nextToken();
                    builder.append(tokenText);
                    builder.append(",");
                    Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
                    chip.setText(tokenText);
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    binding.chipgroup.addView(chip, layoutParams);
                }
            } catch (Exception e) {

            }

        }
    }
}
