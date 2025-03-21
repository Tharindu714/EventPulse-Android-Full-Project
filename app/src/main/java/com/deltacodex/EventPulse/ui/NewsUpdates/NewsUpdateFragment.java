package com.deltacodex.EventPulse.ui.NewsUpdates;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewsUpdateFragment extends Fragment {

    private ImageView newsImageView;
    private EditText headlineEditText, descriptionEditText, imageUrlEditText;  // Added imageUrlEditText

    private String imageUrl;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_update, container, false);

        // Initialize UI Components
        newsImageView = view.findViewById(R.id.newsImageView);
        headlineEditText = view.findViewById(R.id.headlineEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        imageUrlEditText = view.findViewById(R.id.imageUrlEditText);  // Get the URL EditText
        Button uploadNewsButton = view.findViewById(R.id.uploadNewsButton);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(requireContext());  // Use requireContext() for Fragments
        progressDialog.setMessage("Uploading...");

        // Upload News
        uploadNewsButton.setOnClickListener(v -> uploadNews());

        // Listen for image URL change and update preview
        imageUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String url = editable.toString();
                if (!url.isEmpty()) {
                    Picasso.get().load(url).into(newsImageView);  // Load image from URL using Picasso
                }
            }
        });

        return view;
    }

    private void uploadNews() {
        String headline = headlineEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        imageUrl = imageUrlEditText.getText().toString().trim();  // Get URL from EditText

        if (headline.isEmpty() || description.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields and provide an image URL!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String uniqueId = UUID.randomUUID().toString();
        // Create news data object with image URL
        Map<String, Object> newsData = new HashMap<>();
        newsData.put("newsId", uniqueId);
        newsData.put("headline", headline);
        newsData.put("description", description);
        newsData.put("imageUrl", imageUrl);  // Use provided image URL
        newsData.put("timestamp", FieldValue.serverTimestamp());

        // Insert into Firestore
        db.collection("news_updates")
                .add(newsData)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    headlineEditText.setText("");
                    descriptionEditText.setText("");
                    imageUrlEditText.setText("");
                    newsImageView.setImageResource(R.drawable.premiere_wave_studio_inc);
                    Toast.makeText(getContext(), "News Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    sendNotification(headline, imageUrl);  // Send notification after successful upload
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error uploading news!", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotification(String headline, String imageUrl) {
        Context context = getActivity();
        if (context == null) return; // Prevent null errors

        String CHANNEL_ID = "upload_success_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Upload Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Load Image with Glide
        new Thread(() -> {
            try {
                Bitmap largeIcon = Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get(); // Synchronously fetch image

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_rounded)
                        .setContentTitle("Event Pulse News Alert!")
                        .setContentText(headline)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                        .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                        .setLargeIcon(largeIcon)
                        .setStyle(
                                new NotificationCompat.BigPictureStyle()
                                        .bigPicture(largeIcon)
                                        .setSummaryText(headline)
                        )
                        .build();


                notificationManager.notify(1, notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
