package com.cross.juntalk2.fifth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentFifthBinding;
import com.cross.juntalk2.model.CalendarModel;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.ClubViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.Utils;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FifthFragment extends CreateNewFragment {
    private ThreadPoolExecutor executorService;
    private Calendar calendar;
    private List<CalendarModel> calendarList;
    private TreeMap<Integer, List<CalendarModel>> sorting;
    private Map<Integer, List<CalendarModel>> calendarMap;
    private FragmentFifthBinding binding;
    private CalendarFragmentAdapter calendarFragmentAdapter;
    private Handler handler;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
    private String mParam1;
    private String mParam2;
    private RetrofitService service;
    private ClubViewModel clubViewModel;
    private Observer<List<ClubModel>> clubModelObserver;
    private ViewModelFactory viewModelFactory;
    private Gson gson;
    private Type type;
    private boolean isUiThread = false;
    private MyJoinedClubList myJoinedClubList;
    private int user_index;
    private LinkedHashMap<String, List<ClubModel>> sortRegDateMap;
    private BroadcastReceiver broadcastReceiver;
    private SortRegDateThread sortRegDateThread;
    private int count = 0;
    int textviewId[] = {
            R.id.textView0,
            R.id.textView1,
            R.id.textView2,
            R.id.textView3,
            R.id.textView4,
            R.id.textView5,
            R.id.textView6,
            R.id.textView7,
            R.id.textView8,
    };
    private DialogMyMadeClubList myClubListDialog;
    private String myUserId;
    private MyClubListThread myClubListThread;
    private SettingMyCurrentStatusThread settingMyCurrentStatusThread;

    int currentCompleteCount = 0;
    int currentMyWaitingClubCount = 0;
    int currentMyMadeClubListCount = 0;
    private boolean isMyThreadStart = false;

    public FifthFragment() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    public static FifthFragment newInstance(String param1, String param2) {
        FifthFragment fragment = new FifthFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fifth, container, false);
        CreateNewFragment();
        return binding.getRoot();
    }


    @Override
    public void getInterfaceInfo() {
        user_index = JunApplication.getMyModel().user_Index;
        myUserId = JunApplication.getMyModel().userId;


    }

    @Override
    public void getIntentInfo() {

        type = new TypeToken<List<ClubModel>>() {
        }.getType();
        gson = new Gson();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("createClub");

        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currentCompleteCount = 0;
                currentMyWaitingClubCount = 0;
                currentMyMadeClubListCount = 0;
                clubList();
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void init() {
        handler = new Handler(Looper.getMainLooper());
        int maxCore = Runtime.getRuntime().availableProcessors();
        executorService = new ThreadPoolExecutor(3, maxCore, 0L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        executorService.execute(createCalendarObject());
        viewModelFactory = new ViewModelFactory();
        clubViewModel = new ViewModelProvider(this, viewModelFactory).get(ClubViewModel.class);
        clubViewModel.init();
        myClubListDialog = new DialogMyMadeClubList(context);


    }

    int clubCount = 0;

    private class MyJoinedClubList extends Thread {
        @Override
        public void run() {
            super.run();
            for (ClubModel clubModel : clubViewModel.getClubMutableData().getValue()) {
                for (Map<String, Object> mapInfo : clubModel.myJoinInfo) {
                    if (mapInfo.get("userId").equals(myUserId)) {
                        if (mapInfo.get("requestResult") != null) {
                            if (mapInfo.get("requestResult").equals("N") || mapInfo.get("requestResult").equals("Y")) {

                                Shimmer shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                                        .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                                        .setBaseAlpha(0.7f) //the alpha of the underlying children
                                        .setBaseColor(Color.GRAY)
                                        .setHighlightAlpha(0.7f) // the shimmer alpha amount
                                        .setHighlightColor(Color.WHITE)
                                        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                                        .setAutoStart(true)
                                        .build();
                                ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
                                shimmerDrawable.setShimmer(shimmer);
                                RequestOptions requestOptions = new RequestOptions()
                                        .centerCrop()
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .disallowHardwareConfig()
                                        .error(R.drawable.drawable_drawcircle)
                                        .placeholder(shimmerDrawable);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        LayoutInflater inflater = LayoutInflater.from(context);
                                        View customBindingView = inflater.inflate(R.layout.custom_myjoined_clubinfo, null, false);
                                        ShapeableImageView mainRoomPhoto = customBindingView.findViewById(R.id.mainRoomPhoto);

                                        String url = clubModel.allUrls.get(0);

                                        if (url.contains(".mp4")) {
                                            Glide.with(context)
                                                    .setDefaultRequestOptions(requestOptions)
                                                    .load(CommonString.CommonStringInterface.VideoThumbNail + clubModel.allUrls.get(0))
                                                    .thumbnail(0.3f)
                                                    .optionalCenterCrop()
                                                    .into(mainRoomPhoto);

                                        } else {
                                            Glide.with(context)
                                                    .setDefaultRequestOptions(requestOptions)
                                                    .load(CommonString.CommonStringInterface.FileBaseUrl + clubModel.allUrls.get(0))
                                                    .thumbnail(0.3f)
                                                    .optionalCenterCrop()
                                                    .into(mainRoomPhoto);
                                        }

                                        AppCompatTextView mainTitleTextView = customBindingView.findViewById(R.id.mainTitleTextView);
                                        mainTitleTextView.setText(clubModel.title);
                                        AppCompatTextView contentTextView = customBindingView.findViewById(R.id.contentTextView);
                                        contentTextView.setText(clubModel.clubIntroduce);
                                        ChipGroup chipGroup = customBindingView.findViewById(R.id.chipgroup);
                                        StringTokenizer tokenizer = new StringTokenizer(clubModel.hashTagList, ",");
                                        while (tokenizer.hasMoreTokens()) {
                                            Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
                                            chip.setText("#" + tokenizer.nextToken());
                                            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            chipGroup.addView(chip, layoutParams);
                                        }
                                        binding.motionLayout.addView(customBindingView);
                                        clubCount++;
                                    }
                                });
                            }
                        } else {

                        }
                    }
                }
            }
            binding.carousel.setAdapter(new Carousel.Adapter() {
                @Override
                public int count() {
                    Log.e("abc", "textviewId.length : " + clubCount);
                    return clubCount;
                }

                @Override
                public void populate(View view, int index) {

                }

                @Override
                public void onNewItem(int index) {
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //todo : 메인쓰레드인지 아닌지 확인 하여 메인쓰레드가 아닐경우 quit하게 하기
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isUiThread = subHandler.getLooper().isCurrentThread();
        } else {
            isUiThread = Looper.getMainLooper().getThread() == subHandler.getLooper().getThread();
        }
        if (!isUiThread) {
            subHandler.getLooper().quit();
        }*/

        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (myClubListDialog.isShowing()) {
            myClubListDialog.dismiss();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }

    }

    @Override
    public void createObjects() {
        executorService.execute(runnable());
        clubList();

        clubModelObserver = clubModels -> {
            sortRegDateThread = new SortRegDateThread();
            if (!sortRegDateThread.isInterrupted()) {
                SortRegDateThread.interrupted();
            }
            sortRegDateThread.start();
        };
        clubViewModel.getClubMutableData().observe(this, clubModelObserver);


    }


    private class SortRegDateThread extends Thread {
        public SortRegDateThread() {

        }

        @Override
        public void run() {
            super.run();
            List<ClubModel> clubModelList = new ArrayList<>();
            sortRegDateMap = new LinkedHashMap<>();
            for (ClubModel clubModel : clubViewModel.getClubMutableData().getValue()) {
                if (sortRegDateMap.get(clubModel.regDate.substring(0, 10)) != null) {
                    clubModelList.add(clubModel);
                    sortRegDateMap.put(clubModel.regDate.substring(0, 10), clubModelList);
                } else {
                    clubModelList = new ArrayList<>();
                    clubModelList.add(clubModel);
                    sortRegDateMap.put(clubModel.regDate.substring(0, 10), clubModelList);
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    calendarFragmentAdapter.setItems(sortRegDateMap);
                }
            });
        }
    }


    @Override
    public void clickEvent() {
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
        adView.setAdUnitId("ca-app-pub-9868504639371547/1253062251");
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        binding.createClubFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateClubActivity.class);
                startActivity(intent);
            }
        });
        binding.calendarRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int currentPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {
                    if (currentPosition > 12) {
                        binding.dateTV.setText((calendar.get(Calendar.YEAR) + 1) + "년 " + (currentPosition - 12) + "월");
                    } else {
                        binding.dateTV.setText(calendar.get(Calendar.YEAR) + "년 " + (currentPosition + 1) + "월");
                    }
                }
            }
        });

        binding.myClubListImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClubListThread = new MyClubListThread();
                if (!myClubListThread.isInterrupted()) {
                    MyClubListThread.interrupted();
                }
                myClubListThread.start();
            }
        });
    }

    private class MyClubListThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (clubViewModel.getClubMutableData().getValue() != null &&
                    !clubViewModel.getClubMutableData().getValue().isEmpty()) {
                List<ClubModel> clubModelList = new ArrayList<>();
                for (ClubModel clubModel : clubViewModel.getClubMutableData().getValue()) {
                    if (clubModel.userId.equals(myUserId)) {
                        clubModelList.add(clubModel);
                    }
                }
                if (myClubListDialog != null) {
                    myClubListDialog.setMyClubList(clubModelList);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        myClubListDialog = new DialogMyMadeClubList(context);

                        myClubListDialog.show();
                        myClubListDialog.settingData();
                        isMyThreadStart = false;
                    }
                });


            }
        }
    }


    public Runnable runnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.dateTV.setText(calendar.get(Calendar.YEAR) + "년 " + (calendar.get(Calendar.MONTH) + 1) + "월");
                        calendarFragmentAdapter = new CalendarFragmentAdapter(getContext(), calendarMap);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        binding.calendarRecyclerView.setLayoutManager(linearLayoutManager);
                        binding.calendarRecyclerView.setAdapter(calendarFragmentAdapter);
                        binding.calendarRecyclerView.scrollToPosition((calendar.get(Calendar.MONTH)));
                        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                        if (binding.calendarRecyclerView.getOnFlingListener() == null)
                            pagerSnapHelper.attachToRecyclerView(binding.calendarRecyclerView);
                    }
                });
            }
        };
        return runnable;
    }

    public Runnable createCalendarObject() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                calendarMap = new HashMap<>();
                sorting = new TreeMap<>(calendarMap);
                calendar = Calendar.getInstance();
                int daysCount = 0;
                for (int i = (1 - calendar.get(Calendar.MONTH)); i < 13; i++) {

                    try {
                        calendarList = new ArrayList<>();
                        GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), ((calendar.get(Calendar.MONTH) - 1) + i), 1, 0, 0, 0);
                        GregorianCalendar pre_gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), ((calendar.get(Calendar.MONTH) - 1) + i), 1, 0, 0, 0);
                        GregorianCalendar after_gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), ((calendar.get(Calendar.MONTH) - 1) + i), 1, 0, 0, 0);
                        pre_gregorianCalendar.add(Calendar.MONTH, -1);
                        after_gregorianCalendar.add(Calendar.MONTH, +1);

                        //처음 시작하는 달로부터 앞으로 몇일 뒤에 남았는지 확인
                        int dayOfWeek = gregorianCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                        //그날의 최대 날짜 31일이면 31일 30일이면 30일
                        int max = gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        //전 달의 최대 날짜
                        int preMonthMax = pre_gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        //후 달의 최대 날짜
                        int afterMonthMax = after_gregorianCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                        //현재 날짜 기준 앞 뒤로 남아있는 달의 계산
                        for (int j = 0; j < dayOfWeek; j++) {
                            CalendarModel calendarModel = new CalendarModel();
                            pre_gregorianCalendar = new GregorianCalendar(pre_gregorianCalendar.get(Calendar.YEAR), pre_gregorianCalendar.get(Calendar.MONTH), preMonthMax - (dayOfWeek - 1 - j));
                            calendarModel.setGregorianCalendar(pre_gregorianCalendar);
                            calendarModel.setType(2);
                            calendarList.add(calendarModel);
                        }


                        //최대 날짜 계산
                        for (int j = 1; j <= max; j++) {
                            CalendarModel calendarModel = new CalendarModel();
                            calendarModel.setGregorianCalendar(new GregorianCalendar(gregorianCalendar.get(Calendar.YEAR), gregorianCalendar.get(Calendar.MONTH), j));
                            calendarModel.setType(1);
                            calendarList.add(calendarModel);
                        }

                        //뒤로 남은 날짜 몇개 남았는지 계산 하고 넣어줌
                        if (calendarList.size() / 7 > 0) {
                            int extra = calendarList.size() % 7;
                            if (extra > 0) {
                                extra = 7 - extra;
                            }
                            for (int k = 0; k < extra; k++) {
                                CalendarModel calendarModel = new CalendarModel();
                                after_gregorianCalendar = new GregorianCalendar(after_gregorianCalendar.get(Calendar.YEAR), after_gregorianCalendar.get(Calendar.MONTH), k + 1);
                                calendarModel.setGregorianCalendar(after_gregorianCalendar);
                                calendarModel.setType(2);
                                calendarList.add(calendarModel);

                            }
                        } else {

                        }
                        //캘린더 맵에 넣음
                        calendarMap.put(daysCount, calendarList);
                        daysCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        return runnable;
    }

    public void clubList() {
        service.clubList(CommonString.clubController, user_index).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.e("abc", "response : " + response.raw());
                if (response.isSuccessful()) {
                    Map<String, Object> map = response.body();

                    List<ClubModel> clubModelList = gson.fromJson(map.get("clubList").toString(), type);
                    clubViewModel.setItems(clubModelList);

                    settingMyCurrentStatusThread = new SettingMyCurrentStatusThread(clubModelList);
                    if (!settingMyCurrentStatusThread.isInterrupted()) {
                        SettingMyCurrentStatusThread.interrupted();
                    }
                    settingMyCurrentStatusThread.start();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());

            }
        });
    }


    private class SettingMyCurrentStatusThread extends Thread {

        private List<ClubModel> clubModelList;

        public SettingMyCurrentStatusThread(List<ClubModel> clubModelList) {
            super();
            this.clubModelList = clubModelList;
        }

        @Override
        public void run() {
            super.run();
            for (ClubModel clubModel : clubModelList) {
                for (Map<String, Object> myInfoMap : clubModel.myJoinInfo) {
                    if (myInfoMap.get("userId") != null && myInfoMap.get("userId").equals(myUserId)) {

                        Log.e("abc", "myInfoMap.get(\"requestResult\") " + myInfoMap.get("requestResult"));
                        if (myInfoMap.get("requestResult") != null && myInfoMap.get("requestResult").equals("Y")) {

                            currentCompleteCount++;

                        } else if (myInfoMap.get("requestResult") != null && myInfoMap.get("requestResult").equals("R")) {
                            currentMyWaitingClubCount--;
                            if (currentMyWaitingClubCount < 0) {
                                currentMyWaitingClubCount = 0;
                            }
                            currentCompleteCount--;
                            if (currentCompleteCount < 0) {
                                currentCompleteCount = 0;
                            }
                        } else {
                            currentMyWaitingClubCount++;
                        }
                    }
                }

                if (!clubModel.isDelete &&
                        clubModel.userId.equals(myUserId)) {
                    currentMyMadeClubListCount++;
                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    binding.currentCompleteTextView.setText("" + currentCompleteCount);
                    binding.currentWaitingTextView.setText("" + currentMyWaitingClubCount);
                    binding.currentMyMadeClubTextView.setText("" + currentMyMadeClubListCount);
                }
            });

        }
    }


}