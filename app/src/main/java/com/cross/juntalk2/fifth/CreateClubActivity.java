package com.cross.juntalk2.fifth;

import static com.cross.juntalk2.utils.Utils.getPath;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityCreateClubBinding;
import com.cross.juntalk2.model.ClubModel;
import com.cross.juntalk2.model.KakaoMapClassUtis;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.cross.juntalk2.utils.VideoTrimmerActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.royrodriguez.transitionbutton.TransitionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.cross.juntalk2.utils.GlideApp;

@SuppressWarnings("unchecked")
public class CreateClubActivity extends CreateNewCompatActivity {

    private ActivityCreateClubBinding binding;
    private Handler handler;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityResultLauncher<Intent> trimVideoResultLauncher;
    private ActivityResultLauncher<Intent> fileResultLauncher;
    private ActivityResultLauncher<Intent> EditedVideoFileLauncher;
    private ClipData clipData;
    private List<MultipartBody.Part> videoFileList;
    private List<MultipartBody.Part> imageFileList;
    private ClubFileAdapter adapter;
    private List<Bitmap> bitmapList;
    private List<String> imagePathList;
    private DecimalFormat commas = new DecimalFormat("###,###");
    private String result = "";
    private RetrofitService service;
    private long userKakaoOwnNumber;
    private int user_Index;
    private String userId;
    private MyModel myModel;
    private int possibleJoinCount = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private StringBuilder builder;
    private final int CROP_IMAGE = 1;
    private File tempFile;
    private String absolutePath;
    private String mCurrentPhotoPath;
    private int minAge = 0, maxAge = 0;
    private KakaoMapClassUtis.Place localPlace;
    private Disposable backgroundTask;
    private ClubModel getIntentClubModel;
    private List<Bitmap> fileToBitmapList;
    private CreateStringToFile createStringToFile;
    private VideoTrimLauncherThread videoTrimLauncherThread;
    private String place, title, clubIntroduce;
    private int expectedMoney, limitJoinCount;
    private String randomUUID, commonRandomUUID, userMainPhoto;
    private String expectedMoneyEdit, selectedClubJoinDate;
    private LoadingDialog loadingDialog;

    public interface RemoveClickInterface {
        void removeClick(Bitmap bitmap, int position);
    }

    private RemoveClickInterface removeClickInterface;

