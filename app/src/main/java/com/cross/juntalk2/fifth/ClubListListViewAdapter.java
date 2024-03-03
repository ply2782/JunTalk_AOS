package com.cross.juntalk2.fifth;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import worldline.com.foldablelayout.FoldableLayout;

public class ClubListListViewAdapter extends RecyclerView.Adapter<ClubListListViewAdapter.ClubListViewHolder> {

    private final Context context;
    private final Map<Integer, Boolean> mFoldStates = new HashMap<>();
    private final List<ClubModel> clubModelList = new ArrayList<>();
    private ClubListActivity.JoinClickInterface clickInterface;
    private String myId;
    public ClubIntroduceFileAdapter adapter;
    private Handler handler;
    private DecimalFormat moneyFormat = new DecimalFormat("###,###");
    private String blockContent = "";
    private RetrofitService service;


    public ClubListListViewAdapter(Context context) {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

        this.context = context;
        myId = JunApplication.getMyModel().userId;
        handler = new Handler(Looper.getMainLooper());

    }

    public void setItems(List<ClubModel> clubModelList) {
        if (clubModelList == null) return;
        this.clubModelList.clear();
        this.clubModelList.addAll(clubModelList);
        notifyDataSetChanged();
    }


    public void setIndividualsItems(List<ClubModel> clubModelList, int position) {
        if (clubModelList == null) return;
        this.clubModelList.clear();
        this.clubModelList.addAll(clubModelList);
        notifyItemChanged(position);
    }

    public void setClickInterface(ClubListActivity.JoinClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }

