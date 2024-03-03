package com.cross.juntalk2.utils;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class CreateNewCompatActivity extends AppCompatActivity {

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createObjects();
        CreateNewCompatActivity();
    }

    public void createObjects(){
        context = this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void CreateNewCompatActivity() {
        //인텐트로 값 넘길때 받아오기
        getIntentInfo();
        //인터페이스 정보 가져오기
        getInterfaceInfo();
        //초기화
        init();
        //변수 및 기타 생성
        createThings();
        //클릭시 이벤트
        clickEvent();
        //서버 내용 가져오기
        getServer();
    }


    public abstract void getInterfaceInfo();


    public abstract void getIntentInfo();

    public abstract void init();

    public abstract void createThings();


    public abstract void clickEvent();

    public abstract void getServer();


}
