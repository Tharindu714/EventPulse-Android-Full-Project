package com.deltacodex.EventPulse.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String Movie_thumbnailUrl;
    private String Movie_largeImageUrl;
    private String Movie_name;
    private String Movie_duration;
    private String Movie_description;
    private String Movie_imdb;
    private String Movie_rottenTomatoes;
    private String Movie_userLove;
    private String Movie_genre;
    private String Movie_distributed;
    private String Movie_creator;
    private String Movie_downloadLink;
    private String Movie_trailerLink;

    public Movie() {
    }

    public Movie(String movie_thumbnailUrl, String movie_largeImageUrl, String movie_name, String movie_duration, String movie_description, String movie_imdb, String movie_rottenTomatoes, String movie_userlove, String movie_genre, String movie_distributed, String movie_creator, String movie_downloadLink, String movie_trailerLink) {
        Movie_thumbnailUrl = movie_thumbnailUrl;
        Movie_largeImageUrl = movie_largeImageUrl;
        Movie_name = movie_name;
        Movie_duration = movie_duration;
        Movie_description = movie_description;
        Movie_imdb = movie_imdb;
        Movie_rottenTomatoes = movie_rottenTomatoes;
        Movie_userLove = movie_userlove;
        Movie_genre = movie_genre;
        Movie_distributed = movie_distributed;
        Movie_creator = movie_creator;
        Movie_downloadLink = movie_downloadLink;
        Movie_trailerLink = movie_trailerLink;
    }

    public String getMovie_thumbnailUrl() {
        return Movie_thumbnailUrl;
    }

    public void setMovie_thumbnailUrl(String movie_thumbnailUrl) {
        Movie_thumbnailUrl = movie_thumbnailUrl;
    }

    public String getMovie_largeImageUrl() {
        return Movie_largeImageUrl;
    }

    public void setMovie_largeImageUrl(String movie_largeImageUrl) {
        Movie_largeImageUrl = movie_largeImageUrl;
    }

    public String getMovie_name() {
        return Movie_name;
    }

    public void setMovie_name(String movie_name) {
        Movie_name = movie_name;
    }

    public String getMovie_description() {
        return Movie_description;
    }

    public void setMovie_description(String movie_description) {
        Movie_description = movie_description;
    }

    public String getMovie_imdb() {
        return Movie_imdb;
    }

    public void setMovie_imdb(String movie_imdb) {
        Movie_imdb = movie_imdb;
    }

    public String getMovie_rottenTomatoes() {
        return Movie_rottenTomatoes;
    }

    public void setMovie_rottenTomatoes(String movie_rottenTomatoes) {
        Movie_rottenTomatoes = movie_rottenTomatoes;
    }

    public String getMovie_genre() {
        return Movie_genre;
    }

    public void setMovie_genre(String movie_genre) {
        Movie_genre = movie_genre;
    }

    public String getMovie_distributed() {
        return Movie_distributed;
    }

    public void setMovie_distributed(String movie_distributed) {
        Movie_distributed = movie_distributed;
    }

    public String getMovie_creator() {
        return Movie_creator;
    }

    public void setMovie_creator(String movie_creator) {
        Movie_creator = movie_creator;
    }

    public String getMovie_downloadLink() {
        return Movie_downloadLink;
    }

    public void setMovie_downloadLink(String movie_downloadLink) {
        Movie_downloadLink = movie_downloadLink;
    }

    public String getMovie_trailerLink() {
        return Movie_trailerLink;
    }

    public void setMovie_trailerLink(String movie_trailerLink) {
        Movie_trailerLink = movie_trailerLink;
    }

    public String getMovie_duration() {
        return Movie_duration;
    }

    public void setMovie_duration(String movie_duration) {
        Movie_duration = movie_duration;
    }

    public String getMovie_userLove() {
        return Movie_userLove;
    }

    public void setMovie_userLove(String movie_userLove) {
        Movie_userLove = movie_userLove;
    }
}
