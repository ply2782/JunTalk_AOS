package com.cross.juntalk2.third;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityFileUploadBinding;
import com.cross.juntalk2.diffutil.BitmapDifUtil;
import com.cross.juntalk2.diffutil.MusicFileUploadDiffUtil;
import com.cross.juntalk2.model.FileUploadViewModel;
import com.cross.juntalk2.model.MusicModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
//import com.cross.juntalk2.utils.GlideApp;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class FileUploadActivity extends CreateNewCompatActivity {

    private FileUploadAdapter video_FileUploadAdapter;
    private ActivityFileUploadBinding binding;
    private FileUploadViewModel fileUploadViewModel;
    private ViewModelFactory viewModelFactory;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    private SimpleDateFormat todayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Bitmap> videoThumbNailList;
    private List<MusicModel> musicModelList, selectedMusicList;
    private Handler handler;
    private MusicFileUploadAdapter music_FileUploadAdapter;
    private CompositeDisposable compositeDisposable;

    private RetrofitService service;
    private LoadingDialog loadingDialog;
    private Map<String, RequestBody> map;
    private List<MultipartBody.Part> musicFileList;
    private List<MultipartBody.Part> videoFileList;


    private List<MultipartBody.Part> imageFileList;
    private List<MultipartBody.Part> videoThumbNailFileList;
    private String type;

    public interface MusicUploadListInterface {
        void chooseMusic(MusicModel musicModelList, int position);
    }

    MusicUploadListInterface musicUploadListInterface;

    @Override
    public void getInterfaceInfo() {
        if (context instanceof MusicUploadListInterface) {
            musicUploadListInterface = (MusicUploadListInterface) context;
        }
    }

    @Override
    public void getIntentInfo() {
        checkPermission();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

    }


    @Override
    public void init() {
        PhItemAnimator phItemAnimator = new PhItemAnimator(context);
        binding = DataBindingUtil.setContentView(FileUploadActivity.this, R.layout.activity_file_upload);
        viewModelFactory = new ViewModelFactory();
        fileUploadViewModel = new ViewModelProvider(this, viewModelFactory).get(FileUploadViewModel.class);
        video_FileUploadAdapter = new FileUploadAdapter(new BitmapDifUtil());
        music_FileUploadAdapter = new MusicFileUploadAdapter(new MusicFileUploadDiffUtil());
        binding.musicFileListRecyclerView.setAdapter(music_FileUploadAdapter);
        binding.musicFileListRecyclerView.setItemAnimator(phItemAnimator);

        binding.videoFileListRecyclerView.setAdapter(video_FileUploadAdapter);
        binding.videoFileListRecyclerView.setItemAnimator(phItemAnimator);

    }

    public boolean checkNull() {
        if (video_FileUploadAdapter.getItemCount() <= 0 && music_FileUploadAdapter.getItemCount() <= 0) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void createThings() {
        map = new HashMap<>();
        videoThumbNailFileList = new ArrayList<>();
        imageFileList = new ArrayList<>();
        videoFileList = new ArrayList<>();
        musicFileList = new ArrayList<>();

        loadingDialog = new LoadingDialog(context);
        compositeDisposable = new CompositeDisposable();
        handler = new Handler(Looper.getMainLooper());
        musicModelList = new ArrayList<>();
        videoThumbNailList = new ArrayList<>();
        selectedMusicList = new ArrayList<>();
    }

    @Override
    public void clickEvent() {

        binding.selectCategoryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.musicRadioButton:
                        type = "M";
                        binding.musicTitleTextView.setVisibility(View.VISIBLE);
                        binding.musicFileListRecyclerView.setVisibility(View.VISIBLE);

                        binding.videoTitleTextView.setVisibility(View.GONE);
                        binding.videoFileListRecyclerView.setVisibility(View.GONE);
                        binding.explainTextView.setVisibility(View.GONE);
                        break;

                    case R.id.videoRadioButton:
                        type = "V";
                        binding.videoTitleTextView.setVisibility(View.VISIBLE);
                        binding.videoFileListRecyclerView.setVisibility(View.VISIBLE);
                        binding.explainTextView.setVisibility(View.VISIBLE);

                        binding.musicTitleTextView.setVisibility(View.GONE);
                        binding.musicFileListRecyclerView.setVisibility(View.GONE);
                        break;
                }
            }
        });

        musicUploadListInterface = new MusicUploadListInterface() {
            @Override
            public void chooseMusic(MusicModel musicModel, int position) {

                selectedMusicList.add(musicModel);
                music_FileUploadAdapter.submitList(selectedMusicList);
                music_FileUploadAdapter.notifyDataSetChanged();

                File file = new File(getAllMp3Path()[position]);
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("musicFileList", file.getName(), requestFile);
                musicFileList.add(uploadFile);

            }
        };


        binding.okButton.setOnClickListener(v -> {
            if (checkNull()) {

                androidx.appcompat.app.AlertDialog.Builder cautionAlert = new androidx.appcompat.app.AlertDialog.Builder(context);
                View cautionView = LayoutInflater.from(context).inflate(R.layout.dialog_block, null, false);
                cautionAlert.setView(cautionView);
                Button okButton = cautionView.findViewById(R.id.okButton);
                okButton.setText("확인");
                Button reportButton = cautionView.findViewById(R.id.reportButton);
                reportButton.setVisibility(View.GONE);
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
                okButton.setOnClickListener(v1 -> {
                    alertDialog.dismiss();
                    if (type.equals("M")) {
                        api_InsertMusicFileUpload();
                    } else {
                        api_InsertVideoFileUpload();
                    }
                });

                cancelButton.setOnClickListener(v1 -> alertDialog.dismiss());


            } else {
                Toast.makeText(context, "음악과 동영상 중 한 가지 이상 업로드를 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }

        });

        binding.closeImageButton.setOnClickListener(v -> finish());
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                List<Uri> uriList = new ArrayList<>();
                //TODO : result 값이 성공할 경우
                if (result.getResultCode() == RESULT_OK) {
                    if (data == null) {
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
                                    bitmap = ImageDecoder.decodeBitmap(source);
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
                            try {


                                Log.e("abc", "uri : " + uri.toString());

                                if (uri.toString().contains("video")) {
                                    String videoPath = getRealPathFromURI(uri); // path 경로
                                    Bitmap videoThumbNail = createThumbnail(context, videoPath);
                                    videoThumbNailList.add(videoThumbNail);
                                    getVideoFile(videoPath, -1);
                                    saveBitmaptoJpeg(videoThumbNail, "upload", "thumbNail_" + simpleDateFormat.format(new Date()) + "_JunTalk");
                                } else {
                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
//                                    String imagePath = getRealPathFromURI(uri); // path 경로
//                                    getImageFile(imagePath, -1);
                                    saveBitmaptoJpeg(bmRotated, "upload", "musicImage_" + simpleDateFormat.format(new Date()) + "_JunTalk");
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        } else {
                            // TODO : 이미지 선택이 여러장일 경우
                            ClipData clipData = data.getClipData();

                            if (clipData.getItemCount() > 10) {

                                Snackbar.make(getCurrentFocus(), "사진선택은 10장까지만 가능합니다.", Snackbar.LENGTH_SHORT).show();

                            } else {

                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri uri = clipData.getItemAt(i).getUri();
                                    uriList.add(uri);
                                    Bitmap bitmap = null;
                                    try {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                            bitmap = ImageDecoder.decodeBitmap(source);
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
                                    try {
                                        Log.e("abc", "uri : " + uri.toString());
                                        if (uri.toString().contains("video")) {
                                            String videoPath = getRealPathFromURI(uri); // path 경로
//                                            String videoPath = getPath(context, uri);
                                            Bitmap videoThumbNail = createThumbnail(context, videoPath);
                                            videoThumbNailList.add(videoThumbNail);
                                            getVideoFile(videoPath, i);
                                            saveBitmaptoJpeg(videoThumbNail, "upload", "thumbNail_" + simpleDateFormat.format(new Date()) + "_" + i + "_JunTalk");
                                        } else {

                                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
//                                            saveBitmaptoJpeg(bmRotated, "abc", simpleDateFormat.format(new Date()) + "_" + i + "_JunTalk");
//                                            String imagePath = getRealPathFromURI(uri); // path 경로
//                                            getImageFile(imagePath, i);
                                            saveBitmaptoJpeg(bmRotated, "upload", "musicImage_" + simpleDateFormat.format(new Date()) + "_" + i + "_JunTalk");
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } //data.getClipData() == null or
                    }//data == null or
                    video_FileUploadAdapter.submitList(videoThumbNailList);
                    video_FileUploadAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.uploadButton.setOnClickListener(v -> {

            if (type != null) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.drawable.juntalk_logo);
                builder.setTitle("선택");
                if (type.equals("M")) {
                    final CharSequence[] items = {"Music"};
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    if (musicFileList.size() > 0) {

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                        builder1.setIcon(R.drawable.juntalk_logo);
                                        builder1.setTitle("확인");
                                        final CharSequence[] items = {"추가", "삭제"};
                                        builder1.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
//                                                            getMusicData();
                                                        getMusicDataCopy();
                                                        DialogMusicList dialogMusicList = new DialogMusicList(context, musicModelList, musicUploadListInterface);
                                                        dialogMusicList.setCancelable(true);
                                                        dialogMusicList.getWindow().setGravity(Gravity.BOTTOM);
                                                        dialogMusicList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                dialogMusicList.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                                                                dialogMusicList.show();

                                                            }
                                                        });
                                                        break;
                                                    case 1:
                                                        musicFileList.clear();
                                                        selectedMusicList.clear();
                                                        music_FileUploadAdapter.notifyDataSetChanged();
                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                                        alertDialog.show();

                                    } else {
                                        musicFileList.clear();
                                        musicModelList.clear();
//                                            getMusicData();
                                        getMusicDataCopy();
                                        DialogMusicList dialogMusicList = new DialogMusicList(context, musicModelList, musicUploadListInterface);
                                        dialogMusicList.setCancelable(true);
                                        dialogMusicList.getWindow().setGravity(Gravity.BOTTOM);
                                        dialogMusicList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialogMusicList.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                                                dialogMusicList.show();

                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                    break;
                            }
                        }
                    });

                } else {

                    final CharSequence[] items = {"Video"};
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    if (videoThumbNailList.size() > 0) {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                        builder1.setIcon(R.drawable.juntalk_logo);
                                        builder1.setTitle("확인");
                                        final CharSequence[] items = {"추가", "삭제"};
                                        builder1.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case 0:
                                                        Intent intent = new Intent();
                                                        intent.setType("video/*");
                                                        intent.setAction(Intent.ACTION_PICK);
                                                        /*intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);*/
                                                        activityResultLauncher.launch(intent);
                                                        break;
                                                    case 1:
                                                        videoFileList.clear();
                                                        videoThumbNailList.clear();
                                                        videoThumbNailFileList.clear();
                                                        video_FileUploadAdapter.notifyDataSetChanged();
                                                        break;
                                                }
                                            }
                                        });
                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                                        alertDialog.show();

                                    } else {
                                        Intent intent = new Intent();
                                        intent.setType("video/*");
                                        intent.setAction(Intent.ACTION_PICK);
                                        /*intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);*/
                                        activityResultLauncher.launch(intent);
                                    }

                                    dialog.dismiss();


                                    break;
                            }
                        }
                    });

                }

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            } else {
                Toast.makeText(context, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

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

    public void deleteSavedJpegFile(String folder, String name) {
        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();
        String folder_name = "/" + folder + "/";
        String file_name = name;
        String string_path = ex_storage + folder_name;
        File file_path = new File(string_path);
        if (!file_path.isDirectory()) {
            file_path.mkdirs();
        }

    }

    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {

        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();
        String folder_name = "/" + folder + "/";
        String file_name = name;
        String string_path = ex_storage + folder_name;
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
        /*getVideoFileList();*/
        getMusicFileList();
    }


    public void music_SaveBitmaptoJpeg(Bitmap bitmap, String folder, String name) {

        File fileDir = getFilesDir();
        String ex_storage = fileDir.getPath();
        String folder_name = "/" + folder + "/";
        String file_name = name;
        String string_path = ex_storage + folder_name;
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
        getMusicFileList();
    }

    private String videoFileName = "";

    //TODO : 비디오 파일 서버로 전송
    public void getVideoFile(String filePath, int count) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String uuid = UUID.randomUUID().toString();
        /*videoFileName = uuid + "_JunTalk";*/
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        AppCompatEditText editText = new AppCompatEditText(context);
        editText.setMaxLines(3);
        editText.setMaxEms(50);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        editText.setLayoutParams(params);
        builder.setView(editText);
        builder.setMessage("비디오 제목을 입력해주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoFileName = editText.getText().toString() + "_JunTalk";
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("videoFiles", videoFileName.replaceAll(" ", "").replaceAll("\\n", ""), requestFile);
                try {
                    Log.e("abc", "size : " + sizeCalculation(uploadFile.body().contentLength()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoFileList.add(uploadFile);
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoFileName = uuid + "_JunTalk";
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
                try {
                    Log.e("abc", "size : " + sizeCalculation(uploadFile.body().contentLength()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoFileList.add(uploadFile);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        handler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation3;
                alertDialog.show();
            }
        });
        /*Log.e("abc","videoFileName : "+ videoFileName);
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
        try {
            Log.e("abc", "size : " + sizeCalculation(uploadFile.body().contentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFileList.add(uploadFile);*/

    }

    public String sizeCalculation(long size) {
        String CalcuSize = null;
        int i = 0;

        double calcu = (double) size;
        while (calcu >= 1024 && i < 5) { // 단위 숫자로 나누고 한번 나눌 때마다 i 증가
            calcu = calcu / 1024;
            i++;
        }
        DecimalFormat df = new DecimalFormat("##0.0");
        switch (i) {
            case 0:
                CalcuSize = df.format(calcu) + "Byte";
                break;
            case 1:
                CalcuSize = df.format(calcu) + "KB";
                break;
            case 2:
                CalcuSize = df.format(calcu) + "MB";
                break;
            case 3:
                CalcuSize = df.format(calcu) + "GB";
                break;
            case 4:
                CalcuSize = df.format(calcu) + "TB";
                break;
            default:
                CalcuSize = "ZZ"; //용량표시 불가

        }
        return CalcuSize;
    }

    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath, int count) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String imageFileName;
        if (count <= 0) {
            imageFileName = simpleDateFormat.format(new Date()) + "_JunTalk.jpg";
        } else {
            imageFileName = simpleDateFormat.format(new Date()) + "_" + count + "_JunTalk.jpg";

        }
        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile);
        imageFileList.add(uploadFile);
    }

    //TODO : 안드로이드 패키지 안에 있는 files 경로에 있는 파일들을 보내는 부분
    public void getVideoFileList() {
        File fileDir = getFilesDir();
        String path = fileDir.getPath();
        String string_path = path + File.separator + "upload" + File.separator;
        File file = new File(string_path);
        File[] files = file.listFiles();
        for (File myFile : files) {
            Log.e("abc", "files : " + myFile.getPath() + " , " + myFile.getName());
            if (myFile.getName().contains("thumbNail_")) {
                RequestBody requestFile = RequestBody.create(myFile, MediaType.parse("multipart/form-data"));
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("videoThumbNailFile", myFile.getName(), requestFile);
                videoThumbNailFileList.add(uploadFile);
            }
        }
    }

    //TODO : 안드로이드 패키지 안에 있는 files 경로에 있는 파일들을 보내는 부분
    public void getMusicFileList() {
        File fileDir = getFilesDir();
        String path = fileDir.getPath();
        String string_path = path + File.separator + "upload" + File.separator;
        File file = new File(string_path);
        File[] files = file.listFiles();
        for (File myFile : files) {
            if (myFile.getName().contains("musicImage_")) {
                RequestBody requestFile = RequestBody.create(myFile, MediaType.parse("multipart/form-data"));
                MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("imageFiles", myFile.getName(), requestFile);
                imageFileList.add(uploadFile);
            }
        }
    }


    //TODO : 이미지 회전 시키기
    public Bitmap rotateBitmap(Uri uri, Bitmap bitmap) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ExifInterface exifInterface = new ExifInterface(inputStream);
        inputStream.close();
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        Log.e("abc", "orientation : " + orientation);
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
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        compositeDisposable.dispose();

        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void api_InsertMusicFileUpload() {

        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userId", JunApplication.getMyModel().userId);
        userInfoMap.put("regDate", simpleDateFormat.format(new Date().getTime()));
        Observable<Boolean> result = service.musicUpload(CommonString.videoController, userInfoMap, musicFileList, imageFileList);
        compositeDisposable.add(result.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        if (aBoolean) {
                            Intent intent = new Intent();
                            intent.setAction("MusicFragmentRefresh");
                            intent.putExtra("MusicFragmentRefresh", "MusicFragmentRefresh");
                            sendBroadcast(intent);
//                            setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    Snackbar.make(getCurrentFocus(), "서버와의 연결이 불안정합니다.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

                                }
                            });
                        }
                        if (isFinishing()) {
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (isFinishing()) {
                            loadingDialog.dismiss();
                        }
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onComplete()
                    {
                        loadingDialog.dismiss();
                    }
                }));
    }

    public void api_InsertVideoFileUpload() {
        if (!isFinishing() && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }

        Map<String, Object> userInfoMap = new HashMap<>();
        userInfoMap.put("userId", JunApplication.getMyModel().userId);
        userInfoMap.put("regDate", simpleDateFormat.format(new Date().getTime()));
        Observable<Boolean> result = service.videoUpload(CommonString.videoController, userInfoMap, videoFileList, videoThumbNailFileList);
        compositeDisposable.add(result.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        if (aBoolean) {
                            Intent intent = new Intent();
                            intent.setAction("VideoFragmentRefresh");
                            intent.putExtra("VideoFragmentRefresh", "VideoFragmentRefresh");
                            sendBroadcast(intent);
//                            setResult(RESULT_OK,intent);
                            finish();
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(getCurrentFocus(), "서버와의 연결이 불안정합니다.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

                                }
                            });
                        }
                        if (isFinishing()) {
                            loadingDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        if (isFinishing()) {
                            loadingDialog.dismiss();
                        }
                        loadingDialog.dismiss();

                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));

    }


    private void getMusicDataCopy() {
        musicModelList.clear();
        for (String s : getAllMp3Path()) {
            MusicModel musicModel = new MusicModel();
            musicModel.musicName = s.substring("/storage/emulated/0/Music/".length());
            musicModelList.add(musicModel);
        }

    }


    private void getMusicData() {
        //1. 음악파일인지 아닌지, 2. 앨범 아이디, 3. 음원명, 4.가수명, 미디어 파일 아이디(?)
        String[] projection = {
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, // content://로 시작하는 content table uri
                projection, // 어떤 column을 출력할 것인지
                null, // 어떤 row를 출력할 것인지
                null,
                MediaStore.Audio.Media.TITLE); // 어떻게 정렬할 것인지
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    // MediaStore.Audio.Media.IS_MUSIC 값이 1이면 mp3 음원 파일입니다.
                    // 그리고 밑에는 mp3 metadata 이미지 파일의 uri값을 얻어낸것입니다.
                    // 이렇게 얻어낸 데이터를 arraylist에 저장합니다.
                    if (cursor.getInt(0) != 0) {
                        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                        Uri uri = ContentUris.withAppendedId(sArtworkUri, Integer.valueOf(cursor.getString(1)));
//                        ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(sArtworkUri, "r");
//                        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, null);
                        MusicModel musicModel = new MusicModel();
                        musicModel.musicImage = uri.toString();
                        musicModel.musicName = cursor.getString(2);
                        musicModelList.add(musicModel);
                        /*MusicData data = new MusicData();
                        data.setMusicImg(uri);
                        data.setMusicTitle(cursor.getString(2));
                        data.setSinger(cursor.getString(3));
                        data.setAlbumId(cursor.getString(1));
                        data.setMusicId(cursor.getString(4));*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String[] getAllMp3Path() {

        // MP3 경로를 가질 문자열 배열.
        String[] resultPath = null;
        // 외장 메모리 접근 권한을 가지고 있는지 확인. ( Marshmallow 이상 )  // mAcitivity == Main Activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            // 찾고자하는 파일 확장자명.
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
            String[] selectionArgsMp3 = new String[]{mimeType};
            Cursor c = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Media.DATA}, selectionMimeType, selectionArgsMp3, null);
            if (c.getCount() == 0)
                return null;
            resultPath = new String[c.getCount()];
            while (c.moveToNext()) {
                // 경로 데이터 셋팅.
                resultPath[c.getPosition()] = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                Log.i("abc", resultPath[c.getPosition()]);
            }
        }
        return resultPath;
    }

    public class PhItemAnimator extends SimpleItemAnimator {

        private Context mContext;

        public PhItemAnimator(Context a_context) {

            mContext = a_context;
        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder a_holder) {
            return false;
        }

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder a_holder) {
            Animator animator = AnimatorInflater.loadAnimator(mContext, R.animator.zoom_in);
            animator.setInterpolator(new BounceInterpolator());
            animator.setTarget(a_holder.itemView);
            animator.start();

            return true;
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder a_holder, int a_fromX, int a_fromY, int a_toX, int a_toY) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder a_oldHolder, RecyclerView.ViewHolder a_newHolder, int a_fromLeft, int a_fromTop, int a_toLeft, int a_toTop) {
            return false;
        }

        @Override
        public void runPendingAnimations() {

        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder a_item) {

        }

        @Override
        public void endAnimations() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
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
        }
    }

}
