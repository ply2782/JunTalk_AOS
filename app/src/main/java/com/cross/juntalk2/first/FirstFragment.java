package com.cross.juntalk2.first;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentFirstBinding;
import com.cross.juntalk2.databinding.NavigationHeaderViewBinding;
import com.cross.juntalk2.diffutil.FriendListDiffUtil;
import com.cross.juntalk2.main.MainActivity;
import com.cross.juntalk2.model.FriendModel;
import com.cross.juntalk2.model.FriendViewModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.PayInfoModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.room.ChattingDataDB;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewFragment;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kakao.sdk.user.UserApiClient;
import com.skydoves.powermenu.CircularEffect;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends CreateNewFragment {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String ARG_PARAM1 = "myModel";
    private MyModel myModel;
    private FragmentFirstBinding binding;
    private FriendViewModel friendViewModel;
    private ViewModelFactory viewModelFactory;
    private FirstAdapter firstAdapter;
    private BirthdayAdapter birthdayAdapter;
    private RetrofitService service;
    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private NavigationHeaderViewBinding headerViewBinding;
    private View headerView;
    private Animation slideLeft, slideRight;
    private Handler handler;
    private PrayRequestAdapter prayRequestAdapter;
    private Observer<List<FriendModel>> friendListObserver;
    private BroadcastReceiver broadcastReceiver;
    private Gson gson;
    private Type type;
    private Observer<List<Map<String, Object>>> blockUserObserver;
    private CustomPowerMenu customPowerMenu;
    private List<String> licenseItem;
    // 파이어베이스 데이터베이스 연동
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference는 데이터베이스의 특정 위치로 연결하는 거라고 생각하면 된다.
    //현재 연결은 데이터베이스에만 딱 연결해놓고
    //키값(테이블 또는 속성)의 위치 까지는 들어가지는 않은 모습이다.
    private DatabaseReference databaseReference = database.getReference();
    private ChattingDataDB chattingDataDB;

    @Override
    public void onStop() {
        super.onStop();
        //TODO : 레트로핏 서버 통신후 남아있는 스레드 종료

    }

    public FirstFragment() {
        // Required empty public constructor

    }


    public static FirstFragment newInstance(MyModel param1) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setFirstFragment(this);
        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        CreateNewFragment();
        binding.setMyModel(myModel);


        return binding.getRoot();
    }

    public void editImageButtonOnClick(View view) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("type", "me");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);

    }

    @Override
    public void getInterfaceInfo() {
        handler = new Handler(Looper.getMainLooper());
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }


    }

    @Override
    public void getIntentInfo() {
        chattingDataDB = ChattingDataDB.getInstance(context);
    }


    @Override
    public void init() {
        gson = new GsonBuilder().create();
        type = new TypeToken<List<FriendModel>>() {
        }.getType();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        headerView = binding.navigationView.getHeaderView(0);
        headerViewBinding = NavigationHeaderViewBinding.bind(headerView);
        headerViewBinding.setName("# " + myModel.userId);
        headerViewBinding.setMyImage(myModel.userMainPhoto);

        slideLeft = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        slideRight = AnimationUtils.loadAnimation(context, R.anim.slide_right);

        viewModelFactory = new ViewModelFactory();
        friendViewModel = new ViewModelProvider(this, viewModelFactory).get(FriendViewModel.class);
        friendViewModel.init();


        firstAdapter = new FirstAdapter(new FriendListDiffUtil(), context, activity);
        birthdayAdapter = new BirthdayAdapter(new FriendListDiffUtil());
        prayRequestAdapter = new PrayRequestAdapter(new FriendListDiffUtil());


        /**PhItemAnimator phItemAnimator = new PhItemAnimator(context);*/
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);

        binding.birthdayRecyclerView.setAdapter(birthdayAdapter);
        binding.birthdayRecyclerView.setAnimation(alphaAnimation);
        /**binding.birthdayRecyclerView.setItemAnimator(phItemAnimator);*/
        binding.requestRecyclerView.setAdapter(prayRequestAdapter);
        binding.requestRecyclerView.setAnimation(alphaAnimation);
        /**binding.requestRecyclerView.setItemAnimator(phItemAnimator);*/
        binding.myFriendRecyclerView.setAdapter(firstAdapter);
        binding.myFriendRecyclerView.setAnimation(alphaAnimation);
        /**binding.myFriendRecyclerView.setItemAnimator(phItemAnimator);*/

    }

    @Override
    public void createObjects() {

        String[] array = getResources().getStringArray(R.array.openlicense);
        licenseItem = Arrays.asList(array);
        friendListObserver = new Observer<List<FriendModel>>() {
            @Override
            public void onChanged(List<FriendModel> friendModels) {
                prayRequestAdapter = new PrayRequestAdapter(new FriendListDiffUtil());
                prayRequestAdapter.submitList(friendViewModel.getFriendListMutableLiveData().getValue());
                binding.requestRecyclerView.setAdapter(prayRequestAdapter);
                /*prayRequestAdapter.notifyDataSetChanged();*/

            }
        };
        friendViewModel.getFriendListMutableLiveData().observe(this, friendListObserver);
        blockUserListApi("blockUser");
        api_MainList("Main");
        myPayInfo();
        api_UnReadCount();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                if (!dataSnapshot.hasChild("UserInfos/" + myModel.userId)) {

                    //TODO : 없을 때 저장해주기
                    Log.e("abc", "has false");
                    Map<String, Object> firebaseMap = new HashMap<>();
                    firebaseMap.put("userId", myModel.userId);
                    databaseReference.child("UserInfos").child(myModel.userId).setValue(firebaseMap);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e("abc", "error : " + databaseError.getDetails());
            }
        };
