package com.deltacodex.EventPulse;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

import android.animation.ObjectAnimator;
import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class spalshActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spalsh);

        // Fix for window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarUtils.applyGradientStatusBar(this);
        // Bounce effect on Y-axis for logo
        ImageView logoImageView = findViewById(R.id.logo_img);
        Keyframe keyframe1 = Keyframe.ofFloat(0f, 0f);
        Keyframe keyframe2 = Keyframe.ofFloat(0.5f, 50f); // Bounce height
        Keyframe keyframe3 = Keyframe.ofFloat(1f, 0f);  // End at original position
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframe1, keyframe2, keyframe3);
        ObjectAnimator bounceAnimator = ObjectAnimator.ofPropertyValuesHolder(logoImageView, pvhY);
        bounceAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        bounceAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        bounceAnimator.setDuration(1000); // Bounce every second
        bounceAnimator.setInterpolator(new OvershootInterpolator());
        bounceAnimator.start();

        // Text Animation for Event Pulse
        TextView textViewEventPulse = findViewById(R.id.textView2);
        String text = getString(R.string.app_name);  // Ensure this string exists in strings.xml
        textViewEventPulse.setText(""); // Clear the text initially

        // Simulate typewriter effect for Event Pulse text
        new Handler().postDelayed(new Runnable() {
            int charIndex = 0;

            @Override
            public void run() {
                if (charIndex < text.length()) {
                    textViewEventPulse.append(String.valueOf(text.charAt(charIndex)));
                    charIndex++;
                    new Handler().postDelayed(this, 300); // 300ms delay between characters
                }
            }
        }, 0); // Start immediately

        // Move the slogan from bottom to top
        TextView textViewSlogan = findViewById(R.id.textView3);
        ObjectAnimator bottomToTopAnimator = ObjectAnimator.ofFloat(textViewSlogan, "translationY", 300f, 0f);
        bottomToTopAnimator.setDuration(6000); // 2-second duration for the movement
        bottomToTopAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomToTopAnimator.start();


        // Fling animation and transition to next activity
        new Handler().postDelayed(() -> {
            FlingAnimation flingAnimation = new FlingAnimation(logoImageView, DynamicAnimation.TRANSLATION_X);
            flingAnimation.setStartVelocity(100f);
            flingAnimation.setFriction(1f);
            flingAnimation.start();

            Intent intent;
            intent = new Intent(spalshActivity.this, First_Impression_Activity.class);
            startActivity(intent);
            finish();
        }, 7000);


    }


}