package com.ribragimov.interceptingwebview.review;

import android.support.annotation.Nullable;

import com.ribragimov.interceptingwebview.ParseException;

import org.json.JSONArray;

/**
 * Created by ribragimov on 5/2/18.
 */
public class ReviewParser {

    /**
     * This method parses review request. Ideally, it should be executed on background thread.
     *
     * @param packageName task package name
     * @param url url
     * @param responseBody response body
     * @return object which contains all data
     * @throws ParseException exception when parsing
     */
    @Nullable
    public static ReviewParsedData parse(String packageName, String url, String responseBody) throws ParseException {
        if (!url.contains("submitreview")) {
            return null;
        }

        try {
            int trashLastIndex = responseBody.indexOf("'");
            String trimmedResponse = responseBody.substring(trashLastIndex + 1).trim();


            JSONArray array = new JSONArray(trimmedResponse);
            JSONArray mainArray = array.getJSONArray(0);
            JSONArray dataArray = mainArray.getJSONArray(3);


            String id = dataArray.getString(0);
            int rating = dataArray.getInt(2);
            String text = dataArray.getString(4);
            String link = "https://play.google.com/store/apps/details?id=" + packageName + "&reviewId=" + id;

            JSONArray userArray = dataArray.getJSONArray(1);
            String name = userArray.getString(0);
            String imageUrl = userArray.getJSONArray(1).getJSONArray(3).getString(2);


            return new ReviewParsedData(id, imageUrl, name, String.valueOf(rating), text, link);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }

    /**
     * This method checks whether the given text contains required words
     * @param text text to check
     * @param requiredPhrase required phrase
     * @return whether the given text contains required words
     */
    public static boolean textContainsRequiredPhrase(String text, String requiredPhrase) {
        requiredPhrase = requiredPhrase.toLowerCase();
        text = text.toLowerCase();

        int startIndex = text.indexOf(requiredPhrase);
        if (startIndex == -1) {
            return false;
        }

        int lastIndex = startIndex + requiredPhrase.length() - 1;

        // s != 0
        // s - 1 is char
        if (startIndex != 0 && Character.isLetter(text.charAt(startIndex - 1))) {
            return false;
        }

        // l = length
        // l + 1 is not char
        return lastIndex == text.length() - 1 || !Character.isLetter(text.charAt(lastIndex + 1));
    }
}
