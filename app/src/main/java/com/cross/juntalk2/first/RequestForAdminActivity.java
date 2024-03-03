package com.cross.juntalk2.first;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityRequestForAdminBinding;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.datatransport.cct.internal.LogEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestForAdminActivity extends CreateNewCompatActivity {

    private ActivityRequestForAdminBinding binding;
    private RequestForAdminAdapter adapter;
    private Handler handler;
    private RetrofitService service;
    private MyModel myModel = JunApplication.getMyModel();
    private boolean paging = false;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private int pageNum = 0;

    @Override
    public void getInterfaceInfo() {
        binding = DataBindingUtil.setContentView(RequestForAdminActivity.this, R.layout.activity_request_for_admin);
        adapter = new RequestForAdminAdapter(context);
        binding.requestRecyclerView.setAdapter(adapter);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        binding.requestRecyclerView.setAnimation(alphaAnimation);
        binding.requestRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void getIntentInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        handler = new Handler(Looper.getMainLooper());
    }

    public void insertRequestContent(String requestContent, DialogInterface dialog) {
        int user_Index = myModel.user_Index;
        String userId = myModel.userId;
        service.sendRequestQuestionList(CommonString.commonNoticeController, user_Index, userId, requestContent).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    Map<String, Object> addMap = new HashMap<>();
                    addMap.put("requestContent", requestContent);
                    addMap.put("user_Index", user_Index);
                    addMap.put("userId", userId);
                    addMap.put("regDate", format.format(new Date().getTime()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addItem(addMap);
                            binding.emptyTextView.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void requestQuestionPaging(int pageNum) {
        service.requestQuestionList(CommonString.commonNoticeController, pageNum).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    List<Map<String, Object>> requestQuestionMap = response.body();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (requestQuestionMap.isEmpty()) {

                                paging = false;
                            } else {
                                adapter.setRequestMapList(requestQuestionMap);
                                paging = true;
                            }

                            if (adapter.getItemCount() == 0) {
                                binding.emptyTextView.setVisibility(View.VISIBLE);
                            } else {
                                binding.emptyTextView.setVisibility(View.GONE);
                            }

                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    @Override
    public void init() {
        pageNum = 0;
        adapter.clearItems();
        requestQuestionPaging(pageNum);

    }


    @Override
    public void createThings() {
        binding.noticeTextView.setSingleLine(true);    // 한줄로 표시하기
        binding.noticeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
        binding.noticeTextView.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    @Override
    public void clickEvent() {

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 0;
                adapter.clearItems();
                requestQuestionPaging(pageNum);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        binding.requestRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (paging && lastVisibleItem == adapter.getItemCount() - 1) {
                    pageNum++;
                    requestQuestionPaging(pageNum);
                    paging = false;
                }
            }
        });


        binding.closeImageButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
        });

        binding.optionImageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.juntalk_logo);
            builder.setMessage("건의사항을 적어주세요.");
            AppCompatEditText editText = new AppCompatEditText(context);
            editText.setHint("건의사항을 적어주세요.");
            editText.setTextColor(Color.BLACK);
            builder.setView(editText);

            builder.setPositiveButton("입력완료", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String requestContent = editText.getText().toString();
                    insertRequestContent(requestContent, dialog);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });


        });
    }

    @Override
    public void getServer() {

    }
}