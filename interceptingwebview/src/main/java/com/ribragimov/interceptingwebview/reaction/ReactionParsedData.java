package com.ribragimov.interceptingwebview.reaction;

/**
 * Created by ribragimov on 5/2/18.
 */
public class ReactionParsedData {

    public final static String TYPE_LIKE = "like";
    public final static String TYPE_DISLIKE = "dislike";
    public final static String TYPE_SPAM = "spam";


    private String id;
    private int type;

    public ReactionParsedData(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        switch (type) {
            case 1:
                return TYPE_LIKE;
            case 2:
                return TYPE_DISLIKE;
            case 3:
                return TYPE_SPAM;
            default:
                return "";
        }
    }

    public void setType(int type) {
        this.type = type;
    }

}