    @Override
    public void getInterfaceInfo() {
        loadingDialog = new LoadingDialog(context);
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
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
                            Log.e("abc", "data.getClipData() == null");
                            Uri uri = result.getData().getData();
                            uriList.add(uri);
                            Bitmap bitmap = null;
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                    try{
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    }catch(Exception e){
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

                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {
                                Intent intent = new Intent(context, VideoTrimmerActivity.class);
                                intent.putExtra("videoUri", imagePath);
                                trimVideoResultLauncher.launch(intent);

                            } else {
                                imagePathList.add(imagePath);
                                fileToBitmapList.add(bitmap);
//                                bitmapList.add(bitmap);
                                adapter.setItems(fileToBitmapList);
                            }

                        } else {

                            // TODO : 이미지 선택이 여러장일 경우
                            // TODO : 갤러리가 아닌 곳에서 올 경우
                            final Bundle extras = data.getExtras();
                            if (extras != null) {
                                Log.e("abc", "extras != null");

                                if (extras.get("android.intent.extra.STREAM") instanceof List) {
                                    List<Uri> uris = (List<Uri>) extras.get("android.intent.extra.STREAM");
                                    for (int i = 0; i < uris.size(); i++) {
                                        Uri uri = uris.get(i);
                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                                try{
                                                    bitmap = ImageDecoder.decodeBitmap(source);
                                                }catch(Exception e){
                                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                                }
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }
                                            String imagePath = getRealPathFromURI(uri);

                                            if (!uri.toString().contains("video") && !uri.toString().contains(".mp4")) {
                                                try {
                                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                                    imagePathList.add(imagePath);
                                                    fileToBitmapList.add(bmRotated);
                                                    adapter.setItems(fileToBitmapList);
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

                                Log.e("abc", "extra == null");
                                if (data.getClipData() != null) {
                                    Log.e("abc", "data.getClipData() != null");
                                    clipData = data.getClipData();
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        Uri uri = clipData.getItemAt(i).getUri();
                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                                try{
                                                    bitmap = ImageDecoder.decodeBitmap(source);
                                                }catch(Exception e){
                                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                                }
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                            }
                                            String imagePath = getRealPathFromURI(uri);
                                            imagePathList.add(imagePath);
                                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {
                                                try {
                                                    Bitmap videoThumbNail = createThumbnail(context, imagePath);
                                                    Bitmap bmRotated = rotateBitmap(uri, videoThumbNail);
                                                    fileToBitmapList.add(bmRotated);
                                                    adapter.setItems(fileToBitmapList);

                                                } catch (IOException e) {
                                                    e.printStackTrace();

                                                }

                                            } else {

                                                try {
                                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                                    fileToBitmapList.add(bmRotated);
                                                    adapter.setItems(fileToBitmapList);
                                                } catch (IOException e) {
                                                    e.printStackTrace();

                                                }
                                            }
                                        } catch (FileNotFoundException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();

                                        } catch (IOException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
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
                adapter.setItems(fileToBitmapList);
            }
        });
    }


    public String saveBitmaptoJpeg(Bitmap bitmap, String absolutePath) {
        String file_name = System.currentTimeMillis() + "_copy.jpg";
        String foler_Name = "CopyFile";
        String string_path = absolutePath + "/" + foler_Name + "/" + file_name;
        File file_path;
        try {
            file_path = new File(absolutePath + "/" + foler_Name);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (FileNotFoundException exception) {
            Log.e("FileNotFoundException", exception.getMessage());
        } catch (IOException exception) {
            Log.e("IOException", exception.getMessage());
        }
        return string_path;
    }


    public void settingAlreadyInfo() {
        getIntentClubModel = (ClubModel) getIntent().getSerializableExtra("clubModel");

        if (getIntentClubModel != null) {
            randomUUID = getIntentClubModel.club_Uuid;
            try {
                StringTokenizer tokenizer = new StringTokenizer(getIntentClubModel.hashTagList, ",");
                while (tokenizer.hasMoreTokens()) {
                    String tokenText = tokenizer.nextToken();
                    builder.append(tokenText);
                    builder.append(",");
                    Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
                    chip.setText(tokenText);
                    FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    binding.chipgroup.addView(chip, layoutParams);
                    chip.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.delete(builder.lastIndexOf(tokenText + ","), (builder.lastIndexOf(tokenText + ",") + (tokenText.length() + 1)));
                            binding.chipgroup.removeView(v);
                        }
                    });
                }

                fileToBitmapList = new ArrayList<>();
                for (String url : getIntentClubModel.allUrls) {
                    if (url.contains(".mp4")) {
                        saveImageFromUrl(CommonString.CommonStringInterface.VideoThumbNail + url);
                    } else {
                        saveImageFromUrl(CommonString.CommonStringInterface.FileBaseUrl + url);
                    }
                }

                selectedClubJoinDate = getIntentClubModel.regDate;
                place = getIntentClubModel.place;
                title = getIntentClubModel.title;
                expectedMoney = getIntentClubModel.expectedMoney;
                minAge = getIntentClubModel.minAge;
                maxAge = getIntentClubModel.maxAge;
                limitJoinCount = getIntentClubModel.limitJoinCount;
                possibleJoinCount = limitJoinCount;
                clubIntroduce = getIntentClubModel.clubIntroduce;

                JSONObject jsonObject = new JSONObject(getIntentClubModel.place);
                binding.birthDayTextView.setText(selectedClubJoinDate);
                binding.placeTextView.setText(jsonObject.get("place_name").toString() + "\n" + jsonObject.get("address_name").toString() + "\n" + jsonObject.get("phone").toString());
                binding.clubTitleInputEditTextView.setText(title);
                binding.moneyTextInputEditTextView.setText("" + expectedMoney);
                binding.ageRangeBar.setProgress(minAge, maxAge);
                binding.minAgeTextView.setText("" + minAge + "세");
                binding.maxAgeTextView.setText("" + maxAge + "세");
                binding.numberPicker.setValue(limitJoinCount);
                binding.clubIntroduceTextInputEditTextView.setText(clubIntroduce);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap loadBackgroundBitmap(Context context,
                                       String imgFilePath) throws Exception, OutOfMemoryError {

        if (imgFilePath.contains(".mp4") || imgFilePath.contains(".3gp")) {
            return createThumbnail(context, imgFilePath);
        } else {
            // 폰의 화면 사이즈를 구한다.
            Display display = ((WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int displayWidth = display.getWidth();
            int displayHeight = display.getHeight();
            // 읽어들일 이미지의 사이즈를 구한다.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgFilePath, options);
            // 화면 사이즈에 가장 근접하는 이미지의 리스케일 사이즈를 구한다.
            // 리스케일의 사이즈는 짝수로 지정한다. (이미지 손실을 최소화하기 위함.)
            float widthScale = options.outWidth / displayWidth;
            float heightScale = options.outHeight / displayHeight;
            float scale = widthScale > heightScale ? widthScale : heightScale;
            if (scale >= 8) {
                options.inSampleSize = 8;
            } else if (scale >= 6) {
                options.inSampleSize = 6;
            } else if (scale >= 4) {
                options.inSampleSize = 4;
            } else if (scale >= 2) {
                options.inSampleSize = 2;
            } else {
                options.inSampleSize = 1;
            }
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imgFilePath, options);
        }

    }


    /* 백그라운드에서 서버 이미지 Url로 기기에 저장하는 메소드 */
    private void saveImageFromUrl(String imageUrl) {
        backgroundTask = Observable.fromCallable(() -> {
            // Glide를 이용해서 서버의 Url로부터 이미지 파일 반환
            return Glide.with(CreateClubActivity.this)
                    .asFile()
                    .load(imageUrl).submit().get();
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribe(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Throwable {
                        // 이미지 파일 갤러리에 저장

                        saveFile(file, getImageMimeType(imageUrl), getImageName(imageUrl));

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundTask != null)
            backgroundTask.dispose();

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    /* 이미지 파일 갤러리에 저장하는 메소드 */
    public void saveFile(@NonNull final File file, @NonNull final String mimeType, @NonNull final String displayName) throws IOException {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // IS_PENDING을 1로 설정해놓으면, 현재 파일을 업데이트 전까지 외부에서 접근하지 못하도록 할 수 있다.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        final ContentResolver resolver = getContentResolver();
        Uri uri = null;
        try {
            final Uri contentUri;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // 외부저장소-Downloads에 저장하고 싶을 때(Q 이상)
                contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            } else {
                // 외부저장소-Pictures에 저장하고 싶을 때

                if (displayName.contains(".mp4")) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
            }
            uri = resolver.insert(contentUri, values);


            if (uri == null) {
                throw new IOException("Failed to create new MediaStore record.");
            }
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null) {
                    throw new IOException("Failed to open output stream.");
                }
                // 파일을 바이트배열로 변환 후, 파일에 저장
                byte[] bArray = convertFileToByteArray(file);
                stream.write(bArray);
                stream.flush();
                stream.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();
                    // 파일 저장이 완료되었으니, IS_PENDING을 다시 0으로 설정한다.
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    // 파일을 업데이트하면, 파일이 보인다.
                    resolver.update(uri, values, null, null);
                }
            }

            try {
                String filePath = getRealPathFromURI(uri);
                imagePathList.add(filePath);
                File fileToBitmap = new File(filePath);
                fileToBitmapList.add(loadBackgroundBitmap(context, fileToBitmap.getPath()));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(fileToBitmapList);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CreateClubActivity.this, "사진을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            /*if (!backgroundTask.isDisposed()) {
                backgroundTask.dispose();
            }*/
            throw e;
        }
    }

    /* 파일을 바이트배열로 변환해주는 메소드 */
    private byte[] convertFileToByteArray(File file) {
        FileInputStream fis = null;
        // Creating bytearray of same length as file
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            // Reading file content to byte array
            fis.read(bArray);
            fis.close();
        } catch (IOException ioExp) {
            ioExp.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bArray;
    }


    /* 파일 이름 반환하는 메소드 */
    private String getImageName(String imageUrl) {
        String[] name_1 = imageUrl.split("/");
        // 확장자 포함된 이름 반환
        return name_1[(name_1.length - 1)];
    }

    /* mimeType 반환 */
    private String getImageMimeType(String imageUrl) {
        String[] type_arr = imageUrl.split("\\.");
        if (type_arr[(type_arr.length - 1)].toLowerCase().equals("jpg")) {
            return "image/jpeg";
        } else if (type_arr[(type_arr.length - 1)].toLowerCase().equals("mp4")) {
            return "video/mp4";
        } else {
            return "image/" + type_arr[(type_arr.length - 1)].toLowerCase();
        }
    }


    public void imageCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/* video/*");
        intent.putExtra("crop", true);
        intent.putExtra("outputX", 200); // crop한 이미지의 x축 크기
        intent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        intent.putExtra("aspectX", 1); // crop 박스의 x축 비율
        intent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        fileResultLauncher.launch(intent);
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
                                Intent intent = new Intent();
                                intent.setType("image/* video/*");
                                intent.setAction(Intent.ACTION_PICK);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                fileResultLauncher.launch(intent);
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
            Intent intent = new Intent();
//            intent.setType(MediaStore.Images.Media.CONTENT_TYPE+ MediaStore.Video.Media.CONTENT_TYPE);
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            final Intent chooserIntent = Intent.createChooser(intent, "Select Image");
            fileResultLauncher.launch(chooserIntent);

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
        MultipartBody.Part part = MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile);
        imageFileList.add(part);
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
        MultipartBody.Part part = MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile);
        videoFileList.add(part);
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


    private class VideoTrimLauncherThread extends Thread {
        private Intent data;


        public VideoTrimLauncherThread(Intent data) {
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            synchronized (this) {
                clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        String imageRealPath = getRealPathFromURI(uri);
                        if (imageRealPath != null) {
                            if (imageRealPath.contains(".mp4") || imageRealPath.contains(".3gp")) {
                                Intent cropVideo = new Intent(context, VideoTrimmerActivity.class);
                                cropVideo.setDataAndType(uri, "video/*");
                                cropVideo.putExtra("videoUri", imageRealPath);
                                try {
                                    EditedVideoFileLauncher.launch(cropVideo);
                                } catch (Exception e) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "이 파일은 편집할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            } else {
                                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                                editIntent.setDataAndType(uri, "image/*");
                                editIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                editIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                try {
                                    EditedVideoFileLauncher.launch(editIntent);
                                } catch (Exception e) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(context, "편집이 불가능한 파일입니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {

                    Uri uri = data.getData();
                    String imageRealPath = getRealPathFromURI(uri);
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    if (imageRealPath.contains(".mp4") || imageRealPath.contains(".3gp")) {
                        editIntent.setDataAndType(uri, "video/*");
                    } else {
                        editIntent.setDataAndType(uri, "image/*");
                    }
                    editIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    editIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    EditedVideoFileLauncher.launch(editIntent);
                }


            }
        }
    }


    @Override
    public void getIntentInfo() {


        EditedVideoFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode() == RESULT_CANCELED) {
                    if (clipData.getItemCount() > 5) {
                        Snackbar.make(getCurrentFocus(), "사진선택은 5장까지만 가능합니다.", Snackbar.LENGTH_SHORT).show();
                    } else {

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri uri = clipData.getItemAt(i).getUri();
                            Bitmap bitmap = null;
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), uri);
                                    try{
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    }catch(Exception e){
                                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                    }
                                } else {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                }
                                String imagePath = getRealPathFromURI(uri);
                                imagePathList.add(imagePath);
                                if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {
                                    try {
                                        Bitmap videoThumbNail = createThumbnail(context, imagePath);
                                        Bitmap bmRotated = rotateBitmap(uri, videoThumbNail);
//                                        bitmapList.add(bmRotated);
                                        fileToBitmapList.add(bmRotated);
                                        adapter.setItems(fileToBitmapList);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {
                                        Bitmap bmRotated = rotateBitmap(uri, bitmap);
//                                        bitmapList.add(bmRotated);
                                        fileToBitmapList.add(bmRotated);
                                        adapter.setItems(fileToBitmapList);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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
                                            Log.e("abc", "extras : " + extras.toString());
                                        } catch (Exception e) {

                                        }
                                    } else {
                                        if (data.getClipData() != null) {
                                            Log.e("abc", "data.getClipData : " + data.getClipData().toString());
                                        }
                                    }
                                    synchronized (videoTrimLauncherThread) {
                                        videoTrimLauncherThread.notify();
                                    }

                                } else {

                                    String realDataPath = "";
                                    if (uri.toString().contains("content://")) {
                                        realDataPath = getRealPathFromURI(uri);

                                    } else {
                                        realDataPath = uri.toString();
                                    }

                                    if (realDataPath.contains(".mp4") || realDataPath.contains(".3gp")) {
                                        Bitmap videoThumbNail = createThumbnail(context, realDataPath);
//                                        bitmapList.add(videoThumbNail);
                                        fileToBitmapList.add(videoThumbNail);
                                        imagePathList.add(realDataPath);
                                        adapter.setItems(fileToBitmapList);
                                        synchronized (videoTrimLauncherThread) {
                                            videoTrimLauncherThread.notify();
                                        }
                                    } else {


                                        Bitmap bitmap = null;
                                        try {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), Uri.parse("file://" + realDataPath));
                                                try{
                                                    bitmap = ImageDecoder.decodeBitmap(source);
                                                }catch(Exception e){
                                                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                                                }
                                            } else {
                                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse("file://" + realDataPath));
                                            }
//                                            bitmapList.add(bitmap);
                                            fileToBitmapList.add(bitmap);
                                            imagePathList.add(realDataPath);
                                            adapter.setItems(fileToBitmapList);
                                            synchronized (videoTrimLauncherThread) {
                                                videoTrimLauncherThread.notify();
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
                            }
                        }
                    }
                }
            }
        });
        trimVideoResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String trimmedVideoURi = data.getStringExtra("trimmedVideoURi");
                        Bitmap videoThumbNail = createThumbnail(context, trimmedVideoURi);
                        imagePathList.add(trimmedVideoURi);
                        fileToBitmapList.add(videoThumbNail);
                        /*bitmapList.add(videoThumbNail);*/
                        adapter.setItems(fileToBitmapList);

                    }
                }
            }
        });


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        localPlace = (KakaoMapClassUtis.Place) intent.getSerializableExtra("placeInfo");
                        place = localPlace.placeJsonObject().toString();
                        binding.placeTextView.setText(localPlace.place_name + "\n" + localPlace.address_name + "\n" + localPlace.phone);

                    }
                }
            }
        });
    }

    public String uriToFilename(Uri uri) {
        String path = null;
        if (Build.VERSION.SDK_INT < 11) {
            path = getPath(getApplicationContext(), uri);
        } else if (Build.VERSION.SDK_INT < 19) {
            path = getPath(getApplicationContext(), uri);
        } else {
            path = getPath(getApplicationContext(), uri);
        }

        uri = Uri.parse(path);

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToNext();
        @SuppressLint("Range") String resultPath = cursor.getString(cursor.getColumnIndex("_data"));
        cursor.close();
        return resultPath;
    }


    @Override
    public void init() {
        binding = DataBindingUtil.setContentView(CreateClubActivity.this, R.layout.activity_create_club);
        adapter = new ClubFileAdapter(context);
        binding.fileRecyclerView.setAdapter(adapter);
        videoFileList = new ArrayList<>();
        imageFileList = new ArrayList<>();
        bitmapList = new ArrayList<>();
        imagePathList = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
        if (context instanceof RemoveClickInterface) {
            removeClickInterface = (RemoveClickInterface) context;
        }
        fileToBitmapList = new ArrayList<>();
    }

    @Override
    public void createThings() {
        builder = new StringBuilder();
        myModel = JunApplication.getMyModel();
        userKakaoOwnNumber = myModel.userKakaoOwnNumber;
        userId = myModel.userId;
        userMainPhoto = myModel.userMainPhoto;
        user_Index = myModel.user_Index;
        commonRandomUUID = UUID.randomUUID().toString();
        preferences = getSharedPreferences("LookStatus", MODE_PRIVATE);
        editor = preferences.edit();
        binding.numberPicker.setMinValue(0);
        binding.numberPicker.setMaxValue(100);
        minAge = 0;
        maxAge = 100;
        binding.minAgeTextView.setText("" + 0 + "세");
        binding.maxAgeTextView.setText("" + 100 + "세");
        settingAlreadyInfo();

    }

    @Override
    public void clickEvent() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                binding.birthDayTextView.setVisibility(View.VISIBLE);
                binding.birthDayTextView.setTextColor(Color.BLUE);
                binding.birthDayTextView.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                String month = "";
                if ((monthOfYear + 1) < 10) {
                    month = "0" + (monthOfYear + 1);
                } else {
                    month = "" + (monthOfYear + 1);
                }
                String day = "";
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                } else {
                    day = "" + dayOfMonth;
                }
                selectedClubJoinDate = year + "-" + month + "-" + day;
            }
        };
        binding.lottieAnmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.getDatePicker().setMinDate(new Date().getTime());
                dialog.setMessage("클럽 조인을 생성할 날짜를 선택해 주세요.");
                dialog.show();
            }
        });

        removeClickInterface = new RemoveClickInterface() {
            @Override
            public void removeClick(Bitmap bitmap, int position) {
                imagePathList.remove(position);
                fileToBitmapList.remove(position);
                adapter.removeClickEvent(bitmap);

            }
        };
        adapter.setRemoveClickInterface(removeClickInterface);

        binding.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                possibleJoinCount = newVal;
            }
        });

        binding.ageRangeBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(
                    final RangeSeekBar seekBar, final int progressStart, final int progressEnd, final boolean fromUser) {
                minAge = progressStart;
                maxAge = progressEnd;
                binding.minAgeTextView.setText(String.valueOf(progressStart) + "세");
                binding.maxAgeTextView.setText(String.valueOf(progressEnd) + "세");
            }

            @Override
            public void onStartTrackingTouch(final RangeSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(final RangeSeekBar seekBar) {
            }
        });

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(expectedMoneyEdit)) {
                    expectedMoneyEdit = commas.format(Double.parseDouble(charSequence.toString().replaceAll(",", "")));
                    binding.moneyTextInputEditTextView.setText(expectedMoneyEdit);
                    binding.moneyTextInputEditTextView.setSelection(expectedMoneyEdit.length());

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        binding.moneyTextInputEditTextView.addTextChangedListener(watcher);


        binding.plusFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        binding.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                activityResultLauncher.launch(intent);
            }
        });

        binding.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.clubCategoryTextInputEditTextView == null || binding.clubCategoryTextInputEditTextView.getText().toString().equals("")) {

                } else {
                    addHashTag(binding.clubCategoryTextInputEditTextView.getText().toString());
                    binding.clubCategoryTextInputEditTextView.setText("");
                }

            }
        });


        if (preferences.getBoolean("createClubLook", false) == false) {
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forView(findViewById(R.id.friendTextView), "클럽을 생성하세요.", "클럽 생성한 내용들을 다 채우신 후 버튼을 눌러주세요.")
                            // All options below are optional
                            .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.white)   // Specify a color for the target circle
                            .titleTextSize(20)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.white)      // Specify the color of the title text
                            .descriptionTextSize(15)            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.black)  // Specify the color of the description text
                            .textColor(R.color.black)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
