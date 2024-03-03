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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentMusicBinding;
import com.cross.juntalk2.diffutil.MusicListDiffUtil;
import com.cross.juntalk2.model.BulletinBoardViewModel;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.model.MusicViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MusicFragment extends CreateNewFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentMusicBinding binding;
    private MusicListAdapter adapter_Music;
    private RetrofitService service;
    private boolean paging = false;
    private int pageNum = 0;
    private ViewModelFactory viewModelFactory;
    private MusicViewModel musicViewModel;
    private Observer<List<MusicModel>> musicObserver;
    private BroadcastReceiver receiver;
    private List<MusicModel> musicModelList;

    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
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
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music, container, false);
        CreateNewFragment();
        return binding.getRoot();
    }

    @Override
    public void getInterfaceInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
    }

    @Override
    public void getIntentInfo() {
        musicModelList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MusicFragmentRefresh");
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("abc", "MusicFragment onResume");
                musicModelList.clear();
                pageNum = 0;
                pagingMusicList(pageNum);
            }
        };
        context.registerReceiver(receiver, intentFilter);

    }

    @Override
    public void init() {
        viewModelFactory = new ViewModelFactory();
        musicViewModel = new ViewModelProvider(this, viewModelFactory).get(MusicViewModel.class);
        musicViewModel.init();
        adapter_Music = new MusicListAdapter(new MusicListDiffUtil(), context, activity);
        adapter_Music.setHasStableIds(true);
        binding.musicItemRecyclerView.setAdapter(adapter_Music);
        binding.musicItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void createObjects() {
        pagingMusicList(pageNum);


        musicObserver = new Observer<List<MusicModel>>() {
            @Override
            public void onChanged(List<MusicModel> musicModels) {
                adapter_Music.submitList(musicViewModel.getMusicMutableLiveData().getValue());
//                adapter_Music.notifyItemRangeInserted(adapter_Music.getItemCount(), 10);
                adapter_Music.notifyDataSetChanged();
            }
        };

        musicViewModel.getMusicMutableLiveData().observe(this, musicObserver);
    }

    @Override
    public void clickEvent() {

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                musicModelList.clear();
                pageNum = 0;
                pagingMusicList(pageNum);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.musicItemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (paging && lastVisibleItem == musicViewModel.getMusicMutableLiveData().getValue().size() - 1) {
                    pageNum++;
                    pagingMusicList(pageNum);
                    paging = false;
                }

            }
        });
    }


    public void pagingMusicList(int pageNum) {

        service.musicList(CommonString.musicController, "popsong", pageNum).enqueue(new Callback<List<MusicModel>>() {
            @Override
            public void onResponse(Call<List<MusicModel>> call, Response<List<MusicModel>> response) {
                Log.e("abc", "response result : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    List<MusicModel> musicListMap = response.body();
                    musicModelList.addAll(musicListMap);
                    musicViewModel.setItems(musicModelList);
                    if (musicListMap.size() > 0) {
                        paging = true;
                    } else {

                        paging = false;
                    }

                } else {

                }
            }

            @Override
            public void onFailure(Call<List<MusicModel>> call, Throwable t) {
                Log.e("abc", "error  : " + t.getMessage());
            }
        });
    }


}