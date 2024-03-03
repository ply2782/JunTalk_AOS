package com.cross.juntalk2.second;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentSecondBinding;
import com.cross.juntalk2.diffutil.RoomDiffUtil;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.model.RoomViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends CreateNewFragment {

    private static final String ARG_PARAM1 = "param1";
    private MyModel myModel;
    private FragmentSecondBinding binding;
    private Handler handler;
    private RetrofitService service;
    private ChattingListAdapter adapter;
    private ViewModelFactory viewModelFactory;
    private RoomViewModel roomViewModel;
    private LoadingDialog loadingDialog;
    private int page = 0;
    private List<RoomModel> roomModelList;
    private BroadcastReceiver broadcastReceiver;
    private ResumeThread resumeThread;

    public SecondFragment() {
        // Required empty public constructor
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

    }

    public static SecondFragment newInstance(MyModel myModel) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, myModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myModel = (MyModel) getArguments().getSerializable(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_second, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        CreateNewFragment();
        return binding.getRoot();
    }


    public void signNewApi() {
        if(myModel == null){
            myModel =JunApplication.getMyModel();
        }
        service.isNewClubChatting(CommonString.roomController, myModel.userId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    boolean result = response.body();
                    Log.e("abc", "result : " + result);
                    if (result) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.newTextView.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                binding.newTextView.setVisibility(View.GONE);
                            }
                        });
                    }
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
    public void getInterfaceInfo() {

    }

    @Override
    public void getIntentInfo() {
        roomModelList = new ArrayList<>();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onResume() {
        super.onResume();

        signNewApi();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SecondFragment");
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    /*roomModelList = new ArrayList<>();*/
                    resumeThread = new ResumeThread();
                    if (!resumeThread.isInterrupted()) {
                        resumeThread.interrupt();
                    }
                    resumeThread.start();
                }
            };
        }

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    private class ResumeThread extends Thread {
        @Override
        public void run() {
            super.run();
            myModel = JunApplication.getMyModel();
            roomModelList.clear();
//            roomViewModel.clearAllItem();
            page = 0;
            pageingServer(page);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void init() {

        viewModelFactory = new ViewModelFactory();
        loadingDialog = new LoadingDialog(context);
        roomViewModel = new ViewModelProvider(this, viewModelFactory).get(RoomViewModel.class);
        roomViewModel.init();
        handler = new Handler(Looper.myLooper());
        adapter = new ChattingListAdapter(new RoomDiffUtil(), context);
        binding.chattingListRecyclerView.setAdapter(adapter);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        binding.chattingListRecyclerView.setAnimation(alphaAnimation);
        binding.chattingListRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        pageingServer(page);
//        api_UnReadCount();


        if (JunApplication.getLookStatus().getBoolean("chatting", false) == false) {
            TapTargetView.showFor(activity,                 // `this` is an Activity
                    TapTarget.forView(binding.openChattingImageButton, "클럽 채팅에 참여해보세요!", "클럽 참여 수락되면 이곳에서 자유롭게\n오픈 채팅을 이용하실 수 있습니다.!")
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
                            JunApplication.getLookStatus_Editor().putBoolean("chatting", true);
                            JunApplication.getLookStatus_Editor().commit();
                        }
                    });
        }
    }

    @Override
    public void createObjects() {
        Observer<List<RoomModel>> listObserver = new Observer<List<RoomModel>>() {
            @Override
            public void onChanged(List<RoomModel> roomModels) {
                /*adapter.notifyItemRangeInserted(adapter.getItemCount(), 10);*/
                adapter.submitList(roomViewModel.roomModelMutableLiveData().getValue());
                adapter.notifyDataSetChanged();
            }
        };
        roomViewModel.roomModelMutableLiveData().observe(this, listObserver);
    }

    @Override
    public void clickEvent() {

        binding.openChattingImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, OpenChattingActivity.class);
            startActivity(intent);
            service.updateIsNewClubChatting(CommonString.roomController, myModel.userId, false).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.e("abc", "" + response.raw());

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        });


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*roomModelList = new ArrayList<>();*/
                roomModelList.clear();
                page = 0;
                pageingServer(page);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.wholeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    /*if ((roomModelList.size() % 10) != 0) {

                    } else {
                        page++;
                        pageingServer(page);

                    }*/
                }
            }
        });
    }

    //TODO : 페이징 처리 서버
    public void pageingServer(int page) {
        service.roomListPaging(CommonString.roomController, page, myModel.userId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                Log.e("abc", "response : " + response.toString());
                if (response.isSuccessful()) {
                    List<Map<String, Object>> mapList = response.body();
                    if (mapList.size() > 0) {
                        Function<Map<String, Object>, Observable<List<RoomModel>>> function = new Function<Map<String, Object>, Observable<List<RoomModel>>>() {
                            @Override
                            public Observable<List<RoomModel>> apply(@NotNull Map<String, Object> map) throws Exception {
                                RoomModel roomModel = new RoomModel();
                                roomModel.room_Index = Integer.parseInt(String.valueOf(map.get("room_Index")).replace(".0", ""));
                                roomModel.room_Conversation = (String) map.get("room_Conversation");
                                roomModel.room_joinCount = Integer.parseInt(String.valueOf(map.get("room_JoinCount")).replace(".0", ""));
                                roomModel.room_RegDate = (String) map.get("room_RegDate");
                                roomModel.room_Title = (String) map.get("room_Title");
                                roomModel.room_Uuid = (String) map.get("room_Uuid");
                                if (map.get("unreadCount") != null) {
                                    roomModel.unreadCount = Integer.parseInt(String.valueOf((double) map.get("unreadCount")).replace(".0", ""));
                                } else {
                                    roomModel.unreadCount = 0;
                                }
                                if (map.get("alarm") != null) {
                                    if (((String) map.get("alarm")).equals("1")) {
                                        roomModel.alarm = true;
                                    } else {
                                        roomModel.alarm = false;
                                    }
                                }
                                roomModel.roomType = RoomModel.RoomType.valueOf((String) map.get("roomType"));
                                roomModel.conversationTime = (String) map.get("conversationTime");
                                roomModel.joinPeopleImageList = (List<String>) map.get("joinPeopleImageList");
                                roomModel.mainRoomPhoto = (String) map.get("mainRoomPhoto");
                                roomModel.roomHashTag = (String) map.get("roomHashTag");
                                roomModel.joinRoomContent = (String) map.get("joinRoomContent");
                                roomModelList.add(roomModel);
                                Collections.sort(roomModelList, new TimeComparator());
                                return Observable.fromArray(roomModelList);
                            }
                        };

                        Observable.fromIterable(mapList)
                                .flatMap(function)
                                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                                .subscribe(items -> roomViewModel.setItems(items) /*binding.setRoomModel(items)*/);
                    } else {
                        roomViewModel.setItems(roomModelList);
                    }

                } else {
                    Log.e("abc", "pageingServer fail");
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("abc", "pageingServer error : " + t.getMessage());
                loadingDialog.dismiss();
            }
        });
    }

    //todo : 내림차순 정렬
    class TimeComparator implements Comparator<RoomModel> {
        private SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public int compare(RoomModel o1, RoomModel o2) {
            try {
                if (stringToDate.parse(o1.conversationTime).getTime() > stringToDate.parse(o2.conversationTime).getTime()) {
                    return -1;
                } else if (stringToDate.parse(o1.conversationTime).getTime() < stringToDate.parse(o2.conversationTime).getTime()) {
                    return 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;

        }
    }


}