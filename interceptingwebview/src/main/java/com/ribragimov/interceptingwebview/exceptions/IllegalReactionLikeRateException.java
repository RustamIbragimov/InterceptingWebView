package com.ribragimov.interceptingwebview.exceptions;

/**
 * Created by ribragimov on 7/26/18.
 */
public class IllegalReactionLikeRateException extends Exception{

    private int rate;

    public IllegalReactionLikeRateException(int rate) {
        super("Like rate should be 5, got " + rate);
        this.rate = rate;
    }


    public int getRate() {
        return rate;
    }
}
