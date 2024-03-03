package com.cross.juntalk2.main;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityWholeBinding;
import com.cross.juntalk2.fifth.FifthFragment;
import com.cross.juntalk2.first.FirstFragment;
import com.cross.juntalk2.fourth.FourthFragment;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.second.ChattingRoomActivity;
import com.cross.juntalk2.second.SecondFragment;
import com.cross.juntalk2.third.ThirdFragment;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@SuppressWarnings("unchecked")
public class WholeActivity extends CreateNewCompatActivity {

    private ActivityWholeBinding binding;
    private Handler handler;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThirdFragment thirdFragment;
    private FourthFragment fourthFragment;
    private FifthFragment fifthFragment;
    private MyModel myModel;
    private boolean isOpen = false;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private boolean isClickFab = false;
    private BroadcastReceiver receiver;
    private ReviewManager manager;
    private AssetManager assetManager;
    private List<String> wordList;

    @Override
    public void getInterfaceInfo() {
        manager = ReviewManagerFactory.create(context);
    }


    @Override
    public void getIntentInfo() {


    }

    @Override
    public void init() {
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(WholeActivity.this, reviewInfo);
                flow.addOnCompleteListener(otherTask -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
                Log.e("abc", task.getException().getMessage());
            }
        });
        binding = DataBindingUtil.setContentView(WholeActivity.this, R.layout.activity_whole);
        binding.setWholeActivity(this);

        handler = new Handler(Looper.getMainLooper());

        fabOpen = AnimationUtils.loadAnimation
                (context, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation
                (context, R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation
                (context, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation
                (context, R.anim.rotate_backward);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("WholeActivity");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                myModel = JunApplication.getMyModel();
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void createThings() {
        myModel = JunApplication.getMyModel();
        firstFragment = FirstFragment.newInstance(myModel);
        moveFragment(firstFragment);
        getIntentBroadCast();
        showTodayWordsNotification();

    }


    public void showTodayWordsNotification() {
        try {

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

            JSONObject jsonObject = new JSONObject(jsonData);
            String words = jsonObject.getString("words");
            JSONArray jsonArray = new JSONArray(words);
            for (int a = 0; a < jsonArray.length(); a++) {
                String subJsonObject = jsonArray.getString(a);
                wordList.add(subJsonObject);
            }


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

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("abc", "e : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (firstFragment.onBackPressed()) {

        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void clickEvent() {


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                Log.e("abc", "ValueEventListener dataSanpShot : " + dataSnapshot.toString());
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> dataChangedMap = (Map<String, Object>) dataSnapshot.getValue();
                    if ((boolean) dataChangedMap.get("isChecked") == false) {

                        databaseReference.child("UserInfos").child(myModel.userId).child("Notice").child("isChecked").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("abc", "loadPost:onCancelled", databaseError.toException());
            }
        };
//        databaseReference.child("UserInfos").child(myModel.userId).child("Notice").addValueEventListener(postListener);


        ChildEventListener mChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("abc", "ChildEventListener add : " + dataSnapshot.toString() + " , s : " + s);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("abc", "ChildEventListener data String : " + dataSnapshot.toString());
//                Map<String, Object> dataMap = (Map<String, Object>) dataSnapshot.getValue();
//                Log.e("abc", "dataMap : " + dataMap.toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
//        databaseReference.child("UserInfos").child(myModel.userId).child("Notice").addChildEventListener(mChild);


        binding.bottomNavigationView2.bringToFront();
        binding.bottomNavigationView2.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main:
                        if (firstFragment == null) {
                            firstFragment = FirstFragment.newInstance(myModel);
                            addFragment(firstFragment);
                        }
                        showFragment(firstFragment);
                        hideFragment(secondFragment);
                        hideFragment(thirdFragment);
                        hideFragment(fourthFragment);
                        hideFragment(fifthFragment);
                        break;


                    case R.id.chatting:
                        if (secondFragment == null) {
                            secondFragment = SecondFragment.newInstance(myModel);
                            addFragment(secondFragment);
                        }
                        showFragment(secondFragment);
                        hideFragment(firstFragment);
                        hideFragment(thirdFragment);
                        hideFragment(fourthFragment);
                        hideFragment(fifthFragment);
                        break;

                    case R.id.board:
                        if (thirdFragment == null) {
                            thirdFragment = ThirdFragment.newInstance("thirdFragment", "thirdFragment");
                            addFragment(thirdFragment);
                        }
                        showFragment(thirdFragment);
                        hideFragment(firstFragment);
                        hideFragment(secondFragment);
                        hideFragment(fourthFragment);
                        hideFragment(fifthFragment);
                        break;

                    case R.id.bulletinBoard:
                        if (fourthFragment == null) {
                            fourthFragment = FourthFragment.newInstance("fourthFragment", "fourthFragment");
                            addFragment(fourthFragment);
                        }
                        showFragment(fourthFragment);
                        hideFragment(firstFragment);
                        hideFragment(secondFragment);
                        hideFragment(thirdFragment);
                        hideFragment(fifthFragment);
                        break;

                    case R.id.club:
                        if (fifthFragment == null) {
                            fifthFragment = FifthFragment.newInstance("fifthFragment", "fifthFragment");
                            addFragment(fifthFragment);
                        }
                        showFragment(fifthFragment);
                        hideFragment(fourthFragment);
                        hideFragment(firstFragment);
                        hideFragment(secondFragment);
                        hideFragment(thirdFragment);

                        break;

                }
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO : 핸들러 종료
        if (handler != null) {
            handler.removeMessages(0);
        }
    }

    @Override
    public void getServer() {

    }


    public void moveFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainerFrameLayout, fragment).commit();
        }

    }

    public void showFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.show(fragment).commit();
        }

    }

    public void hideFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragment).commit();
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainerFrameLayout, fragment).commit();
        }
    }

    public void animateFab(View view) {
        /*if (isOpen) {
//            binding.mainFloatingActionButton.startAnimation(rotateForward);
            binding.subFloatingActionButton.startAnimation(fabClose);
            binding.callFloatingActionButton.startAnimation(fabClose);
            binding.messageFloatingActionButton.startAnimation(fabClose);
            binding.videoCallFloatingActionButton.startAnimation(fabClose);
            binding.callFloatingActionButton.setClickable(false);
            binding.subFloatingActionButton.setClickable(false);
            binding.messageFloatingActionButton.setClickable(false);
            binding.videoCallFloatingActionButton.setClickable(false);
            isOpen = false;
        } else {
//            binding.mainFloatingActionButton.startAnimation(rotateBackward);
            binding.subFloatingActionButton.startAnimation(fabOpen);
            binding.callFloatingActionButton.startAnimation(fabOpen);
            binding.messageFloatingActionButton.startAnimation(fabOpen);
            binding.videoCallFloatingActionButton.startAnimation(fabOpen);
            binding.callFloatingActionButton.setClickable(true);
            binding.subFloatingActionButton.setClickable(true);
            binding.messageFloatingActionButton.setClickable(true);
            binding.videoCallFloatingActionButton.setClickable(true);
            isOpen = true;
        }*/
    }


    public void clickMainFab(View view) {
        if (isClickFab == false) {
            binding.appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            isClickFab = true;
        } else {
            binding.appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            isClickFab = false;

        }
    }

    public void getIntentBroadCast() {
        Intent intent = getIntent();
        if (intent.getSerializableExtra("roomModel") != null) {
            RoomModel roomModel = (RoomModel) intent.getSerializableExtra("roomModel");
            intent = new Intent(context, ChattingRoomActivity.class);
            intent.putExtra("roomModel", roomModel);
            startActivity(intent);
        }
    }
}
