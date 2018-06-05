package com.ribragimov.interceptingwebview.reaction;

import com.ribragimov.interceptingwebview.ParseException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ribragimov on 6/5/18.
 */
public class MultipleReactionParser {

    private Set<String> mLikeIds;
    private Set<String> mDislikeIds;
    private Set<String> mSpamIds;

    public MultipleReactionParser() {
        mLikeIds = new HashSet<>();
        mDislikeIds = new HashSet<>();
        mSpamIds = new HashSet<>();
    }

    /**
     * This method parses the request and updates reaction sets
     *
     * @param url url
     * @param requestBody request body
     * @throws ParseException exception when parsing
     */
    public void parse(String url, String requestBody) throws ParseException {
        ReactionParsedData parsedData = ReactionParser.parse(url, requestBody);
        if (parsedData == null) return;

        String id = parsedData.getId();

        mLikeIds.remove(id);
        mDislikeIds.remove(id);
        mSpamIds.remove(id);

        switch (parsedData.getType()) {
            case ReactionParsedData.TYPE_LIKE:
                mLikeIds.add(id);
                break;
            case ReactionParsedData.TYPE_DISLIKE:
                mDislikeIds.add(id);
                break;
            case ReactionParsedData.TYPE_SPAM:
                mSpamIds.add(id);
                break;
        }
    }


    public Set<String> getLikeIds() {
        return mLikeIds;
    }

    public int getLikeCount() {
        return mLikeIds.size();
    }

    public Set<String> getDislikeIds() {
        return mDislikeIds;
    }

    public int getDislikeCount() {
        return mDislikeIds.size();
    }

    public Set<String> getSpamIds() {
        return mSpamIds;
    }

    public int getSpamCount() {
        return mSpamIds.size();
    }
}
