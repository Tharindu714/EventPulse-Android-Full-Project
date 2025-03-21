package com.deltacodex.EventPulse;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deltacodex.EventPulse.SQLite_DB.DatabaseHelper;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;

public class SignUpActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "channel1";
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        databaseHelper = new DatabaseHelper(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarUtils.applyGradientStatusBar(this);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel_NO1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        EditText fullNameInput = findViewById(R.id.full_name);
        EditText mobileInput = findViewById(R.id.mobile);
        EditText emailInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);


        Button Sign_in_Button = findViewById(R.id.signin);
        Sign_in_Button.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(view -> {
            String fullName = fullNameInput.getText().toString().trim();
            String mobile = mobileInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();


            if (fullName.isEmpty()) {
                fullNameInput.setError("Username is required");
            } else if (mobile.isEmpty() || !isValidMobile(mobile)) {
                mobileInput.setError("Invalid Mobile Number");
            } else if (email.isEmpty() || !isValidEmail(email)) {
                emailInput.setError("Invalid Email Address");
            } else if (password.isEmpty()) {
                passwordInput.setError("Password is required");
            }else {
                if (databaseHelper.checkUserExists_Signup(email, mobile)) {
                    Toast.makeText(SignUpActivity.this, "User already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    boolean success = databaseHelper.registerUser(fullName, mobile, password, email);
                    if (success) {
                        Notification notification = new NotificationCompat.Builder(SignUpActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logo_rounded)
                                .setContentTitle("Event Pulse Alert")
                                .setContentText(fullName+" has Created a new account")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                                .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.premiere_wave_studio_inc))
                                .setStyle(
                                        new NotificationCompat.BigPictureStyle()
                                                .bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.premiere_wave_studio_inc))
                                                .setSummaryText(fullName+" has Created a new account")
                                )
                                .build();

                        notificationManager.notify(1, notification);
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    public static boolean isValidMobile(String mobile) {
        String mobilePattern = "^[+]?[0-9]{10,13}$";  // Matches numbers like 1234567890 or +1234567890
        return mobile.matches(mobilePattern);
    }

}