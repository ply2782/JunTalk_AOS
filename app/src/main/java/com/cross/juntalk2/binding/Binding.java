package com.cross.juntalk2.binding;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.first.BirthdayAdapter;
import com.cross.juntalk2.first.FirstAdapter;
import com.cross.juntalk2.first.UserBulletinBoardAdapter;
import com.cross.juntalk2.first.UserImageOrVideoAdapter;
import com.cross.juntalk2.fourth.BulletinBoardAdapter;
import com.cross.juntalk2.fourth.ImageOrVideoAdapter;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.ChattingModel;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.second.ChattingListAdapter;
import com.cross.juntalk2.second.ChattingRoomAdapter;
import com.cross.juntalk2.second.OpenChattingRoomAdapter;
import com.cross.juntalk2.second.RoomJoinPeopleImageListAdapter;
import com.cross.juntalk2.third.DialogMusicListAdapter;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.Utils;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Binding {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd aa HH:mm", Locale.KOREA);
    private static final SimpleDateFormat getStringDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private static final SimpleDateFormat stringToDateConversationTIme = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.KOREA);
    private static final SimpleDateFormat customTime = new SimpleDateFormat("yy/MM/dd HH시 mm분 ss초", Locale.KOREA);
    private static final SimpleDateFormat todayDateToString = new SimpleDateFormat("aa HH:mm", Locale.KOREA);

    @BindingAdapter("myImage")
    public static void myImage(ShapeableImageView shapeableImageView, String imageUrl) {
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
                .error(R.drawable.shocked_9)
                .placeholder(shimmerDrawable);

        if (imageUrl == null || imageUrl.equals("")) {


            try {
                Glide.with(shapeableImageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(R.drawable.happy_9)
                        .thumbnail(0.3f)
                        .circleCrop()
                        .optionalCircleCrop()
                        .optionalCenterCrop()
                        .into(shapeableImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

            try {


                if (imageUrl.contains(".jpg")) {
                    Glide.with(shapeableImageView.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(CommonString.CommonStringInterface.FileBaseUrl + imageUrl.replace(" ", "%20"))
                            .thumbnail(0.3f)
                            .circleCrop()
                            .optionalCircleCrop()
                            .optionalCenterCrop()
                            .into(shapeableImageView);


                } else {

                    Glide.with(shapeableImageView.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(CommonString.CommonStringInterface.FileBaseUrl + imageUrl.replace(" ", "%20") + ".jpg")
                            .thumbnail(0.3f)
                            .circleCrop()
                            .optionalCircleCrop()
                            .optionalCenterCrop()
                            .into(shapeableImageView);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @BindingAdapter("bindingFirstAdapter")
    public static void bindingFirstAdapter(RecyclerView recyclerView, List<FriendModel> friendModelList) {
        FirstAdapter adapter = (FirstAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(friendModelList);
        }
    }

    @BindingAdapter("bindingBirthdayAdapter")
    public static void bindingBirthdayAdapter(RecyclerView recyclerView, List<FriendModel> friendModelList) {
        BirthdayAdapter adapter = (BirthdayAdapter) recyclerView.getAdapter();
        if (friendModelList != null && friendModelList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.submitList(friendModelList);
                adapter.notifyDataSetChanged();
            }
        } else {
            recyclerView.setVisibility(View.GONE);
        }

    }

    @BindingAdapter("bindingChattingListAdapter")
    public static void bindingChattingListAdapter(RecyclerView recyclerView, List<RoomModel> roomModelList) {
        ChattingListAdapter chattingListAdapter = (ChattingListAdapter) recyclerView.getAdapter();
        if (chattingListAdapter != null) {
            chattingListAdapter.submitList(roomModelList);
            chattingListAdapter.notifyDataSetChanged();
        }
    }

    @BindingAdapter("bindingChattingAdapter")
    public static void bindingChattingAdapter(RecyclerView recyclerView, ChattingModel chattingModelList) {
        ChattingRoomAdapter chattingRoomAdapter = (ChattingRoomAdapter) recyclerView.getAdapter();
        if (chattingRoomAdapter != null) {
            chattingRoomAdapter.setItems(chattingModelList);
            recyclerView.scrollToPosition(chattingRoomAdapter.getItemCount() - 1);
        }
    }


    @BindingAdapter("bindingOpenChattingAdapter")
    public static void bindingOpenChattingAdapter(RecyclerView recyclerView, ChattingModel chattingModelList) {
        OpenChattingRoomAdapter chattingRoomAdapter = (OpenChattingRoomAdapter) recyclerView.getAdapter();
        if (chattingRoomAdapter != null) {
            chattingRoomAdapter.setItems(chattingModelList);
            recyclerView.scrollToPosition(chattingRoomAdapter.getItemCount() - 1);
        }
    }

    //TODO : 리사이클러뷰는 재사용문제가 있기 때문에 초기화를 잘해줘야함
    //TODO  : 현재 이 메소드의 문제는 이모티콘을 한번 보냈는데 재사용으로 인해 이모티콘 사용안했는데 자동으로 나오는 문제가 있음
    @BindingAdapter("bindingImageViewWithImoticon")
    public static void bindingTextViewWithImoticon(AppCompatImageView imageView, String imageUrl) {
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
                .error(R.drawable.shocked_9)
                .placeholder(shimmerDrawable);
        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {

            imageView.setVisibility(View.GONE);

        } else {

            imageView.setVisibility(View.VISIBLE);
            if (imageUrl.contains(".mp4")) {
                Glide.with(imageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.VideoThumbNail + imageUrl)
                        .optionalCenterCrop()
                        .thumbnail(0.3f)
                        .transform(new CenterCrop(), new RoundedCorners(10))
                        .into(imageView);
            } else {

                Glide.with(imageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.RoomBaseUrl + imageUrl)
                        .optionalCenterCrop()
                        .thumbnail(0.3f)
                        .transform(new CenterCrop(), new RoundedCorners(10))
                        .into(imageView);
            }


        }
    }


    //TODO : 리사이클러뷰는 재사용문제가 있기 때문에 초기화를 잘해줘야함
    //TODO  : 현재 이 메소드의 문제는 이모티콘을 한번 보냈는데 재사용으로 인해 이모티콘 사용안했는데 자동으로 나오는 문제가 있음
    @BindingAdapter("bindingMainRoomPhoto")
    public static void bindingMainRoomPhoto(ShapeableImageView imageView, String imageUrl) {
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
                .error(R.drawable.shocked_9)
                .placeholder(shimmerDrawable);
        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {

            imageView.setVisibility(View.GONE);

        } else {

            imageView.setVisibility(View.VISIBLE);
            if (imageUrl.contains(".mp4")) {
                Glide.with(imageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.VideoThumbNail + imageUrl)
                        .thumbnail(0.3f)
                        .into(imageView);
            } else {

                Glide.with(imageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.RoomBaseUrl + imageUrl)
                        .thumbnail(0.3f)
                        .into(imageView);
            }


        }
    }

    @BindingAdapter("bindingVideoThumbNailImageView")
    public static void bindingVideoThumbNailImageView(AppCompatImageView videoView, String imageUrl) {
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
                .error(R.drawable.shocked_9)
                .placeholder(shimmerDrawable);
        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {

            videoView.setVisibility(View.GONE);

        } else {

            videoView.setVisibility(View.VISIBLE);
            Glide.with(videoView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.VideoThumbNail + imageUrl)
                    .optionalCenterCrop()
                    .optionalCircleCrop()
                    .thumbnail(0.3f)
                    .into(videoView);
        }
    }


    @BindingAdapter({"bindingMusicImage", "bindingMusicFolderName"})
    public static void bindingMusicImage(ShapeableImageView shapeableImageView, String imageUrl, String folderName) {
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
                .disallowHardwareConfig()
                .error(R.drawable.ic_image_headphone)
                .placeholder(shimmerDrawable);

        if (imageUrl == null || imageUrl.equals("null")) {

        } else {

            if (imageUrl == null || imageUrl.equals("null")) {
                Glide.with(shapeableImageView.getContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(R.drawable.ic_image_music)
                        .thumbnail(0.3f)
                        .optionalCenterCrop()
                        .into(shapeableImageView);
            } else {

                if (imageUrl == null || imageUrl.equals("null") || imageUrl.equals("")) {
                    Glide.with(shapeableImageView.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(R.drawable.ic_image_music)
                            .thumbnail(0.3f)
                            .optionalCenterCrop()
                            .into(shapeableImageView);

                } else {
                    Glide.with(shapeableImageView.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(CommonString.CommonStringInterface.MusicBaseURl + folderName + "&imageName=" + imageUrl.replace(" ", "%20"))
                            .thumbnail(0.3f)
                            .optionalCenterCrop()
                            .into(shapeableImageView);
                }

            }
        }
    }

    @BindingAdapter("bindingUserBulletinBoardAdapter")
    public static void bindingUserBulletinBoardAdapter(RecyclerView recyclerView, List<BulletinBoardModel> bulletinBoardListModel) {
        UserBulletinBoardAdapter adapter = (UserBulletinBoardAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(bulletinBoardListModel);
        }
    }

    @BindingAdapter("bindingBulletinBoardAdapter")
    public static void bindingBulletinBoardAdapter(RecyclerView recyclerView, List<BulletinBoardModel> bulletinBoardListModel) {
        BulletinBoardAdapter adapter = (BulletinBoardAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(bulletinBoardListModel);
        }
    }

    @BindingAdapter("bindingUserImageOrVideoAdapter")
    public static void bindingUserImageOrVideoAdapter(RecyclerView recyclerView, List<String> allUrl) {
        UserImageOrVideoAdapter adapter = (UserImageOrVideoAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(allUrl);
        }
    }

    @BindingAdapter("bindingImageOrVideoAdapter")
    public static void bindingImageOrVideoAdapter(RecyclerView recyclerView, List<String> allUrl) {
        ImageOrVideoAdapter adapter = (ImageOrVideoAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.submitList(allUrl);
        }
    }


    @BindingAdapter("bindingBitmap")
    public static void bindingBitmap(AppCompatImageView appCompatImageView, Bitmap bitmap) {
        if (bitmap == null) {
        } else {
            appCompatImageView.setImageBitmap(bitmap);
        }
    }

    @BindingAdapter("bindingMusicThumbNailUri")
    public static void bindingMusicThumbNailUri(ShapeableImageView shapeableImageView, String uri) {
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
                .error(R.drawable.drawable_circle)
                .placeholder(shimmerDrawable);

        shapeableImageView.post(new Runnable() {
            @Override
            public void run() {
                if (uri != null) {
                    Glide.with(shapeableImageView.getContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(Uri.parse(uri))
                            .thumbnail(0.3f)
                            .optionalCenterCrop()
                            .into(shapeableImageView);
                }
            }
        });

    }

    @BindingAdapter("bindingMusicModelAdapterForUpload")
    public static void bindingMusicModelAdapterForUpload(RecyclerView recyclerView, List<MusicModel> musicModels) {
        DialogMusicListAdapter adapter = (DialogMusicListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(musicModels);
        }
    }

    @BindingAdapter("bindingRoomJoinPeopleImageList")
    public static void bindingRoomJoinPeopleImageList(RecyclerView recyclerView, List<String> imageList) {
        RoomJoinPeopleImageListAdapter adapter = (RoomJoinPeopleImageListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(imageList);
        }
    }

    @BindingAdapter("changeRegDateTime")
    public static void changeRegDateTime(AppCompatTextView appCompatTextView, String date) {
        try {
            appCompatTextView.setText(calculateTime(Objects.requireNonNull(getStringDateFormat.parse(date))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static String calculateTime(Date date) {

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

    @BindingAdapter("stringToRegDate")
    public static void stringToRegDate(AppCompatTextView textView, String regDate) {
        try {
            String thisRegDate = customTime.format(Objects.requireNonNull(stringToDateConversationTIme.parse(regDate)));
            textView.setText("최근 로그인 : " + thisRegDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BindingAdapter("imageButtonTint")
    public static void imageButtonTint(AppCompatImageButton appCompatImageButton, boolean bulletin_isUserLike) {
        Log.e("abc","bulletin_isUserLike "+ bulletin_isUserLike);
        if (bulletin_isUserLike) {
            appCompatImageButton.setImageTintList(ColorStateList.valueOf(Color.RED));
        } else {
            appCompatImageButton.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        }
    }

    @BindingAdapter("bulletinTimeText")
    public static void bulletinTimeText(AppCompatTextView appCompatTextView, String regDate) {
        try {
            appCompatTextView.setText(todayDateToString.format(Objects.requireNonNull(getStringDateFormat.parse(regDate))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
