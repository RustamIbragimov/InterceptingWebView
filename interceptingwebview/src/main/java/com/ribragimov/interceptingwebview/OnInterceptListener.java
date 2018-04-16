package com.ribragimov.interceptingwebview;

/**
 * Created by ribragimov on 4/16/18.
 */
public interface OnInterceptListener {

    void onInterceptRequest(String url, String requestBody, String responseBody);

    void onInterceptFailed();

}
