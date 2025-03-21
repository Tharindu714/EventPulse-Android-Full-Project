package com.deltacodex.EventPulse.model;

import java.util.List;

public class ProfileModel {

    private String username;
    private String password;
    private String email;
    private String mobile;
    private String address;
    private String gender;
    private List<String> moviesPreferences;
    private List<String> tvShowsPreferences;
    private List<String> gamesPreferences;
    private boolean loveTrailers;
    private boolean loveTeasers;
    private boolean loveBloopers;

    // Constructors
    public ProfileModel() {}

    public ProfileModel(String username, String password, String email, String mobile, String address,
                        String gender, List<String> moviesPreferences, List<String> tvShowsPreferences,
                        List<String> gamesPreferences, boolean loveTrailers, boolean loveTeasers, boolean loveBloopers) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.gender = gender;
        this.moviesPreferences = moviesPreferences;
        this.tvShowsPreferences = tvShowsPreferences;
        this.gamesPreferences = gamesPreferences;
        this.loveTrailers = loveTrailers;
        this.loveTeasers = loveTeasers;
        this.loveBloopers = loveBloopers;
    }

    // Getters and setters for all fields
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public List<String> getMoviesPreferences() { return moviesPreferences; }
    public void setMoviesPreferences(List<String> moviesPreferences) { this.moviesPreferences = moviesPreferences; }

    public List<String> getTvShowsPreferences() { return tvShowsPreferences; }
    public void setTvShowsPreferences(List<String> tvShowsPreferences) { this.tvShowsPreferences = tvShowsPreferences; }

    public List<String> getGamesPreferences() { return gamesPreferences; }
    public void setGamesPreferences(List<String> gamesPreferences) { this.gamesPreferences = gamesPreferences; }

    public boolean isLoveTrailers() { return loveTrailers; }
    public void setLoveTrailers(boolean loveTrailers) { this.loveTrailers = loveTrailers; }

    public boolean isLoveTeasers() { return loveTeasers; }
    public void setLoveTeasers(boolean loveTeasers) { this.loveTeasers = loveTeasers; }

    public boolean isLoveBloopers() { return loveBloopers; }
    public void setLoveBloopers(boolean loveBloopers) { this.loveBloopers = loveBloopers; }
}
