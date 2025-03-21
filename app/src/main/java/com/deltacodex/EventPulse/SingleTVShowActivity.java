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

public class SingleTVShowActivity extends AppCompatActivity {

    private WebView webView; // Declare WebView
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_tvshow);
        StatusBarUtils.applyGradientStatusBar(this);

        // Initialize UI elements
        webView = findViewById(R.id.webView_tv);  // Reference WebView
        TextView movieName = findViewById(R.id.singleMovieName_tv);
        TextView SeasonEpi = findViewById(R.id.duration_tv);
        TextView movieDescription = findViewById(R.id.singleMovieDescription_tv);
        TextView movieRating = findViewById(R.id.singleMovieRating_tv);
        TextView movieRottenTomatoes = findViewById(R.id.singleMovieRottenTomatoes_tv);
        TextView movieUserLove = findViewById(R.id.usersLove_tv);
        TextView genre = findViewById(R.id.gener_tv);
        TextView creator = findViewById(R.id.Creator_tv);
        ImageView thumb = findViewById(R.id.thumbnail_image_tv);
        ImageView large = findViewById(R.id.large_icon_tv);
        TextView webLink = findViewById(R.id.weblink_tv);

        // Get data from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String season = intent.getStringExtra("seasons");
        String Epi = intent.getStringExtra("episodes");
        String description = intent.getStringExtra("description");
        String imdb = intent.getStringExtra("imdb");
        String rottenTomatoes = intent.getStringExtra("rottenTomatoes");
        String userLove = intent.getStringExtra("userLove");
        String genre1 = intent.getStringExtra("genre");
        String creator1 = intent.getStringExtra("creator");
        String thumbnail_image = intent.getStringExtra("thumbnailUrl");
        String large_url = intent.getStringExtra("largeImageUrl");
        String download_link = intent.getStringExtra("downloadLink");
        videoId = intent.getStringExtra("trailerLink");

        // Set text data to UI elements
        movieName.setText(name);
        SeasonEpi.setText("This Series has "+season+ " Seasons with "+Epi+" Episodes");
        movieDescription.setText(description);
        movieRating.setText("IMDB Ratings: " + imdb);
        movieRottenTomatoes.setText("Rotten Tomatoes rating: " + rottenTomatoes + "%");
        movieUserLove.setText(userLove + "% of Google Users Love this Tv Show");
        genre.setText(genre1);
        creator.setText(creator1+"'s Production");
        setupWebView();
        webLink.setText(download_link);
        webLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(SingleTVShowActivity.this, WebViewActivity.class);
                webIntent.putExtra("Movie_downloadLink", download_link);
                startActivity(webIntent);
            }
        });

        webLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Torrent Link", download_link);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(SingleTVShowActivity.this, name+" Link copied to clipboard!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(SingleTVShowActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
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