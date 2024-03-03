package com.cross.juntalk2.first;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityCallBinding;
import com.cross.juntalk2.utils.CreateNewCompatActivity;
import com.cross.juntalk2.utils.JunApplication;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CallActivity extends CreateNewCompatActivity {

    private ActivityCallBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseRef;
    boolean isPeerConnected = false;
    boolean isAudio = true;
    boolean isVideo = true;
    private String friendId;
    private String myId;
    private String uniqueId;

    @Override
    public void getInterfaceInfo() {

    }

    @Override
    public void getIntentInfo() {
        friendId = getIntent().getStringExtra("friendId");
        myId = JunApplication.getMyModel().userId;
    }

    @Override
    public void init() {

        binding = DataBindingUtil.setContentView(CallActivity.this, R.layout.activity_call);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseRef = firebaseDatabase.getReference();
        setupWebView();

    }

    @Override
    public void createThings() {


    }

    @Override
    public void clickEvent() {
        binding.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("abc","clickBuuton");
                sendCallRequest();
            }
        });

        binding.toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAudio = !isAudio;
                callJavascriptFunction("javascript:toggleAudio('" + isAudio + "')");
                binding.toggleAudioBtn.setImageResource(isAudio ? R.drawable.ic_baseline_mic_24 : R.drawable.ic_baseline_mic_off_24);
            }
        });

        binding.toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideo = !isVideo;
                callJavascriptFunction("javascript:toggleVideo('" + isVideo + "')");
                binding.toggleVideoBtn.setImageResource(isVideo ? R.drawable.ic_baseline_videocam_24 : R.drawable.ic_baseline_videocam_off_24);
            }
        });
    }

    @Override
    public void getServer() {

    }


    private void callJavascriptFunction(String s) {
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    binding.webView.evaluateJavascript(s, null);
                }
            }
        });
    }


    private void sendCallRequest() {
        Log.d("sendCallRequest", "실행");
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String , Object> callMap = new HashMap<>();
        callMap.put("incoming",myId);
        firebaseRef.child("UserInfos").child(friendId).child("VideoCall").setValue(callMap);

        firebaseRef.child("UserInfos").child(friendId).child("VideoCall").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.print("snapshot : " + snapshot.getValue());
                if (String.valueOf(snapshot.getValue()).equals("true")) {
                    listenForConnId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error : " + error);
            }
        });

    }

    private void listenForConnId() {
        Log.e("abc", "listenForConnId 실행");
        firebaseRef.child(friendId).child("VideoCall").child("VideoCall").child("authenticNumber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    return;
                }
                switchToControls();
                callJavascriptFunction("javascript:startCall('" + String.valueOf(snapshot.getValue()) + "')");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.print("error : " + error);
            }
        });

    }





    private void setupWebView() {
        Log.d("setupWebView", "실행");
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }

            }
        });
        binding.webView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSettings.setMediaPlaybackRequiresUserGesture(false);
        }
        binding.webView.addJavascriptInterface(new JavascriptInterface(this), "Android");

        loadVideoCall();
    }


    void onPeerConnected() {

        isPeerConnected = true;
        Log.e("abc", "onPeerConnected 실행");
    }

    private void loadVideoCall() {
        Log.d("loadVideoCall", "실행");
        String filePath = "file:///android_asset/www/peer.html";
        binding.webView.loadUrl(filePath);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                initializePeer();
            }
        });
    }


    private String getUniqueID() {
        return UUID.randomUUID().toString();
    }


    private void initializePeer() {
        Log.d("initializePeer", "실행");
        uniqueId = getUniqueID();
        Log.e("abc","uniqueId : "+ uniqueId);
        callJavascriptFunction("javascript:init('" + uniqueId+ "')");


        firebaseRef.child(myId).child("VideoCall").child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataChange", String.valueOf(snapshot.getValue()));
                onCallRequest(String.valueOf(snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error : " + error);
            }
        });
    }

    private void onCallRequest(String caller) {
        Log.d("onCallRequest", "실행");
        Log.e("abc","caller : "+ caller);
        if (caller.equals("null")) {
            return;
        } else {

            binding.callLayout.setVisibility(View.VISIBLE);
            binding.incomingCallTxt.setText(caller + " is calling...");
            binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseRef.child(myId).child("VideoCall").child("authenticNumber").setValue(uniqueId);
                    firebaseRef.child(myId).child("VideoCall").child("status").setValue(true);
                    binding.callLayout.setVisibility(View.GONE);
                    switchToControls();
                }
            });

            binding.rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseRef.child(myId).child("VideoCall").child("incoming").setValue(null);
                    binding.callLayout.setVisibility(View.GONE);
                }
            });
        }

    }

    private void switchToControls() {
        Log.d("switchToControls", "실행");
        binding.inputLayout.setVisibility(View.GONE);
        binding.callControlLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        Log.d("onDestroy", "실행");
        firebaseRef.child(myId).setValue(null);
        binding.webView.loadUrl("about:blank");
        super.onDestroy();
    }

}




class JavascriptInterface {

    CallActivity callActivity;

    JavascriptInterface(CallActivity callActivity) {
        this.callActivity = callActivity;

    }

    @android.webkit.JavascriptInterface
    public void onPeerConnected() {
        callActivity.onPeerConnected();
        Log.e("abc","peerConnected");
    }

    @android.webkit.JavascriptInterface
    public void console(String s)
    {
        Log.e("abc"," S : "+s);

    }
}
