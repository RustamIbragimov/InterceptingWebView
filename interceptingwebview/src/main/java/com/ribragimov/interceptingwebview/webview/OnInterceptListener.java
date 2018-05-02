package com.ribragimov.interceptingwebview.webview;

/**
 * This interface is a callback on intercept action
 */
public interface OnInterceptListener {

    /**
     * This method is fired when new request comes
     *
     * @param url url of the request
     * @param requestBody request body
     * @param responseBody response body
     */
    void onInterceptRequest(String url, String requestBody, String responseBody);

    /**
     * This method is fired if interception failed due to some reasons
     */
    void onInterceptFailed();

}