//                        .icon(Drawable)                     // Specify a custom drawable to draw as the target
                            .targetRadius(60),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            view.dismiss(true);
                            editor.putBoolean("createClubLook", true);
                            editor.commit();
                        }
                    });

        }


        binding.transitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                if (checkNull()) {
                    // Do your networking task or background work here.
                    binding.transitionButton.startAnimation();
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
                    if (getIntentClubModel == null) {
                        createClub();
                    } else {
                        updateClub();
                    }
                }
            }
        });
    }

    private class CreateStringToFile extends Thread {
        @Override
        public void run() {
            super.run();
            synchronized (this) {
                for (String s : imagePathList) {
                    File file = new File(s);
                    if (file == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "현재 인식할 수 없는 파일입니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
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
    }


    public void updateClub() {
        Map<String, RequestBody> userInfoMap = multiPartMap();
        service.updateClubList(CommonString.clubController, userInfoMap, imageFileList, videoFileList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    Intent intent = new Intent();
                                    intent.setAction("createClub");
                                    intent.putExtra("reFreshMyClubList", "reFreshMyClubList");
                                    sendBroadcast(intent);
                                    finish();
                                }
                            });
                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());

            }
        });

    }

    public void addHashTag(String text) {
        builder.append("#" + text);
        builder.append(",");
        Chip chip = (Chip) LayoutInflater.from(context).inflate(R.layout.view_flexboxlayout_string_item, null);
        chip.setText("#" + text);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.chipgroup.addView(chip, layoutParams);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.delete(builder.lastIndexOf("#" + text + ","), (builder.lastIndexOf("#" + text + ",") + (text.length() + 2)));
                binding.chipgroup.removeView(v);
            }
        });

    }

    public int getDp(int a) {
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, getResources().getDisplayMetrics());
        return dp;
    }


    public Map<String, RequestBody> multiPartMap() {
        title = binding.clubTitleInputEditTextView.getText().toString();
        limitJoinCount = possibleJoinCount;
        expectedMoney = Integer.parseInt(binding.moneyTextInputEditTextView.getText().toString().replaceAll(",", ""));
        clubIntroduce = binding.clubIntroduceTextInputEditTextView.getText().toString();
        Map<String, RequestBody> map = new HashMap<>();
        RequestBody userKakaoOwnNumber = RequestBody.create(String.valueOf(this.userKakaoOwnNumber), MediaType.parse("text/plain"));
        map.put("userKakaoOwnNumber", userKakaoOwnNumber);
        RequestBody user_Index = RequestBody.create(String.valueOf(this.user_Index), MediaType.parse("text/plain"));
        map.put("user_Index", user_Index);
        RequestBody regDate = RequestBody.create(selectedClubJoinDate, MediaType.parse("text/plain"));
        map.put("regDate", regDate);
        RequestBody userMainPhoto = RequestBody.create(CommonString.CommonStringInterface.FileBaseUrl + myModel.userMainPhoto, MediaType.parse("text/plain"));
        map.put("userMainPhoto", userMainPhoto);
        RequestBody userId = RequestBody.create(String.valueOf(this.userId), MediaType.parse("text/plain"));
        map.put("userId", userId);
        RequestBody title = RequestBody.create(this.title, MediaType.parse("text/plain"));
        map.put("title", title);
        RequestBody limitJoinCount = RequestBody.create(String.valueOf(this.limitJoinCount), MediaType.parse("text/plain"));
        map.put("limitJoinCount", limitJoinCount);
        RequestBody minAge = RequestBody.create(String.valueOf(this.minAge), MediaType.parse("text/plain"));
        map.put("minAge", minAge);
        RequestBody maxAge = RequestBody.create(String.valueOf(this.maxAge), MediaType.parse("text/plain"));
        map.put("maxAge", maxAge);
        RequestBody expectedMoney = RequestBody.create(String.valueOf(this.expectedMoney), MediaType.parse("text/plain"));
        map.put("expectedMoney", expectedMoney);
        RequestBody place = RequestBody.create(this.place, MediaType.parse("text/plain"));
        map.put("place", place);
        RequestBody clubIntroduce = RequestBody.create(this.clubIntroduce, MediaType.parse("text/plain"));
        map.put("clubIntroduce", clubIntroduce);
        RequestBody clubState = RequestBody.create(ClubModel.ClubState.N.toString(), MediaType.parse("text/plain"));
        map.put("clubState", clubState);
        RequestBody hashTagList = RequestBody.create(builder.toString(), MediaType.parse("text/plain"));
        map.put("hashTagList", hashTagList);
        RequestBody club_Uuid;
        if (randomUUID == null) {
            club_Uuid = RequestBody.create(commonRandomUUID, MediaType.parse("text/plain"));
        } else {
            club_Uuid = RequestBody.create(randomUUID, MediaType.parse("text/plain"));
        }
        map.put("club_Uuid", club_Uuid);
        return map;
    }

    public void createClub() {
        Map<String, RequestBody> userInfoMap = multiPartMap();
        service.createClub(CommonString.clubController, userInfoMap, imageFileList, videoFileList).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", String.valueOf(response.raw()));
                if (response.isSuccessful()) {
                    makeJoinChattingRoom();

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

    @Override
    public void getServer() {

    }


    public boolean checkNull() {

        if (builder.length() == 0) {
            Toast.makeText(context, "클럽 카테고리를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.clubTitleInputEditTextView.getText().toString() == null || binding.clubTitleInputEditTextView.getText().toString().equals("")) {
            Toast.makeText(context, "클럽 제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.moneyTextInputEditTextView.getText().toString() == null || binding.moneyTextInputEditTextView.getText().toString().equals("")) {
            Toast.makeText(context, "예상 경비 비용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (selectedClubJoinDate == null || selectedClubJoinDate.equals("")) {
            Toast.makeText(context, "클럽 조인 날짜를 지정해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.minAgeTextView.getText().toString() == null || binding.minAgeTextView.getText().toString().equals("")) {
            Toast.makeText(context, "최소 연령을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.maxAgeTextView.getText().toString() == null || binding.maxAgeTextView.getText().toString().equals("")) {
            Toast.makeText(context, "최대 연령을 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (possibleJoinCount == 0) {
            Toast.makeText(context, "참여 인원은 1명 이상이여야 합니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.clubIntroduceTextInputEditTextView.getText().toString() == null || binding.clubIntroduceTextInputEditTextView.getText().toString().equals("")) {
            Toast.makeText(context, "클럽 소개를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.placeTextView.getText().toString() == null || binding.placeTextView.getText().toString().equals("")) {
            Toast.makeText(context, "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (adapter.getItemCount() == 0) {
            Toast.makeText(context, "클럽 소개용 이미지나 동영상을 1개 이상 업로드 해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void makeJoinChattingRoom() {
        service.createJoinChattingRoom(CommonString.roomController, myModel.firebaseToken, commonRandomUUID,
                title, builder.toString(), clubIntroduce, userMainPhoto, userId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.e("abc", "" + response.raw());
                if (response.isSuccessful()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.transitionButton.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                @Override
                                public void onAnimationStopEnd() {
                                    Intent intent = new Intent();
                                    intent.setAction("createClub");
                                    intent.putExtra("createClub", "createClub");
                                    sendBroadcast(intent);
                                    finish();
                                }
                            });
                        }
                    });
                } else {

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("abc", "error : " + t.getMessage());
            }
        });
    }

}






