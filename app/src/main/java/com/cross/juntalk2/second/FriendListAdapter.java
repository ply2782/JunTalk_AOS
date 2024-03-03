package com.cross.juntalk2.second;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.imageview.ShapeableImageView;
import com.skydoves.powermenu.MenuBaseAdapter;

public class FriendListAdapter extends MenuBaseAdapter<FriendModel> {


    //todo : 재사용 문제로 바인딩은 안돼고 일일이 새로 객체 생성 후 넣어줘야함
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_dialog_friendlist_item, viewGroup, false);
        }

        FriendModel item = (FriendModel) getItem(index);
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
                .error(R.drawable.image_question_mark)
                .placeholder(shimmerDrawable);
        ShapeableImageView shapeableImageView = view.findViewById(R.id.personImageView);
        AppCompatTextView compatTextView = view.findViewById(R.id.nickNameTextView);
        if (item.userMainPhoto.contains(".jpg")) {
            Glide.with(shapeableImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + item.userMainPhoto.replace(" ", "%20"))
                    .thumbnail(0.3f)
                    .circleCrop()
                    .optionalCircleCrop()
                    .optionalCenterCrop()
                    .into(shapeableImageView);

        } else {

            Glide.with(shapeableImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + item.userMainPhoto.replace(" ", "%20") + ".jpg")
                    .thumbnail(0.3f)
                    .circleCrop()
                    .optionalCircleCrop()
                    .optionalCenterCrop()
                    .into(shapeableImageView);
        }
        compatTextView.setText(item.userId);

        return super.getView(index, view, viewGroup);
    }
}