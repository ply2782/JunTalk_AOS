package com.cross.juntalk2.first;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityNoticeBinding;
import com.cross.juntalk2.diffutil.CommonNoticeDiffUtil;
import com.cross.juntalk2.model.CommonNoticeModel;
import com.cross.juntalk2.model.CommonNoticeViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeActivity extends CreateNewCompatActivity {
    private Handler handler;
    private LoadingDialog loadingDialog;
    private CompositeDisposable compositeDisposable;
    private ActivityNoticeBinding binding;
    private NoticeAdapter adapter;
    private Observer<List<CommonNoticeModel>> observer;
    private ViewModelFactory factory;
    private CommonNoticeViewModel noticeViewModel;
    private RetrofitService service;

    public interface ItemClickInterface {
        void addItems(String position, CommonNoticeModel commonNoticeModel);

        void removeItems(String position, CommonNoticeModel commonNoticeModel);
    }

    private ItemClickInterface ItemClickInterface;
    private List<String> itemNumber;
    private List<CommonNoticeModel> commonNoticeModels;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing())
            overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
    }

    @Override
    public void getInterfaceInfo() {

        if (context instanceof ItemClickInterface) {
            ItemClickInterface = (ItemClickInterface) context;
        }

        itemNumber = new ArrayList<>();
        commonNoticeModels = new ArrayList<>();
        ItemClickInterface = new ItemClickInterface() {
            @Override
            public void addItems(String position, CommonNoticeModel commonNoticeModel) {
                itemNumber.add(position);
                commonNoticeModels.add(commonNoticeModel);

            }

            @Override
            public void removeItems(String position, CommonNoticeModel commonNoticeModel) {
                itemNumber.remove(position);
                commonNoticeModels.remove(commonNoticeModel);

            }

        };

    }

    @Override
    public void getIntentInfo() {
        service = RetrofitClient.getInstance().getServerInterface();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
        compositeDisposable.clear();
        Intent intent = new Intent();
        intent.setAction("FirstFragment");
        intent.putExtra("FirstFragment", "FirstFragment");
        sendBroadcast(intent);

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void init() {

        factory = new ViewModelFactory();
        noticeViewModel = new ViewModelProvider(this, factory).get(CommonNoticeViewModel.class);
        noticeViewModel.init();
        binding = DataBindingUtil.setContentView(NoticeActivity.this, R.layout.activity_notice);
        adapter = new NoticeAdapter(new CommonNoticeDiffUtil(), context);
        binding.noticeRecyclerView.setAdapter(adapter);
        adapter.setItemClickInterface(ItemClickInterface);
        PhItemAnimator phItemAnimator = new PhItemAnimator(context);
        binding.noticeRecyclerView.setItemAnimator(phItemAnimator);


    }

    @Override
    public void createThings() {
        handler = new Handler(Looper.getMainLooper());
        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        observer = new Observer<List<CommonNoticeModel>>() {
            @Override
            public void onChanged(List<CommonNoticeModel> commonNoticeModels) {
                adapter.submitList(noticeViewModel.getCommmonMutableLiveData().getValue());
                adapter.notifyDataSetChanged();

            }
        };
        noticeViewModel.getCommmonMutableLiveData().observe(this, observer);

    }

    @Override
    public void clickEvent() {
        binding.deleteNoticeButton.setOnClickListener(v -> {
            if (itemNumber == null || itemNumber.size() <= 0) {
                Toast.makeText(context, "선택된 아이템이 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                List<Integer> itemIntegerList = new ArrayList<>();
                for (String s : itemNumber) {
                    itemIntegerList.add(Integer.parseInt(s));
                }

                api_RemoveNotice(itemIntegerList);
            }
        });
        binding.closeImageButton.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
        });
    }


    @Override
    public void getServer() {
        Observable<List<CommonNoticeModel>> observable = service.loadNotice(CommonString.commonNoticeController);
        compositeDisposable.add(
                observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<List<CommonNoticeModel>>() {
                    @Override
                    public void onNext(@NotNull List<CommonNoticeModel> commonNoticeModels) {
                        Log.e("abc", "commonNoticeModels : " + commonNoticeModels.toString());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.show();
                                if (isFinishing()) {
                                    loadingDialog.dismiss();
                                }
                            }
                        });
                        noticeViewModel.setItems(commonNoticeModels);

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        loadingDialog.dismiss();
                        Log.e("abc", "error : " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));

        service.loadMainNoticeContent(CommonString.commonNoticeController).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("abc",""+response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.noticeTextView.setText("" + response.body());
                            binding.noticeTextView.setSingleLine(true);    // 한줄로 표시하기
                            binding.noticeTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE); // 흐르게 만들기
                            binding.noticeTextView.setSelected(true);

                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            binding.noticeTextView.setText("# 공지사항");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc","error : "+ t.getMessage());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.noticeTextView.setText("서버와의 연결이 불안정합니다.");
                    }
                });
            }
        });
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

    public void api_RemoveNotice(List<Integer> noticeNumber) {
        service.removeNotice(CommonString.commonNoticeController, noticeNumber).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                Log.e("abc", "response toString : " + response.toString());
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            noticeViewModel.removeAllItems(commonNoticeModels);
                            itemNumber.clear();
                        }
                    });
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
}