package com.cross.juntalk2.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;

public class NotificationService extends Service {

    private MediaPlayer mediaPlayer;
    private IBinder mBinder = new MyBinder();


    public class MyBinder extends Binder {
        public NotificationService getService() { // 서비스 객체를 리턴
            return NotificationService.this;
        }
    }

    public NotificationService() {
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다
        return mBinder; // 서비스 객체를 리턴
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        } else {
            processComand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void processComand(Intent intent) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "Play":
                    playMusic();
                    break;
                case "Stop":
                    stopMusic();
                    break;
            }
        }
    }

    public void playMusic() {
        try{
            mediaPlayer.start();
        }catch (Exception e){

        }

    }

    public void stopMusic() {
        mediaPlayer.pause();
    }
}
