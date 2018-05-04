package com.ribragimov.interceptingwebview.review;

/**
 * Created by ribragimov on 5/2/18.
 */
public class ReviewParsedData {

    private String id;
    private String imageUrl;
    private String name;
    private String rating;
    private String text;
    private String link;

    public ReviewParsedData(String id, String imageUrl, String name, String rating, String text, String link) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.rating = rating;
        this.text = text;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isFiveStars() {
        return rating != null && rating.equals("5");
    }
}
