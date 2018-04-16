Web View Interceptor
============

## Usage

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
    ...
    maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency
```gradle
dependencies {
    compile 'com.github.RustamIbragimov:InterceptingWebView:{last-version}'
}
```

## XML

```xml
<com.ribragimov.interceptingwebview.InterceptingWebView
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

## Code

#### Add WebViewClient to your InterceptingWebView

```java
mWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // insert required methods here
            }
});
```

#### Add this method to start intercepting requests

```java
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
```

#### Add this methods to Review screen

```java
mWebView.reviewFitScreen(url);
mWebView.setOnReviewCloseClickedListener(url, new OnReviewCloseListener() {
    @Override
    public void onClose() {
        Log.i(TAG, "Review closed");
    }
});
```
