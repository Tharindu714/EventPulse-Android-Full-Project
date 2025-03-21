package com.deltacodex.EventPulse.ui.profile;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.activity.result.contract.ActivityResultContracts;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etPassword, etAddress, etMobile, etlocationName,etEmail;
    private RadioButton radioMale, radioFemale;

    private FusedLocationProviderClient fusedLocationClient;
    RadioGroup genderGroup;
    private FirebaseFirestore db;
    private ImageView profileImageView;
    private SharedPreferences sharedPreferences;
    private String userEmail;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private static final String IMAGE_DIRECTORY = "ProfileImages";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        etMobile = view.findViewById(R.id.et_mobile);
        etAddress = view.findViewById(R.id.et_address);
        radioMale = view.findViewById(R.id.radioMale);
        radioFemale = view.findViewById(R.id.radioFemale);
        etEmail = view.findViewById(R.id.et_email);
        loadProfileData();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        saveImageToLocalStorage(result);
                        loadImageIntoCircle(String.valueOf(result));
                    }
                }
        );

        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }

        // Initialize views
        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        EditText etEmail = view.findViewById(R.id.et_email);
        etMobile = view.findViewById(R.id.et_mobile);
        etAddress = view.findViewById(R.id.et_address);
        etlocationName = view.findViewById(R.id.location_name);

        genderGroup = view.findViewById(R.id.radioGroupGender);
        radioMale = view.findViewById(R.id.radioMale);
        radioFemale = view.findViewById(R.id.radioFemale);


        Button btnSaveProfile = view.findViewById(R.id.btn_save_profile);
        ImageButton btnGetLocation = view.findViewById(R.id.btn_get_location);
        profileImageView = view.findViewById(R.id.profile_image);

        // Get Shared Preferences
        sharedPreferences = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Event Pulse");
        userEmail = sharedPreferences.getString("userEmail", "Never Miss a Premiere");
        String address = sharedPreferences.getString("userAddress", "");
        String location_name = sharedPreferences.getString("userLocation","");

        // Set initial values from SharedPreferences
        etUsername.setText(userName);
        etEmail.setText(userEmail);
        etMobile.setText(sharedPreferences.getString("userMobile", ""));
        etPassword.setText(sharedPreferences.getString("userPassword", ""));
        etAddress.setText(address);
        etlocationName.setText(location_name);

        db = FirebaseFirestore.getInstance();

        btnSaveProfile.setOnClickListener(v -> saveProfileToDatabase());
        btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        profileImageView.setOnClickListener(v -> openImagePicker());
        loadProfileImage();
        return view;
    }
    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void saveImageToLocalStorage(Uri uri) {
        String sessionId = generateUniqueSessionId();  // Use sessionId instead of userId

        if (sessionId == null || uri == null) {
            Log.e("ProfileImage", "Session ID or Uri is null!");
            return;
        }

        // Get the app's internal storage directory
        File directory = new File(getContext().getFilesDir(), IMAGE_DIRECTORY);

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            boolean directoryCreated = directory.mkdirs(); // This ensures that the directory is created
            if (directoryCreated) {
                Log.d("ProfileImage", "Directory created: " + directory.getAbsolutePath());
            } else {
                Log.e("ProfileImage", "Failed to create directory: " + directory.getAbsolutePath());
                return;
            }
        }

        // Now save the image file
        File imageFile = new File(directory, sessionId + ".jpg");

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            saveImagePathToFirebase(imageFile.getAbsolutePath());

            // Load the image into the ImageView
            loadImageIntoCircle(imageFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("ProfileImage", "Error saving image locally", e);
        }
    }

    private void saveImagePathToFirebase(String imagePath) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("profile_image_path", imagePath);
        db.collection("Profile_pic").document(userEmail)
                .set(profileData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Profile Picture successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update Profile Picture", Toast.LENGTH_SHORT).show();
                });
    }


    private String generateUniqueSessionId() {
        long timestamp = System.currentTimeMillis();
        return "session_" + timestamp;
    }

    private void loadPPic(){
        String userEmail = sharedPreferences.getString("userEmail", "");
        db.collection("Profile_pic").document(userEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String pic = documentSnapshot.getString("profile_image_path");
                        Glide.with(getActivity())
                                .load(pic)
                                .transform(new CircleCrop())
                                .into(profileImageView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadProfileImage() {
        String userEmail = sharedPreferences.getString("userEmail", "");
        db.collection("Profile_user").document(userEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String password = documentSnapshot.getString("password");
                        String mobile = documentSnapshot.getString("mobile");
                        String address = documentSnapshot.getString("address");
                        String location_name = documentSnapshot.getString("location_name");
                        etUsername.setText(username);
                        etPassword.setText(password);
                        etMobile.setText(mobile);
                        etAddress.setText(address);
                        etlocationName.setText(location_name);
                        loadPPic();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveUserSession(String userId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Store the logged-in user's ID
        editor.putString("loggedInUserId", userId);
        editor.apply();
    }

    private String getLoggedInUserId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        return sharedPreferences.getString("loggedInUserId", "default_user");
    }

    // Load user preferences (checkboxes) from SharedPreferences


    private void saveProfileToDatabase() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String address = etAddress.getText().toString();
        String location_name = etlocationName.getText().toString();
        String mobile = etMobile.getText().toString();
        String gender = (radioMale.isChecked()) ? "Male" : (radioFemale.isChecked()) ? "Female" : "";

        // Save preferences to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();

        // Save profile data to Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("username", username);
        profileData.put("password", password);
        profileData.put("email", userEmail);
        profileData.put("mobile", mobile);
        profileData.put("address", address);
        profileData.put("location_name", location_name);
        profileData.put("gender", gender);
        profileData.put("status", "active");

        db.collection("Profile_user").document(userEmail)
                .set(profileData)
                .addOnSuccessListener(aVoid -> {
                    etAddress.setText(address);
                    etMobile.setText(mobile);
                    etPassword.setText(password);
                    etlocationName.setText(location_name);
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void turnOnGPS() {
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            getCurrentLocation(); // Now fetch location
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(getActivity(), 100);
                } catch (IntentSender.SendIntentException ex) {
                    ex.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCurrentLocation() {
        turnOnGPS();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Make sure the fragment's view is not null before accessing the UI
                        if (getView() != null) {
                            TextView locationTextView = getView().findViewById(R.id.et_address);
                            locationTextView.setText("Lat: " + latitude + ", Long: " + longitude);
                            getLocationName(latitude, longitude);
                        } else {
                            Log.e("ProfileFragment", "Fragment's view is null, cannot update location.");
                        }
                    } else {
                        Toast.makeText(getContext(), "Location not found. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getLocationName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String locationName = addresses.get(0).getAddressLine(0); // Get full address

                // Display location name in another text field
                TextView locationNameTextView = getView().findViewById(R.id.location_name);
                locationNameTextView.setText(locationName);
            } else {
                Toast.makeText(getContext(), "Unable to get location name", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void loadImageIntoCircle(String imagePath) {
        File imageFile = new File(imagePath);

        if (imageFile.exists()) {
            Log.d("ProfileImage", "Image file exists at path: " + imageFile.getAbsolutePath());
            Glide.with(getActivity())
                    .load(imageFile)
                    .transform(new CircleCrop())
                    .into(profileImageView);
        } else {
            Log.e("ProfileImage", "Image file does not exist at path: " + imageFile.getAbsolutePath());
        }
    }

    private void loadProfileData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = sharedPreferences.getString("userEmail", "");
        String userId = "";
        saveUserSession(userId);
        // Fetch profile data from Firebase Firestore
        db.collection("Profile_user")
                .document(userEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the fields
                        String username = documentSnapshot.getString("username");
                        String password = documentSnapshot.getString("password");
                        String mobile = documentSnapshot.getString("mobile");
                        String address = documentSnapshot.getString("address");
                        String location_name = documentSnapshot.getString("location_name");
                        String gender = documentSnapshot.getString("gender");

                        // Set the data in the corresponding EditText fields
                        etUsername.setText(username);
                        etPassword.setText(password);
                        etMobile.setText(mobile);
                        etAddress.setText(address);
                        etlocationName.setText(location_name);

                        // Handle the gender field (Radio Buttons)
                        if ("Male".equals(gender)) {
                            radioMale.setChecked(true);
                            radioFemale.setChecked(false);
                        } else if ("Female".equals(gender)) {
                            radioFemale.setChecked(true);
                            radioMale.setChecked(false);
                        }

                    } else {
                        Toast.makeText(getContext(), "Profile data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
                });
    }

}
