package com.cross.juntalk2.fourth;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterBulletinboardItemBinding;
import com.cross.juntalk2.diffutil.ImageOrVideoDiffUtil;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RefreshInterface;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.ImageControlActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BulletinBoardAdapter extends ListAdapter<BulletinBoardModel, BulletinBoardAdapter.BulletinBoardViewHolder> {
    private Context context;
    private Activity activity;
    private RetrofitService service;
    private Handler handler;
    private final SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private MyModel myModel;
    private RefreshInterface refreshInterface;
    private LinkedHashMap<String, Object> clickMap;
    private BulletinBoardViewHolder bulletinBoardViewHolder;
    private String blockContent = "null";
    private FourthFragment.RecyclerViewClickInterface recyclerViewClickInterface;

    public interface ReplyClickInterface {
        void reFresh(int size, int position);
    }

    private ReplyClickInterface replyClickInterface;

    public void setRefreshInterface(RefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    public void setRecyclerViewClickInterface(FourthFragment.RecyclerViewClickInterface recyclerViewClickInterface) {
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    public BulletinBoardAdapter(@NonNull @NotNull DiffUtil.ItemCallback<BulletinBoardModel> diffCallback, Context context, Activity activity) {
        super(diffCallback);
        this.context = context;
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        myModel = JunApplication.getMyModel();
        if (context instanceof LilsVideoListAdapter.ReplyClickInterface) {
            replyClickInterface = (ReplyClickInterface) context;
        }
        if (service == null) {
            service = RetrofitClient.getServerInterface();
        }
        clickMap = new LinkedHashMap<>();

    }

    @NonNull
    @NotNull
    @Override
    public BulletinBoardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterBulletinboardItemBinding binding = AdapterBulletinboardItemBinding.inflate(inflater, parent, false);
        binding.setBulletinBoardAdapter(this);
        bulletinBoardViewHolder = new BulletinBoardViewHolder(binding);
        return bulletinBoardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BulletinBoardViewHolder holder, int position) {
        int currentPosition = holder.getBindingAdapterPosition();
        if (currentPosition > getItemCount() - 1) {
            currentPosition = getItemCount() - 1;
        }
        holder.onBind(getItem(currentPosition), currentPosition);

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                try {

                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);

                        if (bulletinBoardViewHolder != null) {
                            int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {

                            } else {
                                ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                                if (imageOrVideoViewHolder == null) {

                                } else {
                                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
//                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {

                        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        recyclerViewClickInterface.clickPosition(firstVisibleItem);

                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (bulletinBoardViewHolder != null) {
                            firstVisibleItem = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {
                            } else {
                                ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                                if (imageOrVideoViewHolder == null) {
                                } else {
                                    imageOrVideoViewHolder.binding.imageVideoView.playVideo();
                                    imageOrVideoViewHolder.binding.playImageView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("abc", "error : " + e.getMessage());
                }
            }

        });
    }


    public void userPhotoMove(View view, String imageUrl) {
        Intent intent = new Intent(context, ImageControlActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_in);
    }


    public void blockImageButton(View view, BulletinBoardModel bulletinBoardModel, int position) {

        if (bulletinBoardModel.userId.equals(myModel.userId)) {

            AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
            View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_bulletinboard, null, false);
            cautionAlert.setView(cautionView);
            Button deleteButton = cautionView.findViewById(R.id.deleteButton);
            Button editButton = cautionView.findViewById(R.id.editButton);
            AlertDialog alertDialog = cautionAlert.create();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });

            deleteButton.setOnClickListener(v -> api_DeleteBulletinBoard(bulletinBoardModel, alertDialog, position));

            editButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, CreateBulletinBoardItemActivity.class);
                intent.putExtra("bulletin_Uuid", bulletinBoardModel.bulletin_Uuid);
                intent.putExtra("type", "update");
                context.startActivity(intent);
                alertDialog.dismiss();
            });


        } else {


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
            reportButton.setOnClickListener(v -> reportDialog(bulletinBoardModel, alertDialog, position));
            okButton.setOnClickListener(v -> api_Block(bulletinBoardModel, alertDialog, position));
            cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        }
    }

    public void reportDialog(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog, int position) {
        android.app.AlertDialog.Builder cautionAlert = new android.app.AlertDialog.Builder(context);
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
        android.app.AlertDialog reportAlertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                reportAlertDialog.getWindow().setGravity(Gravity.BOTTOM);
                reportAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                reportAlertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                reportAlertDialog.show();
            }
        });
        okButton.setOnClickListener(v -> {

            if (blockContent.replace(" ", "").trim().equals("")) {
                Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();

            } else {
                reportAlertDialog.dismiss();
                try {
                    api_Block(bulletinBoardModel, alertDialog, position);
                } catch (Exception e) {

                }
            }

        });

        cancelButton.setOnClickListener(v -> reportAlertDialog.dismiss());
    }


    public void api_DeleteBulletinBoard(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog, int position) {
        service.deleteBulletinBoard(CommonString.bulletinBoardController, bulletinBoardModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ImageOrVideoAdapter.ImageOrVideoViewHolder viewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                            if (viewHolder != null) {
                                if (viewHolder.binding.imageVideoView != null) {
                                    viewHolder.binding.imageVideoView.pauseVideo();
                                }
                            }
                            refreshInterface.refresh();
                            alertDialog.dismiss();
                        }
                    });
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
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


    public void api_heartClick(String bulletin_Uuid, String userId, int position, int bulletin_LikeCount) {
        service.clickLike(CommonString.bulletinBoardController, bulletin_Uuid, userId, myModel.userId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response result : " + response.toString());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!result) {

                                getItem(position).bulletin_LikeCount = (bulletin_LikeCount + 1);
                                getItem(position).bulletin_isUserLike = true;
                            } else {

                                getItem(position).bulletin_LikeCount = (bulletin_LikeCount - 1);
                                getItem(position).bulletin_isUserLike = false;
                            }
                            notifyItemChanged(position);
                            notifyDataSetChanged();

                        }
                    });

                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void setThemeAnimation(AppCompatImageButton imageButton, int fromColor, int toColor) {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(animator -> {
            imageButton.setColorFilter((int) animator.getAnimatedValue());
        });
        valueAnimator.start();
    }


    public void api_Block(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog, int position) {

        Map<String, Object> blockInfoMap = new HashMap<>();
        blockInfoMap.put("userId", myModel.userId);
        blockInfoMap.put("user_Index", myModel.user_Index);
        blockInfoMap.put("bulletin_Uuid", bulletinBoardModel.bulletin_Uuid);
        blockInfoMap.put("category", bulletinBoardModel.category);
        blockInfoMap.put("bulletin_UserId", bulletinBoardModel.userId);
        blockInfoMap.put("isBlock", true);
        blockInfoMap.put("blockReportContent", blockContent);
        service.blockBulletinBoard(CommonString.bulletinBoardController, blockInfoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ImageOrVideoAdapter.ImageOrVideoViewHolder viewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                            if (viewHolder != null) {
                                if (viewHolder.binding.imageVideoView != null) {
                                    viewHolder.binding.imageVideoView.pauseVideo();
                                }
                            }
                            refreshInterface.refresh();
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void api_BulletinCommentList(String bulletin_Uuid, int position) {
        service.bulletinCommentList(CommonString.bulletinBoardController, bulletin_Uuid, myModel.user_Index).enqueue(new Callback<List<BulletinCommentModel>>() {
            @Override
            public void onResponse(Call<List<BulletinCommentModel>> call, Response<List<BulletinCommentModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "response body : " + response.body().size());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showCommentList(response.body(), bulletin_Uuid, position);
                        }
                    });
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<List<BulletinCommentModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void showCommentList(List<BulletinCommentModel> bulletinCommentModels, String bulletin_Uuid, int position) {
        DialogCustomCommentList dialogCustomCommentList = new DialogCustomCommentList(context, bulletinCommentModels, bulletin_Uuid);
        dialogCustomCommentList.setReplyClickInterface(replyClickInterface);
        dialogCustomCommentList.setCurrentPosition(position);
        dialogCustomCommentList.setCancelable(true);
        dialogCustomCommentList.getWindow().setGravity(Gravity.BOTTOM);
        dialogCustomCommentList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        handler.post(new Runnable() {
            @Override
            public void run() {
                dialogCustomCommentList.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                dialogCustomCommentList.show();

            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull @NotNull BulletinBoardViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.adapter.imageOrVideoViewHolder != null) {
            ImageOrVideoAdapter.ImageOrVideoViewHolder viewHolder = holder.adapter.imageOrVideoViewHolder;
            if (viewHolder != null) {
                ImageVideoView imageVideoView = viewHolder.binding.imageVideoView;
                if (imageVideoView != null) {
                    imageVideoView.releasePlayer();
                }
            }
        }
    }

    public int getDp(int a) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, context.getResources().getDisplayMetrics());
        return dp;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_bulletinboard_item;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    public class BulletinBoardViewHolder extends RecyclerView.ViewHolder {
        public AdapterBulletinboardItemBinding binding;
        public ImageOrVideoAdapter adapter;
        private View parent;

        public BulletinBoardViewHolder(@NonNull @NotNull AdapterBulletinboardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            parent = binding.getRoot();
        }


        public void onBind(BulletinBoardModel bulletinBoardViewModel, int position) {
            try {

                binding.setBulletinBoardViewHolder(this);
                binding.setBulletinBoardModel(bulletinBoardViewModel);
                binding.setPosition(position);
                adapter = new ImageOrVideoAdapter(new ImageOrVideoDiffUtil(), context);
                adapter.setHasStableIds(true);
                binding.imageOrVideoRecyclerView.setAdapter(adapter);
                binding.imageOrVideoRecyclerView.getLayoutParams().height = getDp(350);
                if (bulletinBoardViewModel.allUrls.size() == 0) {
                    binding.imageOrVideoRecyclerView.getLayoutParams().height = 0;
                } else {
                    binding.imageOrVideoRecyclerView.getLayoutParams().height = getDp(350);
                }

                RecyclerView.ItemAnimator animator = binding.imageOrVideoRecyclerView.getItemAnimator();
                if (animator instanceof SimpleItemAnimator) {
                    ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
                }
                PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                if (binding.imageOrVideoRecyclerView.getOnFlingListener() == null)
                    pagerSnapHelper.attachToRecyclerView(binding.imageOrVideoRecyclerView);


                binding.executePendingBindings();
                parent.setTag(this);
                replyClickInterface = new ReplyClickInterface() {
                    @Override
                    public void reFresh(int size, int position) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                getItem(position).bulletin_CommentCount = size;
                                notifyItemChanged(position);
                                notifyDataSetChanged();

                            }
                        });

                    }
                };

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
    }
}