    @NonNull
    @Override
    public ClubListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClubListViewHolder(new FoldableLayout(parent.getContext()));

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        /*recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                Log.e("abc", "attached");
                RecyclerView imageOrVideoRecyclerView = view.findViewById(R.id.imageOrVideoRecyclerView);
                View childView = imageOrVideoRecyclerView.getChildAt(0);
                if (childView != null) {
                    ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder1 = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                    if (!viewHolder1.imageVideoView.isPlaying()) {
                        Log.e("abc", "play");
                        viewHolder1.imageVideoView.playVideo();
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                Log.e("abc", "detached");
                RecyclerView imageOrVideoRecyclerView = view.findViewById(R.id.imageOrVideoRecyclerView);
                View childView = imageOrVideoRecyclerView.getChildAt(0);
                if (childView != null) {
                    ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder1 = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                    if (viewHolder1.imageVideoView.isPlaying()) {
                        Log.e("abc", "pause");
                        viewHolder1.imageVideoView.releasePlayer();
                    }
                }
            }
        });*/

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                        View childView = recyclerView.getChildAt(0);
                        ClubListListViewAdapter.ClubListViewHolder viewHolder = (ClubListListViewAdapter.ClubListViewHolder) childView.getTag();
                        childView = viewHolder.imageOrVideoRecyclerView.getChildAt(0);
                        if (childView != null) {
                            ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder1 = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                            if (viewHolder1.imageVideoView.isPlaying()) {
                                viewHolder1.imageVideoView.pauseVideo();
                            }
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        View childView = recyclerView.getChildAt(0);
                        ClubListListViewAdapter.ClubListViewHolder viewHolder = (ClubListListViewAdapter.ClubListViewHolder) childView.getTag();
                        childView = viewHolder.imageOrVideoRecyclerView.getChildAt(0);
                        if (childView != null) {
                            ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder1 = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                            if (!viewHolder1.imageVideoView.isPlaying()) {
                                viewHolder1.imageVideoView.playVideo();
                                viewHolder1.playButton.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    public void reportDialog(ClubModel clubModel) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_blockreport, null, false);
        cautionAlert.setView(cautionView);
        RadioGroup radioGroup = cautionView.findViewById(R.id.radioGroupLayout);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.firstReportRadioButton:
                        blockContent = "부적절한 사진 및 동영상";
                        break;
                    case R.id.secondReportRadioButton:
                        blockContent = "선정적 / 폭력적 사진 및 동영상";
                        break;
                    case R.id.thirdReportRadioButton:
                        blockContent = "불쾌감을 자극하는 잔인한 사진 및 동영상";
                        break;
                    case R.id.fourthReportRadioButton:
                        blockContent = "기타 부적절한 사진 및 동영상";
                        break;
                }
            }
        });
        Button okButton = cautionView.findViewById(R.id.reportButton);
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blockContent.replace(" ", "").trim().equals("")) {
                    Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    try {
                        clickInterface.foldReportFunction(clubModel, alertDialog, blockContent);
                    } catch (Exception e) {

                    }
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public void onBindViewHolder(@NonNull ClubListViewHolder holder, int position) {
        ClubModel clubModel = clubModelList.get(holder.getAbsoluteAdapterPosition());

        // Bind data

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
        String url = clubModel.allUrls.get(0);
        if (url.contains(".mp4")) {
            Glide.with(holder.mainThumbNailImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.VideoThumbNail + clubModel.allUrls.get(0))
                    .thumbnail(0.3f)
                    .optionalCenterCrop()
                    .into(holder.mainThumbNailImageView);

        } else {
            Glide.with(holder.mainThumbNailImageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(CommonString.CommonStringInterface.FileBaseUrl + clubModel.allUrls.get(0))
                    .thumbnail(0.3f)
                    .optionalCenterCrop()
                    .into(holder.mainThumbNailImageView);
        }


        holder.titleTextView.setText(clubModel.title);
        holder.mainTitleTextView.setText(clubModel.title);
        holder.joinCountTextView.setText("참여가능 인원 : " + clubModel.limitJoinCount);
        holder.ageCountTextView.setText("참여 가능 연령대 : " + clubModel.minAge + "세 ~ " + clubModel.maxAge + "세");
        holder.clubIntroduceTextView.setText(clubModel.clubIntroduce);
        holder.expectedMoneyTextView.setText("예상 경비 : ₩ " + moneyFormat.format(clubModel.expectedMoney));
        adapter = new ClubIntroduceFileAdapter(context);
        adapter.setItems(clubModel.allUrls);
        holder.imageOrVideoRecyclerView.setAdapter(adapter);
        PagerSnapHelper pagerSnapHelper =new PagerSnapHelper();
        if (holder.imageOrVideoRecyclerView.getOnFlingListener() == null)
            pagerSnapHelper.attachToRecyclerView(holder.imageOrVideoRecyclerView);

        if (clubModel.userId.equals(myId)) {
            holder.joinButton.setText("참여 리스트 보러가기");
            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JoinClubPeopleListActivity.class);
                    intent.putExtra("club_Uuid", clubModel.club_Uuid);
                    intent.putExtra("owner", clubModel.user_Index);
                    intent.putExtra("ownerId", clubModel.userId);
                    context.startActivity(intent);
                }
            });

        } else {

            if (clubModel.myJoinInfo != null) {

                if (clubModel.myJoinInfo.isEmpty()) {
                    holder.joinButton.setBackgroundResource(R.drawable.drawable_gradation_background_orange_rectangle);
                    holder.joinButton.setText("참여하기");
                    holder.joinButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            androidx.appcompat.app.AlertDialog.Builder cautionAlert = new androidx.appcompat.app.AlertDialog.Builder(context);
                            View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
                            cautionAlert.setView(cautionView);
                            AppCompatImageView imageView = cautionView.findViewById(R.id.imageView);
                            imageView.setVisibility(View.GONE);
                            AppCompatTextView textView = cautionView.findViewById(R.id.mainTextView);
                            textView.setText("");
                            AppCompatTextView subTextView = cautionView.findViewById(R.id.subTextView);
                            subTextView.setText("클럽에 참여하시겠습니까?");
                            Button reportButton = cautionView.findViewById(R.id.reportButton);
                            reportButton.setVisibility(View.GONE);
                            Button okButton = cautionView.findViewById(R.id.okButton);
                            okButton.setText("확인");
                            Button cancelButton = cautionView.findViewById(R.id.cancelButton);
                            androidx.appcompat.app.AlertDialog alertDialog = cautionAlert.create();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                                    alertDialog.show();
                                }
                            });
                            okButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickInterface.joinClick(holder.joinButton, clubModel, holder.getAbsoluteAdapterPosition());
                                    alertDialog.dismiss();
                                }
                            });

                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });

                } else {


                    for (Map<String, Object> myInfoMap : clubModel.myJoinInfo) {
                        if (myInfoMap.get("userId").equals(myId)) {
                            if (myInfoMap.get("requestResult") != null) {
                                if (myInfoMap.get("requestResult").equals("Y")) {
                                    holder.joinButton.setText("참여완료");
                                    holder.joinButton.setBackgroundResource(R.drawable.drawable_gradation_background_orange);
                                    holder.joinButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(context, JoinClubPeopleListActivity.class);
                                            intent.putExtra("club_Uuid", clubModel.club_Uuid);
                                            intent.putExtra("owner", clubModel.user_Index);
                                            intent.putExtra("ownerId", clubModel.userId);
                                            context.startActivity(intent);
                                        }
                                    });


                                } else {

                                    if (myInfoMap.get("requestResult").equals("R")) {
                                        holder.joinButton.setVisibility(View.GONE);

                                    } else {

                                        if (clubModel.limitJoinCount == clubModel.currentSumJoinCount) {
                                            holder.joinButton.setText("모집 마감");
                                            holder.joinButton.setBackgroundResource(R.drawable.drawable_fulljoincount_gradation);
                                            holder.joinButton.setOnClickListener(null);
                                        } else {

                                            holder.joinButton.setText("수락 대기 중...");
                                            holder.joinButton.setBackgroundResource(R.drawable.drawable_accepdted_gradation);
                                            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(context, JoinClubPeopleListActivity.class);
                                                    intent.putExtra("club_Uuid", clubModel.club_Uuid);
                                                    intent.putExtra("owner", clubModel.user_Index);
                                                    intent.putExtra("ownerId", clubModel.userId);
                                                    context.startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }


        holder.blockImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
                View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
                cautionAlert.setView(cautionView);


                Button reportButton = cautionView.findViewById(R.id.reportButton);
                Button okButton = cautionView.findViewById(R.id.okButton);
                Button cancelButton = cautionView.findViewById(R.id.cancelButton);
                AlertDialog alertDialog = cautionAlert.create();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                        alertDialog.show();
                    }
                });
                reportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        reportDialog(clubModel);
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickInterface.foldFunction(clubModel, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        try {
            StringTokenizer tokenizer = new StringTokenizer(clubModel.hashTagList, ",");
            while (tokenizer.hasMoreTokens()) {
                Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
                chip.setText(tokenizer.nextToken());
                FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                holder.hashTagChipGroup.addView(chip, layoutParams);
            }

            JSONObject jsonObject = new JSONObject(clubModel.place);
            holder.locationTextView.setText(jsonObject.get("place_name").toString());
            holder.lookMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Bind state
        if (mFoldStates.containsKey(position)) {
            if (mFoldStates.get(position) == Boolean.TRUE) {
                if (!holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.foldWithoutAnimation();

                }
            } else if (mFoldStates.get(position) == Boolean.FALSE) {
                if (holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.unfoldWithoutAnimation();
                }
            }
        } else {
            holder.mFoldableLayout.foldWithoutAnimation();
        }


        holder.mFoldableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*clickInterface.foldFunction();*/
                if (holder.mFoldableLayout.isFolded()) {

                    holder.mFoldableLayout.unfoldWithAnimation();
                    holder.mFoldableLayout.getDetailView().requestFocus();
                    View childView = holder.imageOrVideoRecyclerView.getChildAt(0);
                    if (childView != null) {
                        ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                        if (!viewHolder.imageVideoView.isPlaying()) {
                            try {
                                viewHolder.imageVideoView.requestFocus();
                                viewHolder.imageVideoView.playVideo();
                                viewHolder.playButton.setVisibility(View.GONE);
                            } catch (Exception e) {

                            }
                        }
                    }

                } else {

                    holder.mFoldableLayout.foldWithAnimation();
                    View childView = holder.imageOrVideoRecyclerView.getChildAt(0);
                    if (childView != null) {
                        ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                        if (viewHolder.imageVideoView.isPlaying()) {
                            viewHolder.imageVideoView.pauseVideo();
                        }
                    }
                }
            }
        });

        holder.wholeRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.mFoldableLayout.isFolded()) {
                    holder.mFoldableLayout.foldWithAnimation();
                    View childView = holder.imageOrVideoRecyclerView.getChildAt(0);
                    if (childView != null) {
                        ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                        if (viewHolder.imageVideoView.isPlaying()) {
                            viewHolder.imageVideoView.pauseVideo();
                        }
                    }
                }
            }
        });
        holder.mFoldableLayout.setFoldListener(new FoldableLayout.FoldListener() {
            @Override
            public void onUnFoldStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(10);
                }
            }

            @Override
            public void onUnFoldEnd() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(0);
                }
                mFoldStates.put(holder.getBindingAdapterPosition(), false);
            }

            @Override
            public void onFoldStart() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(10);
                }
            }

            @Override
            public void onFoldEnd() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.mFoldableLayout.setElevation(0);
                }
                mFoldStates.put(holder.getBindingAdapterPosition(), true);
            }
        });

    }


    @Override
    public int getItemCount() {
        return clubModelList == null ? 0 : clubModelList.size();
    }

    public class ClubListViewHolder extends RecyclerView.ViewHolder {
        protected FoldableLayout mFoldableLayout;
        public RecyclerView imageOrVideoRecyclerView;
        private ChipGroup hashTagChipGroup;
        private AppCompatTextView titleTextView;
        private AppCompatTextView joinCountTextView;
        private AppCompatTextView locationTextView;
        private AppCompatButton lookMapButton;
        private AppCompatTextView clubIntroduceTextView;
        private AppCompatTextView ageCountTextView;
        private AppCompatTextView expectedMoneyTextView;
        private AppCompatButton joinButton;
        private AppCompatImageView mainThumbNailImageView;
        private AppCompatTextView mainTitleTextView;
        private AppCompatImageButton blockImageButton;
        private RelativeLayout wholeRelativeLayout;
        private View parent;
        private CardView wholeCardViewLayout;

        public ClubListViewHolder(FoldableLayout foldableLayout) {
            super(foldableLayout);
            parent = foldableLayout;
            parent.setTag(this);
            foldableLayout.setupViews(R.layout.adapter_clublist_item_cover, R.layout.adapter_clublist_itme_detail, R.dimen.main_card_cover_height, itemView.getContext());
            wholeCardViewLayout = foldableLayout.findViewById(R.id.wholeCardViewLayout);
            mFoldableLayout = foldableLayout;
            blockImageButton = foldableLayout.findViewById(R.id.blockImageButton);
            expectedMoneyTextView = foldableLayout.findViewById(R.id.expectedMoneyTextView);
            wholeRelativeLayout = foldableLayout.findViewById(R.id.wholeRelativeLayout);
            imageOrVideoRecyclerView = foldableLayout.findViewById(R.id.imageOrVideoRecyclerView);
            hashTagChipGroup = foldableLayout.findViewById(R.id.hashTagChipGroup);
            mainTitleTextView = foldableLayout.findViewById(R.id.mainTitleTextView);
            titleTextView = foldableLayout.findViewById(R.id.titleTextView);
            joinCountTextView = foldableLayout.findViewById(R.id.joinCountTextView);
            locationTextView = foldableLayout.findViewById(R.id.locationTextView);
            lookMapButton = foldableLayout.findViewById(R.id.lookMapButton);
            mainThumbNailImageView = foldableLayout.findViewById(R.id.mainThumbNailImageView);
            clubIntroduceTextView = foldableLayout.findViewById(R.id.clubIntroduceTextView);
            ageCountTextView = foldableLayout.findViewById(R.id.ageCountTextView);
            joinButton = foldableLayout.findViewById(R.id.joinButton);
        }
    }
}
