package com.ribragimov.interceptingwebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@linkplain WebView webview} that intercepts requests and able to do some magic
 */
public class InterceptingWebView extends WebView {

    private OnInterceptListener mOnInterceptListener;
    private OnReviewCloseListener mOnReviewCloseListener;

    private AtomicBoolean mIsStartedIntercepting;
    private AtomicBoolean mIsFailedIntercepting;
    private Handler mHandler;

    private final String EREVIEW_LINK = "https://play.google.com/store/ereview?docId=";

    private Runnable mReviewFitScreenRunnable;
    private Runnable mReviewCloseRunnable;

    private final int REPEAT_TIMEOUT = 2_000;

    public InterceptingWebView(Context context) {
        super(context);
        initView();
    }

    public InterceptingWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InterceptingWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public InterceptingWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initView();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initView() {
        addJavascriptInterface(new InterceptInterface(), "InterceptInterface");
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());

        mIsStartedIntercepting = new AtomicBoolean(false);
        mIsFailedIntercepting = new AtomicBoolean(false);
        mHandler = new Handler();

        mReviewFitScreenRunnable = new ReviewFitScreenRunnable(this, mHandler, REPEAT_TIMEOUT);
        mReviewCloseRunnable = new ReviewCloseRunnable(this, mHandler, REPEAT_TIMEOUT);
    }


    /**
     * This method should be executed in onDestroy method in Fragments and Activities.
     * Otherwise, there will be memory leak.
     */
    public void onDestroy() {
        mHandler.removeCallbacks(mReviewFitScreenRunnable);
        mHandler.removeCallbacks(mReviewCloseRunnable);
    }


    /**
     * This method intercepts all requests from web page.
     * It should be executed in onPageFinished of WebViewClient. It can be executed multiple times.
     *
     * @param onInterceptListener listener for intercepts
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setOnInterceptListener(OnInterceptListener onInterceptListener) {
        this.mOnInterceptListener = onInterceptListener;

        if (mIsStartedIntercepting.get()) {
            return;
        }

        mIsStartedIntercepting.set(true);
        evaluateJavascript("(function (send) {\n" +
                "    XMLHttpRequest.prototype.send = function (body) {\n" +
                "        var oldready = this.onreadystatechange;\n" +
                "        this.onreadystatechange = function () {\n" +
                "            if (this.readyState == 4 && this.status == 200) {\n" +
                "                if (InterceptInterface.hasOwnProperty(\"onInterceptRequest\")) {\n" +
                "                    InterceptInterface.onInterceptRequest(this.responseURL, body, this.responseText);\n" +
                "                }\n" +
                "            }\n" +
                "            return oldready.apply(this);\n" +
                "        }\n" +
                "\n" +
                "        send.call(this, body);\n" +
                "    }\n" +
                "})(XMLHttpRequest.prototype.send);", null);
    }

    /**
     * This method should be executed to make Google eReview full screen
     * @param url url of a current page
     */
    public void reviewFitScreen(String url) {
        if (url == null) {
            throw new NullPointerException("url cannot be null");
        }

        mHandler.removeCallbacks(mReviewFitScreenRunnable);
        if (!url.contains(EREVIEW_LINK)) {
            return;
        }
        mHandler.postDelayed(mReviewFitScreenRunnable, REPEAT_TIMEOUT);
    }


    /**
     * This method sets callback when user clicks {close} button in Google eReview
     *
     * @param url url of a current page
     * @param onReviewCloseClickedListener callback to be fired
     */
    public void setOnReviewCloseClickedListener(String url, OnReviewCloseListener onReviewCloseClickedListener) {
        if (url == null) {
            throw new NullPointerException("url cannot be null");
        }

        this.mOnReviewCloseListener = onReviewCloseClickedListener;

        mHandler.removeCallbacks(mReviewCloseRunnable);
        if (!url.contains(EREVIEW_LINK)) {
            return;
        }
        mHandler.postDelayed(mReviewCloseRunnable, REPEAT_TIMEOUT);
    }


    class InterceptInterface {
        @JavascriptInterface
        public void onInterceptRequest(String url, String request, String response) {
            if (mIsFailedIntercepting.get()) return;

            if (mOnInterceptListener != null) {
                if (url.equalsIgnoreCase("undefined")) {
                    mIsFailedIntercepting.set(true);
                    mOnInterceptListener.onInterceptFailed();
                } else {
                    mOnInterceptListener.onInterceptRequest(url, request, response);
                }
            }
        }


        @JavascriptInterface
        public void onReviewClose() {
            if (mOnReviewCloseListener != null) {
                mOnReviewCloseListener.onClose();
            }
        }
    }
}












