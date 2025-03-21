package com.deltacodex.EventPulse.model;

import java.io.Serializable;
import java.util.List;

public class TVShow implements Serializable {
    private String thumbnailUrl;
    private String name;
    private String imdb;  // Use Double instead of double
    private String rottenTomatoes;  // Use Double instead of double
    private String genre;
    private String creator;
    private String description;
    private String downloadLink;
    private String episodes;
    private String seasons;
    private String largeImageUrl;
    private String userLove;
    private String trailerLink;

    public TVShow() {} // Required for Firebase

    public TVShow(String thumbnailUrl, String name, String imdb, String rottenTomatoes, String genre, String creator, String description, String downloadLink, String episodes, String seasons, String largeImageUrl, String userLove, String trailerLink) {
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.imdb = imdb;
        this.rottenTomatoes = rottenTomatoes;
        this.genre = genre;
        this.creator = creator;
        this.description = description;
        this.downloadLink = downloadLink;
        this.episodes = episodes;
        this.seasons = seasons;
        this.largeImageUrl = largeImageUrl;
        this.userLove = userLove;
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

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getRottenTomatoes() {
        return rottenTomatoes;
    }

    public void setRottenTomatoes(String rottenTomatoes) {
        this.rottenTomatoes = rottenTomatoes;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public String getEpisodes() {
        return episodes;
    }

    public void setEpisodes(String episodes) {
        this.episodes = episodes;
    }

    public String getSeasons() {
        return seasons;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getUserLove() {
        return userLove;
    }

    public void setUserLove(String userLove) {
        this.userLove = userLove;
    }

    public String getTrailerLink() {
        return trailerLink;
    }

    public void setTrailerLink(String trailerLink) {
        this.trailerLink = trailerLink;
    }
    // Getters and Setters


}
