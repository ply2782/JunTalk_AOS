package com.cross.juntalk2.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cross.juntalk2.R;
import com.cross.juntalk2.main.MainActivity;
import com.cross.juntalk2.main.WholeActivity;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.second.ChattingRoomActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "abc";
    private MyModel myModel = JunApplication.getMyModel();
    private Handler handler;
    private RetrofitService service;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        handler = new Handler(Looper.getMainLooper());
        // Check if message contains a data payload.
        try {

            if (remoteMessage.getData().size() > 0) {
                Log.e(TAG, "Message data payload: " + remoteMessage.getData());

                if (/* Check if data needs to be processed by long running job */ true) {
                    // For long-running tasks (10 seconds or more) use WorkManager.
                    Map<String, String> data = remoteMessage.getData();

                    if ((String) data.get("type") != null) {

                        if (((String) data.get("type")).equals("friendRequest")) {

//                            friendRequest(data);
                            String friendId = data.get("room_JoinPeopleName");
                            String room_Uuid = data.get("room_Uuid");
                            String friendMainPhoto = data.get("room_JoinPeopleImage");
                            String room_Index = data.get("room_Index");
                            createNotification(friendId, Integer.parseInt(room_Index), room_Uuid, friendMainPhoto);

                        } else if (((String) data.get("type")).equals("chatting")) {
                            Intent generalChattingIntent = new Intent();
                            generalChattingIntent.setAction("SecondFragment");
                            generalChattingIntent.putExtra("SecondFragment", "SecondFragment");
                            sendBroadcast(generalChattingIntent);

                            Intent openChattingIntent = new Intent();
                            openChattingIntent.setAction("OpenChatting");
                            openChattingIntent.putExtra("OpenChatting", "OpenChatting");
                            sendBroadcast(openChattingIntent);

                            String userId = data.get("title");
                            String message = data.get("body");
                            String userImage = data.get("image");
                            String room_Uuid = data.get("room_Uuid");
                            String room_Index = data.get("room_Index");
                            sendChattingNotification(room_Uuid, room_Index, userId, userImage, message);
//                            chatting(data);
                        } else if (((String) data.get("type")).equals("join")) {
                            Intent intent = new Intent();
                            intent.setAction("FirstFragment");
                            intent.putExtra("FirstFragment", "FirstFragment");
                            sendBroadcast(intent);
                        } else if (((String) data.get("type")).equals("unReadCount")) {
                            Intent intent = new Intent();
                            intent.setAction("SecondFragment");
                            intent.putExtra("SecondFragment", "SecondFragment");
                            sendBroadcast(intent);

                            String userImage = (String) data.get("userImage");
                            String userConversation = (String) data.get("userConversation");
                            String id = (String) data.get("userId");
                            sendNotification(id, userConversation, userImage);
                        } else if (((String) data.get("type")).equals("clubListAccepted")) {
                            String club_Uuid = data.get("club_Uuid");
                            String club_Index = data.get("club_index");
                            String type = data.get("type");
                            String friendId = data.get("friendId");
                            clubListNotification(club_Index, club_Uuid, friendId, type);


                        } else if (((String) data.get("type")).equals("clubListRejected")) {
                            String club_Uuid = data.get("club_Uuid");
                            String club_Index = data.get("club_index");
                            String type = data.get("type");
                            String friendId = data.get("friendId");
                            clubListNotification(club_Index, club_Uuid, friendId, type);

                        } else if (((String) data.get("type")).equals("JoinChattingRoom")) {
                            sendNotificationJoinChattingRoom(data);

                        } else if (((String) data.get("type")).equals("RequestJoin")) {
                            sendNotificationRequestJoin(data);
                        } else if (((String) data.get("type")).equals("alarmCurrentClub")) {
                            sendNotificationCurrentClub(data);
                        }


                    }


                } else {
                    // Handle message within 10 seconds
                    handleNow();
                }
            }


            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                if (JunApplication.getSaveAutoLogin().getBoolean("enter", false) == true) {

                } else {
                    Log.e("abc", "remoteMessage.getNotification().getBody() : " + remoteMessage.getNotification().getBody());
                    String[] bodySplit = remoteMessage.getNotification().getBody().split(",");
                    sendNotification(remoteMessage.getNotification().getTitle(), bodySplit[1], bodySplit[0]);
                }
            }
        } catch (Exception e) {

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        api_updateFireBaseToken(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    public void api_updateFireBaseToken(String userToken) {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userToken", userToken);
        userInfoMap.put("userKakaoOwnNumber", myModel.userKakaoOwnNumber);
        service.updateToken(CommonString.fcmController, userInfoMap).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Log.e("abc", "success");
                } else {
                    Log.e("abc", "fail");
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error :  " + t.getMessage());
            }
        });
    }

    private void friendRequest(Map<String, String> data) {

        Intent intent = new Intent();
        intent.setAction("SecondFragment");
        intent.putExtra("SecondFragment", "SecondFragment");
        sendBroadcast(intent);

        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH));
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + data.get("room_JoinPeopleImage"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.juntalk_logo);
        builder.setLargeIcon(userImageBitmap);
        builder.setTicker("알람 간단한 설명");
        builder.setContentTitle(data.get("title"));
        builder.setContentText(data.get("room_JoinPeopleName") + "님이 초대하셨습니다.");
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setNumber(1);
        builder.setVibrate(new long[]{1000});
        builder.setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, 5000);*/


        /*String userName = data.get("title");
        long currentTime = System.currentTimeMillis();
        GlideApp.with(getApplicationContext()).asBitmap().load(CommonString.CommonStringInterface.FileBaseUrl + data.get("room_JoinPeopleImage"))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //할일
                        IconCompat iconCompat = IconCompat.createWithBitmap(resource);  // 1
                        Person owner = new Person.Builder().setIcon(iconCompat).setName(userName).build();// 4
                        NotificationCompat.MessagingStyle inboxStyle = new NotificationCompat.MessagingStyle(owner);
                        inboxStyle.addMessage(data.get("room_JoinPeopleName") + "님이 초대하셨습니다.", currentTime, owner);
                        builder.setStyle(inboxStyle);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }
                });*/
