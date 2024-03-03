package com.cross.juntalk2.first;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterUserbulletinboardItemBinding;
import com.cross.juntalk2.diffutil.ImageOrVideoDiffUtil;
import com.cross.juntalk2.fourth.CreateBulletinBoardItemActivity;
import com.cross.juntalk2.fourth.DialogCustomCommentList;
import com.cross.juntalk2.fourth.ImageVideoView;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RefreshInterface;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBulletinBoardAdapter extends ListAdapter<BulletinBoardModel, UserBulletinBoardAdapter.BulletinBoardViewHolder> {
    private Context context;
    private Activity activity;
    private RetrofitService service;
    private Handler handler;
    private SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private MyModel myModel;
    private RefreshInterface refreshInterface;
    private String blockContent = "";

    public interface ReplyClickInterface {
        void reFresh(int size, int position);
    }

    private ReplyClickInterface replyClickInterface;


    public void setRefreshInterface(RefreshInterface refreshInterface) {
        this.refreshInterface = refreshInterface;
    }

    public UserBulletinBoardAdapter(@NonNull @NotNull DiffUtil.ItemCallback<BulletinBoardModel> diffCallback, Context context, Activity activity) {
        super(diffCallback);
        this.context = context;
        this.activity = activity;
        handler = new Handler(Looper.getMainLooper());
        myModel = JunApplication.getMyModel();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        if (context instanceof ReplyClickInterface) {
            replyClickInterface = (ReplyClickInterface) context;
        }
    }

    @NonNull
    @NotNull
    @Override
    public BulletinBoardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterUserbulletinboardItemBinding binding = AdapterUserbulletinboardItemBinding.inflate(inflater, parent, false);
        return new BulletinBoardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BulletinBoardViewHolder holder, int position) {
        holder.onBind(getItem(position), holder.getBindingAdapterPosition());

    }


    /*@Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {

                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);

                    if (bulletinBoardViewHolder != null) {
                        int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {


                        } else {

                            UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                            if (imageOrVideoViewHolder == null) {

                            } else {
                                imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
//                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {


                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                    if (bulletinBoardViewHolder != null) {
                        firstVisibleItem = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {
                        } else {

                            UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                            if (imageOrVideoViewHolder == null) {
                            } else {
                                imageOrVideoViewHolder.binding.imageVideoView.playVideo();
                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        });
    }*/


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
                        UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (bulletinBoardViewHolder != null) {
                            int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {

                            } else {
                                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                                if (imageOrVideoViewHolder == null) {

                                } else {
                                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
//                                imageOrVideoViewHolder.binding.playImageView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                        if (bulletinBoardViewHolder != null) {
                            firstVisibleItem = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {
                            } else {
                                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
                                if (imageOrVideoViewHolder == null) {
                                } else {
                                    imageOrVideoViewHolder.binding.imageVideoView.playVideo();
                                    imageOrVideoViewHolder.binding.playImageView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }

        });
    }


    @Override
    public void onViewRecycled(@NonNull @NotNull BulletinBoardViewHolder holder) {
        super.onViewRecycled(holder);
        UserImageOrVideoAdapter.ImageOrVideoViewHolder viewHolder = holder.adapter.imageOrVideoViewHolder;
        if (viewHolder != null) {
            ImageVideoView imageVideoView = viewHolder.binding.imageVideoView;
            if (imageVideoView != null) {
                imageVideoView.releasePlayer();
            }
        }


    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_userbulletinboard_item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class BulletinBoardViewHolder extends RecyclerView.ViewHolder {
        public AdapterUserbulletinboardItemBinding binding;
        public UserImageOrVideoAdapter adapter;
        private View parent;

        public BulletinBoardViewHolder(@NonNull @NotNull AdapterUserbulletinboardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            parent = this.binding.getRoot();
        }


        public void onBind(BulletinBoardModel bulletinBoardViewModel, int position) {
            try {
                parent.setTag(this);
                binding.setBulletinBoardViewHolder(this);
                binding.setBulletinBoardModel(bulletinBoardViewModel);
                adapter = new UserImageOrVideoAdapter(new ImageOrVideoDiffUtil(), context);
                adapter.setHasStableIds(true);
                binding.imageOrVideoRecyclerView.setAdapter(adapter);
                binding.setPosition(position);
                binding.executePendingBindings();

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

            }
        }

        public void blockImageButton(View view, BulletinBoardModel bulletinBoardModel) {

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

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api_DeleteBulletinBoard(bulletinBoardModel, alertDialog);
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CreateBulletinBoardItemActivity.class);
                        intent.putExtra("bulletin_Uuid", bulletinBoardModel.bulletin_Uuid);
                        intent.putExtra("type", "update");
                        context.startActivity(intent);
                        alertDialog.dismiss();
                    }
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

                reportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportDialog(bulletinBoardModel, alertDialog);
                    }
                });

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        api_Block(bulletinBoardModel, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

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


        public void api_DeleteBulletinBoard(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog) {

            service.deleteBulletinBoard(CommonString.bulletinBoardController, bulletinBoardModel).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Log.e("abc", "response toString : " + response.toString());
                    if (response.isSuccessful()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter != null) {
                                    if (adapter.imageOrVideoViewHolder != null) {
                                        if (adapter.imageOrVideoViewHolder.binding.imageVideoView != null) {
                                            adapter.imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                                        }
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

        public void api_heartClick(String bulletin_Uuid, String userId, int position, int bulletin_LikeCount) {
            service.clickLike(CommonString.bulletinBoardController, bulletin_Uuid, userId, myModel.userId).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    Log.e("abc", "response result : " + response.toString());
                    if (response.isSuccessful()) {
                        boolean result = response.body();
                        Log.e("abc", "result : " + result);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (result == false) {
                                    getItem(position).bulletin_LikeCount = (bulletin_LikeCount + 1);
                                    getItem(position).bulletin_isUserLike = true;
//                                    setThemeAnimation(binding.heartImageButton, Color.BLACK, Color.RED);

                                } else {
                                    getItem(position).bulletin_LikeCount = (bulletin_LikeCount - 1);
                                    getItem(position).bulletin_isUserLike = false;
//                                    setThemeAnimation(binding.heartImageButton, Color.RED, Color.BLACK);

                                }
                                notifyItemChanged(position);
                                notifyDataSetChanged();
                                /*binding.setBulletinBoardModel(getItem(position));
                                binding.executePendingBindings();*/
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


        public void reportDialog(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog) {
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
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (blockContent.replace(" ", "").trim().equals("")) {
                        Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();

                    } else {
                        reportAlertDialog.dismiss();
                        try {
                            api_Block(bulletinBoardModel, alertDialog);
                        } catch (Exception e) {

                        }
                    }

                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportAlertDialog.dismiss();
                }
            });
        }


        public void api_Block(BulletinBoardModel bulletinBoardModel, AlertDialog alertDialog) {

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
                                if (adapter != null) {
                                    UserImageOrVideoAdapter.ImageOrVideoViewHolder viewHolder = adapter.imageOrVideoViewHolder;
                                    if (viewHolder != null) {
                                        ImageVideoView imageVideoView = viewHolder.binding.imageVideoView;
                                        if (imageVideoView != null) {
                                            imageVideoView.pauseVideo();
                                        }
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

            service.bulletinCommentList(CommonString.bulletinBoardController, bulletin_Uuid , myModel.user_Index).enqueue(new Callback<List<BulletinCommentModel>>() {
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
            dialogCustomCommentList.setCancelable(true);
            dialogCustomCommentList.setCurrentPosition(position);
            dialogCustomCommentList.setUser_replyClickInterface(replyClickInterface);
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
    }
}
