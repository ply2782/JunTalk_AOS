package com.cross.juntalk2.third;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.FragmentThirdBinding;
import com.cross.juntalk2.utils.CreateNewFragment;
import com.google.android.material.tabs.TabLayout;


public class ThirdFragment extends CreateNewFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentThirdBinding binding;
    private VideoFragment videoFragment;
    private MusicFragment musicFragment;
    private TabLayout.Tab musicTab, videoTab;
    private AppCompatTextView musicTitleTextView, videoTitleTextView;
    private BroadcastReceiver receiver;

    public ThirdFragment() {
        // Required empty public constructor
    }

    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentThirdBinding.inflate(inflater, container, false);
        CreateNewFragment();
        return binding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void getInterfaceInfo() {

    }

    @Override
    public void getIntentInfo() {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void init() {
        musicTab = binding.tabLayout.getTabAt(0);
        videoTab = binding.tabLayout.getTabAt(1);
        LayoutInflater inflater = LayoutInflater.from(context);
        View includeTabItemLayout_Music = inflater.inflate(R.layout.tablayout_tab_title_item, null, false);
        musicTitleTextView = includeTabItemLayout_Music.findViewById(R.id.mainTitleTextView);
        musicTitleTextView.setText("음악");
        musicTab.setCustomView(includeTabItemLayout_Music);
        View includeTabItemLayout_Video = inflater.inflate(R.layout.tablayout_tab_title_item, null, false);
        videoTitleTextView = includeTabItemLayout_Video.findViewById(R.id.mainTitleTextView);
        videoTitleTextView.setText("동영상");
        videoTab.setCustomView(includeTabItemLayout_Video);


    }

    @Override
    public void createObjects() {
        musicFragment = MusicFragment.newInstance("musicFragment", "musicFragment");
        videoFragment = VideoFragment.newInstance("videoFragment", "videoFragment");
        addFragment(musicFragment);
        addFragment(videoFragment);
        showFragment(musicFragment);
        hideFragment(videoFragment);
    }

    @Override
    public void clickEvent() {

        ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            // Handle the Intent
                            String resultString = intent.getStringExtra("aa");


                        }
                    }
                });

        binding.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FileUploadActivity.class);
//                mStartForResult.launch(intent);
                startActivity(intent);
            }
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        musicTitleTextView.setTextColor(Color.parseColor("#000000"));
                        showFragment(musicFragment);
                        break;
                    case 1:
                        videoTitleTextView.setTextColor(Color.parseColor("#000000"));
                        showFragment(videoFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        musicTitleTextView.setTextColor(Color.parseColor("#4D000000"));
                        hideFragment(musicFragment);
                        break;
                    case 1:
                        videoTitleTextView.setTextColor(Color.parseColor("#4D000000"));
                        hideFragment(videoFragment);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    public void showFragment(Fragment fragment) {
        if (fragment != null) {
            /*FragmentManager fragmentManager = activity.getSupportFragmentManager();*/
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.show(fragment).commit();
        }

    }

    public void hideFragment(Fragment fragment) {
        if (fragment != null) {
            /*FragmentManager fragmentManager = activity.getSupportFragmentManager();*/
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragment).commit();
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment != null) {

            /*FragmentManager fragmentManager = activity.getSupportFragmentManager();*/
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragmentContainer, fragment).commit();
        }
    }


}