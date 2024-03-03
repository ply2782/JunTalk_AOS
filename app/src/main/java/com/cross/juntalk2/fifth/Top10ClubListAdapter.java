package com.cross.juntalk2.fifth;

import android.content.ActivityNotFoundException;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.loopeer.cardstack.StackAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.loopeer.cardstack.CardStackView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

public class Top10ClubListAdapter extends StackAdapter<ClubModel> {

    private final RecyclerView.RecycledViewPool sharedPool = new RecyclerView.RecycledViewPool();
    public ClubIntroduceFileAdapter adapter;
    private final Context context;
    private final String myId;
    private DialogMyMadeClubList.RemoveInterface removeInterface;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private final SimpleDateFormat parseDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private final Date now;
    private final DecimalFormat moneyFormat = new DecimalFormat("###,###");

    public void setRemoveInterface(DialogMyMadeClubList.RemoveInterface removeInterface) {
        this.removeInterface = removeInterface;
    }

    public Top10ClubListAdapter(Context context) {
        super(context);
        this.context = context;
        myId = JunApplication.getMyModel().userId;
        now = new Date();
    }

    @Override
    public void bindViewHolder(CardStackView.ViewHolder holder, int position) {
        super.bindViewHolder(holder, position);
    }

    @Override
    public void bindView(ClubModel clubModel, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                    .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                    .setBaseAlpha(1f) //the alpha of the underlying children
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


            Glide.with(h.mainImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + clubModel.allUrls.get(0))
                    .thumbnail(0.3f)
                    .optionalCenterCrop()
                    .into(h.mainImageView);


            if (clubModel.allUrls.get(0).contains(".mp4")) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.VideoThumbNail + clubModel.allUrls.get(0))
                        .thumbnail(0.3f)
                        .circleCrop()
                        .optionalCircleCrop()
                        .optionalCenterCrop()
                        .into(h.mainImageView);

            } else {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(CommonString.CommonStringInterface.FileBaseUrl + clubModel.allUrls.get(0))
                        .thumbnail(0.3f)
                        .circleCrop()
                        .optionalCircleCrop()
                        .optionalCenterCrop()
                        .into(h.mainImageView);
            }

            try {
                Date clubRegDate = parseDateFormat.parse(clubModel.regDate);
                if (clubRegDate.compareTo(now) < 0) {
                    h.currentMyClubStatusImageView.setBackgroundResource(R.drawable.image_close_sign);
                }else if(clubModel.limitJoinCount == clubModel.currentSumJoinCount){
                    h.currentMyClubStatusImageView.setBackgroundResource(R.drawable.image_close_sign);
                } else {
                    h.currentMyClubStatusImageView.setBackgroundResource(R.drawable.image_open_sign);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int currentJoincount = 0;
            int currentWaitingcount = 0;
            for (Map<String, Object> joinMap : clubModel.myJoinInfo) {
                if (joinMap.get("requestResult") != null) {
                    if (joinMap.get("requestResult").equals("Y")) {
                        currentJoincount++;
                    } else {
                        if (joinMap.get("requestResult").equals("N")) {
                            currentWaitingcount++;
                        }
                    }
                }
            }

            h.currentJoinTextView.setText("" + currentJoincount);
            h.currentWaitingTextView.setText("" + currentWaitingcount);
            h.expectedMoneyTextView.setText("예상 경비 : ₩ " + moneyFormat.format(clubModel.expectedMoney));
            h.titleTextView.setText(clubModel.title);
            h.joinCountTextView.setText("참여가능 인원 : " + clubModel.limitJoinCount);
            h.ageCountTextView.setText("참여 가능 연령대 : " + clubModel.minAge + "세 ~ " + clubModel.maxAge + "세");
            h.clubIntroduceTextView.setText(clubModel.clubIntroduce);

            adapter = new ClubIntroduceFileAdapter(context);
            adapter.setItems(clubModel.allUrls);
            h.imageOrVideoRecyclerView.setAdapter(adapter);
            h.imageOrVideoRecyclerView.setRecycledViewPool(sharedPool);
            PagerSnapHelper pagerSnapHelper =new PagerSnapHelper();
            if (h.imageOrVideoRecyclerView.getOnFlingListener() == null)
                pagerSnapHelper.attachToRecyclerView(h.imageOrVideoRecyclerView);

            h.lookMyClubStatusButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, JoinClubPeopleListActivity.class);
                intent.putExtra("club_Uuid", clubModel.club_Uuid);
                intent.putExtra("owner", clubModel.user_Index);
                intent.putExtra("ownerId", clubModel.userId);
                context.startActivity(intent);
            });

            h.modifyButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, CreateClubActivity.class);
                intent.putExtra("clubModel", clubModel);
                context.startActivity(intent);
                h.mContainerContent.setVisibility(View.GONE);
            });

            h.cancelButton.setOnClickListener(v -> removeInterface.remove(clubModel));

            try {
                StringTokenizer tokenizer = new StringTokenizer(clubModel.hashTagList, ",");
                while (tokenizer.hasMoreTokens()) {
                    Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
                    chip.setText("#" + tokenizer.nextToken());
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    h.hashTagChipGroup.addView(chip, layoutParams);
                }

                JSONObject jsonObject = new JSONObject(clubModel.place);
                h.locationTextView.setText(jsonObject.get("place_name").toString());
                h.lookMapButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://place?id=" + jsonObject.getInt("id")));
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "카카오 지도앱을 다운로드 받아주세요.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map"));
                        context.startActivity(intent);


                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    public static class ColorItemViewHolder extends CardStackView.ViewHolder {
        private final View mContainerContent;
        private final ImageFilterView mainImageView;
        public RecyclerView imageOrVideoRecyclerView;
        private final ChipGroup hashTagChipGroup;
        private final AppCompatTextView titleTextView;
        private final AppCompatTextView joinCountTextView;
        private final AppCompatTextView locationTextView;
        private final AppCompatButton lookMapButton;
        private final AppCompatTextView clubIntroduceTextView;
        private final AppCompatTextView ageCountTextView;
        private final AppCompatButton modifyButton;
        private final AppCompatButton cancelButton;
        private final AppCompatButton lookMyClubStatusButton;
        private final AppCompatImageView currentMyClubStatusImageView;
        private final AppCompatTextView currentJoinTextView;
        private final AppCompatTextView currentWaitingTextView;
        private final AppCompatTextView expectedMoneyTextView;

        public ColorItemViewHolder(View view) {
            super(view);
            lookMyClubStatusButton = view.findViewById(R.id.lookMyClubStatusButton);
            currentMyClubStatusImageView = view.findViewById(R.id.currentMyClubStatusImageView);
            currentJoinTextView = view.findViewById(R.id.currentJoinTextView);
            currentWaitingTextView = view.findViewById(R.id.currentWaitingTextView);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mainImageView = view.findViewById(R.id.mainImageView);
            imageOrVideoRecyclerView = view.findViewById(R.id.imageOrVideoRecyclerView);
            hashTagChipGroup = view.findViewById(R.id.hashTagChipGroup);
            expectedMoneyTextView = view.findViewById(R.id.expectedMoneyTextView);
            titleTextView = view.findViewById(R.id.titleTextView);
            joinCountTextView = view.findViewById(R.id.joinCountTextView);
            locationTextView = view.findViewById(R.id.locationTextView);
            lookMapButton = view.findViewById(R.id.lookMapButton);
            clubIntroduceTextView = view.findViewById(R.id.clubIntroduceTextView);
            ageCountTextView = view.findViewById(R.id.ageCountTextView);
            modifyButton = view.findViewById(R.id.modifyButton);
            cancelButton = view.findViewById(R.id.cancelButton);
        }


        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
            View childView = imageOrVideoRecyclerView.getChildAt(0);
            if (childView != null) {
                ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                if (viewHolder.imageVideoView.isPlaying()) {
                    viewHolder.imageVideoView.pauseVideo();
                }
            }
        }
    }

}
