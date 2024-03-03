package com.cross.juntalk2.fifth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityClubListBinding;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.ClubViewModel;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubListActivity extends CreateNewCompatActivity {

    private ActivityClubListBinding binding;
    private ClubListListViewAdapter adapter;
    private ClubViewModel clubViewModel;
    private Observer<List<ClubModel>> clubModelObserver;
    private ViewModelFactory viewModelFactory;
    private Map<String, Object> clubInfoMap;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String myId;
    private int my_User_Index;
    private Type type;
    private Gson gson;
    private long myOwnKakaoNumber;
    private String userMainPhoto;
    private Handler handler;
    private BroadcastReceiver receiver;
    private String currentDate;

    public interface JoinClickInterface {
        void joinClick(AppCompatButton button, ClubModel clubModel, int position);

        void foldFunction(ClubModel clubModel, AlertDialog alertDialog);

        void foldReportFunction(ClubModel clubModel, AlertDialog alertDialog, String reportContent);
    }

    private RetrofitService service;

    private JoinClickInterface clickInterface;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        View childView = binding.clubListRecyclerView.getChildAt(0);
        if (childView != null) {
            ClubListListViewAdapter.ClubListViewHolder viewHolder = (ClubListListViewAdapter.ClubListViewHolder) childView.getTag();
            childView = viewHolder.imageOrVideoRecyclerView.getChildAt(0);
            if (childView != null) {
                ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder viewHolder1 = (ClubIntroduceFileAdapter.ClubIntroduceFileAdapterViewHolder) childView.getTag();
                viewHolder1.imageVideoView.releasePlayer();
            }
        }

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("clubList");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData();
            }
        };
        context.registerReceiver(receiver, filter);
    }

    @Override
    public void getInterfaceInfo() {
        handler = new Handler(Looper.getMainLooper());
        if (context instanceof JoinClickInterface) {
            clickInterface = (JoinClickInterface) context;
        }


        clickInterface = new JoinClickInterface() {
            @Override
            public void joinClick(AppCompatButton button, ClubModel clubModel, int position) {

                clubInfoMap = new HashMap<>();
                clubInfoMap.put("userKakaoOwnNumber", myOwnKakaoNumber);
                clubInfoMap.put("user_Index", my_User_Index);
                clubInfoMap.put("userId", myId);
                clubInfoMap.put("owner_Index", clubModel.user_Index);
                clubInfoMap.put("ownerId", clubModel.userId);
                clubInfoMap.put("club_Uuid", clubModel.club_Uuid);
                clubInfoMap.put("requestResult", "N");
                clubInfoMap.put("club_Index", clubModel.club_Index);
                clubInfoMap.put("userMainPhoto", userMainPhoto);
                service.joinClub(CommonString.clubController, clubInfoMap).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.e("abc", "" + response.raw());
                        if (response.isSuccessful()) {
                            /*button.setText("수락 대기 중...");
                            button.setBackgroundResource(R.drawable.drawable_accepdted_gradation);*/
                            List<ClubModel> currentClubModelList = clubViewModel.getClubMutableData().getValue();
                            currentClubModelList.set(position, clubModel).myJoinInfo.add(clubInfoMap);
                            clubViewModel.setItems(currentClubModelList);
//                            adapter.setIndividualsItems(clubViewModel.getClubMutableData().getValue(), position);
                            Intent intent = new Intent();
                            intent.setAction("createClub");
                            intent.putExtra("createClub", "createClub");
                            sendBroadcast(intent);

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
            public void foldFunction(ClubModel clubModel, AlertDialog alertDialog) {
                api_BlockClubList(clubModel, alertDialog, "null");
                View childView = binding.clubListRecyclerView.getChildAt(0);
                ClubListListViewAdapter.ClubListViewHolder viewHolder = (ClubListListViewAdapter.ClubListViewHolder) childView.getTag();
                if (!viewHolder.mFoldableLayout.isFolded()) {
                    viewHolder.mFoldableLayout.foldWithAnimation();
                }
            }

            @Override
            public void foldReportFunction(ClubModel clubModel, AlertDialog alertDialog, String reportContent) {
                api_BlockClubList(clubModel, alertDialog, reportContent);
                View childView = binding.clubListRecyclerView.getChildAt(0);
                ClubListListViewAdapter.ClubListViewHolder viewHolder = (ClubListListViewAdapter.ClubListViewHolder) childView.getTag();
                if (!viewHolder.mFoldableLayout.isFolded()) {
                    viewHolder.mFoldableLayout.foldWithAnimation();
                }
            }
        };
    }

    public void api_BlockClubList(ClubModel clubModel, AlertDialog alertDialog, String blockReportContent) {
        service.blockClubList(CommonString.clubController, clubModel.club_Uuid, myId, my_User_Index, blockReportContent).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            clubViewModel.removeItems(clubModel);
                            alertDialog.dismiss();
                            Intent intent = new Intent();
                            intent.setAction("createClub");
                            intent.putExtra("createClub", "createClub");
                            sendBroadcast(intent);
                            if (blockReportContent == null || blockReportContent.equals("null")) {
                                Toast.makeText(context, "차단되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                            }
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
    public void getIntentInfo() {
        myOwnKakaoNumber = JunApplication.getMyModel().userKakaoOwnNumber;
        myId = JunApplication.getMyModel().userId;
        my_User_Index = JunApplication.getMyModel().user_Index;
        userMainPhoto = JunApplication.getMyModel().userMainPhoto;
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        gson = new Gson();
        currentDate = getIntent().getStringExtra("currentDate");
        viewModelFactory = new ViewModelFactory();
        clubViewModel = new ViewModelProvider(this, viewModelFactory).get(ClubViewModel.class);
        clubViewModel.init();
        type = new TypeToken<List<ClubModel>>() {
        }.getType();
        sharedPreferences = getSharedPreferences("LookStatus", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getData();

    }

    public void getData() {
        service.clubListModel(CommonString.clubController, currentDate, my_User_Index).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    Map<String, Object> map = response.body();
                    Log.e("abc", "map : " + map.toString());
                    List<ClubModel> clubModelList = gson.fromJson(map.get("currentDateClubList").toString(), type);
                    clubViewModel.setItems(clubModelList);
                } else {

                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(ClubListActivity.this, R.layout.activity_club_list);
        adapter = new ClubListListViewAdapter(context);
        binding.clubListRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });
        binding.clubListRecyclerView.setAdapter(adapter);
        binding.clubListRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter.setClickInterface(clickInterface);

    }


    @Override
    public void createThings() {
        clubModelObserver = new Observer<List<ClubModel>>() {
            @Override
            public void onChanged(List<ClubModel> clubModels) {

                adapter.setItems(clubViewModel.getClubModelList());
            }
        };
        clubViewModel.getClubMutableData().observe(this, clubModelObserver);
    }

    @Override
    public void clickEvent() {
        if (sharedPreferences.getBoolean("clubListLook", false) == false) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.mainTitleTextView), "클럽에 참여해보세요 !", "클럽 소개 및 관련 내용들을 보고 원하는 클럽에 참여해 보세요 !\n클럽을 클릭하면 영상 및 이미지를 확인할 수 있습니다.")
                            // All options below are optional
                            .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.black)  // Specify the color of the description text
                            .textColor(R.color.black)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                            .targetRadius(60),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            view.dismiss(true);
                            editor.putBoolean("clubListLook", true);
                            editor.commit();
                        }
                    });

        }

    }

    @Override
    public void getServer() {

    }
}