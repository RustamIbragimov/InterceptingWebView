package com.ribragimov.interceptingwebview.reaction;

import android.support.annotation.Nullable;

import com.ribragimov.interceptingwebview.exceptions.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ribragimov on 5/2/18.
 */
public class ReactionParser {

    /**
     * This method parses reaction request. Ideally, it should be executed on background thread.
     *
     * @param url         url
     * @param requestBody request body
     * @return object which contains all data
     * @throws ParseException exception when parsing
     */
    @Nullable
    public static ReactionParsedData parse(String url, String requestBody) throws ParseException {
        if (!url.contains("play.google.com/_/PlayStoreUi/data/batchexecute")) {
            return null;
        }

        if (!requestBody.contains("gp%3A")) {
            return null;
        }

        try {
            Map<String, String> params = parseFormData(requestBody);
            String data = URLDecoder.decode(params.get("f.req"));

            String innerString = new JSONArray(data).getJSONArray(0).getJSONArray(0).getString(1);
            JSONArray innerArray = new JSONArray(innerString).getJSONArray(0);

            String id = innerArray.getString(2);
            int reaction = innerArray.getInt(3);

            return new ReactionParsedData(id, reaction);
        } catch (Exception e) {
            throw new ParseException(e);
        }
    }


    private static Map<String, String> parseFormData(String str) {
        Map<String, String> map = new HashMap<>();

        String[] params = str.split("&");
        for (String param : params) {
            String[] splitted = param.split("=");
            if (splitted.length < 2) continue;

            map.put(splitted[0], splitted[1]);
        }

        return map;
    }

}
