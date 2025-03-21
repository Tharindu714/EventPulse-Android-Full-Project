package com.deltacodex.EventPulse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
    private EditText postTitleInput, postContentInput;
    private Spinner postCategorySpinner;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        StatusBarUtils.applyGradientStatusBar(this);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Initialize UI components
        postTitleInput = findViewById(R.id.postTitleInput);
        postContentInput = findViewById(R.id.postContentInput);
        postCategorySpinner = findViewById(R.id.postCategorySpinner);
        ImageButton submitPostBtn = findViewById(R.id.submitPostBtn);

        // Set up the category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        postCategorySpinner.setAdapter(adapter);

        // Add TextWatchers for live validation
        postTitleInput.addTextChangedListener(postValidationWatcher);
        postContentInput.addTextChangedListener(postValidationWatcher);

        // Submit the post
        submitPostBtn.setOnClickListener(v -> uploadPost());
    }

    // TextWatcher for validating input fields
    private final TextWatcher postValidationWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            // You can add additional checks here if needed
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            // Re-enable the button if the fields are non-empty
            validateForm();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // Add any actions needed after text has changed
        }
    };

    // Method to validate form and enable/disable the submit button
    private void validateForm() {
        ImageButton submitPostBtn = findViewById(R.id.submitPostBtn);
        boolean isValid = !postTitleInput.getText().toString().trim().isEmpty() &&
                !postContentInput.getText().toString().trim().isEmpty() &&
                postCategorySpinner.getSelectedItem() != null;
        submitPostBtn.setEnabled(isValid);
    }

    private void uploadPost() {
        String title = postTitleInput.getText().toString().trim();
        String content = postContentInput.getText().toString().trim();

        // Validate category selection
        if (postCategorySpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        String category = postCategorySpinner.getSelectedItem().toString();

        // Retrieve user details from SharedPreferences
        String username = sharedPreferences.getString("userName", "Event Pulse");
        String userEmail = sharedPreferences.getString("userEmail", "Never Miss a Premiere");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Validation for empty title/content
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user is logged in
        if (!isLoggedIn) {
            Toast.makeText(this, "You must be logged in to post!", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create the post data map
        Map<String, Object> post = new HashMap<>();
        post.put("postTitle", title);
        post.put("postContent", content);
        post.put("postCategory", category);
        post.put("username", username);
        post.put("userEmail", userEmail);
        post.put("likes", 0);
        post.put("comments", 0); // Placeholder comments count
        post.put("timestamp", FieldValue.serverTimestamp());
        post.put("status", "approved");


        db.collection("CommunityForum").add(post)
                .addOnSuccessListener(documentReference -> {
                    // After successful post creation
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "Post Added Successfully!", Toast.LENGTH_SHORT).show();

                    // Send user to the SingleForumPostActivity with the post ID
                    Intent intent = new Intent(AddPostActivity.this, SingleForumPostActivity.class);
                    intent.putExtra("postId", documentReference.getId());
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


