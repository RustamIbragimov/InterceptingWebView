package com.ribragimov.interceptingwebview_example;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ribragimov.interceptingwebview.exceptions.IllegalReactionLikeRateException;
import com.ribragimov.interceptingwebview.exceptions.ParseException;
import com.ribragimov.interceptingwebview.UrlGenerator;
import com.ribragimov.interceptingwebview.reaction.MultipleReactionParser;
import com.ribragimov.interceptingwebview.reaction.ReactionRateParser;
import com.ribragimov.interceptingwebview.webview.InterceptingWebView;
import com.ribragimov.interceptingwebview.webview.OnInterceptListener;
import com.ribragimov.interceptingwebview.webview.OnReviewCloseListener;
import com.ribragimov.interceptingwebview.webview.OnReviewInstallAppRequiredListener;

public class MainActivity extends AppCompatActivity {

    private InterceptingWebView mWebView;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.webview);
        initReactionMulti();
    }


    private void initReview() {
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



    private void initReactionMulti() {
        mWebView.loadUrl(UrlGenerator.getReactionUrl("https://play.google.com/store/apps/details?id=park.ramadan.app"));

        final ReactionRateParser rateParser = new ReactionRateParser();
        final MultipleReactionParser parser = new MultipleReactionParser(rateParser);

        mWebView.setReactionRateParser(rateParser);
        mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                mWebView.requestHtml();
                mWebView.setOnInterceptListener(new OnInterceptListener() {
                    @Override
                    public void onInterceptRequest(String url, String requestBody, String responseBody) {
                        try {
                            parser.parseLikes(url, requestBody);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (IllegalReactionLikeRateException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onInterceptFailed() {
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
