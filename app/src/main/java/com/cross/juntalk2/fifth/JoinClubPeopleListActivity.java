package com.cross.juntalk2.fifth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityJoinClubPeopleListBinding;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopeer.cardstack.CardStackView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinClubPeopleListActivity extends CreateNewCompatActivity {

    private ActivityJoinClubPeopleListBinding binding;
    private RetrofitService service;
    private JoinClubPeopleListAdapter adapter;
    private String club_Uuid, ownerId;
    private int user_Index;
    private Handler handler;
    private Gson gson;
    private Type type;
    private SimpleDateFormat notice_TodayFormat = new SimpleDateFormat("yy/MM/dd (E) aa HH:mm");
    private List<Map<String, Object>> map;

    public interface AcceptInterface {
        void accept(Map<String, Object> acceptMap, AppCompatButton button, int position);

        void reject(Map<String, Object> rejectMap, AppCompatButton button, int position);
    }

    private AcceptInterface acceptInterface;


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void getInterfaceInfo() {
        handler = new Handler(Looper.getMainLooper());
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        if (context instanceof AcceptInterface) {
            acceptInterface = (AcceptInterface) context;
        }
    }

    @Override
    public void getIntentInfo() {
        club_Uuid = getIntent().getStringExtra("club_Uuid");
        user_Index = getIntent().getIntExtra("owner", 0);
        ownerId = getIntent().getStringExtra("ownerId");
        type = new TypeToken<List<ClubModel>>() {
        }.getType();
        gson = new Gson();
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(JoinClubPeopleListActivity.this, R.layout.activity_join_club_people_list);
        adapter = new JoinClubPeopleListAdapter(context, user_Index);
        binding.peopleListRecyclerView.setAdapter(adapter);
        binding.peopleListRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void createThings() {
        acceptInterface = new AcceptInterface() {
            @Override
            public void accept(Map<String, Object> acceptMap, AppCompatButton button, int position) {
                acceptMap.put("updateResult", "Y");
                acceptMap.put("club_Uuid", club_Uuid);
                acceptMap.put("friendId", ownerId);
                acceptMap.put("friend_Index", user_Index);
                acceptMap.put("notice_RegDate", notice_TodayFormat.format(new Date().getTime()));
                service.updateRequestResult(CommonString.clubController, acceptMap).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.e("abc", "" + response.raw());
                        if (response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    for (Map<String, Object> mapCopy : map) {
                                        if (mapCopy.get("user_Index").equals(acceptMap.get("user_Index"))) {
                                            mapCopy.put("requestResult", "Y");
                                        }
                                    }
                                    adapter.setPeopleList(map);
                                    adapter.notifyItemChanged(position);
                                    button.setVisibility(View.VISIBLE);

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

            @Override
            public void reject(Map<String, Object> rejectMap, AppCompatButton button, int position) {
                rejectMap.put("updateResult", "R");
                rejectMap.put("club_Uuid", club_Uuid);
                rejectMap.put("friendId", ownerId);
                rejectMap.put("friend_Index", user_Index);
                rejectMap.put("notice_RegDate", notice_TodayFormat.format(new Date().getTime()));
                service.updateRequestResult(CommonString.clubController, rejectMap).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.e("abc", "" + response.raw());
                        if (response.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    map.remove(position);
                                    adapter.setPeopleList(map);
                                    adapter.notifyItemRemoved(position);
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
        };
        adapter.setAcceptInterface(acceptInterface);

    }

    @Override
    public void clickEvent() {

    }

    @Override
    public void getServer() {
        service.joinPeopleList(CommonString.clubController, club_Uuid, user_Index).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    map = response.body();
                    if (map.size() > 0) {
                        adapter.setPeopleList(map);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.emptyTextView.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.emptyTextView.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                } else {

                }

            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }
}