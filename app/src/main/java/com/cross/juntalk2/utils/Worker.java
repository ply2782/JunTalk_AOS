package com.cross.juntalk2.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.WorkerParameters;

import com.cross.juntalk2.R;
import com.cross.juntalk2.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Worker extends androidx.work.Worker {
    private final String TAG = Worker.class.getSimpleName();
    private AssetManager assetManager;
    private List<String> wordList;
    private Handler handler;
    private Context context;

    public Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        try {
            this.context= context;
            handler = new Handler(Looper.getMainLooper());
            wordList = new ArrayList<>();
            assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("ThanksGivingWords.json");
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            StringBuffer buffer = new StringBuffer();
            String line = bufferedReader.readLine();

            while (line != null) {
                buffer.append(line + "\n");
                line = bufferedReader.readLine();
            }
            String jsonData = buffer.toString();
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                String words = jsonObject.getString("words");
                JSONArray jsonArray = new JSONArray(words);
                for (int a = 0; a < jsonArray.length(); a++) {
                    String subJsonObject = jsonArray.getString(a);
                    wordList.add(subJsonObject);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public Result doWork() {

        try {
            Random random = new Random(System.currentTimeMillis());
            int sumSize = wordList.size();
            int randomNumber = random.nextInt(sumSize);
            if (randomNumber > (sumSize - 1)) {
                randomNumber = (sumSize - 1);
            }
            //todo : NOTIFICATION_ID 와 channelId가 같으면 foreground headup 알림이 안뜨므로 다르게 해줘야 뜸
            int NOTIFICATION_ID = 1;
            String channelId = NOTIFICATION_ID + "" + context.getPackageName();
            // 알림 표시
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(channelId), "JunTalk2", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("채널에 대한 설명.");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setShowBadge(true);
                notificationChannel.setVibrationPattern(new long[]{1000});
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.juntalk_logo);

            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            builder.setLargeIcon(logoBitmap)
                    .setContentTitle("JunTalk")
                    .setSubText("Today's Word")
                    .setContentText(wordList.get(randomNumber))
                    .setSmallIcon(R.drawable.juntalk_logo)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{1000})
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setAutoCancel(true);
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        } catch (Exception e) {
            Log.e("abc","e : "+ e.getMessage());
            e.printStackTrace();
        }
        return Result.success();
    }
}