package com.deltacodex.EventPulse.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;


public class ForumPost implements Serializable {
    private String postId;
    private String postTitle;
    private String postContent;
    private String postCategory;
    private String postImageUrl;
    private String userId;
    private String username;

    private long likesCount;
    private long likes;
    private long comments;

    private Timestamp  timestamp;

    private String userEmail;

    public ForumPost() {
    }

    public ForumPost(String postId, String postTitle, String postContent, String postCategory,
                     String postImageUrl, String userId, String username, long likes, long comments, String userEmail) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postCategory = postCategory;
        this.postImageUrl = postImageUrl;
        this.userId = userId;
        this.username = username;
        this.likes = likes;
        this.comments = comments;
        this.userEmail = userEmail;

    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }
}

