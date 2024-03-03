package com.cross.juntalk2.first;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.cross.juntalk2.R;
import com.cross.juntalk2.databinding.ActivityPolicyBinding;
import com.cross.juntalk2.utils.CreateNewCompatActivity;

import java.net.URISyntaxException;

public class PolicyActivity extends CreateNewCompatActivity {

    private ActivityPolicyBinding binding;


    @Override
    public void getIntentInfo() {
        binding = DataBindingUtil.setContentView(PolicyActivity.this, R.layout.activity_policy);


    }

    @Override
    public void getInterfaceInfo() {
        String policyUrl = getIntent().getStringExtra("policyUrl");
        settingWebview(binding.policyWebView);
        if (policyUrl != null) {
            loadUrl(policyUrl);
        } else {
            Toast.makeText(this, "현재 존재하는 URL이 아닙니다.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void init() {

    }

    @Override
    public void createThings() {

    }

    @Override
    public void clickEvent() {

    }

    @Override
    public void getServer() {

    }

    public void settingWebview(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClientClass());
        webView.getSettings().setUserAgentString("app");
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url != null && url.startsWith("intent://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void loadUrl(String url) {
        binding.policyWebView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && binding.policyWebView.canGoBack()) {
            binding.policyWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void textFormColor(View v, String text, int left, int right) {
        if (text == null) return;
        if (left < 0 || right < 0) return;
        if (text.length() < left + right) return;
        if (v.getClass() == AppCompatTextView.class || v.getClass() == TextView.class) {
            SpannableString s = new SpannableString(text);
            s.setSpan(new ForegroundColorSpan(Color.RED), left, right, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) v).setText(s);

        }
    }
}