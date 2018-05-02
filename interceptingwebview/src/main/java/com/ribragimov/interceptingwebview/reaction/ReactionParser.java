package com.ribragimov.interceptingwebview.reaction;

import com.ribragimov.interceptingwebview.ParseException;

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
     * @param url url
     * @param requestBody request body
     * @return object which contains all data
     * @throws ParseException exception when parsing
     */
    public static ReactionParsedData parse(String url, String requestBody) throws ParseException {
        if (!url.contains("play.google.com/_/PlayStoreUi/mutate")) {
            return null;
        }

        try {
            Map<String, String> params = parseFormData(requestBody);
            String data = URLDecoder.decode(params.get("f.req"));

            JSONArray array = new JSONArray(data);
            JSONArray dataArray = array.getJSONArray(1).getJSONArray(0);
            int objectId = dataArray.getInt(1);
            JSONArray objectArray = dataArray.getJSONArray(2);
            JSONObject dataObject = objectArray.getJSONObject(0);
            JSONArray mainArray = dataObject.getJSONArray(String.valueOf(objectId));
            JSONArray innerArray = mainArray.getJSONArray(0);

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
            map.put(splitted[0], splitted[1]);
        }

        return map;
    }

}
