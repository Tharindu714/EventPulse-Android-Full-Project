package com.deltacodex.EventPulse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class GalleryActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST = 101;
    private String posterUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        StatusBarUtils.applyGradientStatusBar(this);
        // Initialize UI components
        ImageView fullPosterImage = findViewById(R.id.fullPosterImage);
        TextView posterHeadline = findViewById(R.id.posterHeadline);
        TextView posterDescription = findViewById(R.id.posterDescription);

        // Retrieve data from intent
        Intent intent = getIntent();
        posterUrl = intent.getStringExtra("imageUrl_poster");
        String headline = intent.getStringExtra("headline_poster");
        String description = intent.getStringExtra("description_poster");

        // Load Poster Image
        Glide.with(this).load(posterUrl).into(fullPosterImage);

        // Set Text Data
        posterHeadline.setText(headline);
        posterDescription.setText(description);

        // Handle Back Button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // ✅ Long Press to Download Image
        fullPosterImage.setOnLongClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // No need to request permission for Android 10+ (Scoped Storage)
                downloadImage();
            } else {
                // Request Storage Permission for Android 9 and below
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                } else {
                    downloadImage();
                }
            }
            return true;
        });
    }

    private void downloadImage() {
        if (posterUrl == null || posterUrl.isEmpty()) {
            Toast.makeText(this, "No image to download", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(posterUrl));
        request.setTitle("Downloading Poster...");
        request.setDescription("Saving image to your device");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "EventPulse/" + System.currentTimeMillis() + ".jpg");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Image Downloaded to device Successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Download Manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ Handle Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
