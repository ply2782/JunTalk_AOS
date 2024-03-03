package com.cross.juntalk2.fourth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityCreateBulletinBoardItemBinding;
import com.cross.juntalk2.diffutil.CategoryListDiffUtil;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.cross.juntalk2.utils.VideoTrimmerActivity;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@SuppressWarnings("unchecked")
public class CreateBulletinBoardItemActivity extends CreateNewCompatActivity {
    //    private ActivityResultLauncher<Intent> activityResultLauncher;
    private CompositeDisposable compositeDisposable;
    private ActivityCreateBulletinBoardItemBinding binding;
    private CategoryViewPager2Adapter adapter;
    private SimpleDateFormat simpleDateFormat;
    private Handler handler;
    private List<MultipartBody.Part> videoFileList;
    private List<MultipartBody.Part> imageFileList;
    private boolean isExistOfPicture = false;
    private RetrofitService service;
    private LoadingDialog loadingDialog;
    private MyModel myModel;
    private String category;
    private String bulletin_Uuid;
    private String type;
    private ClipData clipData;
    private List<String> categoryList;
    private List<String> imagePathList;
    private ActivityResultLauncher<Intent> EditedVideoFileLauncher;
    private CreateStringToFile createStringToFile;
    private ActivityResultLauncher<Intent> trimVideoResultLauncher;
    private ActivityResultLauncher<Intent> fileResultLauncher;
    private DisplayMetrics dm;
    private int width, height;

