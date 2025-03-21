package com.deltacodex.EventPulse.ui.Crashlythics;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.deltacodex.EventPulse.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class Bug_report_Fragment extends Fragment {
    private WebView webView;
    // Uncomment the following if you add a ProgressBar in your layout:
    // private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bug_report, container, false);

        webView = view.findViewById(R.id.webViewCrashlytics);
        Button btnForceCrash = view.findViewById(R.id.btnForceCrash);
        setupWebView();

        // Force Crash Button Click Listener
        btnForceCrash.setOnClickListener(v -> {
            FirebaseCrashlytics.getInstance().log("🔥 Force Crash Button Clicked!");
            throw new RuntimeException("🔥 Forced Crash from Bug Report Fragment!");
        });

        return view;
    }

    private void setupWebView() {
        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Optionally, set a custom user agent string (if needed by the site)
        String defaultUserAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(defaultUserAgent + " CustomUserAgent/1.0");

        // Set a custom WebViewClient to stay in-app and handle errors
        webView.setWebViewClient(new CustomWebViewClient());

        // Set a WebChromeClient to monitor progress (optional)
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        // Load the Firebase Crashlytics URL
        webView.loadUrl("https://console.firebase.google.com/project/final-viva-app/overview");
    }

    // Custom WebViewClient to handle page navigation and errors
    private static class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Returning false ensures the WebView loads the URL itself
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            // Log the error (or handle it with a user-friendly message)
            Log.e("BugReportFragment", "Error loading page: " + error.getDescription());
            // Optionally, show a friendly error page or message to the user here.
            super.onReceivedError(view, request, error);
        }
    }
}
