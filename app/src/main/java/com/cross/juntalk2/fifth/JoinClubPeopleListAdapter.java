package com.cross.juntalk2.fifth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.second.PictureActivity;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JoinClubPeopleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Map<String, Object>> peopleList = new ArrayList<>();

    private int user_Index;
    private int my_user_Index;
    private JoinClubPeopleListActivity.AcceptInterface acceptInterface;

    public void setAcceptInterface(JoinClubPeopleListActivity.AcceptInterface acceptInterface) {
        this.acceptInterface = acceptInterface;
    }

    public JoinClubPeopleListAdapter(Context context, int user_Index) {
        this.context = context;
        this.user_Index = user_Index;
        my_user_Index = JunApplication.getMyModel().user_Index;

    }

    public void setPeopleList(List<Map<String, Object>> peopleList) {
        if (peopleList == null) return;
        this.peopleList.clear();
        this.peopleList.addAll(peopleList);
        notifyDataSetChanged();
    }

    public void removePeopleList(Map<String, Object> people) {
        peopleList.remove(people);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType, parent, false);
        return new JoinClubPeopleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Map<String, Object> item = peopleList.get(holder.getAbsoluteAdapterPosition());
        if (holder instanceof JoinClubPeopleListViewHolder) {
            Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                    .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                    .setBaseAlpha(0.7f) //the alpha of the underlying children
                    .setBaseColor(Color.GRAY)
                    .setHighlightAlpha(0.7f) // the shimmer alpha amount
                    .setHighlightColor(Color.WHITE)
                    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                    .setAutoStart(true)
                    .build();
            ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
            shimmerDrawable.setShimmer(shimmer);
            RequestOptions requestOptions = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .disallowHardwareConfig()
                    .error(R.drawable.btn_x)
                    .placeholder(shimmerDrawable);


            Glide.with(((JoinClubPeopleListViewHolder) holder).personImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + item.get("userMainPhoto"))
                    .thumbnail(0.3f)
                    .circleCrop()
                    .optionalCircleCrop()
                    .optionalCenterCrop()
                    .into(((JoinClubPeopleListViewHolder) holder).personImageView);
            ((JoinClubPeopleListViewHolder) holder).personIdTextView.setText("" + item.get("userId"));
            ((JoinClubPeopleListViewHolder) holder).personImageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PictureActivity.class);
                intent.putExtra("imageUrl", (String) item.get("userMainPhoto"));
                context.startActivity(intent);
            });

            ((JoinClubPeopleListViewHolder) holder).acceptLinearLayout.setVisibility(View.VISIBLE);
            ((JoinClubPeopleListViewHolder) holder).acceptButton.setVisibility(View.GONE);
            ((JoinClubPeopleListViewHolder) holder).rejectButton.setVisibility(View.GONE);
            if (user_Index != my_user_Index) {
                ((JoinClubPeopleListViewHolder) holder).acceptButton.setVisibility(View.GONE);
                ((JoinClubPeopleListViewHolder) holder).rejectButton.setVisibility(View.GONE);
            } else {
                ((JoinClubPeopleListViewHolder) holder).acceptButton.setVisibility(View.VISIBLE);
                ((JoinClubPeopleListViewHolder) holder).rejectButton.setVisibility(View.VISIBLE);
                ((JoinClubPeopleListViewHolder) holder).acceptButton.setOnClickListener(v -> acceptInterface.accept(item, ((JoinClubPeopleListViewHolder) holder).acceptedButton, holder.getAbsoluteAdapterPosition()));
                ((JoinClubPeopleListViewHolder) holder).rejectButton.setOnClickListener(v -> acceptInterface.reject(item, ((JoinClubPeopleListViewHolder) holder).acceptedButton, holder.getAbsoluteAdapterPosition()));
            }
        }

        ((JoinClubPeopleListViewHolder) holder).acceptedButton.setVisibility(View.GONE);
        if (item.get("requestResult").equals("Y")) {
            ((JoinClubPeopleListViewHolder) holder).acceptedButton.setVisibility(View.VISIBLE);
            ((JoinClubPeopleListViewHolder) holder).acceptButton.setVisibility(View.GONE);
            ((JoinClubPeopleListViewHolder) holder).rejectButton.setVisibility(View.GONE);
        } else {
            ((JoinClubPeopleListViewHolder) holder).acceptedButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return peopleList == null ? 0 : peopleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_joinpeoplelist_person_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class JoinClubPeopleListViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView personImageView;
        private AppCompatTextView personIdTextView;
        private AppCompatButton acceptButton, rejectButton;
        private LinearLayout acceptLinearLayout;
        private AppCompatButton acceptedButton;

        public JoinClubPeopleListViewHolder(@NonNull View itemView) {
            super(itemView);
            personIdTextView = itemView.findViewById(R.id.personIdTextView);
            personImageView = itemView.findViewById(R.id.personImageView);
            acceptLinearLayout = itemView.findViewById(R.id.acceptLinearLayout);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            acceptedButton = itemView.findViewById(R.id.acceptedButton);
        }
    }
}
