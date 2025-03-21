package com.deltacodex.EventPulse.ui.About;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.deltacodex.EventPulse.R;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // Initialize WebView
        webView = view.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // To keep WebView within app
        webView.getSettings().setJavaScriptEnabled(true); // Enable JS

        // Initialize the views
        TextView developerWebsite = view.findViewById(R.id.developer_website);
        TextView developerEmail = view.findViewById(R.id.developer_email);
        TextView developerPhone = view.findViewById(R.id.developer_phone);
        TextView developerSMS = view.findViewById(R.id.developer_sms);

        // Set up click listeners
        developerWebsite.setOnClickListener(v -> openWebsite());
        developerEmail.setOnClickListener(v -> sendEmail());
        developerPhone.setOnClickListener(v -> dialPhoneNumber());
        developerSMS.setOnClickListener(v -> sendSMS());

        resizeDrawable(developerWebsite, R.drawable.ic_website, 40, 40);
        resizeDrawable(developerEmail, R.drawable.ic_email, 40, 40);
        resizeDrawable(developerPhone, R.drawable.ic_call, 40, 40);
        resizeDrawable(developerSMS, R.drawable.ic_message, 40, 40);

        return view;
    }

    // Open the website in WebView inside the app
    private void openWebsite() {
        String url = "https://tharindu714.github.io/deltacodexrobotics/";
        webView.setVisibility(View.VISIBLE); // Show WebView when link is clicked
        webView.loadUrl(url);
    }

    // Send email
    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:deltacodexsoftwares@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "I have some feedback regarding your app...");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    // Dial phone number
    private void dialPhoneNumber() {
        String phoneNumber = "tel:+94751441764";
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
        startActivity(dialIntent);
    }

    private void sendSMS() {
        String phoneNumber = "+94751441764";
        String message = "Hi, I wanted to reach out you to inform...";
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }

    private void resizeDrawable(TextView textView, int drawableResId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableResId);
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height);
            textView.setCompoundDrawables(drawable, null, null, null);
        }
    }
}