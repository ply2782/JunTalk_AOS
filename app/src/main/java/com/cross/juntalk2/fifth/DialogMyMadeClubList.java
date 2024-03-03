package com.cross.juntalk2.fifth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogMymadeclublistBinding;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.loopeer.cardstack.CardStackView;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogMyMadeClubList extends Dialog {
    private Context context;
    private RetrofitService service;
    private Handler handler;
    private StringBuilder builder;
    private DialogMymadeclublistBinding binding;
    private Top10ClubListAdapter mTestStackAdapter;
    private CardStackView.ItemExpendListener itemExpendListener = new CardStackView.ItemExpendListener() {
        @Override
        public void onItemExpend(boolean expend) {
            Log.e("abc","expend : "+ expend);
        }
    };
    private List<ClubModel> myClubList;

    public interface RemoveInterface {
        void remove(ClubModel clubModel);
    }

    private RemoveInterface removeInterface;

    public DialogMyMadeClubList(@NonNull Context context) {
        super(context);
        this.context = context;
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        handler = new Handler(Looper.getMainLooper());
        builder = new StringBuilder();
        if (context instanceof RemoveInterface) {
            removeInterface = (RemoveInterface) context;
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void setMyClubList(List<ClubModel> myClubList) {
        this.myClubList = myClubList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_mymadeclublist, null, false);
        setContentView(binding.getRoot());
        removeItems();
        init();
    }

    public void removeItems() {
        removeInterface = new RemoveInterface() {
            @Override
            public void remove(ClubModel clubModel) {
                service.deleteClubList(CommonString.clubController, clubModel).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.e("abc", "" + response.raw());
                        if (response.isSuccessful()) {
                            Intent intent = new Intent();
                            intent.setAction("createClub");
                            intent.putExtra("reFreshMyClubList", "reFreshMyClubList");
                            context.sendBroadcast(intent);
                            dismiss();


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
    }

    public void init() {
        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        Point deviceSize = new Point();
        display.getSize(deviceSize);
        getWindow().getAttributes().width = deviceSize.x;
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
        binding.top10ClubListView.setItemExpendListener(itemExpendListener);
        mTestStackAdapter = new Top10ClubListAdapter(context);
        binding.top10ClubListView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.setRemoveInterface(removeInterface);


    }


    public void settingData() {
        if (myClubList != null && !myClubList.isEmpty()) {
            try {
                mTestStackAdapter.updateData(myClubList);
                binding.top10ClubListView.clearScrollYAndTranslation();
            } catch (Exception e) {

            }
        }
    }

}
