package com.ribragimov.interceptingwebview;

import android.support.annotation.NonNull;

import java.net.URLEncoder;

/**
 * Created by ribragimov on 5/4/18.
 */
public class UrlGenerator {

    @NonNull
    public static String getReviewUrl(String packageName) {
        return "https://play.google.com/store/ereview?docId=" + packageName;
    }

    @NonNull
    public static String getReactionUrl(String reviewLink) {
        return "https://accounts.google.com/signin/v2/sl/pwd?service=googleplay&passive=86400&continue="
                + URLEncoder.encode(reviewLink) +
                "&flowName=GlifWebSignIn&flowEntry=ServiceLogin";
    }


    public static String getMultipleReactionUrl(String packageName) {
        String url = "https://play.google.com/store/apps/details?id=" + packageName;
        return getReactionUrl(url);
    }

}
