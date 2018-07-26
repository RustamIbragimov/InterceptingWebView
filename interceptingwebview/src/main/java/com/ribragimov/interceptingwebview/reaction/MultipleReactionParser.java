package com.ribragimov.interceptingwebview.reaction;

import com.ribragimov.interceptingwebview.exceptions.IllegalReactionLikeRateException;
import com.ribragimov.interceptingwebview.exceptions.ParseException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ribragimov on 6/5/18.
 */
public class MultipleReactionParser {

    private ReactionRateParser mReactionRateParser;

    private Set<String> mLikeIds;
    private Set<String> mDislikeIds;
    private Set<String> mSpamIds;

    public MultipleReactionParser(ReactionRateParser reactionRateParser) {
        this.mReactionRateParser = reactionRateParser;

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
    public void parseLikes(String url, String requestBody) throws ParseException, IllegalReactionLikeRateException {
        ReactionParsedData parsedData = ReactionParser.parse(url, requestBody);
        if (parsedData == null) return;

        String id = parsedData.getId();

        mLikeIds.remove(id);

        int rate = mReactionRateParser.getRate(id);

        if (parsedData.getType().equals(ReactionParsedData.TYPE_LIKE)) {
            if (rate != 5) {
                throw new IllegalReactionLikeRateException(rate);
            } else {
                mLikeIds.add(id);
            }
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

    @Override
    public String toString() {
        return "MultipleReactionParser{" +
                "mLikeIds=" + mLikeIds +
                ", mDislikeIds=" + mDislikeIds +
                ", mSpamIds=" + mSpamIds +
                '}';
    }
}
