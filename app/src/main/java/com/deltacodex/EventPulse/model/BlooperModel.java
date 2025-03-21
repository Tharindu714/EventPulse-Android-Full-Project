package com.deltacodex.EventPulse.model;

import com.google.firebase.Timestamp;

public class BlooperModel {
    private String videoUrl_blooper;
    private String videoId_blooper;
    private String thumbnailUrl_blooper;
    private String headline_blooper;
    private Timestamp timestamp_blooper;

    private boolean isPlaying = false;

    public BlooperModel() {}

    public BlooperModel(String videoUrl_blooper, String videoId_blooper, String thumbnailUrl_blooper, String headline_blooper, Timestamp timestamp_blooper) {
        this.videoUrl_blooper = videoUrl_blooper;
        this.videoId_blooper = videoId_blooper;
        this.thumbnailUrl_blooper = thumbnailUrl_blooper;
        this.headline_blooper = headline_blooper;
        this.timestamp_blooper = timestamp_blooper;
    }

    public String getVideoUrl_blooper() {
        return videoUrl_blooper;
    }

    public void setVideoUrl_blooper(String videoUrl_blooper) {
        this.videoUrl_blooper = videoUrl_blooper;
    }

    public String getVideoId_blooper() {
        return videoId_blooper;
    }

    public void setVideoId_blooper(String videoId_blooper) {
        this.videoId_blooper = videoId_blooper;
    }

    public String getThumbnailUrl_blooper() {
        return thumbnailUrl_blooper;
    }

    public void setThumbnailUrl_blooper(String thumbnailUrl_blooper) {
        this.thumbnailUrl_blooper = thumbnailUrl_blooper;
    }

    public String getHeadline_blooper() {
        return headline_blooper;
    }

    public void setHeadline_blooper(String headline_blooper) {
        this.headline_blooper = headline_blooper;
    }

    public Timestamp getTimestamp_blooper() {
        return timestamp_blooper;
    }

    public void setTimestamp_blooper(Timestamp timestamp_blooper) {
        this.timestamp_blooper = timestamp_blooper;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}