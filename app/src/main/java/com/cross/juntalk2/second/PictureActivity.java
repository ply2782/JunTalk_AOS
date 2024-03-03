package com.cross.juntalk2.second;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityPictureBinding;
import com.cross.juntalk2.utils.CreateNewCompatActivity;

public class PictureActivity extends CreateNewCompatActivity {

    private ActivityPictureBinding binding;
    private String imageUrl;

    @Override
    public void getInterfaceInfo() {

    }

    @Override
    public void getIntentInfo() {
        imageUrl = getIntent().getStringExtra("imageUrl");
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(PictureActivity.this, R.layout.activity_picture);
        binding.setImageUrl(imageUrl);
    }

    @Override
    public void createThings() {
        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {
            binding.pictureImageView.setVisibility(View.GONE);
        } else {
            binding.pictureImageView.setVisibility(View.VISIBLE);
            binding.pictureImageView.setUrl(imageUrl);

        }

        if (imageUrl.contains(".mp4")) {
            binding.playImageView.setVisibility(View.VISIBLE);
        } else {
            binding.playImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (binding.pictureImageView.isPlaying()) {
            binding.pictureImageView.pauseVideo();
            binding.pictureImageView.releasePlayer();
        }

    }

    @Override
    public void clickEvent() {
        binding.pictureImageView.setOnClickListener(v -> {
            if (!binding.pictureImageView.isPlaying()) {
                binding.pictureImageView.playVideo();
                binding.playImageView.setVisibility(View.GONE);
            } else {
                binding.pictureImageView.pauseVideo();
                binding.playImageView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void getServer() {

    }
}