//        databaseReference.addValueEventListener(postListener);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.clear();
        compositeDisposable.dispose();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();

    }


    public void blockUserListApi(String type) {
        if (type.equals("blockUser")) {
            service.blockUserList(CommonString.userController, myModel.user_Index).enqueue(new Callback<List<FriendModel>>() {
                @Override
                public void onResponse(Call<List<FriendModel>> call, Response<List<FriendModel>> response) {
                    Log.e("abc", "" + response.raw());
                    if (response.isSuccessful()) {
                        List<FriendModel> blockList = response.body();

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<List<FriendModel>> call, Throwable t) {
                    Log.e("abc", "error : " + t.getMessage());
                }
            });
        }

    }

    public void api_UnReadCount() {

        service.unReadCount(CommonString.userController, myModel.userKakaoOwnNumber).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    int unReadCount = response.body();

                    if (binding != null) {
                        binding.setUnReadCount(unReadCount);
                    }

                } else {
                    if (binding != null) {
                        binding.setUnReadCount(0);
                    }

                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "서버와의 통신이 원할하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void api_MainList(String direct) {
        Observable<Map<String, Object>> friendModelObservable = service.mainList("userController", myModel.userKakaoOwnNumber, simpleDateFormat.format(new Date().getTime()), "pray");
        compositeDisposable.add(friendModelObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Map<String, Object>>() {
                    @Override
                    public void onNext(@NotNull Map<String, Object> map) {
                        List<FriendModel> friendModelList = gson.fromJson(map.get("mainFriendModel").toString(), type);
                        binding.setFriendModelList(friendModelList);

                        List<FriendModel> birthdayModelList = gson.fromJson(map.get("friendBirthdayModel").toString(), type);
                        binding.setFriendBirthdayModelList(birthdayModelList);

                        List<FriendModel> prayRequestModelList = gson.fromJson(map.get("userPrayRequestList").toString(), type);
                        friendViewModel.setItems(prayRequestModelList);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (prayRequestModelList != null && prayRequestModelList.size() > 0) {
                                    binding.requestRecyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    binding.requestRecyclerView.setVisibility(View.GONE);
                                }
                            }
                        });


                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error : " + e.getMessage());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                        loadingDialog.dismiss();
                    }
                }));
    }

    @Override
    public void clickEvent() {

        headerViewBinding.helloLottie.setOnClickListener(v -> {
            Intent intent = new Intent(context, PayActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
        });


        headerViewBinding.lottieAnimation.setOnClickListener(v -> {
            Intent intent = new Intent(context, PayActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
        });


        binding.noticeImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoticeActivity.class);
            startActivity(intent);
            activity.overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
        });

        binding.navigationView.bringToFront();
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.sendMessageForAdmin:
                        intent = new Intent(context, RequestForAdminActivity.class);
                        startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        break;

                    case R.id.policy:
                        intent = new Intent(context, PolicyActivity.class);
                        intent.putExtra("policyUrl", "http://ply2782ply2782.cafe24.com:8080/privacy.html");
                        startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                        break;
                    case R.id.myService:
