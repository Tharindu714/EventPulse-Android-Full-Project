package com.deltacodex.EventPulse;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.deltacodex.EventPulse.SQLite_DB.DatabaseHelper;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "channel1";
    EditText user_NameInput, passwordInput;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyGradientStatusBar(this);
        notificationManager = getSystemService(NotificationManager.class);
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

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_in);
        user_NameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.email);
        databaseHelper = new DatabaseHelper(this);

        Button Sign_up_Button = findViewById(R.id.register_1);
        Sign_up_Button.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        Button Sign_in_Button = findViewById(R.id.signIn_1);
        Sign_in_Button.setOnClickListener(view -> {
            String userName = user_NameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "UserName or Password not Entered", Toast.LENGTH_LONG).show();
            } else {
                boolean isValidUser = databaseHelper.checkUserExists(userName, password);

                if (isValidUser) {
                    // Check if the profile has been updated before checking status in Firebase
                    boolean isProfileUpdated = sharedPreferences.getBoolean("isProfileUpdated", false);

                    if (isProfileUpdated) {
                        // If the profile has been updated, check the user status in Firebase
                        checkUserStatusFromFirebase(userName);
                    } else {
                        // If the profile hasn't been updated, just proceed with login
                        proceedWithLogin();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void proceedWithLogin() {
        String userName = user_NameInput.getText().toString().trim();
        String userEmail = databaseHelper.getEmailByUsername(userName); // Fetch email using the username

        if (userEmail != null) {
            // Store username and email in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userName", userName);  // Store username
            editor.putString("userEmail", userEmail);  // Store email
            editor.apply();

            Notification notification = new NotificationCompat.Builder(SignInActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo_rounded)
                    .setContentTitle("Event Pulse Alert")
                    .setContentText(userName + " Logged in to Event Pulse Account Just Now")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                            .setSummaryText(userEmail + " has Logged Now"))
                    .build();

            notificationManager.notify(1, notification);
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(SignInActivity.this, "Email not found for user!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserStatusFromFirebase(String userName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profile_user") // Assuming you have a "users" collection in Firebase
                .whereEqualTo("username", userName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String status = doc.getString("status"); // Assuming status field exists in the user's document

                        if ("blocked".equals(status)) {
                            showBlockedUserUI();
                        } else {
                            proceedWithLogin();
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "User not found in the database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, "Failed to check user status", Toast.LENGTH_SHORT).show());
    }

    private void showBlockedUserUI() {
        setContentView(R.layout.activity_user_blocked);  // Create a new layout with the message and a call button

        // Find the call button and set up an action to call admin
        Button callAdminButton = findViewById(R.id.callAdminButton);
        callAdminButton.setOnClickListener(v -> {

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+94751441764"));  // Replace with the actual admin phone number
            startActivity(callIntent);
        });
    }
}

