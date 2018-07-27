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

    private Set<String> mBadLikeIds;

    public MultipleReactionParser(ReactionRateParser reactionRateParser) {
        this.mReactionRateParser = reactionRateParser;

        mLikeIds = new HashSet<>();
        mDislikeIds = new HashSet<>();
        mSpamIds = new HashSet<>();
        mBadLikeIds = new HashSet<>();
    }

    /**
     * This method parses the request and updates reaction sets
     *
     * @param url         url
     * @param requestBody request body
     * @throws ParseException exception when parsing
     * @throws IllegalReactionLikeRateException exception if it is like and rate is not 5
     * @return whether this request is reaction
     */
    public boolean parseLikes(String url, String requestBody) throws ParseException, IllegalReactionLikeRateException {
        ReactionParsedData parsedData = ReactionParser.parse(url, requestBody);
        if (parsedData == null) return false;

        String id = parsedData.getId();

        mLikeIds.remove(id);

        int rate = mReactionRateParser.getRate(id);

        if (parsedData.getType().equals(ReactionParsedData.TYPE_LIKE)) {
            if (rate != 5) {
                mBadLikeIds.add(id);
                throw new IllegalReactionLikeRateException(rate);
            } else {
                mLikeIds.add(id);
            }
        } else if (parsedData.getType().equals(ReactionParsedData.TYPE_DISLIKE)
                || parsedData.getType().equals(ReactionParsedData.TYPE_SPAM)) {
            mBadLikeIds.remove(id);
        }

        return true;
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

    public Set<String> getBadLikeIds() {
        return mBadLikeIds;
    }


    public int getBadLikeCount() {
        return mBadLikeIds.size();
    }

    @Override
    public String toString() {
        return "MultipleReactionParser{" +
                "mReactionRateParser=" + mReactionRateParser +
                ", mLikeIds=" + mLikeIds +
                ", mDislikeIds=" + mDislikeIds +
                ", mSpamIds=" + mSpamIds +
                ", mBadLikeIds=" + mBadLikeIds +
                '}';
    }
}
