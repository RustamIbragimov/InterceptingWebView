package com.ribragimov.interceptingwebview.reaction;

/**
 * Created by ribragimov on 5/2/18.
 */
public class ReactionParsedData {

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
                return "like";
            case 2:
                return "dislike";
            case 3:
                return "spam";
            default:
                return "";
        }
    }

    public void setType(int type) {
        this.type = type;
    }

}
