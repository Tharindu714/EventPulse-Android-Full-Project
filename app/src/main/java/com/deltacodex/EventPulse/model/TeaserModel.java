package com.deltacodex.EventPulse.model;

import com.google.firebase.Timestamp;

public class TeaserModel {
    private String headline_Teasers;
    private String thumbnailUrl_Teasers;
    private Timestamp timestamp_Teasers;
    private String videoId_Teasers;
    private String videoUrl_Teasers;

    private boolean isPlaying = false;

    public TeaserModel() {
    }


    public TeaserModel(String headline_Teasers, String thumbnailUrl_Teasers, Timestamp timestamp_Teasers, String videoId_Teasers, String videoUrl_Teasers) {
        this.headline_Teasers = headline_Teasers;
        this.thumbnailUrl_Teasers = thumbnailUrl_Teasers;
        this.timestamp_Teasers = timestamp_Teasers;
        this.videoId_Teasers = videoId_Teasers;
        this.videoUrl_Teasers = videoUrl_Teasers;
    }

    public String getHeadline_Teasers() {
        return headline_Teasers;
    }

    public void setHeadline_Teasers(String headline_Teasers) {
        this.headline_Teasers = headline_Teasers;
    }

    public String getThumbnailUrl_Teasers() {
        return thumbnailUrl_Teasers;
    }

    public void setThumbnailUrl_Teasers(String thumbnailUrl_Teasers) {
        this.thumbnailUrl_Teasers = thumbnailUrl_Teasers;
    }

    public Timestamp getTimestamp_Teasers() {
        return timestamp_Teasers;
    }

    public void setTimestamp_Teasers(Timestamp timestamp_Teasers) {
        this.timestamp_Teasers = timestamp_Teasers;
    }

    public String getVideoId_Teasers() {
        return videoId_Teasers;
    }

    public void setVideoId_Teasers(String videoId_Teasers) {
        this.videoId_Teasers = videoId_Teasers;
    }

    public String getVideoUrl_Teasers() {
        return videoUrl_Teasers;
    }

    public void setVideoUrl_Teasers(String videoUrl_Teasers) {
        this.videoUrl_Teasers = videoUrl_Teasers;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
