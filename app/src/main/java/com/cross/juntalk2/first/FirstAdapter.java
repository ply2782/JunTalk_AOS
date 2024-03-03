package com.cross.juntalk2.first;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.AdapterFirstBinding;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.ImageControlActivity;
import com.cross.juntalk2.utils.JunApplication;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstAdapter extends ListAdapter<FriendModel, FirstAdapter.FristViewHolder> {

    private final Context context;
    private final Activity activity;
    private RetrofitService service;
    private final Handler handler;
    private final MyModel myModel;
    private String blockContent = "";

    public FirstAdapter(@NonNull @NotNull DiffUtil.ItemCallback<FriendModel> diffCallback, Context context, Activity activity) {
        super(diffCallback);
        this.context = context;
        this.activity = activity;
        myModel = JunApplication.getMyModel();
        handler = new Handler(Looper.getMainLooper());
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

    }


    @Override
    public int getItemViewType(int position) {
        return R.layout.adapter_first;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void reportPersonApi(String blockReportUser, int position) {
        int friend_Index = getItem(position).user_Index;
        String friendUserId = getItem(position).userId;
        service.blockReportUser(CommonString.userController, myModel.user_Index, myModel.userId, friend_Index, friendUserId, blockReportUser).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    intent.setAction("FirstFragment");
                    intent.putExtra("blockUser", "blockUser");
                    context.sendBroadcast(intent);
                    handler.post(() -> Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show());
                } else {
                    handler.post(() -> Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void blockPersonApi(View view, int position) {
        AlertDialog.Builder cautionAlert = new AlertDialog.Builder(context);
        View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
        cautionAlert.setView(cautionView);
        AppCompatImageView imageView = cautionView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.ic_blockpeople);
        imageView.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        Button reportButton = cautionView.findViewById(R.id.reportButton);
        reportButton.setVisibility(View.VISIBLE);
        AppCompatTextView mainTextView = cautionView.findViewById(R.id.mainTextView);
        mainTextView.setText("차단하기 | 신고하기");
        AppCompatTextView subTextView = cautionView.findViewById(R.id.subTextView);
        subTextView.setText("해당 유저를 차단 | 신고하시겠습니까?");
        Button okButton = cautionView.findViewById(R.id.okButton);
        okButton.setText("차단하기");
        Button cancelButton = cautionView.findViewById(R.id.cancelButton);
        cancelButton.setText("취소하기");
        AlertDialog alertDialog = cautionAlert.create();
        handler.post(new Runnable() {
            @Override
            public void run() {

                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });

        reportButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            AlertDialog.Builder cautionAlert1 = new AlertDialog.Builder(context);
            View cautionView1 = LayoutInflater.from(context).inflate(R.layout.dialog_blockreport, null, false);
            cautionAlert1.setView(cautionView1);
            RadioGroup radioGroup = cautionView1.findViewById(R.id.radioGroupLayout);
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
            Button okButton1 = cautionView1.findViewById(R.id.reportButton);
            Button cancelButton1 = cautionView1.findViewById(R.id.cancelButton);
            AlertDialog alertDialog1 = cautionAlert1.create();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog1.getWindow().setGravity(Gravity.BOTTOM);
                    alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog1.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog1.show();
                }
            });
            okButton1.setOnClickListener(v1 -> {

                if (blockContent.replace(" ", "").trim().equals("")) {
                    Toast.makeText(context, "항목을 선택해주세요.", Toast.LENGTH_SHORT).show();

                } else {

                    try {
                        alertDialog1.dismiss();
                        reportPersonApi(blockContent, position);
                    } catch (Exception e) {

                    }
                }

            });


            cancelButton1.setOnClickListener(v1 -> alertDialog1.dismiss());
        });


        okButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            try {
                int friend_Index = getItem(position).user_Index;
                String friendUserId = getItem(position).userId;
                service.blockUser(
                        CommonString.userController,
                        myModel.user_Index,
                        myModel.userId,
                        friend_Index,
                        friendUserId
                ).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.e("abc", "" + response.raw());
                        if (response.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.setAction("FirstFragment");
                            intent.putExtra("blockUser", "blockUser");
                            context.sendBroadcast(intent);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "차단되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Log.e("abc", "error : " + t.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "현재 서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (Exception e) {

            }
        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

    }

    public void editImageButtonOnClickEvent(View view, int position) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", "friend");
        intent.putExtra("friendModel", getItem(position));
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }


    public void pictureClickEvent(View view, String imageUrl) {
        Intent intent = new Intent(context, ImageControlActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_in);
    }

    @NonNull
    @NotNull
    @Override
    public FristViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AdapterFirstBinding binding = AdapterFirstBinding.inflate(inflater, parent, false);
        binding.setFirstAdapter(this);
        return new FristViewHolder(binding);
    }

    @Override
    protected FriendModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FristViewHolder holder, int position) {
        holder.onBind(getItem(position), position);
    }

    public class FristViewHolder extends RecyclerView.ViewHolder {
        private AdapterFirstBinding binding;

        public FristViewHolder(@NonNull @NotNull AdapterFirstBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(FriendModel friendModel, int position) {
            binding.setFriendModel(friendModel);
            binding.setPosition(position);
            binding.executePendingBindings();

        }
    }
}
