package com.ribragimov.interceptingwebview.runnables;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.ribragimov.interceptingwebview.webview.InterceptingWebView;

/**
 * Created by ribragimov on 4/17/18.
 */
public class ReviewInstallAppRequiredRunnable implements Runnable {


    private InterceptingWebView mWebView;
    private Handler mHandler;
    private int mTimeout;

    public ReviewInstallAppRequiredRunnable(InterceptingWebView webView, Handler handler, int timeout) {
        this.mWebView = webView;
        this.mHandler = handler;
        this.mTimeout = timeout;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        mWebView.evaluateJavascript("(function() {\n" +
                "    var elems = document.getElementsByClassName(\"id-unowned-app\");\n" +
                "    if (elems && elems.length >= 1) {\n" +
                "        if (InterceptInterface.hasOwnProperty(\"onReviewInstallAppRequired\")) {\n" +
                "            InterceptInterface.onReviewInstallAppRequired();\n" +
                "        }\n" +
                "    }\n" +
                "})()", null);
        mHandler.postDelayed(this, mTimeout);
    }
}
