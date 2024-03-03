package com.cross.juntalk2.first;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityRequestQuestionDetailBinding;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressWarnings("unchecked")
public class RequestQuestionDetailActivity extends CreateNewCompatActivity {


    private ActivityRequestQuestionDetailBinding binding;
    private String myId;
    private Handler handler;
    private RetrofitService service;
    private int user_Index, requestQuestion_Index;

    @Override
    public void getInterfaceInfo() {

        Map<String, Object> requestQuestionMap = (Map<String, Object>) getIntent().getSerializableExtra("requestQuestionMap");
        String userId = (String) requestQuestionMap.get("userId");
        myId = JunApplication.getMyModel().userId;

        if (requestQuestionMap.get("user_Index") instanceof Integer) {
            int user_IndexInteger = (int) requestQuestionMap.get("user_Index");
            user_Index = user_IndexInteger;
        } else if (requestQuestionMap.get("user_Index") instanceof Double) {
            double user_IndexDouble  = (double) requestQuestionMap.get("user_Index");
            user_Index = Integer.parseInt(String.valueOf(user_IndexDouble).replace(".0", ""));
        }

        if(requestQuestionMap.get("requestQuestion_Index") instanceof Integer){
            int requestQuestion_IndexInteger = (int) requestQuestionMap.get("requestQuestion_Index");
            requestQuestion_Index = requestQuestion_IndexInteger;
        }else if(requestQuestionMap.get("requestQuestion_Index") instanceof Double){
            double requestQuestion_IndexDouble  = (double) requestQuestionMap.get("requestQuestion_Index");
            requestQuestion_Index = Integer.parseInt(String.valueOf(requestQuestion_IndexDouble).replace(".0", ""));
        }


        String regDate = (String) requestQuestionMap.get("regDate");
        String requestContent = (String) requestQuestionMap.get("requestContent");
        String adminAnswer = (String) requestQuestionMap.get("adminAnswer");


        if (myId.equals("JunTalk")) {
            binding.adminAnswerButton.setVisibility(View.VISIBLE);
        } else {
            binding.adminAnswerButton.setVisibility(View.GONE);
        }
        binding.regDateTextView.setText("" + regDate);
        binding.contentTextView.setText("" + requestContent);
        if (adminAnswer != null && !adminAnswer.equals("null")) {
            binding.adminAnswerTextView.setText("" + adminAnswer);
            binding.adminAnswerWaitingLinearLayout.setVisibility(View.GONE);
            binding.answerCardView.setVisibility(View.VISIBLE);
        } else {
            binding.adminAnswerWaitingLinearLayout.setVisibility(View.VISIBLE);
            binding.answerCardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void getIntentInfo() {
        handler = new Handler(Looper.getMainLooper());
        binding = DataBindingUtil.setContentView(RequestQuestionDetailActivity.this, R.layout.activity_request_question_detail);

        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void createThings() {

    }

    @Override
    public void clickEvent() {
        binding.closeImageButton.setOnClickListener(v -> finish());

        binding.adminAnswerButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.juntalk_logo);
            builder.setMessage("답변을 적어주세요.");
            AppCompatEditText editText = new AppCompatEditText(context);
            editText.setHint("답변을 적어주세요.");
            editText.setTextColor(Color.BLACK);
            builder.setView(editText);

            builder.setPositiveButton("입력완료", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String requestContent = editText.getText().toString();
                    service.updateAdminAnswer(CommonString.commonNoticeController, user_Index, requestQuestion_Index, requestContent).enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Log.e("abc", "" + response.raw());
                            if (response.isSuccessful()) {
                                handler.post(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        binding.adminAnswerTextView.setText("" + requestContent);
                                        binding.adminAnswerWaitingLinearLayout.setVisibility(View.GONE);
                                        binding.answerCardView.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.e("abc", "error : " + t.getMessage());
                        }
                    });
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