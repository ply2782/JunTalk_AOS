package com.cross.juntalk2.main;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.DialogAuthenticationBinding;
import com.cross.juntalk2.model.MyModel;
import com.cross.juntalk2.retrofit.RetrofitClient;
import com.cross.juntalk2.retrofit.RetrofitService;
import com.cross.juntalk2.utils.JunApplication;
import com.cross.juntalk2.utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Dialog_SelfAuthentication extends Dialog {
    private String number;
    private DialogAuthenticationBinding binding;
    private Context context;
    private RetrofitService service;
    private CompositeDisposable compositeDisposable;
    private LoadingDialog loadingDialog;
    private Handler handler;
    private MyModel myModel;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId, smsCode;
    private Activity activity;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private int certCharLength = 5;
    private String identifyNumber = "";
    private final char[] characterTable = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'J', 'L', 'M', 'N', 'O', 'P'
            , 'Q', 'R', 'S', 'T', 'U', 'V'
            , 'W', 'X', 'Y', 'Z', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', '0'};

    public Dialog_SelfAuthentication(@NonNull Context context, Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
        compositeDisposable = new CompositeDisposable();
        loadingDialog = new LoadingDialog(context);
        handler = new Handler(Looper.getMainLooper());
        myModel = JunApplication.getMyModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_authentication, null, false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(binding.getRoot());

        //todo : firebase 문자인증 초기
        mAuth = FirebaseAuth.getInstance();

        //TODO : 다이얼로그 넓이와 높이 동적 조정
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initViews();
        clickEvent();
    }


    //todo : firebase User 확인후 ui 업데이트
    private void updateUI(FirebaseUser user) {

    }


    //TODO : 초기 스피너 관련 정보 세팅
    public void initViews() {
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
        identifyNumber = executeGenerate();
        /**binding.recognizedNumberTextView.setText("인증번호 : " + identifyNumber);*/
        binding.recognizedNumberTextView.setText("인증번호를 발송했습니다. 잠시만 기다려주세요.. ");
    }

    //todo : [START verify_with_code]
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
    }

    //todo :  [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //todo : 파이어베이스에서 온 문자인증번호를 제대로 인증했는지 판단
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "인증 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            Intent intent = new Intent(context, JoinActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            dismiss();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.e("abc", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            binding.certifyNumberTextInputEditTextView.setText("");
                            Toast.makeText(context, "인증 실패하였습니다.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    @SuppressLint("WrongConstant")
    public void clickEvent() {

        //todo : firebase SMS 인증 Callback
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e("abc", "onVerificationCompleted:" + credential);
                loadingDialog.dismiss();
                Toast.makeText(context, "인증번호가 도착했습니다. 60초 이내로 입력해주세요.", Toast.LENGTH_SHORT).show();
                smsCode = credential.getSmsCode().toString();
                binding.certifyNumberTextInputEditTextView.setText("" + smsCode);
                signInWithPhoneAuthCredential(credential);


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.e("abc", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(context, "인증을 실패했습니다.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(context, "인증 서버에 문제가 발생했습니다.\n나중에 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                }


                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e("abc", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };


        //TODO : 스피너 옵션
        binding.firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == binding.firstSpinner.getCount()) {

                } else {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        binding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.firstSpinner.getSelectedItem() == null || binding.firstSpinner.getSelectedItem().equals("") ||
                        Objects.requireNonNull(binding.secondEditText.getText()).toString() == null || binding.secondEditText.getText().toString().equals("") ||
                        Objects.requireNonNull(binding.thirdEditText.getText()).toString() == null || binding.thirdEditText.getText().toString().equals("")) {
                    Snackbar.make(v, "전화번호를 입력해주세요", Snackbar.LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE).show();
                } else {

                    String myPhoneNumber = (String) binding.firstSpinner.getSelectedItem() + "-" + binding.secondEditText.getText().toString() + "-" + binding.thirdEditText.getText().toString();
                    String firstPhoneNumber = ((String) binding.firstSpinner.getSelectedItem()).substring(1);
                    String secondPhoneNumber = binding.secondEditText.getText().toString();
                    String thirdPhoneNumber = binding.thirdEditText.getText().toString();

                    String changeMyPhoneNumber = "+82" + firstPhoneNumber + secondPhoneNumber + thirdPhoneNumber;
                    myModel.userPhoneNumber = myPhoneNumber;
                    JunApplication.setMyModel(myModel);


                    //todo : 임시로 주석처
                    /**getRandomCheckNumber(myPhoneNumber);*/
                    String bindingButtonText = binding.okButton.getText().toString();
                    if (bindingButtonText.equals("인증번호 재요청")) {
                        resendVerificationCode(changeMyPhoneNumber, mResendToken);
                    } else {

                        Intent intent = new Intent(context, JoinActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        /*loadingDialog.show();
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(changeMyPhoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(activity)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);*/
                    }

                    /*binding.okButton.setText("인증번호 재요청");*/
                }

            }
        });

        binding.labelButton.setOnClickListener(v -> {
            //TODO : 사르륵 애니메이션
            Animation animation = new AlphaAnimation(0, 1);
            animation.setDuration(1000);

            if (binding.infoLinearLayout != null) {
                binding.infoLinearLayout.setVisibility(View.VISIBLE);
                binding.infoLinearLayout.setAnimation(animation);
            }

            if (number == null || number.equals("")) {
                Snackbar.make(v, "번호를 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE).show();

            } else {

                if (binding.certifyNumberTextInputEditTextView.getText().toString().equals("123")) {
                    Intent intent = new Intent(context, JoinActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    dismiss();
                } else {
                    Snackbar.make(v, "인증번호가 올바르지 않습니다. 다시한번 입력해주세요.", Snackbar.LENGTH_SHORT).setAnimationMode(ANIMATION_MODE_SLIDE).show();
                }
            }
        });
    }


    public String executeGenerate() {
        Random random = new Random(System.currentTimeMillis());
        int tableLength = characterTable.length;
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < certCharLength; i++) {
            buffer.append(characterTable[random.nextInt(tableLength)]);
        }
        return buffer.toString();
    }

    //TODO : 인증번호 요청 후 입력 판단
    public void getRandomCheckNumber(String phoneNumber) {
        loadingDialog.show();
        service = RetrofitClient.getInstance().getServerInterface();
        Observable<String> stringResult = service.getRandomCheckNumber("userController", phoneNumber);
        compositeDisposable.add(stringResult.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.from(JunApplication.getThreadPoolExecutor()))
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(@NotNull String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            number = jsonObject.getString("recognizedNumber");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e("abc", "getRandomCheckNumber error : " + e.getMessage());
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {

                        //TODO : 사르륵 애니메이션
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Animation animation = new AlphaAnimation(0, 1);
                                animation.setDuration(1000);
                                binding.certifyNumberLayout.setVisibility(View.VISIBLE);
                                binding.certifyNumberLayout.setAnimation(animation);
                            }
                        });
                        loadingDialog.dismiss();
                    }
                }));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //TODO : 레트로핏 서버 통신후 남아있는 스레드 종료
        compositeDisposable.clear();
        compositeDisposable.dispose();
        //TODO : 핸들러 종료

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
