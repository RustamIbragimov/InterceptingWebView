package com.ribragimov.interceptingwebview;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

class ReviewCloseRunnable implements Runnable {

    private InterceptingWebView mWebView;
    private Handler mHandler;
    private int mTimeout;

    ReviewCloseRunnable(InterceptingWebView webView, Handler handler, int timeout) {
        this.mWebView = webView;
        this.mHandler = handler;
        this.mTimeout = timeout;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        mWebView.evaluateJavascript("(function () {\n" +
                "    var elems = document.getElementsByClassName(\"review-action-button-container cancel-button\");\n" +
                "    if (elems && elems.length >= 2) { \n" +
                "        elems[1].onclick = function () {\n" +
                "            if (InterceptInterface.hasOwnPropery(\"onReviewClose\")) {\n" +
                "                InterceptInterface.onReviewClose();\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "})()", null);
        mHandler.postDelayed(this, mTimeout);
    }
}