//        builder.setColor(Color.BLUE);
//        builder.setProgress(100,100,true);
//        builder.addAction(android.R.drawable.btn_star, "왼쪽으로 밀어주세요 ",null);
        // id값은
        // 정의해야하는 각 알림의 고유한 int값

    }


    private void sendNotificationCurrentClub(Map<String, String> data) {

        Intent intent = new Intent();
        intent.setAction("createClub");
        intent.putExtra("createClub", "createClub");
        sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction("clubList");
        intent1.putExtra("clubList", "clubList");
        sendBroadcast(intent1);

        String title = data.get("title");
        String currentClubCount = data.get("currentClubCount");


        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent notificationIntent = new Intent(getApplicationContext(), WholeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_accepted);
        builder.setLargeIcon(icon);
        builder.setSmallIcon(R.drawable.juntalk_logo);
        builder.setContentTitle(title);
        builder.setContentText("현재 활성화 된 클럽은 " + currentClubCount + " 입니다.\n많은 참여 부탁드립니다.");
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setNumber(1);
        builder.setVibrate(new long[]{1000});
        builder.setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, 5000);*/
    }

    private void sendNotificationRequestJoin(Map<String, String> data) {

        Intent intent = new Intent();
        intent.setAction("createClub");
        intent.putExtra("createClub", "createClub");
        sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction("clubList");
        intent1.putExtra("clubList", "clubList");
        sendBroadcast(intent1);

        String userMainPhoto = data.get("userMainPhoto");
        String userId = data.get("userId");

        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent notificationIntent = new Intent(getApplicationContext(), WholeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + userMainPhoto);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setLargeIcon(userImageBitmap);
        builder.setSmallIcon(R.drawable.ic_accepted);
        builder.setContentTitle("클럽 참여 요청");
        builder.setContentText(userId + "님이 클럽 참여 요청을 하였습니다.");
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setNumber(1);
        builder.setVibrate(new long[]{1000});
        builder.setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, 5000);*/
    }


    private void sendNotificationJoinChattingRoom(Map<String, String> data) {

        Intent intent = new Intent();
        intent.setAction("SecondFragment");
        intent.putExtra("SecondFragment", "SecondFragment");
        sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction("createClub");
        intent1.putExtra("reFreshMyClubList", "reFreshMyClubList");
        sendBroadcast(intent1);

        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_DEFAULT));
        }

        Intent notificationIntent = new Intent(getApplicationContext(), WholeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.drawable.ic_accepted);
        builder.setTicker("알람 간단한 설명");
        builder.setContentTitle(data.get("room_Title"));
        builder.setContentText("클럽 전용 채팅방이 생성되었습니다.");
        builder.setWhen(System.currentTimeMillis());
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setAutoCancel(true);
        builder.setNumber(1);
        builder.setVibrate(new long[]{1000});
        builder.setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, 5000);*/
    }

    @SuppressLint("CheckResult")
    public void createNotification(String friendId, int room_Index, String room_Uuid, String friendMainPhoto) {


        Intent secondIntent = new Intent();
        secondIntent.setAction("SecondFragment");
        secondIntent.putExtra("SecondFragment", "SecondFragment");
        sendBroadcast(secondIntent);

        Intent firstIntent = new Intent();
        firstIntent.setAction("FirstFragment");
        firstIntent.putExtra("FirstFragment", "FirstFragment");
        sendBroadcast(firstIntent);

        /*Intent chattingIntent_Accept = new Intent(getApplicationContext(), MainActivity.class);
        RoomModel roomModel = new RoomModel();
        roomModel.room_Index = room_Index;
        roomModel.room_Uuid = room_Uuid;
        chattingIntent_Accept.putExtra("roomModel", roomModel);
        chattingIntent_Accept.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent_Accept = PendingIntent.getActivity(getApplicationContext(), 0, chattingIntent_Accept, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent chattingIntent_Reject = new Intent(getApplicationContext(), MainActivity.class);
        chattingIntent_Reject.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent_Reject = PendingIntent.getActivity(getApplicationContext(), 0, chattingIntent_Reject, PendingIntent.FLAG_UPDATE_CURRENT);*/

        try {
            int NOTIFICATION_ID = 1;
            String channelId = NOTIFICATION_ID + "" + getPackageName();
            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("채널에 대한 설명.");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{1000, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + friendMainPhoto);

            userImageBitmap = getBitmapCircleCrop(userImageBitmap, 0, 0);
            IconCompat userIcon = IconCompat.createWithBitmap(userImageBitmap);
            String userName = friendId;
            long timestamp = System.currentTimeMillis();
            Person user = new Person.Builder().setIcon(userIcon).setName(userName).build();
            NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);
            style.addMessage(userName + "님이 " + room_Index + "번방으로 채팅을 초대하셨습니다.", timestamp, user);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            builder.setSmallIcon(R.drawable.juntalk_logo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(userImageBitmap)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{1000})
                    .setStyle(style)
//                    .addAction(android.R.drawable.ic_menu_add, "YES", pendingIntent_Accept)
//                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "NO", pendingIntent_Reject)
                    .setAutoCancel(true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void chatting(Map<String, String> data) {

        Log.e("abc", "data : " + data.toString());
        String room_Uuid = data.get("room_Uuid");
        String userConversationTime = data.get("userConversationTime");
        String room_Index = data.get("room_Index");
        String userId = data.get("myUserId");
        String myUserImage = data.get("myUserImage");
        String userImage = data.get("userImage");
        String userConversation = data.get("userConversation");

        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();
        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("채널에 대한 설명.");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(false);
            notificationChannel.setVibrationPattern(new long[]{1000, 1000});
            notificationManager.createNotificationChannel(notificationChannel);
        }


        RoomModel roomModel = new RoomModel();
        roomModel.room_Uuid = room_Uuid;
        roomModel.room_Conversation = userConversationTime;
        roomModel.roomType = RoomModel.RoomType.P;
        roomModel.room_Index = Integer.parseInt(room_Index);
        roomModel.room_JoinPeopleName = userId;
        roomModel.room_JoinPeopleImage = myUserImage;

        Intent notificationIntent = new Intent(getApplicationContext(), ChattingRoomActivity.class);
        notificationIntent.putExtra("roomModel", roomModel);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget_chatting_notification);
        remoteViews.setTextViewText(R.id.nickNameTextView, userId);
        remoteViews.setTextViewText(R.id.regDateTextView, userConversationTime);
        remoteViews.setTextViewText(R.id.conversationTextView, userConversation);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("Title")
                .setContentText("Content")
                .setSmallIcon(R.drawable.juntalk_logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
                .setAutoCancel(true)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 이 Activity 로 되돌아옴
        NotificationTarget notificationTarget = new NotificationTarget(this.getApplicationContext(), R.id.mainPhotoImageView, remoteViews, builder.build(), NOTIFICATION_ID);

        Glide.with(getApplicationContext()).asBitmap().load(CommonString.CommonStringInterface.FileBaseUrl + userImage)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        //할일
                        Glide.with(getApplicationContext()).asBitmap().circleCrop().load(resource).into(notificationTarget);
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        }, 5000);*/
    }


    private void scheduleJob(Map<String, String> data) {
        // [START dispatch_job]
        /*OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance(this).beginWith(work).enqueue();*/
        // [END dispatch_job]


    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.e(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    private void sendNotification(String title, String content, String userImage) {

        Intent intent = new Intent();
        intent.setAction("createClub");
        sendBroadcast(intent);
        try {
            int NOTIFICATION_ID = 1;
            String channelId = NOTIFICATION_ID + "" + getPackageName();
            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("채널에 대한 설명.");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{1000, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + userImage);
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            builder.setLargeIcon(userImageBitmap)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.juntalk_logo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{1000})
                    .setAutoCancel(true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clubListNotification(String title, String content, String friendId, String type) {
        Intent intent = new Intent();
        intent.setAction("createClub");
        intent.putExtra("createClub", "createClub");
        sendBroadcast(intent);

        Intent intent1 = new Intent();
        intent1.setAction("SecondFragment");
        intent1.putExtra("SecondFragment", "SecondFragment");
        sendBroadcast(intent1);


        try {
            int NOTIFICATION_ID = 1;
            String channelId = NOTIFICATION_ID + "" + getPackageName();
            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("채널에 대한 설명.");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{1000, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
//            Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + userImage);
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            Drawable image = getApplicationContext().getDrawable(R.drawable.ic_accepted);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            String contentTitle = "";
            if (type.equals("clubListRejected")) {
                contentTitle = friendId + "님이 참여요청에 거절하였습니다.";
            } else {
                contentTitle = friendId + "님이 참여요청에 수락하였습니다.\n클럽채팅에 참여해보세요 ! ";


            }
            SpannableString span = new SpannableString(contentTitle);
            span.setSpan(new ImageSpan(image), 0, 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setContentTitle("클럽 요청 참여")
                    .setContentIntent(pendingIntent)
                    .setContentText(span)
                    .setSmallIcon(R.drawable.juntalk_logo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{1000})
                    .setAutoCancel(true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendChattingNotification(String room_Uuid, String room_Index, String userId, String userImage, String userConversation) {
        RoomModel roomModel = new RoomModel();
        roomModel.room_Uuid = room_Uuid;
        roomModel.roomType = RoomModel.RoomType.P;
        roomModel.room_Index = Integer.parseInt(room_Index);
        roomModel.room_JoinPeopleName = userId;
        roomModel.room_JoinPeopleImage = userImage;

        int NOTIFICATION_ID = 1;
        String channelId = NOTIFICATION_ID + "" + getPackageName();

        try {
            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId, "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("채널에 대한 설명.");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{1000, 1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);

            Bitmap userImageBitmap = getImageFromURL(CommonString.CommonStringInterface.FileBaseUrl + userImage);
            userImageBitmap = getBitmapCircleCrop(userImageBitmap, 0, 0);
            IconCompat userIcon = IconCompat.createWithBitmap(userImageBitmap);
            String userName = userId;
            long timestamp = System.currentTimeMillis();
            Person user = new Person.Builder().setIcon(userIcon).setName(userName).build();
            NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);
            style.addMessage(userConversation, timestamp, user);



            builder.setSmallIcon(R.drawable.juntalk_logo)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setLargeIcon(userImageBitmap)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{1000})
                    .setStyle(style)
                    .setAutoCancel(true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

//            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
//            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            notificationIntent.putExtra("roomModel", roomModel);
//            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImageFromURL(String imageURL) {
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return imgBitmap;
    }


    public Bitmap getBitmapCircleCrop(Bitmap bitmap, int Width, int Height) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        Bitmap CroppedBitmap = output;
        //width, Height에 0,0을 넣으면 원본 사이즈 그대로 출력
        if (Width != 0 && Height != 0)
            CroppedBitmap = Bitmap.createScaledBitmap(output, Width, Height, false);
        return CroppedBitmap;
    }


}