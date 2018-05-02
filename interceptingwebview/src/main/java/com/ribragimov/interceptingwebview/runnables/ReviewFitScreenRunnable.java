package com.ribragimov.interceptingwebview.runnables;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.ribragimov.interceptingwebview.webview.InterceptingWebView;

public class ReviewFitScreenRunnable implements Runnable {

    private InterceptingWebView mWebView;
    private Handler mHandler;
    private int mTimeout;

    public ReviewFitScreenRunnable(InterceptingWebView webView, Handler handler, int timeout) {
        this.mWebView = webView;
        this.mHandler = handler;
        this.mTimeout = timeout;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        mWebView.evaluateJavascript("(function () {\n" +
                "    var elems = document.getElementsByClassName(\"websky-modal-dialog review-widget-dialog\");\n" +
                "    if (elems && elems.length >= 1) {\n" +
                "        elems[0].style.cssText = \"width: 100%; height: 100%;\";\n" +
                "    } \n" +
                "})()", null);
        mHandler.postDelayed(this, mTimeout);
    }
}
