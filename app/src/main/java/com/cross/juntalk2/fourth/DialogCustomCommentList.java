package com.cross.juntalk2.fourth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogCustomcommentlistBinding;
import com.cross.juntalk2.first.UserBulletinBoardAdapter;
import com.cross.juntalk2.model.BulletinCommentModel;
import com.cross.juntalk2.model.CommentModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.JunApplication;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogCustomCommentList extends Dialog {

    private DialogCustomcommentlistBinding binding;
    private Context context;
    private String bulletin_Uuid;
    private List<BulletinCommentModel> bulletinCommentModels;
    private DialogCustomCommentListAdapter adapter;
    private MyModel myModel;
    private RetrofitService service;
    private Observer<List<BulletinCommentModel>> observer;
    private ViewModelFactory viewModelFactory;
    private Handler handler;
    private BulletinBoardAdapter.ReplyClickInterface replyClickInterface;
    private UserBulletinBoardAdapter.ReplyClickInterface user_replyClickInterface;
    private int currentPosition;

    public interface CommentListClickInterface {
        void report(BulletinCommentModel commentModel, String blockReportContent, AlertDialog alertDialog);

        void block(BulletinCommentModel commentModel, String blockReportContent, AlertDialog alertDialog);

    }


    @Override
    public void dismiss() {
        super.dismiss();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private CommentListClickInterface commentListClickInterface;

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setUser_replyClickInterface(UserBulletinBoardAdapter.ReplyClickInterface user_replyClickInterface) {
        this.user_replyClickInterface = user_replyClickInterface;
    }

    public void setReplyClickInterface(BulletinBoardAdapter.ReplyClickInterface replyClickInterface) {
        this.replyClickInterface = replyClickInterface;
    }


    public DialogCustomCommentList(@NonNull Context context, List<BulletinCommentModel> bulletinCommentModels, String bulletin_Uuid) {
        super(context);
        this.context = context;
        this.bulletin_Uuid = bulletin_Uuid;
        this.bulletinCommentModels = bulletinCommentModels;
        myModel = JunApplication.getMyModel();
        handler = new Handler(Looper.getMainLooper());
        if (context instanceof CommentListClickInterface) {
            commentListClickInterface = (CommentListClickInterface) context;
        }
        commentListClickInterface = new CommentListClickInterface() {
            @Override
            public void report(BulletinCommentModel bulletinCommentModel, String blockReportContent, AlertDialog alertDialog) {
                insertBlockCommentList(bulletinCommentModel, blockReportContent, alertDialog);
            }

            @Override
            public void block(BulletinCommentModel bulletinCommentModel, String blockReportContent, AlertDialog alertDialog) {
                insertBlockCommentList(bulletinCommentModel, blockReportContent, alertDialog);
            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_customcommentlist, null, false);
        setContentView(binding.getRoot());
        init();
    }

    public void init() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        viewModelFactory = new ViewModelFactory();
        adapter = new DialogCustomCommentListAdapter(context);

        adapter.setItems(bulletinCommentModels);
        binding.bulletinCommentListRecyclerView.setAdapter(adapter);
        binding.bulletinCommentListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        observer = new Observer<List<BulletinCommentModel>>() {
            @Override
            public void onChanged(List<BulletinCommentModel> bulletinCommentModels) {
                adapter.setItems(bulletinCommentModels);
            }
        };

        //TODO : 다이얼로그 넓이와 높이 동적 조정
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        addEventListener();


        adapter.setCommentListClickInterface(commentListClickInterface);
    }

    public void addEventListener() {
        binding.okButton.setOnClickListener(v -> dismiss());
        binding.writeResponseImageButton.setOnClickListener(v -> {
            if (binding.idTextInputEditTextView.getText().toString() != null && !binding.idTextInputEditTextView.getText().toString().equals("")) {
                String replyMessage = binding.idTextInputEditTextView.getText().toString();
                if (replyMessage == null && replyMessage.equals("") || replyMessage.equals("null")) {

                } else {
                    BulletinCommentModel bulletinCommentModel = new BulletinCommentModel();
                    bulletinCommentModel.userComment = replyMessage;
                    bulletinCommentModel.userMainPhoto = myModel.userMainPhoto;
                    bulletinCommentModel.user_Index = myModel.user_Index;
                    bulletinCommentModel.bulletin_Uuid = bulletin_Uuid;
                    bulletinCommentModel.userId = myModel.userId;
                    bulletinCommentModels.add(bulletinCommentModel);
                    api_InsertBulletinBoardComment(bulletinCommentModel);
                    binding.idTextInputEditTextView.setText("");
                }

            }


        });
    }


    public void insertBlockCommentList(BulletinCommentModel bulletinCommentModel, String blockReportContent, AlertDialog alertDialog) {
        Map<String, Object> map = new HashMap<>();
        map.put("bulletin_Uuid", bulletinCommentModel.bulletin_Uuid);
        map.put("user_Index", bulletinCommentModel.user_Index);
        map.put("userId", bulletinCommentModel.userId);
        map.put("blockReportContent", blockReportContent);
        map.put("fromUser_Index", myModel.user_Index);
        map.put("fromUser", myModel.userId);
        service.insertBulletinBoard_CommentList(CommonString.bulletinBoardController, map).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            Iterator<BulletinCommentModel> commentModelIterator = bulletinCommentModels.listIterator();
                            while (commentModelIterator.hasNext()) {
                                BulletinCommentModel mainBulletinCommentModel = commentModelIterator.next();
                                if (mainBulletinCommentModel.userId.equals(bulletinCommentModel.userId)) {
                                    commentModelIterator.remove();
                                }
                            }
                            adapter.setItems(bulletinCommentModels);

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

    public void api_InsertBulletinBoardComment(BulletinCommentModel bulletinCommentModel) {
        service.insertBulletinBoardComment(CommonString.bulletinBoardController, bulletinCommentModel).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    api_BulletinCommentList(bulletin_Uuid);
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

    public void api_BulletinCommentList(String bulletin_Uuid) {
        service.bulletinCommentList(CommonString.bulletinBoardController, bulletin_Uuid, myModel.user_Index).enqueue(new Callback<List<BulletinCommentModel>>() {
            @Override
            public void onResponse(Call<List<BulletinCommentModel>> call, Response<List<BulletinCommentModel>> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "response body : " + response.body().size());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.idTextInputEditTextView.setText("");
                            adapter.setItems(response.body());
                            if (user_replyClickInterface != null) {
                                user_replyClickInterface.reFresh(response.body().size(), currentPosition);
                            }
                            if (replyClickInterface != null) {
                                replyClickInterface.reFresh(response.body().size(), currentPosition);
                            }

                            binding.bulletinCommentListRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

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

}
