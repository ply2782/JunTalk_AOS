package com.cross.juntalk2.third;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentVideoBinding;
import com.cross.juntalk2.diffutil.VideoListDiffUtil;
import com.cross.juntalk2.model.VideoModel;
import com.cross.juntalk2.model.VideoViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoFragment extends CreateNewFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentVideoBinding binding;
    private VideoListAdapter adapter_Video;
    private RetrofitService service;
    private boolean isLoading = false;
    private boolean isLastLoading = false;
    private Observer<List<VideoModel>> observer;
    private ViewModelFactory factory;
    private VideoViewModel videoViewModel;
    private int page = 0;
    private boolean serverpaging = false;
    private BroadcastReceiver receiver;
    private List<VideoModel> videoModelList;


    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);
        CreateNewFragment();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("VideoFragmentRefresh");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("abc", "VideoFragment");
                videoModelList.clear();
                page = 0;
                pagingVideoList(page);
            }
        };
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("abc", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("abc", "onDestroy");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver.clearAbortBroadcast();
            receiver = null;
        }
    }

    @Override
    public void getInterfaceInfo() {
        videoModelList = new ArrayList<>();
    }

    @Override
    public void getIntentInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
    }


    @Override
    public void init() {
        factory = new ViewModelFactory();
        videoViewModel = new ViewModelProvider(this, factory).get(VideoViewModel.class);
        videoViewModel.init();
        adapter_Video = new VideoListAdapter(new VideoListDiffUtil(), context);
        adapter_Video.setHasStableIds(true);
        binding.videoItemRecyclerView.setAdapter(adapter_Video);
        binding.videoItemRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void createObjects() {
        videoModelList.clear();
        pagingVideoList(page);

        observer = new Observer<List<VideoModel>>() {
            @Override
            public void onChanged(List<VideoModel> videoModels) {
                adapter_Video.submitList(videoViewModel.getVideoMutableLiveData().getValue());
//                adapter_Video.notifyItemRangeInserted(adapter_Video.getItemCount(), 10);
                adapter_Video.notifyDataSetChanged();
            }
        };
        videoViewModel.getVideoMutableLiveData().observe(this, observer);


    }

    @Override
    public void clickEvent() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoModelList.clear();
                page = 0;
                pagingVideoList(page);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.videoItemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if (serverpaging && lastItem == videoViewModel.getVideoMutableLiveData().getValue().size() - 1) {
                    page++;
                    pagingVideoList(page);
                    serverpaging = false;

                }
            }
        });
    }

    public void pagingVideoList(int pageNum) {
        service.videoList(CommonString.videoController, "null", pageNum).enqueue(new Callback<List<VideoModel>>() {
            @Override
            public void onResponse(Call<List<VideoModel>> call, Response<List<VideoModel>> response) {
                Log.e("abc", "response result : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    List<VideoModel> videoListMap = response.body();
                    videoModelList.addAll(videoListMap);
                    videoViewModel.setItems(videoModelList);
                    if (videoListMap != null && videoListMap.size() > 0) {
                        serverpaging = true;
                    } else {
                        serverpaging = false;
                    }

                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<List<VideoModel>> call, Throwable t) {
                Log.e("abc", "error  : " + t.getMessage());
            }
        });
    }
}