    @Override
    public void getInterfaceInfo() {
        dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }

        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    public void getIntentInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("abc", "OnResume");
    }

    private class CreateStringToFile extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (this) {
                @NonNull List<String> copyImagePathList;
                if (imagePathList.size() > 5) {
                    copyImagePathList = imagePathList.subList(0, 5);
                } else {
                    copyImagePathList = imagePathList;
                }
                for (String s : copyImagePathList) {
                    File file = new File(s);
                    if (s.contains(".mp4") || s.contains(".3gp")) {
                        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                        String videoFileName = UUID.randomUUID().toString() + "_JunTalk.mp4";
                        MultipartBody.Part part = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
                        videoFileList.add(part);
                    } else {
                        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                        String imageFileName = UUID.randomUUID().toString() + "_JunTalk.jpg";
                        MultipartBody.Part part = MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile);
                        imageFileList.add(part);
                    }
                }
            }
        }
    }

    public boolean checkNull() {
        switch (binding.categoryViewPager2.getCurrentItem()) {
            case 0:
                category = "Thanks";
                break;
            case 1:
                category = "Pray";
                break;
            case 2:
                category = "News";
                break;
            case 3:
                category = "Notice";
                break;
            case 4:
                category = "Introduce";
                break;
            case 5:
                category = "Daily";
                break;
            case 6:
                category = "Show";
                break;
            default:
                category = "null";
                break;
        }
        if (category.equals("null")) {
            Toast.makeText(context, "카테고리를 선택하여 주세요.", Toast.LENGTH_SHORT).show();

            return false;
        } else if (binding.bulletinContentEditTextView.getText() == null || binding.bulletinContentEditTextView.getText().toString().equals("")) {
            Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void init() {

        binding = DataBindingUtil.setContentView(CreateBulletinBoardItemActivity.this, R.layout.activity_create_bulletin_board_item);
        adapter = new CategoryViewPager2Adapter(new CategoryListDiffUtil(), context);
        binding.categoryViewPager2.setOffscreenPageLimit(2);
        binding.categoryViewPager2.setAdapter(adapter);
        binding.categoryViewPager2.setPageTransformer(new ZoomInPageTransformer());
        imagePathList = new ArrayList<>();
        categoryList = new ArrayList<>();
        categoryList.add("# 감사");
        categoryList.add("# 중보");
        categoryList.add("# 소식");
        categoryList.add("# 공지");
        categoryList.add("# 소개");
        categoryList.add("# 일상");
        categoryList.add("# 자랑");
        adapter.submitList(categoryList);
    }

    @Override
    public void createThings() {
        String updateBulletin_Uuid = getIntent().getStringExtra("bulletin_Uuid");
        type = getIntent().getStringExtra("type");
        if (updateBulletin_Uuid == null || updateBulletin_Uuid.equals("")) {
            bulletin_Uuid = UUID.randomUUID().toString();
        } else {
            bulletin_Uuid = updateBulletin_Uuid;
        }

        myModel = JunApplication.getMyModel();
        loadingDialog = new LoadingDialog(context);
        compositeDisposable = new CompositeDisposable();
        handler = new Handler(Looper.getMainLooper());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        imageFileList = new ArrayList<>();
        videoFileList = new ArrayList<>();
    }




    @Override
    public void clickEvent() {
        binding.bulletinContentEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.categoryViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.e("abc", "position : " + position);
                switch (position) {
                    case 0:
                        category = "Thanks";
                        break;
                    case 1:
                        category = "Pray";
                        break;
                    case 2:
                        category = "News";
                        break;
                    case 3:
                        category = "Notice";
                        break;
                    case 4:
                        category = "Introduce";
                    case 5:
                        category = "Daily";
                    case 6:
                        category = "Show";
                    default:
                        category = "null";
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        EditedVideoFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.e("abc", "result : " + result.toString());
                if (result.getResultCode() == RESULT_CANCELED) {
                    if (clipData != null) {
                        if (clipData.getItemCount() > 5) {
                            Snackbar.make(getCurrentFocus(), "사진선택은 5장까지만 가능합니다.", Snackbar.LENGTH_SHORT).show();
                        } else {

                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri uri = clipData.getItemAt(i).getUri();
                                Bitmap bitmap = null;
                                try {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    } else {
                                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                    }
                                    String imagePath = getRealPathFromURI(uri);
                                    imagePathList.add(imagePath);

                                    if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {

                                        Intent intent = new Intent(context, VideoTrimmerActivity.class);
                                        intent.putExtra("videoUri", imagePath);
                                        trimVideoResultLauncher.launch(intent);
//                                        bitmapList.add(bmRotated);
                                            /*Bitmap videoThumbNail = createThumbnail(context, imagePath);
                                            Bitmap bmRotated = rotateBitmap(uri, videoThumbNail);
                                            Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                                            bmp_Copy.recycle();
                                            addThumbNail(null, bmp_Copy);*/

                                    } else {

                                        try {
                                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                            Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                                            bmRotated.recycle();
//                                        Bitmap originBitmap_copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                                        bitmapList.add(bmRotated);
                                            addThumbNail(null, bmp_Copy);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    /*synchronized (videoTrimLauncherThread) {
                                        videoTrimLauncherThread.notify();
                                    }*/

                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {

                    if (result.getResultCode() == RESULT_OK) {
                        if (result != null) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri uri = data.getData();
                                if (uri == null) {
                                    final Bundle extras = data.getExtras();
                                    if (extras != null) {
                                        try {
                                            Log.e("abc", "extras !!!!: " + extras.toString());
                                        } catch (Exception e) {

                                        }
                                    } else {
                                        if (data.getClipData() != null) {
                                            Log.e("abc", "data.getClipData : " + data.getClipData().toString());
                                        }
                                    }
                                    /*synchronized (videoTrimLauncherThread) {
                                        videoTrimLauncherThread.notify();
                                    }*/

                                } else {

                                    Log.e("abc", "ndasjkdnasjkdnsajkd");

                                    String realDataPath = "";
                                    if (uri.toString().contains("content://")) {
                                        realDataPath = getRealPathFromURI(uri);
                                    } else {
                                        realDataPath = uri.toString();
                                    }

                                    if (realDataPath.contains(".mp4") || realDataPath.contains(".3gp")) {
                                        Bitmap videoThumbNail = createThumbnail(context, realDataPath);
                                        addThumbNail(null, videoThumbNail);
                                        imagePathList.add(realDataPath);
//                                        bitmapList.add(videoThumbNail);
                                        /*synchronized (videoTrimLauncherThread) {
                                            videoTrimLauncherThread.notify();
                                        }*/

                                    } else {


                                        Log.e("abc", "scncncn213123123213211321312312321");
                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), Uri.parse("file://" + realDataPath));
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://" + realDataPath));
                                            }
                                            addThumbNail(null, bitmap);
                                            imagePathList.add(realDataPath);

//                                            bitmapList.add(bitmap);
                                            /*synchronized (videoTrimLauncherThread) {
                                                videoTrimLauncherThread.notify();
                                            }*/

                                        } catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            }
        });
        trimVideoResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Log.e("abc", "result : " + result);
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String trimmedVideoURi = data.getStringExtra("trimmedVideoURi");
                        Bitmap videoThumbNail = createThumbnail(context, trimmedVideoURi);
                        imagePathList.add(trimmedVideoURi);
                        addThumbNail(null, videoThumbNail);
                    }
                }
            }
        });

        fileResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                List<Uri> uriList = new ArrayList<>();

                //TODO : result 값이 성공할 경우
                if (result.getResultCode() == RESULT_OK) {
                    if (data == null) {
                        Log.e("abc", "data : " + data.toString());
                        //TODO : 이미지 선택이 없을 경우
                        Snackbar.make(getCurrentFocus(), "현재 선택된 사진이 없습니다.", Snackbar.LENGTH_SHORT).show();

                    } else {
                        //TODO : 이미지 선택이 하나일 경우
                        if (data.getClipData() == null) {
                            Uri uri = result.getData().getData();
                            uriList.add(uri);
                            Bitmap bitmap = null;
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                    try {
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    } catch (Exception e) {
                                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                    }
                                } else {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                }


                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            String imagePath = getRealPathFromURI(uri);
                            Log.e("abc", "bitmap Thumbnail imagePath : " + imagePath);
                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {
                                Intent intent = new Intent(context, VideoTrimmerActivity.class);
                                intent.putExtra("videoUri", imagePath);
                                trimVideoResultLauncher.launch(intent);

                            } else {
                                Log.e("abc", "addThumbnail asdsadsadas");
                                addThumbNail(null, bitmap);
                                imagePathList.add(imagePath);
                            }

                        } else {


                            // TODO : 이미지 선택이 여러장일 경우
                            // TODO : 갤러리가 아닌 곳에서 올 경우
                            final Bundle extras = data.getExtras();
                            Log.e("abc", "extras asdasdsadsad: " + extras);
                            if (extras != null) {
                                for (String key : extras.keySet()) {
                                    Log.e("abc", "key : " + key + " , value : " + extras.get(key));
                                }

                                if (extras.get("android.intent.extra.STREAM") instanceof List) {
                                    List<Uri> uris = (List<Uri>) extras.get("android.intent.extra.STREAM");
                                    for (int i = 0; i < uris.size(); i++) {
                                        Uri uri = uris.get(i);
                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                                try {
                                                    bitmap = ImageDecoder.decodeBitmap(source);
                                                } catch (Exception e) {
                                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                                }
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }
                                            String imagePath = getRealPathFromURI(uri);

                                            if (!uri.toString().contains("video") && !uri.toString().contains(".mp4")) {
                                                try {
                                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                                    addThumbNail(null, bmRotated);
                                                    imagePathList.add(imagePath);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                Intent intent = new Intent(context, VideoTrimmerActivity.class);
                                                intent.putExtra("videoUri", imagePath);
                                                trimVideoResultLauncher.launch(intent);
                                            }
                                        } catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                /**if (videoTrimLauncherThread != null) {
                                 if (!videoTrimLauncherThread.isInterrupted()) {
                                 videoTrimLauncherThread.interrupt();
                                 }
                                 }
                                 videoTrimLauncherThread = new VideoTrimLauncherThread(data);
                                 videoTrimLauncherThread.start();*/

                            } else {


                                Log.e("abc", "extras ==null");
                                if (data.getClipData() != null) {

                                    Log.e("abc", "data.getClipData()");
                                    clipData = data.getClipData();
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        Uri uri = clipData.getItemAt(i).getUri();
                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                                try {
                                                    bitmap = ImageDecoder.decodeBitmap(source);
                                                } catch (Exception e) {
                                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                                }
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }
                                            String imagePath = getRealPathFromURI(uri);
                                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {

                                                Intent intent = new Intent(context, VideoTrimmerActivity.class);
                                                intent.putExtra("videoUri", imagePath);
                                                trimVideoResultLauncher.launch(intent);

                                                    /*imagePathList.add(imagePath);
                                                    Bitmap videoThumbNail = createThumbnail(context, imagePath);
                                                    Bitmap bmRotated = rotateBitmap(uri, videoThumbNail);
                                                    Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                                                    bmp_Copy.recycle();
                                                    addThumbNail(null, bmp_Copy);*/
                                            } else {
                                                try {
                                                    imagePathList.add(imagePath);
                                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                                    Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                                                    bmRotated.recycle();

                                                    addThumbNail(null, bmp_Copy);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "이 파일은 지원하지 않는 형식입니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "이 파일은 지원하지 않는 형식입니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    /**if (videoTrimLauncherThread != null) {
                                     if (!videoTrimLauncherThread.isInterrupted()) {
                                     videoTrimLauncherThread.interrupt();
                                     }
                                     }
                                     videoTrimLauncherThread = new VideoTrimLauncherThread(data);
                                     videoTrimLauncherThread.start();*/

                                }
                            }
                        }
                        //data.getClipData() == null or
                    }//data == null or
                }
            }
        });

        binding.closeImageButton.setOnClickListener(v -> finish());

        binding.plusButton.setOnClickListener(v -> {
            checkPermission();
        });

        binding.okButton.setOnClickListener(v -> {

            if (checkNull()) {
                androidx.appcompat.app.AlertDialog.Builder cautionAlert = new androidx.appcompat.app.AlertDialog.Builder(context);
                View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
                cautionAlert.setView(cautionView);
                Button reportButton = cautionView.findViewById(R.id.reportButton);
                reportButton.setVisibility(View.GONE);
                Button okButton = cautionView.findViewById(R.id.okButton);
                okButton.setText("확인");
                Button cancelButton = cautionView.findViewById(R.id.cancelButton);
                androidx.appcompat.app.AlertDialog alertDialog = cautionAlert.create();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                        alertDialog.show();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        createStringToFile = new CreateStringToFile();
                        if (!createStringToFile.isInterrupted()) {
                            createStringToFile.interrupt();
                        }
                        createStringToFile.start();
                        try {
                            createStringToFile.join();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (type == null || type.equals("")) {
                            api_InsertBulletinBoard();
                        } else {
                            api_UpdateBulletinBoard();
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            } else {

            }

        });
    }


    //TODO : 동영상 섬네일 만들기
    public Bitmap createThumbnail(Context activity, String path) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        Bitmap bitmap = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(activity, Uri.parse(path));
            //timeUs는 마이크로 초 이므로 1000000초 곱해줘야 초단위다.
            bitmap = mediaMetadataRetriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) {
                try {
                    mediaMetadataRetriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    //TODO : 이미지 회전
    public Bitmap rotateBitmap(Uri uri, Bitmap bitmap) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(inputStream);
        inputStream.close();
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            if (bitmap.isRecycled()) {
                return null;
            } else {
                Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return bmRotated;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath, int position) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String imageFileName = "";
        if (position == -1) {
            imageFileName = UUID.randomUUID().toString() + "_JunTalk.jpg";
        } else {
            imageFileName = UUID.randomUUID().toString() + "_" + position + "_JunTalk.jpg";
        }
        imageFileList.add(MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile));
    }

    //TODO : 동영상 파일 서버로 전송
    public void getVideoFile(String filePath, int position) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String videoFileName = "";
        if (position == -1) {
            videoFileName = UUID.randomUUID().toString() + "_JunTalk.mp4";
        } else {
            videoFileName = UUID.randomUUID().toString() + "_" + position + "_JunTalk.mp4";
        }

        videoFileList.add(MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile));
    }


    //TODO : 멀티파일 형식을 이용하여 파일을 담아서 서버로 보냄
    public List<MultipartBody.Part> getFileList() {
        List<MultipartBody.Part> fileList = new ArrayList<>();
        File fileDir = getFilesDir();
        String path = fileDir.getPath();
        String string_path = path + "/abc";
        File file = new File(string_path);
        File[] files = file.listFiles();
        for (File myFile : files) {
            RequestBody requestFile = RequestBody.create(myFile, MediaType.parse("multipart/form-data"));
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("files", myFile.getName(), requestFile);
            fileList.add(uploadFile);
        }
        return fileList;
    }


    //TODO : uri 로부터 사진 절대위치 구하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    //TODO : 저장소 저장하기 권한 묻기
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
            }
        } else {

            AlertDialog.Builder builder;
            AlertDialog alertDialog;
            if (isExistOfPicture == false) {
                builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.juntalk_logo);
                builder.setTitle("알림");
                builder.setMessage("사진을 새로 추가하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if ((binding.thumbNailFlexBoxLayout.getChildCount() - 1) >= 5) {
                            Toast.makeText(context, "사진은 5장까지 입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/* video/*");
                            intent.setAction(Intent.ACTION_PICK);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            fileResultLauncher.launch(intent);
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = builder.create();
            } else {

                builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.juntalk_logo);
                builder.setTitle("알림");
                builder.setMessage("사진을 변경 하시겠습니까?");
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ((binding.thumbNailFlexBoxLayout.getChildCount() - 1) >= 5) {
                            Toast.makeText(context, "사진은 5장까지 입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent();
                            intent.setType("image/* video/*");
                            intent.setAction(Intent.ACTION_PICK);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            fileResultLauncher.launch(intent);
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog = builder.create();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                    alertDialog.show();
                }
            });
        }
    }

    public String saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {

        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();

        String foler_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + foler_name;
        File file_path;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path + file_name);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return fileDir + File.separator + folder + File.separator + file_name;
    }

    //TODO : 글라이드
    public RequestManager glide() {
        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .disallowHardwareConfig()
                .error(R.drawable.image_question_mark)
                .placeholder(R.drawable.juntalk_logo);
        return Glide.with(context)
                .setDefaultRequestOptions(requestOptions);
    }


    @Override
    public void getServer() {

    }


    public void addThumbNail(Uri uri, Bitmap bitmap) {
        AppCompatImageView imageView = (AppCompatImageView) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_item, null);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(width, getDp(200));
        layoutParams.rightMargin = getDp(10);
        layoutParams.bottomMargin = getDp(10);
        layoutParams.leftMargin = getDp(10);
        layoutParams.topMargin = getDp(10);
        if (uri == null) {
            glide()
                    .load(bitmap)
                    .optionalCenterCrop()
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .disallowHardwareConfig()
                    .transform(new CenterCrop(), new RoundedCorners(15))
                    .into(imageView);
        } else {
            glide()
                    .load(uri)
                    .optionalCenterCrop()
                    .thumbnail(0.3f)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .disallowHardwareConfig()
                    .transform(new CenterCrop(), new RoundedCorners(15))
                    .into(imageView);

        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                binding.thumbNailFlexBoxLayout.addView(imageView, binding.thumbNailFlexBoxLayout.getChildCount() - 1, layoutParams);
                binding.fileCountTextView.setText("( " + (binding.thumbNailFlexBoxLayout.getChildCount() - 1) + " / 5 )");

            }
        });
    }


    public void api_UpdateBulletinBoard() {
        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        Map<String, RequestBody> insertBulletinBoardMap = new HashMap<>();
        RequestBody bulletin_Uuid = RequestBody.create(this.bulletin_Uuid, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("bulletin_Uuid", bulletin_Uuid);
        RequestBody userId = RequestBody.create(myModel.userId, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("userId", userId);
        RequestBody user_Index = RequestBody.create(String.valueOf(myModel.user_Index), MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("user_Index", user_Index);
        RequestBody bulletin_Content = RequestBody.create(binding.bulletinContentEditTextView.getText().toString(), MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("bulletin_Content", bulletin_Content);
        RequestBody userMainPhoto = RequestBody.create(myModel.userMainPhoto, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("userMainPhoto", userMainPhoto);
        RequestBody category = RequestBody.create(this.category, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("category", category);

        Observable<Boolean> editBulletinBoard =
                service.editBulletinBoard(
                        CommonString.bulletinBoardController,
                        insertBulletinBoardMap,
                        videoFileList, imageFileList);

        compositeDisposable.add(editBulletinBoard.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        if (aBoolean) {
                            if (isFinishing() || loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            Intent FourthFragmentIntent = new Intent();
                            FourthFragmentIntent.setAction("FourthFragment");
                            FourthFragmentIntent.putExtra("FourthFragment", "FourthFragment");
                            sendBroadcast(FourthFragmentIntent);

                            Intent UserBulletinActivityIntent = new Intent();
                            UserBulletinActivityIntent.setAction("UserBulletinActivity");
                            UserBulletinActivityIntent.putExtra("UserBulletinActivity", "UserBulletinActivity");
                            sendBroadcast(UserBulletinActivityIntent);
                            finish();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "서버와 연걸이 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error : " + e.getMessage());
                        loadingDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와 연걸이 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));

    }


    public void api_InsertBulletinBoard() {
        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        Map<String, RequestBody> insertBulletinBoardMap = new HashMap<>();
        RequestBody bulletin_Uuid = RequestBody.create(this.bulletin_Uuid, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("bulletin_Uuid", bulletin_Uuid);
        RequestBody userId = RequestBody.create(myModel.userId, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("userId", userId);
        RequestBody user_Index = RequestBody.create(String.valueOf(myModel.user_Index), MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("user_Index", user_Index);
        RequestBody bulletin_Content = RequestBody.create(binding.bulletinContentEditTextView.getText().toString(), MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("bulletin_Content", bulletin_Content);
        RequestBody userMainPhoto = RequestBody.create(myModel.userMainPhoto, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("userMainPhoto", userMainPhoto);
        RequestBody category = RequestBody.create(this.category, MediaType.parse("text/plain"));
        insertBulletinBoardMap.put("category", category);

        Observable<Boolean> insertBulletinBoard = service.insertBulletinBoard(CommonString.bulletinBoardController, insertBulletinBoardMap,
                videoFileList, imageFileList);
        compositeDisposable.add(insertBulletinBoard.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        if (aBoolean) {
                            if (isFinishing() || loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }

                            Intent intent = new Intent("FourthFragment");
                            intent.setAction("FourthFragment");
                            intent.putExtra("FourthFragment", "FourthFragment");
                            sendBroadcast(intent);

                            Intent UserBulletinActivityIntent = new Intent();
                            UserBulletinActivityIntent.setAction("UserBulletinActivity");
                            UserBulletinActivityIntent.putExtra("UserBulletinActivity", "UserBulletinActivity");
                            sendBroadcast(UserBulletinActivityIntent);

                            finish();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "서버와 연걸이 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error : " + e.getMessage());
                        loadingDialog.dismiss();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "서버와 연걸이 불안정합니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));

    }

    public int getDp(int a) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, getResources().getDisplayMetrics());
        return dp;
    }

    public class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
// This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
// Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
// Fade the page out.
                view.setAlpha(1 - position);

// Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

// Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
// This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    private class ZoomInPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f; // scale // sizes
        private float imageMargin = getResources().getDimensionPixelOffset(R.dimen.image_margin);
        private float imageSize = getResources().getDimensionPixelSize(R.dimen.image_size);
        private float screenWidth = getResources().getDisplayMetrics().widthPixels;
        private float offsetPx = screenWidth - imageMargin - imageSize;

        @Override
        public void transformPage(@NonNull View view, float position) {
            view.setTranslationX(position * -offsetPx);
            if (position < -1) return;
            if (position <= 1) { // animation views
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position * getEase(Math.abs(position))));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor); // If you use to LinearLayout in list_item, Use after codes. //
                int height = view.getHeight();
                float y = -((float) height - (scaleFactor * height)) / 4f;
                view.setTranslationY(y);
            } else { // side views
                view.setScaleX(MIN_SCALE);
                view.setScaleY(MIN_SCALE);
            }
        }

        private float getEase(float position) {
            float sqt = position * position;
            return sqt / (2.0f * (sqt - position) + 1.0f);
        }
    }
}


