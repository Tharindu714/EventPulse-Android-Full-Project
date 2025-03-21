package com.deltacodex.EventPulse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.squareup.picasso.Picasso;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        StatusBarUtils.applyGradientStatusBar(this);
        // Initialize views
        ImageView newsImageView = findViewById(R.id.singleNewsImageView);
        TextView headlineTextView = findViewById(R.id.singleHeadlineTextView);
        TextView descriptionTextView = findViewById(R.id.singleDescriptionTextView);
        TextView timestampTextView = findViewById(R.id.singleTimestampTextView);

        // Get the passed data
        Intent intent = getIntent();
        String headline = intent.getStringExtra("headline");
        String description = intent.getStringExtra("description");
        String timestamp = intent.getStringExtra("timestamp");
        String image = intent.getStringExtra("imageUrl");

        // Set the data to views
        headlineTextView.setText(headline);
        descriptionTextView.setText(description);
        timestampTextView.setText(timestamp);
        Picasso.get().load(image).into(newsImageView);  // Load image into ImageView
    }
}
