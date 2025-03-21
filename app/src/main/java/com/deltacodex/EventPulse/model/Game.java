package com.deltacodex.EventPulse.model;

import java.io.Serializable;

public class Game implements Serializable {
    private String thumbnailUrl;
    private String name;
    private String Developer;
    private String genre;
    private String Platforms;
    private String Released_Date;
    private String description;
    private String downloadLink;
    private String largeImageUrl;
    private String trailerLink;

    public Game() {}

    public Game(String thumbnailUrl, String name, String developer, String genre, String platforms, String released_Date, String description, String downloadLink, String largeImageUrl, String trailerLink) {
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        Developer = developer;
        this.genre = genre;
        Platforms = platforms;
        Released_Date = released_Date;
        this.description = description;
        this.downloadLink = downloadLink;
        this.largeImageUrl = largeImageUrl;
        this.trailerLink = trailerLink;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloper() {
        return Developer;
    }

    public void setDeveloper(String developer) {
        Developer = developer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPlatforms() {
        return Platforms;
    }

    public void setPlatforms(String platforms) {
        Platforms = platforms;
    }

    public String getReleased_Date() {
        return Released_Date;
    }

    public void setReleased_Date(String released_Date) {
        Released_Date = released_Date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }
}
