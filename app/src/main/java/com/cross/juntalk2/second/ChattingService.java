package com.cross.juntalk2.second;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cross.juntalk2.R;
import com.cross.juntalk2.model.ChattingModel;

public class ChattingService extends Service {

    private IBinder mBinder = new ChattingService.MyBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public ChattingService getService() { // 서비스 객체를 리턴
            return ChattingService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY;
        } else {
            createNotification(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void createNotification(Intent intent) {

        ChattingModel chattingModel = (ChattingModel) intent.getSerializableExtra("chattingModel");

        int NOTIFICATION_ID = 0;
        String channelId = getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("채널에 대한 설명.");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), ChattingRoomActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_chatting_notification);
        remoteViews.setTextViewText(R.id.nickNameTextView, "chattingModel.userId");
        remoteViews.setTextViewText(R.id.regDateTextView, "chattingModel.userConversationTime");
        remoteViews.setTextViewText(R.id.conversationTextView, "chattingModel.userConversation");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Title")
                .setContentText("Content")
                .setSmallIcon(R.drawable.juntalk_logo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
                .setAutoCancel(true)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        /*NotificationTarget notificationTarget = new NotificationTarget(this.getApplicationContext(), R.id.mainPhotoImageView, remoteViews, builder.build(), NOTIFICATION_ID);
        GlideApp.with(getApplicationContext()).asBitmap().load(CommonString.CommonStringInterface.FileBaseUrl + chattingModel.userImage)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //할일
                        GlideApp.with(getApplicationContext()).asBitmap().circleCrop().load(resource).into(notificationTarget);

                    }
                });*/
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
