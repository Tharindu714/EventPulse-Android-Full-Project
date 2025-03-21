package com.deltacodex.EventPulse;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class WebViewActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);  // Set the layout containing WebView
        StatusBarUtils.applyGradientStatusBar(this);
        String Movie_downloadLink = getIntent().getStringExtra("Movie_downloadLink");
        String TvShow_downloadLink = getIntent().getStringExtra("downloadLink");
        String Game_downloadLink = getIntent().getStringExtra("downloadLink");

        WebView webView = findViewById(R.id.webview1);
        WebSettings webSettings = webView.getSettings();
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);  // Allow mixed content (HTTP/HTTPS)
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Handle error
                Toast.makeText(WebViewActivity.this, "Failed to load video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        if (Movie_downloadLink != null && !Movie_downloadLink.isEmpty()) {
            webView.loadUrl(Movie_downloadLink);
        } else if (TvShow_downloadLink != null && !TvShow_downloadLink.isEmpty()) {
            webView.loadUrl(TvShow_downloadLink);
        } else if (Game_downloadLink != null && !Game_downloadLink.isEmpty()) {
            webView.loadUrl(Game_downloadLink);
        } else {
            Toast.makeText(this, "No valid download link provided", Toast.LENGTH_SHORT).show();
        }
    }
}
