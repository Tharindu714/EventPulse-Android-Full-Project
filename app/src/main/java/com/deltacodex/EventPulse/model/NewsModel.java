package com.deltacodex.EventPulse.model;

import java.io.Serializable;

public class NewsModel implements Serializable {
    private String imageUrl;
    private String headline;
    private String description;

    private String formattedTimestamp;


    public NewsModel() {
    }

    public NewsModel(String imageUrl, String headline, String description) {
        this.imageUrl = imageUrl;
        this.headline = headline;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }

    public void setFormattedTimestamp(String formattedTimestamp) {
        this.formattedTimestamp = formattedTimestamp;
    }
}

