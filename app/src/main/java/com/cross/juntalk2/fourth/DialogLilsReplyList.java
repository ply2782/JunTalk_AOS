package com.cross.juntalk2.fourth;

import androidx.appcompat.app.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogLilsReplyBinding;
import com.cross.juntalk2.databinding.DialogMymadeclublistBinding;
import com.cross.juntalk2.fifth.DialogMyMadeClubList;
import com.cross.juntalk2.fifth.Top10ClubListAdapter;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.LilsReplyModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.VideoListModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.JunApplication;
import com.loopeer.cardstack.CardStackView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogLilsReplyList extends Dialog {
    private DialogLilsReplyBinding binding;
    private Context context;
    private RetrofitService service;
    private Handler handler;
    private StringBuilder builder;
    private LilsReplyListAdapter adapter;
    private MyModel myModel;
    private LilsVideoListAdapter.ReplyClickInterface replyClickInterface;
    private MyLilsVideoAdapter.ReplyClickInterface my_replyClickInterface;
    private String lils_Uuid;
    private int lils_Index;
    private List<LilsReplyModel> lilsReplyModels;
    private int currentPosition;
    private SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat mainRegDateFormat = new SimpleDateFormat("yyyy-MM-dd E요일");

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setLilsReplyModels(List<LilsReplyModel> lilsReplyModels) {
        if (lilsReplyModels == null) return;
        this.lilsReplyModels = lilsReplyModels;
    }

    public void setLils_Uuid(String lils_Uuid) {
        this.lils_Uuid = lils_Uuid;
    }

    public void setLils_Index(int lils_Index) {
        this.lils_Index = lils_Index;
    }

    public void setReplyClickInterface(LilsVideoListAdapter.ReplyClickInterface replyClickInterface) {
        this.replyClickInterface = replyClickInterface;
    }

    public void setMy_replyClickInterface(MyLilsVideoAdapter.ReplyClickInterface my_replyClickInterface) {
        this.my_replyClickInterface = my_replyClickInterface;
    }

    public interface ClickInterface {
        void report(LilsReplyModel lilsReplyModel, String blockContent, AlertDialog alertDialog);

        void block(LilsReplyModel lilsReplyModel, AlertDialog alertDialog);
    }

    private ClickInterface clickInterface;

    public DialogLilsReplyList(@NonNull Context context) {
        super(context);
        this.context = context;
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        handler = new Handler(Looper.getMainLooper());
        builder = new StringBuilder();
        myModel = JunApplication.getMyModel();
        if (context instanceof ClickInterface) {
            clickInterface = (ClickInterface) context;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_lils_reply, null, false);
        setContentView(binding.getRoot());
        init();
        clickEvent();
    }

    public void init() {
        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point deviceSize = new Point();
        display.getSize(deviceSize);
        getWindow().getAttributes().width = deviceSize.x;
        getWindow().getAttributes().gravity = Gravity.BOTTOM;

        adapter = new LilsReplyListAdapter(context);
        binding.replyListView.setAdapter(adapter);

        if (lilsReplyModels != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.setItems(lilsReplyModels);
                }
            });
        }

        clickInterface = new ClickInterface() {

            @Override
            public void report(LilsReplyModel lilsReplyModel, String blockContent, AlertDialog alertDialog) {
                reportLilsReplyList(lilsReplyModel, blockContent, alertDialog);
            }

            @Override
            public void block(LilsReplyModel lilsReplyModel, AlertDialog alertDialog) {
                blockLilsReplyList(lilsReplyModel, alertDialog);
            }
        };
        adapter.setClickInterface(clickInterface);
    }

    public void reportLilsReplyList(LilsReplyModel lilsReplyModel, String blockContent, AlertDialog alertDialog) {
        service.reportLilsReplyList(CommonString.bulletinBoardController, lilsReplyModel.userId, blockContent, lilsReplyModel.lils_Uuid, myModel.userId, myModel.user_Index).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                Iterator<LilsReplyModel> lilsReplyModelIterator = lilsReplyModels.listIterator();
                                while (lilsReplyModelIterator.hasNext()) {
                                    LilsReplyModel replyModel = lilsReplyModelIterator.next();
                                    if (replyModel.userId.equals(lilsReplyModel.userId)) {
                                        lilsReplyModelIterator.remove();
                                    }
                                }
                                adapter.setItems(lilsReplyModels);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void blockLilsReplyList(LilsReplyModel lilsReplyModel, AlertDialog alertDialog) {
        service.blockLilsReplyList(CommonString.bulletinBoardController, lilsReplyModel.userId, lilsReplyModel.lils_Uuid, myModel.userId, myModel.user_Index).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "차단되었습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                Iterator<LilsReplyModel> lilsReplyModelIterator = lilsReplyModels.listIterator();
                                while (lilsReplyModelIterator.hasNext()) {
                                    LilsReplyModel replyModel = lilsReplyModelIterator.next();
                                    if (replyModel.userId.equals(lilsReplyModel.userId)) {
                                        lilsReplyModelIterator.remove();
                                    }
                                }
                                adapter.setItems(lilsReplyModels);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    public void clickEvent() {
        binding.sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String replyMessage = binding.replyEditTextView.getText().toString();
                if (replyMessage == null || replyMessage.equals("")) {

                } else {
                    Map<String, Object> infoMap = new HashMap<>();
                    infoMap.put("userId", myModel.userId);
                    infoMap.put("user_Index", myModel.user_Index);
                    infoMap.put("userMainPhoto", myModel.userMainPhoto);
                    infoMap.put("replyMessage", replyMessage);
                    infoMap.put("lils_Index", lils_Index);
                    infoMap.put("lils_Uuid", lils_Uuid);


                    LilsReplyModel lilsReplyModel = new LilsReplyModel();
                    lilsReplyModel.userId = myModel.userId;
                    lilsReplyModel.userMainPhoto = myModel.userMainPhoto;
                    lilsReplyModel.replyMessage = replyMessage;
                    lilsReplyModel.lils_Index = lils_Index;
                    lilsReplyModel.lils_Uuid = lils_Uuid;
                    lilsReplyModel.regDate = today.format(new Date().getTime());
                    /*if (adapter.getCount() == 0) {
                        AppCompatTextView appCompatTextView = new AppCompatTextView(context);
                        appCompatTextView.setGravity(Gravity.CENTER);
                        Typeface typeface;
                        //api 26이상
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            typeface = context.getResources().getFont(R.font.seven_l);
                        } else {
                            //api 26 이하
                            typeface = Typeface.createFromAsset(context.getAssets(), "font/seven_l.ttf"); // font 폴더내에 있는 jua.ttf 파일을 typeface로 설정
                        }
                        appCompatTextView.setTypeface(typeface);
                        appCompatTextView.setTextSize(15f);
                        try {
                            Date parseDate = today.parse(lilsReplyModel.regDate);
                            String dateFormat = mainRegDateFormat.format(parseDate);
                            appCompatTextView.setText(dateFormat);
                            binding.replyListView.addHeaderView(appCompatTextView);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }*/
                    adapter.addItems(lilsReplyModel);

                    binding.replyListView.smoothScrollToPosition(adapter.getCount() - 1);
                    replyLilsVideo(infoMap);
                    binding.replyEditTextView.setText("");
                }


            }
        });
    }

    public void replyLilsVideo(Map<String, Object> infoMap) {

        service.lilsReply(CommonString.bulletinBoardController, infoMap).enqueue(new Callback<List<LilsReplyModel>>() {
            @Override
            public void onResponse(Call<List<LilsReplyModel>> call, Response<List<LilsReplyModel>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    List<LilsReplyModel> lilsReplyModels = response.body();
                    if (replyClickInterface != null) {
                        replyClickInterface.reFresh(lilsReplyModels.size(), currentPosition);
                    }
                    if (my_replyClickInterface != null) {
                        my_replyClickInterface.reFresh(lilsReplyModels.size(), currentPosition);
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<LilsReplyModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


}
