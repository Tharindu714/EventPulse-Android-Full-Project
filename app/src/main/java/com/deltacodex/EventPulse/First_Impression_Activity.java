package com.deltacodex.EventPulse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class First_Impression_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_impression);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarUtils.applyGradientStatusBar(this);
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        ImageView visitHomeButton = findViewById(R.id.imgVisitHome);
        ImageView registerNowButton = findViewById(R.id.imgRegisterNow);

        FrameLayout visitHome_Frame = findViewById(R.id.visitHome_Frame_Layout);
        FrameLayout register_Frame = findViewById(R.id.Register_frame_layout);

        if (isLoggedIn) {
            visitHome_Frame.setVisibility(View.GONE);
            register_Frame.setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(First_Impression_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }, 5000); // Hold screen for 5 secs
        } else {
            visitHomeButton.setOnClickListener(view -> {
                Intent intent = new Intent(First_Impression_Activity.this, HomeActivity.class);
                startActivity(intent);
            });

            registerNowButton.setOnClickListener(view -> {
                Intent intent = new Intent(First_Impression_Activity.this, SignUpActivity.class);
                startActivity(intent);
            });
        }



    }
}