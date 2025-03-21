package com.deltacodex.EventPulse.model;

public class PosterModel {
    private String description_poster;
    private String headline_poster;
    private String imageUrl_poster;

    public PosterModel() {
    }

    public PosterModel(String description_poster, String headline_poster, String imageUrl_poster) {
        this.description_poster = description_poster;
        this.headline_poster = headline_poster;
        this.imageUrl_poster = imageUrl_poster;
    }

    public String getDescription_poster() {
        return description_poster;
    }

    public void setDescription_poster(String description_poster) {
        this.description_poster = description_poster;
    }

    public String getHeadline_poster() {
        return headline_poster;
    }

    public void setHeadline_poster(String headline_poster) {
        this.headline_poster = headline_poster;
    }

    public String getImageUrl_poster() {
        return imageUrl_poster;
    }

    public void setImageUrl_poster(String imageUrl_poster) {
        this.imageUrl_poster = imageUrl_poster;
    }
}