//                        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title));
//                        startActivity(new Intent(context, OssLicensesMenuActivity.class));
                        customPowerMenu = new CustomPowerMenu.Builder<>(context, new CustomPopUpMenuAdapter())
                                .setHeaderView(R.layout.dialog_popupmenu_header) // header used for title
                                .setFooterView(R.layout.dialog_popupmenu_footer) // footer used for Read More and Close buttons
                                // this is body
                                .setWidth(1000)
                                .addItemList(licenseItem)
                                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                                .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                                .setMenuRadius(10f) // sets the corner radius.
                                .setMenuShadow(10f) // sets the shadow.
                                .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                                .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                                .build();
                        customPowerMenu.showAtCenter(binding.getRoot());
                        break;

                    case R.id.deleteMyAccount:
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View footerView = inflater.inflate(R.layout.dialog_deleteaccount_footer, null, false);
                        AppCompatButton okButton = footerView.findViewById(R.id.okButton);
                        AppCompatButton cancelButton = footerView.findViewById(R.id.cancelButton);

                        PowerMenu powerMenu = new PowerMenu.Builder(context)
                                .setHeaderView(R.layout.dialog_popupmenu_header) // header used for title
                                .setFooterView(footerView) // footer used for Read More and Close buttons
                                .addItem(new PowerMenuItem("회원을 탈퇴하시겠습니까?", false)) // this is body
                                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                                .setMenuRadius(10f)
                                .setMenuShadow(10f)
                                .setWidth(800)
                                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                                .setAnimation(MenuAnimation.ELASTIC_CENTER) // Animation start point (TOP | LEFT).
                                .setMenuRadius(10f) // sets the corner radius.
                                .setMenuShadow(10f) // sets the shadow.
                                .setCircularEffect(CircularEffect.BODY) // shows circular revealed effects for all body of the popup menu.
                                .setCircularEffect(CircularEffect.INNER) // Shows circular revealed effects for the content view of the popup menu.
                                .setSelectedEffect(false)
                                .build();
                        powerMenu.showAtCenter(binding.getRoot());
                        okButton.setOnClickListener(v -> {
                            api_DeleteAccount();
                            powerMenu.dismiss();
                        });

                        cancelButton.setOnClickListener(v -> powerMenu.dismiss());


                        break;

                    case R.id.logOutAccount:
                        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                if (throwable != null) {
                                    Log.e("abc", "error : " + throwable.getMessage());
                                } else {
                                    /*Session.getCurrentSession().close();*/
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                return null;
                            }
                        });

                }
                return true;
            }
        });

        headerViewBinding.backArrowImageButton.setOnClickListener(v -> binding.drawerLayout.closeDrawer(GravityCompat.END));
        binding.optionImageButton.setOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END);

            }
        });
    }

    public void myPayInfo() {
        service.userPayInfo(CommonString.userController, myModel.user_Index, myModel.userId).enqueue(new Callback<List<PayInfoModel>>() {
            @Override
            public void onResponse(Call<List<PayInfoModel>> call, Response<List<PayInfoModel>> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    List<PayInfoModel> payInfoModels = response.body();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (payInfoModels != null && payInfoModels.size() > 0) {
                                headerViewBinding.lottieAnimation.setVisibility(View.GONE);
                                headerViewBinding.subscribeTextView.setVisibility(View.GONE);
                                headerViewBinding.helloLottie.setVisibility(View.VISIBLE);
                                headerViewBinding.subscribeTextViewHelloLottie.setVisibility(View.VISIBLE);

                            } else {

                                headerViewBinding.subscribeTextView.setVisibility(View.VISIBLE);
                                headerViewBinding.lottieAnimation.setVisibility(View.VISIBLE);
                                headerViewBinding.helloLottie.setVisibility(View.GONE);
                                headerViewBinding.subscribeTextViewHelloLottie.setVisibility(View.GONE);

                            }
                        }
                    });


                } else {

                }
            }

            @Override
            public void onFailure(Call<List<PayInfoModel>> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }


    public void api_DeleteAccount() {

        service.deleteAccount(CommonString.userController, myModel.userKakaoOwnNumber).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    /*Utils.resetApp(context);*/
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            chattingDataDB.chattingDao().removeAllDataInConversation();
                        }
                    });
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

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

    public void checkPayInfo() {
        myModel = JunApplication.getMyModel();
        if (myModel.myPayInfo != null && myModel.myPayInfo.size() > 0) {
            headerViewBinding.lottieAnimation.setVisibility(View.GONE);
            headerViewBinding.subscribeTextView.setVisibility(View.GONE);
            headerViewBinding.helloLottie.setVisibility(View.VISIBLE);
            headerViewBinding.subscribeTextViewHelloLottie.setVisibility(View.VISIBLE);

        } else {

            headerViewBinding.subscribeTextView.setVisibility(View.VISIBLE);
            headerViewBinding.lottieAnimation.setVisibility(View.VISIBLE);
            headerViewBinding.helloLottie.setVisibility(View.GONE);
            headerViewBinding.subscribeTextViewHelloLottie.setVisibility(View.GONE);

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("FirstFragment");
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("abc", "onResume In BroadCast");

                PhItemAnimator phItemAnimator = new PhItemAnimator(context);
                firstAdapter = new FirstAdapter(new FriendListDiffUtil(), context, activity);
                birthdayAdapter = new BirthdayAdapter(new FriendListDiffUtil());
                prayRequestAdapter = new PrayRequestAdapter(new FriendListDiffUtil());
                if (binding != null) {
                    binding.birthdayRecyclerView.setAdapter(birthdayAdapter);
                    binding.birthdayRecyclerView.setItemAnimator(phItemAnimator);
                    binding.requestRecyclerView.setAdapter(prayRequestAdapter);
                    binding.requestRecyclerView.setItemAnimator(phItemAnimator);
                    binding.myFriendRecyclerView.setAdapter(firstAdapter);
                    binding.myFriendRecyclerView.setItemAnimator(phItemAnimator);
                }


                checkPayInfo();
                isExistOfId("onResume");
                api_MainList("onResume");
                api_UnReadCount();
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
        }

    }

    public boolean onBackPressed() {
        if (customPowerMenu != null) {
            if (customPowerMenu.isShowing()) {
                customPowerMenu.dismiss();
            }
        }
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        } else {
            return false;
        }
    }

    public class PhItemAnimator extends SimpleItemAnimator {

        private Context mContext;

        public PhItemAnimator(Context a_context) {

            mContext = a_context;
        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder a_holder) {
            return false;
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder a_holder) {
            Animator animator = AnimatorInflater.loadAnimator(mContext, R.animator.zoom_in);
            animator.setInterpolator(new BounceInterpolator());
            animator.setTarget(a_holder.itemView);
            animator.start();
            return true;
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder a_holder, int a_fromX, int a_fromY, int a_toX, int a_toY) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder a_oldHolder, RecyclerView.ViewHolder a_newHolder, int a_fromLeft, int a_fromTop, int a_toLeft, int a_toTop) {
            return false;
        }

        @Override
        public void runPendingAnimations() {

        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder a_item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }

    public void isExistOfId(String direct) {

        Observable<MyModel> booleanResult = service.isExistOfId("userController", myModel.userKakaoOwnNumber);
        compositeDisposable.add(booleanResult.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<MyModel>() {
                    @Override
                    public void onNext(@NotNull MyModel myModel) {
                        if (myModel == null) {

                        } else {
                            JunApplication.setMyModel(myModel);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    binding.setMyModel(myModel);
                                }
                            });
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (activity != null) {
                                    if (activity.isFinishing() && loadingDialog.isShowing()) {
                                        loadingDialog.dismiss();
                                    }
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));
    }


}
