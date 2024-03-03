package com.cross.juntalk2.utils;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityImagecontrolBinding;
import com.cross.juntalk2.databinding.ActivityPictureBinding;
import com.cross.juntalk2.first.ProfileActivity;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;

public class ImageControlActivity extends CreateNewCompatActivity {

    private ActivityImagecontrolBinding binding;
    private String imageUrl;

    enum TOUCH_MODE {
        NONE,   // 터치 안했을 때
        SINGLE, // 한손가락 터치
        MULTI   //두손가락 터치
    }

    private Shimmer shimmer;
    private ShimmerDrawable shimmerDrawable;
    private TOUCH_MODE touchMode;
    private Matrix matrix;      //기존 매트릭스
    private Matrix savedMatrix; //작업 후 이미지에 매핑할 매트릭스
    private PointF startPoint;  //한손가락 터치 이동 포인트
    private PointF midPoint;    //두손가락 터치 시 중신 포인트
    private float oldDistance;  //터치 시 두손가락 사이의 거리
    private double oldDegree = 0; // 두손가락의 각도

    @Override
    public void getInterfaceInfo() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing())
            overridePendingTransition(R.anim.acitivity_anim_fade_in, R.anim.acitivity_anim_fade_out);
    }

    @Override
    public void getIntentInfo() {
        imageUrl = getIntent().getStringExtra("imageUrl");
    }

    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(ImageControlActivity.this, R.layout.activity_imagecontrol);
        binding.setImageUrl(imageUrl);
        matrix = new Matrix();
        savedMatrix = new Matrix();
        binding.imageView.setOnTouchListener(onTouch);
        binding.imageView.setScaleType(ImageView.ScaleType.MATRIX);
        shimmer = new Shimmer.ColorHighlightBuilder()// The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1000) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.7f) //the alpha of the underlying children
                .setBaseColor(Color.GRAY)
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setHighlightColor(Color.WHITE)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
    }

    @Override
    public void createThings() {
        if (imageUrl == null || imageUrl.equals("") || imageUrl.equals("null")) {
            binding.imageView.setVisibility(View.GONE);
        } else {
            binding.imageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(CommonString.CommonStringInterface.FileBaseUrl + imageUrl).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).placeholder(shimmerDrawable).into(binding.imageView);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.equals(binding.imageView)) {
                int action = event.getAction();
                switch (action & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        touchMode = TOUCH_MODE.SINGLE;
                        donwSingleEvent(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (event.getPointerCount() == 2) { // 두손가락 터치를 했을 때
                            touchMode = TOUCH_MODE.MULTI;
                            downMultiEvent(event);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (touchMode == TOUCH_MODE.SINGLE) {
                            moveSingleEvent(event);
                        } else if (touchMode == TOUCH_MODE.MULTI) {
                            moveMultiEvent(event);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        touchMode = TOUCH_MODE.NONE;
                        break;
                }
            }


            return true;
        }
    };

    private PointF getMidPoint(MotionEvent e) {

        float x = (e.getX(0) + e.getX(1)) / 2;
        float y = (e.getY(0) + e.getY(1)) / 2;

        return new PointF(x, y);
    }

    private float getDistance(MotionEvent e) {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void donwSingleEvent(MotionEvent event) {
        savedMatrix.set(matrix);
        startPoint = new PointF(event.getX(), event.getY());
    }

    private void downMultiEvent(MotionEvent event) {
        oldDistance = getDistance(event);
        if (oldDistance > 5f) {
            savedMatrix.set(matrix);
            midPoint = getMidPoint(event);
            double radian = Math.atan2(event.getY() - midPoint.y, event.getX() - midPoint.x);
            oldDegree = (radian * 180) / Math.PI;
        }
    }

    private void moveSingleEvent(MotionEvent event) {
        matrix.set(savedMatrix);
        matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
        binding.imageView.setImageMatrix(matrix);
    }

    private void moveMultiEvent(MotionEvent event) {
        float newDistance = getDistance(event);
        if (newDistance > 5f) {
            matrix.set(savedMatrix);
            float scale = newDistance / oldDistance;
            matrix.postScale(scale, scale, midPoint.x, midPoint.y);

            double nowRadian = Math.atan2(event.getY() - midPoint.y, event.getX() - midPoint.x);
            double nowDegress = (nowRadian * 180) / Math.PI;
            float degree = (float) (nowDegress - oldDegree);
            matrix.postRotate(degree, midPoint.x, midPoint.y);


            binding.imageView.setImageMatrix(matrix);

        }
    }

    @Override
    public void clickEvent() {

    }

    @Override
    public void getServer() {

    }
}