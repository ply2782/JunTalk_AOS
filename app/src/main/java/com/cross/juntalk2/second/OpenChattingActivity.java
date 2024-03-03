package com.cross.juntalk2.second;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityOpenChattingBinding;
import com.cross.juntalk2.diffutil.RoomDiffUtil;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.model.RoomModel;
import com.cross.juntalk2.model.RoomViewModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.retrofit.ViewModelFactory;
import com.cross.juntalk2.utils.CommonString;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpenChattingActivity extends CreateNewCompatActivity {

    private ActivityOpenChattingBinding binding;
    private ActivityResultLauncher<Intent> fileResultLauncher;
    private List<MultipartBody.Part> videoFileList;
    private List<MultipartBody.Part> imageFileList;
    private List<Bitmap> bitmapList;
    private DialogCreateOpenChatting openChatting;
    private RetrofitService service;
    private ViewModelFactory viewModelFactory;
    private RoomViewModel roomViewModel;
    private OpenChattingListAdapter adapter;

    private LoadingDialog loadingDialog;
    private int page = 0;
    private List<RoomModel> roomModelList;
    private BroadcastReceiver broadcastReceiver;
    private MyModel myModel;
    private ResumeThread resumeThread;

    public interface Refresh {
        void refresh();
    }

    Refresh refreshInterface;


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void getInterfaceInfo() {
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }
        if (context instanceof Refresh) {
            refreshInterface = (Refresh) context;
        }
        refreshInterface = new Refresh() {
            @Override
            public void refresh() {
                myModel = JunApplication.getMyModel();
                roomModelList.clear();
                roomViewModel.clearAllItem();
                page = 0;
                pageingServer(page);
            }
        };

    }

    @Override
    public void getIntentInfo() {
        roomModelList = new ArrayList<>();
        bitmapList = new ArrayList<>();
        videoFileList = new ArrayList<>();
        imageFileList = new ArrayList<>();
        fileResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                List<Uri> uriList = new ArrayList<>();
                bitmapList.clear();
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
                            String imagePath = getRealPathFromURI(uri);

                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {

                                try {
                                    Bitmap videoThumbNail = createThumbnail(context, imagePath);
                                    Bitmap bmRotated = rotateBitmap(uri, videoThumbNail);
                                    bitmapList.add(bmRotated);

                                    if (openChatting != null) {
                                        openChatting.setBitmapImage(bmRotated);
                                    }
                                    getVideoFile(imagePath, -1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Bitmap bmRotated = rotateBitmap(uri, bitmap);
                                    bitmapList.add(bmRotated);
                                    if (openChatting != null) {
                                        openChatting.setBitmapImage(bmRotated);
                                    }
                                    getImageFile(imagePath, -1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }//data == null or
                }
            }
        });
    }

    @Override
    public void init() {
        myModel = JunApplication.getMyModel();
        binding = DataBindingUtil.setContentView(OpenChattingActivity.this, R.layout.activity_open_chatting);
        viewModelFactory = new ViewModelFactory();
        loadingDialog = new LoadingDialog(context);
        roomViewModel = new ViewModelProvider(this, viewModelFactory).get(RoomViewModel.class);
        roomViewModel.init();

        adapter = new OpenChattingListAdapter(new RoomDiffUtil(), context);
        binding.chattingListRecyclerView.setAdapter(adapter);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        binding.chattingListRecyclerView.setAnimation(alphaAnimation);
        binding.chattingListRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        pageingServer(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("OpenChatting");
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
        }
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    resumeThread = new ResumeThread();
                    if (!resumeThread.isInterrupted()) {
                        resumeThread.interrupt();
                    }
                    resumeThread.start();

                }
            };
        }

        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    private class ResumeThread extends Thread {
        @Override
        public void run() {
            super.run();
            myModel = JunApplication.getMyModel();
            roomModelList.clear();
//            roomViewModel.clearAllItem();
            page = 0;
            pageingServer(page);
        }
    }

    @Override
    public void createThings() {

    }

    //TODO : 페이징 처리 서버
    public void pageingServer(int page) {
        service.loadOpenChatting(CommonString.roomController, page, myModel.userId).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                Log.e("abc", "response : " + response.toString());
                if (response.isSuccessful()) {
                    List<Map<String, Object>> mapList = response.body();
                    if (mapList.size() > 0) {

                        Function<Map<String, Object>, Observable<List<RoomModel>>> function = new Function<Map<String, Object>, Observable<List<RoomModel>>>() {
                            @Override
                            public Observable<List<RoomModel>> apply(@NotNull Map<String, Object> map) throws Exception {
                                RoomModel roomModel = new RoomModel();
                                roomModel.room_Index = Integer.parseInt(String.valueOf(map.get("room_Index")).replace(".0", ""));
                                roomModel.joinRoomContent = (String) map.get("joinRoomContent");
                                roomModel.roomType = RoomModel.RoomType.valueOf((String) map.get("roomType"));
                                roomModel.room_Conversation = (String) map.get("room_Conversation");
                                roomModel.room_joinCount = Integer.parseInt(String.valueOf(map.get("room_JoinCount")).replace(".0", ""));
                                roomModel.room_RegDate = (String) map.get("room_RegDate");
                                roomModel.room_Title = (String) map.get("room_Title");
                                roomModel.mainRoomPhoto = (String) map.get("mainRoomPhoto");
                                if (map.get("unreadCount") != null) {
                                    roomModel.unreadCount = Integer.parseInt(String.valueOf((double) map.get("unreadCount")).replace(".0", ""));
                                } else {
                                    roomModel.unreadCount = 0;
                                }
                                if (map.get("alarm") != null) {
                                    if (((String) map.get("alarm")).equals("1")) {
                                        roomModel.alarm = true;
                                    } else {
                                        roomModel.alarm = false;
                                    }
                                }
                                roomModel.roomHashTag = (String) map.get("roomHashTag");
                                roomModel.room_Uuid = (String) map.get("room_Uuid");
                                roomModel.room_joinCount = Integer.parseInt(String.valueOf(map.get("room_JoinCount")).replace(".0", ""));
                                roomModel.conversationTime = (String) map.get("conversationTime");
                                roomModelList.add(roomModel);
                                return Observable.fromArray(roomModelList);
                            }
                        };

                        Observable.fromIterable(mapList)
                                .flatMap(function)
                                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                                .subscribe(items -> roomViewModel.setItems(roomModelList)/*binding.setRoomModel(items)*/);

                    } else {
                        roomViewModel.setItems(roomModelList);
                    }

                } else {
                    Log.e("abc", "pageingServer fail");
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("abc", "pageingServer error : " + t.getMessage());
                loadingDialog.dismiss();
            }
        });
    }

    //todo : 내림차순 정렬
    class TimeComparator implements Comparator<RoomModel> {
        private SimpleDateFormat stringToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public int compare(RoomModel o1, RoomModel o2) {
            try {
                if ((o1.conversationTime != null || !o1.conversationTime.equals("null")) &&
                        (o2.conversationTime != null || o2.conversationTime.equals("null"))) {
                    if (stringToDate.parse(o1.conversationTime).getTime() > stringToDate.parse(o2.conversationTime).getTime()) {
                        return -1;
                    } else if (stringToDate.parse(o1.conversationTime).getTime() < stringToDate.parse(o2.conversationTime).getTime()) {
                        return 1;
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;

        }
    }

    @Override
    public void clickEvent() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                /*roomModelList = new ArrayList<>();*/
                roomModelList.clear();
                page = 0;
                pageingServer(page);
                if (binding.swipeRefreshLayout.isRefreshing()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        binding.wholeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if ((roomModelList.size() % 10) != 0) {

                    } else {
                        page++;
                        pageingServer(page);

                    }
                }
            }
        });

        /*binding.createOpenChattingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatting = new DialogCreateOpenChatting(context, fileResultLauncher);
                openChatting.setRefreshInterface(refreshInterface);
                openChatting.show();
            }
        });*/
    }

    @Override
    public void getServer() {
        Observer<List<RoomModel>> listObserver = new Observer<List<RoomModel>>() {
            @Override
            public void onChanged(List<RoomModel> roomModels) {
                /*adapter.notifyItemRangeInserted(adapter.getItemCount(), 10);*/
                adapter.submitList(roomModels);
                adapter.notifyDataSetChanged();
            }
        };
        roomViewModel.roomModelMutableLiveData().observe(this, listObserver);

    }

    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath, int position) {
        imageFileList.clear();
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String imageFileName = "";
        if (position == -1) {
            imageFileName = UUID.randomUUID().toString() + "_JunTalk.jpg";
        } else {
            imageFileName = UUID.randomUUID().toString() + "_" + position + "_JunTalk.jpg";
        }
        imageFileList.add(MultipartBody.Part.createFormData("imageFiles", imageFileName, requestFile));
        if (openChatting != null) {
            openChatting.setImageFileList(imageFileList);
        }
    }

    //TODO : 동영상 파일 서버로 전송
    public void getVideoFile(String filePath, int position) {
        videoFileList.clear();
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        String videoFileName = "";
        if (position == -1) {
            videoFileName = UUID.randomUUID().toString() + "_JunTalk.mp4";
        } else {
            videoFileName = UUID.randomUUID().toString() + "_" + position + "_JunTalk.mp4";
        }

        videoFileList.add(MultipartBody.Part.createFormData("videoFiles", videoFileName, requestFile));
        if (openChatting != null) {
            openChatting.setVideoFileList(videoFileList);
        }
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
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
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
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO : uri 로부터 사진 절대위치 구하기
    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

}