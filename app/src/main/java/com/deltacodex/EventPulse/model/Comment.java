package com.deltacodex.EventPulse.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Comment implements Serializable {
    private String userId;
    private String username;
    private String commentText;
    private Timestamp timestamp;

    public Comment() {}

    public Comment(String userId, String username, String commentText, Timestamp timestamp) {
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getCommentText() { return commentText; }
    public Timestamp getTimestamp() { return timestamp; }
}

