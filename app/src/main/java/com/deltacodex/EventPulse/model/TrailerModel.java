package com.deltacodex.EventPulse.model;

public class TrailerModel {

    private String description_trailer;
    private String headline_trailer;
    private String releaseDate_trailer;
    private String thumbnailUrl_trailer;
    private String videoId_trailer;
    private String videoUrl_trailer;

    public TrailerModel() {
    }

    public TrailerModel(String description_trailer, String headline_trailer, String releaseDate_trailer, String thumbnailUrl_trailer, String videoId_trailer, String videoUrl_trailer) {
        this.description_trailer = description_trailer;
        this.headline_trailer = headline_trailer;
        this.releaseDate_trailer = releaseDate_trailer;
        this.thumbnailUrl_trailer = thumbnailUrl_trailer;
        this.videoId_trailer = videoId_trailer;
        this.videoUrl_trailer = videoUrl_trailer;
    }

    public String getDescription_trailer() {
        return description_trailer;
    }

    public void setDescription_trailer(String description_trailer) {
        this.description_trailer = description_trailer;
    }

    public String getHeadline_trailer() {
        return headline_trailer;
    }

    public void setHeadline_trailer(String headline_trailer) {
        this.headline_trailer = headline_trailer;
    }

    public String getReleaseDate_trailer() {
        return releaseDate_trailer;
    }

    public void setReleaseDate_trailer(String releaseDate_trailer) {
        this.releaseDate_trailer = releaseDate_trailer;
    }

    public String getThumbnailUrl_trailer() {
        return thumbnailUrl_trailer;
    }

    public void setThumbnailUrl_trailer(String thumbnailUrl_trailer) {
        this.thumbnailUrl_trailer = thumbnailUrl_trailer;
    }

    public String getVideoId_trailer() {
        return videoId_trailer;
    }

    public void setVideoId_trailer(String videoId_trailer) {
        this.videoId_trailer = videoId_trailer;
    }

    public String getVideoUrl_trailer() {
        return videoUrl_trailer;
    }

    public void setVideoUrl_trailer(String videoUrl_trailer) {
        this.videoUrl_trailer = videoUrl_trailer;
    }
}
