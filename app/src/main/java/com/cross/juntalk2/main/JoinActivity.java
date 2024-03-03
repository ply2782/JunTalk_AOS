package com.cross.juntalk2.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityJoinBinding;
import com.cross.juntalk2.model.CommonNoticeModel;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
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
import java.util.Calendar;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JoinActivity extends CreateNewCompatActivity {
    private CompositeDisposable compositeDisposable;
    private RetrofitService service;
    private ActivityJoinBinding binding;
    private String[] firstPhoneNumber;
    private String selected_gender;
    private boolean checkOverLapId = false;
    private LoadingDialog loadingDialog;
    private MyModel myModel;
    private Handler handler;
    private Animation upAnimation;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean isExistOfPicture = false;
    private MultipartBody.Part userMainPhoto;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
    private String imageFileName;
    private Map<String, String> checkIdMap;
    private TelephonyManager tm;
    private String android_id;

    @Override
    public void getInterfaceInfo() {
        upAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_up);
        checkIdMap = new HashMap<>();
    }

    @Override
    public void getIntentInfo() {
        myModel = JunApplication.getMyModel();
        if (service == null) {
            service = RetrofitClient.getInstance().getServerInterface();
        }

    }

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
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                                activityResultLauncher.launch(intent);
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
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            activityResultLauncher.launch(intent);
        }
    }

    @Override
    public void init() {

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        handler = new Handler(Looper.getMainLooper());
        binding = DataBindingUtil.setContentView(JoinActivity.this, R.layout.activity_join);
        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        //TODO : 스피너
        firstPhoneNumber = context.getResources().getStringArray(R.array.entries);
        ArrayList<String> spinnerItem = new ArrayList<>();
        spinnerItem.add("010");
        spinnerItem.add("016");
        spinnerItem.add("011");
        spinnerItem.add("017");
        spinnerItem.add("031");
        spinnerItem.add("070");
        spinnerItem.add("선택");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerItem) {
            @Override
            public View getDropDownView(int position, @Nullable @org.jetbrains.annotations.Nullable View convertView, @NonNull @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (position == (getCount())) {
                    ((TextView) view.findViewById(android.R.id.text1)).setText("");
                    ((TextView) view.findViewById(android.R.id.text1)).setHint("선택");
                    ((TextView) view.findViewById(android.R.id.text1)).setHintTextColor(Color.parseColor("#707070"));
                }
                return view;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        binding.firstSpinner.setAdapter(adapter);
        binding.firstSpinner.setSelection(binding.firstSpinner.getCount());

        //TODO : 시작할때 위로 올라오는 애니메이션
        binding.scrollView.setAnimation(upAnimation);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void createThings() {
        settingInfo();
    }


    //TODO : 미리 정보 세팅해놓기
    public void settingInfo() {
        try {
            String[] getPhoneNumber = myModel.userPhoneNumber.split("-");
            binding.firstSpinner.setEnabled(false);
            switch (getPhoneNumber[0].replace(" ", "").trim()) {
                case "010":
                    binding.firstSpinner.setSelection(0);
                    break;
                case "011":
                    binding.firstSpinner.setSelection(2);
                    break;
                case "016":
                    binding.firstSpinner.setSelection(1);
                    break;
                case "017":
                    binding.firstSpinner.setSelection(3);
                    break;
                case "031":
                    binding.firstSpinner.setSelection(4);
                    break;
                case "070":
                    binding.firstSpinner.setSelection(5);
                    break;
                default:
                    break;

            }
            binding.secondEditText.setText(getPhoneNumber[1]);
            binding.secondEditText.setEnabled(false);
            binding.thirdEditText.setText(getPhoneNumber[2]);
            binding.thirdEditText.setEnabled(false);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO : 레트로핏 서버 통신후 남아있는 스레드 종료
        compositeDisposable.clear();
        compositeDisposable.dispose();
        //TODO : 핸들러 종료
        if (handler != null) {
            handler.removeMessages(0);
        }
        if (loadingDialog != null &&
                loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
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
    public void clickEvent() {
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
                        Log.e("abc", "data is null");
                    } else {
                        //TODO : 이미지 선택이 하나일 경우
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
                            isExistOfPicture = true;

                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String imagePath = getRealPathFromURI(uri);
                        getImageFile(imagePath);
                        try {
                            Bitmap bmRotated = rotateBitmap(uri, bitmap);
                            Bitmap bmp_Copy = bmRotated.copy(Bitmap.Config.ARGB_8888, true);
                            if (!isDestroyed() && !isFinishing()) {
                                glide().load(bmp_Copy)
                                        .optionalCenterCrop()
                                        .thumbnail(0.3f)
                                        .into(binding.profileImageView);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        binding.insertPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        //TODO : 2번쨰 전화번호
        binding.secondEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.secondEditText.setText(s.toString().substring(0, s.length() - 1));
                    binding.secondEditText.setSelection(s.length() - 1);
                    binding.secondEditText.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.secondEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //TODO : 3번쨰 전화번호
        binding.thirdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.thirdEditText.setText(s.toString().substring(0, s.length() - 1));
                    binding.thirdEditText.setSelection(s.length() - 1);
                    binding.thirdEditText.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.thirdEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //TODO : 비밀번호 공백 체크
        binding.passwordTextInputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.passwordTextInputEditTextView.setText(s.toString().substring(0, s.length() - 1));
                    binding.passwordTextInputEditTextView.setSelection(s.length() - 1);
                    binding.passwordTextInputLayout.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.passwordTextInputLayout.setError(null);
                }

                if (s.length() == 0) {
                    binding.firstLevel.setVisibility(View.INVISIBLE);
                    binding.secondLevel.setVisibility(View.INVISIBLE);
                    binding.thirdLevel.setVisibility(View.INVISIBLE);
                } else if (0 < s.length() && s.length() < 5) {
                    binding.firstLevel.setVisibility(View.VISIBLE);
                    binding.secondLevel.setVisibility(View.INVISIBLE);
                    binding.thirdLevel.setVisibility(View.INVISIBLE);
                } else if (s.length() < 10) {
                    binding.firstLevel.setVisibility(View.VISIBLE);
                    binding.secondLevel.setVisibility(View.VISIBLE);
                    binding.thirdLevel.setVisibility(View.INVISIBLE);
                } else if (10 < s.length()) {
                    binding.firstLevel.setVisibility(View.VISIBLE);
                    binding.secondLevel.setVisibility(View.VISIBLE);
                    binding.thirdLevel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //TODO : 교번 공백 체크
        /*binding.identificationTextInputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.identificationTextInputEditTextView.setText(s.toString().substring(0, s.length() - 1));
                    binding.identificationTextInputEditTextView.setSelection(s.length() - 1);
                    binding.identificationTextInputLayout.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.identificationTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        //TODO : 아아디 공백 체크
        binding.idTextInputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.idTextInputEditTextView.setText(s.toString().substring(0, s.length() - 1));
                    binding.idTextInputEditTextView.setSelection(s.length() - 1);
                    binding.idTextInputLayout.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.idTextInputLayout.setError(null);
                }

                if (checkIdMap.get(s.toString()) != null && checkIdMap.get(s.toString()).equals("true")) {
                    binding.checkImageButton.setVisibility(View.GONE);
                    binding.checkLottieAnimation.setVisibility(View.VISIBLE);
                    binding.checkLottieAnimation.playAnimation();
                    checkOverLapId = true;
                } else {
                    binding.checkImageButton.setVisibility(View.VISIBLE);
                    binding.checkLottieAnimation.setVisibility(View.INVISIBLE);
                    checkOverLapId = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //TODO : 이름 공백 체크
        binding.nameTextInputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    s.toString().replaceAll(" ", "").trim();
                    binding.nameTextInputEditTextView.setText(s.toString().substring(0, s.length() - 1));
                    binding.nameTextInputEditTextView.setSelection(s.length() - 1);
                    binding.nameTextInputLayout.setError("공백은 사용하지 못합니다.");
                } else {
                    binding.nameTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //TODO : 성별 라디오 버튼 선택
        binding.genderChoiceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.manRadioButton:
                        selected_gender = "M";
                        break;
                    case R.id.womanRadioButton:
                        selected_gender = "F";
                        break;
                }
            }
        });


        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                binding.birthDayTextView.setVisibility(View.VISIBLE);
                binding.birthDayTextView.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
            }
        };
        binding.lottieAnmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(JoinActivity.this, listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });

        binding.closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    checkNull();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.checkImageButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (binding.idTextInputEditTextView.getText().toString().replace(" ", "").trim().equals("")) {
                    Snackbar.make(v, "아이디를 입력해주세요", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                } else {

                    if (checkOverLapId == false) {
                        checkOverLapId(binding.idTextInputEditTextView.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public void getServer() {

    }

    public void checkOverLapId(String userId) {
        Observable<Boolean> booleanResult = service.overLapId("userController", userId);
        compositeDisposable.add(booleanResult.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        Log.e("abc", "overLapId result : " + aBoolean);
                        if (aBoolean) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    binding.checkLottieAnimation.setVisibility(View.VISIBLE);
                                    binding.checkImageButton.setVisibility(View.GONE);
                                    binding.checkLottieAnimation.playAnimation();
                                    checkIdMap.put(userId, "true");
                                }
                            });
                            checkOverLapId = true;
                        } else {
                            handler.post(new Runnable() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void run() {
                                    binding.idTextInputEditTextView.getText().clear();
                                    Snackbar.make(getCurrentFocus(), "중복되는 아이디입니다.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                                }
                            });
                            checkOverLapId = false;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "overLapId error : " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    @SuppressLint({"ShowToast", "WrongConstant"})
    public void checkNull() throws ParseException {

        if (checkOverLapId == false) {
            binding.checkImageButton.requestFocus();
            Snackbar.make(binding.idTextInputEditTextView, "중복체크를 해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

        } else if (binding.passwordTextInputEditTextView.getText().toString() == null || binding.passwordTextInputEditTextView.getText().toString().replace(" ", "").trim().equals("")) {
            binding.passwordTextInputEditTextView.requestFocus();
            Snackbar.make(binding.passwordTextInputEditTextView, "비밀번호를 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

        } else if (binding.nameTextInputEditTextView.getText().toString() == null || binding.nameTextInputEditTextView.getText().toString().replace(" ", "").trim().equals("")) {
            binding.nameTextInputEditTextView.requestFocus();
            Snackbar.make(binding.nameTextInputEditTextView, "성함을 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

        } else if (selected_gender == null || selected_gender.equals("")) {
            binding.genderChoiceRadioGroup.requestFocus();
            Snackbar.make(binding.genderChoiceRadioGroup, "성별을 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

        } else if (binding.birthDayTextView.getText().toString() == null || binding.birthDayTextView.getText().toString().replace(" ", "").trim().equals("")) {
            binding.birthDayTextView.requestFocus();
            Snackbar.make(binding.birthDayTextView, "생년월일을 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();

        } else if (binding.firstSpinner.getSelectedItem() == null || ((String) binding.firstSpinner.getSelectedItem()).equals("")) {
            binding.firstSpinner.requestFocus();
            Toast.makeText(context, "첫번쨰 전화번호를 입력해주세요..", Toast.LENGTH_SHORT).show();

        } else if (binding.secondEditText.getText().toString() == null || binding.secondEditText.getText().toString().equals("")) {
            binding.secondEditText.requestFocus();
            Toast.makeText(context, "두번쨰 전화번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();

        } else if (binding.thirdEditText.getText().toString() == null || binding.thirdEditText.getText().toString().equals("")) {
            binding.thirdEditText.requestFocus();
            Toast.makeText(context, "세번쨰 전화번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();

        } else if (userMainPhoto == null) {
            Snackbar.make(binding.insertPictureButton, "사진을 넣어주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
            binding.insertPictureButton.requestFocus();

        }
        /*else if (binding.identificationTextInputEditTextView.getText().toString() == null || binding.identificationTextInputEditTextView.getText().toString().equals("")) {
            binding.identificationTextInputEditTextView.requestFocus();
            Toast.makeText(context, "교번을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        else {
            myModel.userName = binding.nameTextInputEditTextView.getText().toString();
            myModel.userId = binding.idTextInputEditTextView.getText().toString();
            myModel.userIdentity = "101";
            /*binding.identificationTextInputEditTextView.getText().toString();*/
            myModel.userBirthDay = binding.birthDayTextView.getText().toString();
            if (selected_gender.equals("M")) {
                myModel.userGender = MyModel.Gender.M;
            } else {
                if (selected_gender == null |
                        selected_gender.replaceAll(" ", "").trim().equals("")) {
                    myModel.userGender = MyModel.Gender.N;
                } else {
                    myModel.userGender = MyModel.Gender.F;
                }
            }
            myModel.userPassword = binding.passwordTextInputEditTextView.getText().toString();
            myModel.userPhoneNumber = String.valueOf(binding.firstSpinner.getSelectedItem()) + "-" + binding.secondEditText.getText() + "-" + binding.thirdEditText.getText();

            api_SaveMainPhoto(userMainPhoto);
        }
    }

    public void api_SaveMainPhoto(MultipartBody.Part userMainPhoto) {
        service.saveMainUserPhoto(CommonString.userController, userMainPhoto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "사진 저장 완료되었습니다. \n잠시만 기다려주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                            api_Join(myModel);
                        }
                    });
                }else{

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    public void api_Join(MyModel myModel) {
        loadingDialog.show();
        myModel.networkCountryIso = tm.getNetworkCountryIso();
        myModel.simCountryIso = tm.getSimCountryIso();
        myModel.networkOperator = tm.getNetworkOperator();
        myModel.simOperator = tm.getSimOperator();
        myModel.networkOperatorName = tm.getNetworkOperatorName();
        myModel.simOperatorName = tm.getSimOperatorName();
        myModel.simState = String.valueOf(tm.getSimState());
        myModel.android_id = android_id;
        myModel.userMainPhoto = imageFileName;
        myModel.device = "android";
        Observable<Boolean> booleanResult = service.join(CommonString.userController, myModel);
        compositeDisposable.add(booleanResult.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NotNull Boolean aBoolean) {
                        if (aBoolean) {
                            loadingDialog.dismiss();
                            finish();
                        } else {
                            handler.post(new Runnable() {
                                @SuppressLint("WrongConstant")
                                @Override
                                public void run() {
                                    Snackbar.make(findViewById(R.id.content), "서버와 통신 오류가 발생하였습니다.", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "error : " + e.getMessage());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }));

    }


    //TODO : 이미지 회전
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
//                matrix.setRotate(90);
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
            if (bmRotated != bitmap) {
                bitmap.recycle();
            }
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    //TODO : 이미지 파일 서버로 전송
    public void getImageFile(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        imageFileName = UUID.randomUUID().toString() + "_JunTalk.jpg";
        userMainPhoto = MultipartBody.Part.createFormData("userMainPhoto", imageFileName, requestFile);

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


}