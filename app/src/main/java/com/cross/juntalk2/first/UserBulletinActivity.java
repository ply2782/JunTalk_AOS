package com.cross.juntalk2.first;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityUserBulletinBinding;
import com.cross.juntalk2.diffutil.BulletinDiffUtil;
import com.cross.juntalk2.fourth.CreateBulletinBoardItemActivity;
import com.cross.juntalk2.model.BulletinBoardModel;
import com.cross.juntalk2.model.BulletinBoardViewModel;
import com.cross.juntalk2.model.BulletinCommentViewModel;
import com.cross.juntalk2.retrofit.RefreshInterface;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
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

public class UserBulletinActivity extends CreateNewCompatActivity {

    private ActivityUserBulletinBinding binding;
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
    private UserBulletinBoardAdapter adapter;
    private BroadcastReceiver broadcastReceiver;
    private String type;
    private String userId, myId;
    private int myIndex , user_Index;
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
            api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
            powerMenu.dismiss();
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        /*if (adapter != null) {
            View childView = binding.bulletinBoardRecyclerView.getChildAt(0);
            if (childView != null) {
                UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) childView.getTag();
                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                if (imageOrVideoViewHolder != null) {
                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                }
            }
        }*/
        int firstVisibleItem = ((LinearLayoutManager) binding.bulletinBoardRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) binding.bulletinBoardRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
        if (bulletinBoardViewHolder != null) {
            int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {
            } else {

                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                if (imageOrVideoViewHolder == null) {

                } else {
                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if (adapter != null) {
            View childView = binding.bulletinBoardRecyclerView.getChildAt(0);
            if (childView != null) {
                UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) childView.getTag();
                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = bulletinBoardViewHolder.adapter.imageOrVideoViewHolder;
                if (imageOrVideoViewHolder != null) {
                    imageOrVideoViewHolder.binding.imageVideoView.pauseVideo();
                }
            }
        }*/

        int firstVisibleItem = ((LinearLayoutManager) binding.bulletinBoardRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        UserBulletinBoardAdapter.BulletinBoardViewHolder bulletinBoardViewHolder = (UserBulletinBoardAdapter.BulletinBoardViewHolder) binding.bulletinBoardRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
        if (bulletinBoardViewHolder != null) {
            int firstVisibleItem_ImageViewHolder = ((LinearLayoutManager) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (bulletinBoardViewHolder.adapter.imageOrVideoViewHolder == null) {

            } else {

                UserImageOrVideoAdapter.ImageOrVideoViewHolder imageOrVideoViewHolder = (UserImageOrVideoAdapter.ImageOrVideoViewHolder) bulletinBoardViewHolder.binding.imageOrVideoRecyclerView.findViewHolderForAdapterPosition(firstVisibleItem_ImageViewHolder);
                if (imageOrVideoViewHolder == null) {

                } else {
                    imageOrVideoViewHolder.binding.imageVideoView.releasePlayer();
                }
            }
        }
    }

    @Override
    public void getInterfaceInfo() {
        userId = getIntent().getStringExtra("userId");
        user_Index = getIntent().getIntExtra("user_Index", 0);
        myIndex = getIntent().getIntExtra("myIndex", 0);
        myId = getIntent().getStringExtra("myId");
        refreshInterface = new RefreshInterface() {
            @Override
            public void refresh() {
                Intent intent = new Intent();
                intent.setAction("FourthFragment");
                intent.putExtra("FourthFragment", "FourthFragment");
                sendBroadcast(intent);
                pageNum = 0;
                bullteinCopy.clear();
                api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UserBulletinActivity");
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pageNum = 0;
                bullteinCopy.clear();
                api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void getIntentInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        viewModelFactory = new ViewModelFactory();
        bulletinBoardViewModel = new ViewModelProvider(this, viewModelFactory).get(BulletinBoardViewModel.class);
        bulletinBoardViewModel.init();
        bulletinCommentViewModel = new ViewModelProvider(this, viewModelFactory).get(BulletinCommentViewModel.class);
        bulletinCommentViewModel.init();

        bullteinCopy = new ArrayList<>();

        Observer<List<BulletinBoardModel>> listObserver = new Observer<List<BulletinBoardModel>>() {
            @Override
            public void onChanged(List<BulletinBoardModel> bulletinBoardModels) {
                adapter.submitList(bulletinBoardViewModel.getBulletinBoardMutableLiveData().getValue());
                adapter.notifyDataSetChanged();
            }
        };
        bulletinBoardViewModel.getBulletinBoardMutableLiveData().observe(this, listObserver);
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(UserBulletinActivity.this, R.layout.activity_user_bulletin);
        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        handler = new Handler(Looper.getMainLooper());
        adapter = new UserBulletinBoardAdapter(new BulletinDiffUtil(), context, UserBulletinActivity.this);
        adapter.setHasStableIds(true);
        adapter.setRefreshInterface(refreshInterface);
        binding.titleTextView.setText("# 전체");
        binding.bulletinBoardRecyclerView.setAdapter(adapter);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        binding.bulletinBoardRecyclerView.setAnimation(alphaAnimation);
        type = "A";
        bullteinCopy.clear();
        api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing())
            overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
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

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void createThings() {

    }

    @Override
    public void clickEvent() {

        binding.bulletinBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (paging && lastVisibleItem == bulletinBoardViewModel.getBulletinBoardMutableLiveData().getValue().size() - 1) {
                    pageNum++;
                    api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
                    paging = false;
                }
            }
        });


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bullteinCopy.clear();
                pageNum = 0;
                api_getBulletinBoard(userId, myIndex, myId, type, pageNum);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.addBulletinBoardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateBulletinBoardItemActivity.class);
                startActivity(intent);
            }
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
                .setMenuRadius(10.0f)
                .setPadding(20)
                .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                .setSelectedMenuColor(ContextCompat.getColor(context, R.color.teal_200))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();

        binding.menuFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powerMenu.showAtCenter(binding.getRoot());

            }
        });
    }

    public void api_getBulletinBoard(String userId, int myIndex, String myId, String category, int pageNum) {
        if (!UserBulletinActivity.this.isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        Observable<List<BulletinBoardModel>> bulletinBoardObservable =
                service.personal_BulletinBoard(CommonString.bulletinBoardController, userId, myIndex, myId, category, pageNum);
        compositeDisposable.add(bulletinBoardObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<List<BulletinBoardModel>>() {
                    @Override
                    public void onNext(@NotNull List<BulletinBoardModel> bulletinBoardModels) {
                        bullteinCopy.addAll(bulletinBoardModels);
                        bulletinBoardViewModel.setItems(bullteinCopy);
                        if (!bulletinBoardModels.isEmpty()) {
                            paging = true;
                        } else {
                            paging = false;
                        }

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error  :" + e.getMessage());
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

    @Override
    public void getServer() {

    }
}