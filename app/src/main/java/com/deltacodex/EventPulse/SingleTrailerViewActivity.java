package com.deltacodex.EventPulse;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class SingleTrailerViewActivity extends AppCompatActivity {

    private WebView webViewTrailer; // Declare WebView
    private String videoId; // Declare video ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_trailer_view);
        StatusBarUtils.applyGradientStatusBar(this);

        // Initialize UI Components
        webViewTrailer = findViewById(R.id.webViewTrailer);  // Reference WebView
        TextView trailerHeadline = findViewById(R.id.trailerHeadline);
        TextView trailerReleaseDate = findViewById(R.id.trailerReleaseDate);
        TextView trailerDescription = findViewById(R.id.trailerDescription);
        Button backButton = findViewById(R.id.backButton);

        // Get Data from Intent
        Intent intent = getIntent();
        String headline = intent.getStringExtra("headline_trailer");
        String releaseDate = intent.getStringExtra("releaseDate_trailer");
        String description = intent.getStringExtra("description_trailer");
        videoId = intent.getStringExtra("videoUrl_trailer"); // Get videoId for YouTube embed

        // Set text data to UI elements
        trailerHeadline.setText(headline);
        trailerReleaseDate.setText("Release Date: " + releaseDate);
        trailerDescription.setText(description);

        // Set up the WebView to load the trailer video
        setupWebView();

        // Back Button Action
        backButton.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webViewTrailer.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webViewTrailer.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(SingleTrailerViewActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });
        String videoUrl = "https://www.youtube.com/embed/"+videoId;
        webViewTrailer.loadUrl(videoUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webViewTrailer.onPause();  // Pause the WebView when the activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        webViewTrailer.onResume();  // Resume the WebView when the activity is resumed
    }

}
