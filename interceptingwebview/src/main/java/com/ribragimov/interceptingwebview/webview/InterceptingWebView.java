package com.ribragimov.interceptingwebview.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.ribragimov.interceptingwebview.reaction.ReactionRateParser;
import com.ribragimov.interceptingwebview.runnables.ReviewCloseRunnable;
import com.ribragimov.interceptingwebview.runnables.ReviewFitScreenRunnable;
import com.ribragimov.interceptingwebview.runnables.ReviewInstallAppRequiredRunnable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@linkplain WebView webview} that intercepts requests and able to do some magic
 */
public class InterceptingWebView extends WebView {

    private OnInterceptListener mOnInterceptListener;
    private OnReviewCloseListener mOnReviewCloseListener;
    private OnReviewInstallAppRequiredListener mOnReviewInstallAppRequiredListener;

    private ReactionRateParser mReactionRateParser;

    private AtomicBoolean mIsFailedIntercepting;
    private AtomicBoolean mIsInstallAppRequired;
    private Handler mHandler;

    private final String EREVIEW_LINK = "https://play.google.com/store/ereview?docId=";

    private Runnable mReviewFitScreenRunnable;
    private Runnable mReviewCloseRunnable;
    private Runnable mReviewInstallAppRequiredRunnable;

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

        mIsFailedIntercepting = new AtomicBoolean(false);
        mIsInstallAppRequired = new AtomicBoolean(false);
        mHandler = new Handler(Looper.getMainLooper());

        mReviewFitScreenRunnable = new ReviewFitScreenRunnable(this, mHandler, REPEAT_TIMEOUT);
        mReviewCloseRunnable = new ReviewCloseRunnable(this, mHandler, REPEAT_TIMEOUT);
        mReviewInstallAppRequiredRunnable = new ReviewInstallAppRequiredRunnable(this, mHandler, REPEAT_TIMEOUT);
    }


    /**
     * This method should be executed in onDestroy method in Fragments and Activities.
     * Otherwise, there will be memory leak.
     */
    public void onDestroy() {
        mHandler.removeCallbacks(mReviewFitScreenRunnable);
        mHandler.removeCallbacks(mReviewCloseRunnable);
        mHandler.removeCallbacks(mReviewInstallAppRequiredRunnable);
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
        evaluateJavascript("(function (send) {\n" +
                "    if (XMLHttpRequest.prototype.isAdded) return;\n" +
                "\n" +
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
                "        send.call(this, body);\n" +
                "    }\n" +
                "\n" +
                "    XMLHttpRequest.prototype.isAdded = true;\n" +
                "})(XMLHttpRequest.prototype.send);", null);
    }

    /**
     * This method should be executed to make Google eReview full screen
     *
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
     * @param url                          url of a current page
     * @param onReviewCloseClickedListener callback to be fired
     */
    public void setOnReviewCloseClickedListener(String url,
                                                OnReviewCloseListener onReviewCloseClickedListener) {
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


    /**
     * This methods sets a callback when install app before proceeding is showed
     *
     * @param url                                url of a current page
     * @param onReviewInstallAppRequiredListener callback to be fired
     */
    public void setOnReviewInstallAppRequiredListener(String url,
                                                      OnReviewInstallAppRequiredListener onReviewInstallAppRequiredListener) {
        if (url == null) {
            throw new NullPointerException("url cannot be null");
        }

        this.mOnReviewInstallAppRequiredListener = onReviewInstallAppRequiredListener;

        mHandler.removeCallbacks(mReviewInstallAppRequiredRunnable);
        if (!url.contains(EREVIEW_LINK)) {
            return;
        }
        mHandler.postDelayed(mReviewInstallAppRequiredRunnable, REPEAT_TIMEOUT);
    }


    /**
     * This method clears cookies
     */
    public void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(getContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    /**
     * This method clears cache, history and cookies
     */
    public void clearAll() {
        clearCache(true);
        clearHistory();
        clearCookies();
    }

    /**
     * This method sets a reaction rate parser to this webview
     *
     * @param reactionRateParser rate parser for reaction
     */
    public void setReactionRateParser(ReactionRateParser reactionRateParser) {
        this.mReactionRateParser = reactionRateParser;
    }


    /**
     * This method requests html content from webview
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void requestHtml() {
        evaluateJavascript("InterceptInterface.onHtmlReady" +
                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');", null);
    }


    class InterceptInterface {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @JavascriptInterface
        public void onInterceptRequest(final String url, final String request, final String response) {
            if (mIsFailedIntercepting.get()) return;

            if (mOnInterceptListener != null) {
                requestHtml();
                if (url.equalsIgnoreCase("undefined")) {
                    mIsFailedIntercepting.set(true);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnInterceptListener.onInterceptFailed();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnInterceptListener.onInterceptRequest(url, request, response);
                        }
                    });
                }
            }
        }


        @JavascriptInterface
        public void onReviewClose() {
            if (mOnReviewCloseListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnReviewCloseListener.onClose();
                    }
                });
            }
        }

        @JavascriptInterface
        public void onReviewInstallAppRequired() {
            if (mOnReviewInstallAppRequiredListener != null) {
                if (!mIsInstallAppRequired.get()) {
                    mIsInstallAppRequired.set(true);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mOnReviewInstallAppRequiredListener.onReviewInstallAppRequired();
                        }
                    });
                }
            }
        }

        @JavascriptInterface
        public void onHtmlReady(String html) {
            if (mReactionRateParser != null) {
                mReactionRateParser.setHtml(html);
            }
        }
    }
}












