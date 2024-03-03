package com.cross.juntalk2.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cross.juntalk2.R;
import com.cross.juntalk2.utils.videotrimmer.K4LVideoTrimmer;
import com.cross.juntalk2.utils.videotrimmer.interfaces.OnK4LVideoListener;
import com.cross.juntalk2.utils.videotrimmer.interfaces.OnTrimVideoListener;


public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnK4LVideoListener {

    private K4LVideoTrimmer videoTrimmer;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);
        videoTrimmer = findViewById(R.id.timeLine);
        Intent intent = getIntent();
        String uri = intent.getStringExtra("videoUri");
        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        if (videoTrimmer != null) {
            videoTrimmer.setMaxDuration(15);
            videoTrimmer.setOnTrimVideoListener(this);
            videoTrimmer.setOnK4LVideoListener(this);
            videoTrimmer.setVideoURI(Uri.parse(uri));
            videoTrimmer.setVideoInformationVisibility(true);
        }

    }

    @Override
    public void onTrimStarted() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    @Override
    public void getResult(final Uri uri) {
        mProgressDialog.cancel();
        Intent intent = getIntent();
        intent.putExtra("trimmedVideoURi", uri.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        videoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoTrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VideoTrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}