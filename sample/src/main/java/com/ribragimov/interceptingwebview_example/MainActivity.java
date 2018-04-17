package com.ribragimov.interceptingwebview_example;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ribragimov.interceptingwebview.InterceptingWebView;
import com.ribragimov.interceptingwebview.OnInterceptListener;
import com.ribragimov.interceptingwebview.OnReviewCloseListener;
import com.ribragimov.interceptingwebview.OnReviewInstallAppRequiredListener;

public class MainActivity extends AppCompatActivity {

    private InterceptingWebView mWebView;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        initWebView();
    }


    private void initWebView() {
        mWebView.loadUrl("https://play.google.com/store/ereview?docId=editor.video.motion.fast.slow");

        mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                mWebView.setOnInterceptListener(new OnInterceptListener() {
                    @Override
                    public void onInterceptRequest(String url, String requestBody, String responseBody) {
                        Log.i(TAG, "url: " + url + ", request: "
                                + requestBody + ", response: " + responseBody);
                    }

                    @Override
                    public void onInterceptFailed() {
                        Log.e(TAG, "intercept failed");
                    }
                });

                mWebView.reviewFitScreen(url);
                mWebView.setOnReviewCloseClickedListener(url, new OnReviewCloseListener() {
                    @Override
                    public void onClose() {
                        Log.i(TAG, "Review closed");
                    }
                });
                mWebView.setOnReviewInstallAppRequiredListener(url, new OnReviewInstallAppRequiredListener() {
                    @Override
                    public void onReviewInstallAppRequired() {
                        Log.i(TAG, "Review install app required");
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }
}
