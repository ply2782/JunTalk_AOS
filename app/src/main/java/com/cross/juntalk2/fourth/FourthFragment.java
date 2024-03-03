package com.cross.juntalk2.fourth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentFourthBinding;
import com.cross.juntalk2.diffutil.BulletinDiffUtil;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.BulletinBoardViewModel;
import com.cross.juntalk2.model.BulletinCommentViewModel;
import com.cross.juntalk2.retrofit.RefreshInterface;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class FourthFragment extends CreateNewFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentFourthBinding binding;
    private String mParam1;
    private String mParam2;
    private List<BulletinBoardModel> bullteinCopy;
    private PowerMenu powerMenu;
    private RetrofitService service;
    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private Handler handler;
    private boolean paging = false;
    private int pageNum = 0;
    private ViewModelFactory viewModelFactory;
    private BulletinBoardViewModel bulletinBoardViewModel;
    private BulletinBoardAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private String type;
    private String myId;
    private int myIndex;
    private BulletinCommentViewModel bulletinCommentViewModel;
    private RefreshInterface refreshInterface;
    private OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            binding.titleTextView.setText(item.getTitle().toString());
            powerMenu.setSelectedPosition(position); // change selected item
            switch (item.getTitle().toString()) {
                case "# 전체":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "A";
                    break;

                case "# 감사":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Thanks";
                    break;

                case "# 중보":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Pray";
                    break;

                case "# 소식":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "News";
                    break;

                case "# 공지":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Notice";
                    break;

                case "# 소개":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Introduce";
                    break;

                case "# 일상":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Daily";
                    break;

                case "# 자랑":
                    bullteinCopy.clear();
                    pageNum = 0;
                    type = "Show";
                    break;

            }
            api_getBulletinBoard(myId, myIndex, type, pageNum);
            powerMenu.dismiss();
        }
    };

    public interface RecyclerViewClickInterface {
        void clickPosition(int position);
    }

    private RecyclerViewClickInterface recyclerViewClickInterface;


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            View childView = binding.bulletinBoardRecyclerView.getChildAt(0);
            if (childView != null) {
                BulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardAdapter.BulletinBoardViewHolder) childView.getTag();
                ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                if (imageOrVideoViewHolder != null) {
                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                }
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        int firstVisibleItem = ((LinearLayoutManager) binding.bulletinBoardRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        BulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardAdapter.BulletinBoardViewHolder) binding.bulletinBoardRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
        if (bulletinBoardViewHolder != null) {
            int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {

            } else {

                ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                if (imageOrVideoViewHolder == null) {

                } else {
                    /**imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();*/
                    imageOrVideoViewHolder.binding.imageVideoView.releasePlayer();
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("abc", "hidden : " + hidden);
        if (hidden) {
            /*if (adapter != null) {
                View childView = binding.bulletinBoardRecyclerView.getChildAt(0);
                if (childView != null) {
                    BulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardAdapter.BulletinBoardViewHolder) childView.getTag();
                    ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                    if (imageOrVideoViewHolder != null) {
                        imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                    }
                }
            }*/
            int firstVisibleItem = ((LinearLayoutManager) binding.bulletinBoardRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            BulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (BulletinBoardAdapter.BulletinBoardViewHolder) binding.bulletinBoardRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
            if (bulletinBoardViewHolder != null) {
                int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {

                } else {
                    ImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (ImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                    if (imageOrVideoViewHolder == null) {

                    } else {
                        imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                    }
                }
            }
        }
    }

    public FourthFragment() {
        // Required empty public constructor
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        if (context instanceof RecyclerViewClickInterface) {
            recyclerViewClickInterface = (RecyclerViewClickInterface) context;
        }
    }

    public static FourthFragment newInstance(String param1, String param2) {
        FourthFragment fragment = new FourthFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fourth, container, false);
        CreateNewFragment();
        return binding.getRoot();
    }

    @Override
    public void getInterfaceInfo() {
        myId = JunApplication.getMyModel().userId;
        myIndex = JunApplication.getMyModel().user_Index;
        refreshInterface = new RefreshInterface() {
            @Override
            public void refresh() {
                myId = JunApplication.getMyModel().userId;
                pageNum = 0;
                bullteinCopy = new ArrayList<>();
                api_getBulletinBoard(myId, myIndex, type, pageNum);
            }
        };
    }

    @Override
    public void getIntentInfo() {

        recyclerViewClickInterface = new RecyclerViewClickInterface() {
            @Override
            public void clickPosition(int position) {
                if (position != RecyclerView.NO_POSITION) {
                    View childView = binding.bulletinBoardRecyclerView.getChildAt(0);
                    BulletinBoardAdapter.BulletinBoardViewHolder viewHolder = (BulletinBoardAdapter.BulletinBoardViewHolder) childView.getTag();
                    viewHolder.binding.imageOrVideoRecyclerView.requestFocus();
                    viewHolder.binding.imageOrVideoRecyclerView.requestLayout();
                }
            }
        };

        viewModelFactory = new ViewModelFactory();
        bulletinBoardViewModel = new ViewModelProvider(this, viewModelFactory).get(BulletinBoardViewModel.class);
        bulletinBoardViewModel.init();
        bulletinCommentViewModel = new ViewModelProvider(this, viewModelFactory).get(BulletinCommentViewModel.class);
        bulletinCommentViewModel.init();

        bullteinCopy = new ArrayList<>();

        Observer<List<BulletinBoardModel>> listObserver = new Observer<List<BulletinBoardModel>>() {
            @Override
            public void onChanged(List<BulletinBoardModel> bulletinBoardModels) {

                adapter.submitList(bulletinBoardViewModel.getBulletinBoardModelList());
                adapter.notifyDataSetChanged();
            }
        };
        bulletinBoardViewModel.getBulletinBoardMutableLiveData().observe(this, listObserver);

    }


    @Override
    public void init() {


        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        handler = new Handler(Looper.getMainLooper());
        adapter = new BulletinBoardAdapter(new BulletinDiffUtil(), context, activity);
        adapter.setRecyclerViewClickInterface(recyclerViewClickInterface);
        adapter.setHasStableIds(true);
        adapter.setRefreshInterface(refreshInterface);
        binding.titleTextView.setText("# 전체");
        binding.bulletinBoardRecyclerView.setAdapter(adapter);
        binding.bulletinBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemAnimator animator = binding.bulletinBoardRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        type = "A";
        bullteinCopy.clear();
        api_getBulletinBoard(myId, myIndex, type, pageNum);

    }

    @Override
    public void createObjects() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("abc", "onResume");

        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FourthFragment");
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    myId = JunApplication.getMyModel().userId;
                    pageNum = 0;
                    bullteinCopy.clear();
                    api_getBulletinBoard(myId, myIndex, type, pageNum);
                }
            };
        }
        context.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            broadcastReceiver.clearAbortBroadcast();
            broadcastReceiver = null;
        }
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void clickEvent() {

        binding.LilsFloatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, LilsVideoListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
        });

        binding.bulletinBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (paging && lastVisibleItem == bulletinBoardViewModel.getBulletinBoardMutableLiveData().getValue().size() - 1) {
                    pageNum++;
                    api_getBulletinBoard(myId, myIndex, type, pageNum);
                    paging = false;
                }
            }
        });


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                bullteinCopy = new ArrayList<>();
                bullteinCopy.clear();
                pageNum = 0;
                api_getBulletinBoard(myId, myIndex, type, pageNum);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.addBulletinBoardImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateBulletinBoardItemActivity.class);
            startActivity(intent);
        });

        List<PowerMenuItem> powerMenuItems = new ArrayList<>();
        powerMenuItems.add(new PowerMenuItem("# 감사", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 중보", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 소식", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 공지", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 소개", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 일상", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 자랑", R.drawable.juntalk_logo, false));
        powerMenuItems.add(new PowerMenuItem("# 전체", R.drawable.juntalk_logo, true));
        powerMenu = new PowerMenu.Builder(context)
                .addItemList(powerMenuItems) // list has "Novel", "Poerty", "Art"
                .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(context, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setDividerHeight(10)
                .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                .setSelectedMenuColor(ContextCompat.getColor(context, R.color.teal_200))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();

        binding.menuFloatingActionButton.setOnClickListener(v -> powerMenu.showAtCenter(binding.getRoot()));
    }


    public void api_getBulletinBoard(String userId, int user_Index, String category, int pageNum) {
        Observable<List<BulletinBoardModel>> bulletinBoardObservable =
                service.getBulletinBoard(CommonString.bulletinBoardController, userId, user_Index, category, pageNum);
        compositeDisposable.add(bulletinBoardObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<List<BulletinBoardModel>>() {
                    @Override
                    public void onNext(@NotNull List<BulletinBoardModel> bulletinBoardModels) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.show();
                            }
                        });
                        bullteinCopy.addAll(bulletinBoardModels);
                        bulletinBoardViewModel.setItems(bullteinCopy);
                        if (!bulletinBoardModels.isEmpty() && bulletinBoardModels.size() > 0) {
                            paging = true;
                        } else {
                            paging = false;
                        }

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error  :" + e.getMessage());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bullteinCopy.size() > 0) {
                                    binding.emptyTextView.setVisibility(View.GONE);
                                } else {
                                    binding.emptyTextView.setVisibility(View.VISIBLE);
                                }

                            }
                        });
                    }
                }));
    }
}