package com.cross.juntalk2.utils;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cross.juntalk2.main.WholeActivity;


public abstract class CreateNewFragment extends Fragment {

    public Activity activity;
    public Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }


    public void CreateNewFragment() {
        //인터페이스
        getInterfaceInfo();
        //인텐트 정보
        getIntentInfo();
        //초기화
        init();
        //변수 설정
        createObjects();
        //클릭 이벤트
        clickEvent();
    }


    public abstract void getInterfaceInfo();

    public abstract void getIntentInfo();

    public abstract void init();

    public abstract void createObjects();

    public abstract void clickEvent();


}
