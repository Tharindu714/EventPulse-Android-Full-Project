package com.deltacodex.EventPulse;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.deltacodex.EventPulse.Adapters.CommentAdapter;
import com.deltacodex.EventPulse.model.Comment;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SingleForumPostActivity extends AppCompatActivity {
    private TextView postTitle, postContent, postCategory, postTimestamp, postLikes, postComments;
    private EditText commentInput;
    private RecyclerView commentsRecyclerView;

    private ImageButton likeButton;

    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String postId;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList;

    private boolean hasLiked = false;  // To track if the user has already liked the post

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_forum_post);
        StatusBarUtils.applyGradientStatusBar(this);

        // 🔹 Initialize Firebase
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        onLikeClick(isLoggedIn);

        // Initialize UI components
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postCategory = findViewById(R.id.postCategory);
        postTimestamp = findViewById(R.id.postTimestamp);
        postLikes = findViewById(R.id.postLikes);
        postComments = findViewById(R.id.postComments);
        commentInput = findViewById(R.id.commentInput);
        likeButton = findViewById(R.id.likeButton); // Initialize likeButton
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        // Set onClickListener for Like Button
        likeButton.setOnClickListener(v -> onLikeClick(true)); // Ensure this is set after the button is initialized
        ImageButton submitCommentBtn = findViewById(R.id.submitCommentBtn);
        submitCommentBtn.setOnClickListener(v -> postComment());
        // Get Post ID and load data
        postId = getIntent().getStringExtra("postId");
        loadPostData();
        loadComments();
    }

    private void loadPostData() {
        if (postId == null) {
            Log.e("SingleForumPostActivity", "Error: Post ID is null");
            Toast.makeText(this, "Post ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference postRef = db.collection("CommunityForum").document(postId);
        postRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                postTitle.setText(document.getString("postTitle"));
                postContent.setText(document.getString("postContent"));
                postCategory.setText(document.getString("postCategory"));

                // Convert timestamp
                Timestamp timestamp = document.getTimestamp("timestamp");
                if (timestamp != null) {
                    Date date = timestamp.toDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                    postTimestamp.setText(sdf.format(date));
                }

                postLikes.setText(document.getLong("likes") + " Likes");
                postComments.setText(document.getLong("comments") + " Comments");

                // Check if the user has already liked the post
                checkIfUserLikedPost();
            }
        }).addOnFailureListener(e -> {
            Log.e("SingleForumPostActivity", "Error loading post data: " + e.getMessage());
            Toast.makeText(this, "Error loading post data", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadComments() {
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        db.collection("CommunityForum").document(postId).collection("Comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e == null) {
                        commentList.clear();
                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            for (var doc : queryDocumentSnapshots) {
                                commentList.add(doc.toObject(Comment.class));
                            }
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void postComment() {
        String commentText = commentInput.getText().toString().trim();

        // Step 1: Ensure the comment is not empty
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 2: Get current user info from SharedPreferences
        String userName = sharedPreferences.getString("userName", "Event Pulse");
        String userEmail = sharedPreferences.getString("userEmail", "Never Miss a Premiere");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Toast.makeText(this, "You must be logged in to commment!", Toast.LENGTH_SHORT).show();
        }else {
            // Step 3: Prepare comment data to be saved in Firestore
            Map<String, Object> comment = new HashMap<>();
            comment.put("userId", userEmail);  // Use a unique identifier (like userEmail)
            comment.put("username", userName);  // Use the actual username
            comment.put("commentText", commentText);
            comment.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

            db.collection("CommunityForum").document(postId).collection("Comments")
                    .add(comment)
                    .addOnSuccessListener(documentReference -> {
                        // Step 5: After successfully adding the comment, update the comment count
                        DocumentReference postRef = db.collection("CommunityForum").document(postId);
                        postRef.get().addOnSuccessListener(postDoc -> {
                            if (postDoc.exists()) {
                                long currentComments = postDoc.getLong("comments") != null ? postDoc.getLong("comments") : 0;
                                long updatedComments = currentComments + 1;

                                // Step 6: Update the comment count in the post
                                postRef.update("comments", updatedComments)
                                        .addOnSuccessListener(aVoid -> {
                                            // Update the UI with the new comment count
                                            postComments.setText(updatedComments + " Comments");

                                            // Optionally, clear the comment input field after submitting
                                            commentInput.setText("");
                                            Toast.makeText(SingleForumPostActivity.this, "Comment posted!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SingleForumPostActivity", "Error updating comment count: " + e.getMessage());
                                            Toast.makeText(this, "Error updating comment count", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Step 7: Log and handle any failure during comment submission
                        Log.e("SingleForumPostActivity", "Error posting comment: " + e.getMessage());
                        Toast.makeText(this, "Error posting comment", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void checkIfUserLikedPost() {
        // Get the current user info from SharedPreferences
        String userEmail = sharedPreferences.getString("userEmail", "");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            Toast.makeText(this, "You must be logged in to commment!", Toast.LENGTH_SHORT).show();
        }else {
            db.collection("CommunityForum").document(postId).collection("likes")
                    .document(userEmail) // Use the user email as the document ID
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // User has already liked the post
                            hasLiked = true;
                            likeButton.setImageResource(R.drawable.ic_like_filled);  // Set filled like icon
                        } else {
                            // User has not liked the post
                            hasLiked = false;
                            likeButton.setImageResource(R.drawable.ic_like);  // Set unfilled like icon
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SingleForumPostActivity", "Error checking if user liked post: " + e.getMessage());
                        Toast.makeText(this, "Error checking like status", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void onLikeClick(boolean isLoggedIn) {
        if (!isLoggedIn) {
            Toast.makeText(this, "You must be logged in to like this post!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (postId == null || postId.isEmpty()) {
            Log.e("SingleForumPostActivity", "Error: Post ID is null or empty");
            Toast.makeText(this, "Error: Post ID is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasLiked) {
            Toast.makeText(this, "You have already liked this post.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference postRef = db.collection("CommunityForum").document(postId);

        postRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                long currentLikes = document.getLong("likes") != null ? document.getLong("likes") : 0;
                long updatedLikes = currentLikes + 1;

                String userEmail = sharedPreferences.getString("userEmail", "");
                if (userEmail.isEmpty()) {
                    Log.e("OnLieClick", "User Email Not Found!!");
                    return;
                }

                Map<String, Object> likeData = new HashMap<>();
                likeData.put("userId", userEmail);

                db.collection("CommunityForum").document(postId).collection("likes")
                        .document(userEmail)
                        .set(likeData)
                        .addOnSuccessListener(aVoid -> {
                            postRef.update("likes", updatedLikes)
                                    .addOnSuccessListener(aVoid1 -> {
                                        postLikes.setText(updatedLikes + " Likes");
                                        likeButton.setImageResource(R.drawable.ic_like_filled);
                                        hasLiked = true;
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SingleForumPostActivity", "Error updating likes count: " + e.getMessage());
                                        Toast.makeText(this, "Error updating like count", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e("SingleForumPostActivity", "Error adding like: " + e.getMessage());
                            Toast.makeText(this, "Error liking post", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Log.e("SingleForumPostActivity", "Post does not exist");
                Toast.makeText(this, "Post not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("SingleForumPostActivity", "Error retrieving post: " + e.getMessage());
            Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
        });
    }

}
