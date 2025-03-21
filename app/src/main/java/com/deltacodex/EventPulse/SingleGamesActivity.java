package com.deltacodex.EventPulse;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class SingleGamesActivity extends AppCompatActivity {

    private WebView webView; // Declare WebView
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_games);
        StatusBarUtils.applyGradientStatusBar(this);

        // Initialize UI elements
        webView = findViewById(R.id.webView_game);  // Reference WebView
        TextView gameName = findViewById(R.id.singleName_game);
        TextView release_date = findViewById(R.id.release_date);
        TextView gameDescription = findViewById(R.id.singleDescription_game);
        TextView Game_genre = findViewById(R.id.gener_game);
        TextView Game_creator = findViewById(R.id.Creator_game);
        TextView platform1 = findViewById(R.id.platform_game);
        ImageView thumb = findViewById(R.id.thumbnail_image_game);
        ImageView large = findViewById(R.id.large_icon_game);
        TextView webLink = findViewById(R.id.weblink_game);

        // Get data from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String date = intent.getStringExtra("Released_Date");
        String description = intent.getStringExtra("description");
        String genre1 = intent.getStringExtra("genre");
        String developer = intent.getStringExtra("Developer");
        String platform = intent.getStringExtra("Platforms");
        String thumbnail_image = intent.getStringExtra("thumbnailUrl");
        String large_url = intent.getStringExtra("largeImageUrl");
        String download_link = intent.getStringExtra("downloadLink");
        videoId = intent.getStringExtra("trailerLink");

        // Set text data to UI elements
        gameName.setText(name);
        release_date.setText(date);
        gameDescription.setText(description);
        Game_genre.setText(genre1);
        Game_creator.setText(developer+"'s Production");
        platform1.setText("Eligible to play in "+platform);
        setupWebView();
        webLink.setText(download_link);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(SingleGamesActivity.this, WebViewActivity.class);
                webIntent.putExtra("downloadLink", download_link);
                startActivity(webIntent);
            }
        });

        webLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Steam Link", download_link);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(SingleGamesActivity.this, name+" Steam Link copied to clipboard!", Toast.LENGTH_LONG).show();
                return true; // Return true to indicate the event is handled
            }
        });
        Glide.with(this)
                .load(thumbnail_image)  // URL for the thumbnail image
                .into(thumb); // The ImageView where the thumbnail will be shown

        Glide.with(this)
                .load(large_url)  // URL for the large image
                .into(large); // The ImageView where the large image will be shown
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        // Enable JavaScript for WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);  // Allow mixed content (HTTP/HTTPS)
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Set a WebViewClient to handle loading events and errors
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Handle error
                Toast.makeText(SingleGamesActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        // Load the YouTube embed URL
        String videoUrl = "https://www.youtube.com/embed/"+videoId;
        webView.loadUrl(videoUrl);
    }


    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();  // Pause the WebView when the activity is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();  // Resume the WebView when the activity is resumed
    }
}