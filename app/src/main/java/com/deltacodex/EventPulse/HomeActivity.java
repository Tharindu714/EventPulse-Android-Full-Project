package com.deltacodex.EventPulse;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.deltacodex.EventPulse.Utils.NetworkUtils;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.deltacodex.EventPulse.ui.Maps.MapsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.deltacodex.EventPulse.databinding.ActivityHomeBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String CHANNEL_ID = "channel1";

    NotificationManager notificationManager;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private GoogleMap mMap;
    private double userLat;
    private double userLng;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkBlockedStatus();
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        checkServerStatus();
        notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel_NO1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation(); // Call method directly if permission is already granted
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
        // Set up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fab);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.appBarHome.fab.setOnClickListener(view -> {
            Snackbar.make(view, "Opening Google Maps with Nearby Events", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show();
            openMap();  // Call method to open the map
        });

        // Floating Action Button action
        binding.appBarHome.fab.setOnClickListener(view -> openMap());
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_Tv_shows, R.id.nav_news_update, R.id.nav_news_feed,
                R.id.nav_trailer, R.id.nav_bloopers, R.id.nav_teasers,
                R.id.nav_Profile, R.id.nav_gallery, R.id.nav_admin, R.id.nav_community,
                R.id.nav_About, R.id.nav_bug_report)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Handle different navigation items here
            if (id == R.id.nav_logout) {
                showLogoutDialog();  // Call logout when logout item is clicked
            } else if (id == R.id.nav_SignUp) {
                showRegisterDialog();
            } else if (id == R.id.nav_Tv_shows) {
                navController.navigate(R.id.nav_Tv_shows);
            } else if (id == R.id.nav_Profile) {
                navController.navigate(R.id.nav_Profile);
            } else if (id == R.id.nav_admin) {
                navController.navigate(R.id.nav_admin);
            } else if (id == R.id.nav_news_update) {
                navController.navigate(R.id.nav_news_update);
            } else if (id == R.id.nav_news_feed) {
                navController.navigate(R.id.nav_news_feed);
            } else if (id == R.id.nav_trailer) {
                navController.navigate(R.id.nav_trailer);
            } else if (id == R.id.nav_bloopers) {
                navController.navigate(R.id.nav_bloopers);
            } else if (id == R.id.nav_teasers) {
                navController.navigate(R.id.nav_teasers);
            } else if (id == R.id.nav_gallery) {
                navController.navigate(R.id.nav_gallery);
            } else if (id == R.id.nav_community) {
                navController.navigate(R.id.nav_community);
            } else if (id == R.id.nav_About) {
                navController.navigate(R.id.nav_About);
            } else if (id == R.id.nav_bug_report) {
                navController.navigate(R.id.nav_bug_report);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        StatusBarUtils.applyGradientStatusBar(this);

        // Open the drawer automatically when HomeActivity starts
        new Handler().postDelayed(() -> binding.drawerLayout.openDrawer(GravityCompat.START), 500);

        // Get the header of the NavigationView and set user details
        View headerView = navigationView.getHeaderView(0);

        TextView nameTextView = headerView.findViewById(R.id.Name_textView);
        TextView emailTextView = headerView.findViewById(R.id.email_TextView);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Event Pulse");
        String userEmail = sharedPreferences.getString("userEmail", "Never Miss a Premiere");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Log to confirm email retrieval
        Log.i("EventPulse-Log", "userName retrieved: " + userName);
        Log.i("EventPulse-Log", "userEmail retrieved: " + userEmail);

        nameTextView.setText(userName);
        emailTextView.setText(userEmail);

        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        MenuItem signUpItem = menu.findItem(R.id.nav_SignUp);
        MenuItem newsUpdateItem = menu.findItem(R.id.nav_news_update);
        MenuItem bugReport = menu.findItem(R.id.nav_bug_report);
        MenuItem profile_page = menu.findItem(R.id.nav_Profile);

        boolean visible_toMe = isLoggedIn && "Tharindu Chanaka".equals(userName) && "tharinduchanaka6@gmail.com".equals(userEmail);
        newsUpdateItem.setVisible(visible_toMe);
        bugReport.setVisible(visible_toMe);

        if (isLoggedIn) {
            logoutItem.setVisible(true);
            signUpItem.setVisible(false);
            profile_page.setVisible(true);
        } else {
            logoutItem.setVisible(false);
            signUpItem.setVisible(true);
            profile_page.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Check if the logout item is clicked
        if (id == R.id.nav_logout) {
            showLogoutDialog();
            return true;  // Indicate we handled this item
        } else if (id == R.id.nav_SignUp) {
            showRegisterDialog();
            return true;  // Indicate we handled this item
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        // Ensure dialog context is correct
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);  // Explicit context
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Get NotificationManager instance
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Ensure notificationManager is not null
                    if (notificationManager != null) {
                        // Show notification first
                        Notification notification = new NotificationCompat.Builder(HomeActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.logo_rounded)
                                .setContentTitle("Event Pulse Alert")
                                .setContentText("User Logged out Successfully")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                                .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                                .setStyle(
                                        new NotificationCompat.BigPictureStyle()
                                                .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                                                .setSummaryText("User Logged out Successfully")
                                )
                                .build();

                        notificationManager.notify(1, notification);  // Send the notification
                    } else {
                        // Handle the case where notificationManager is null
                        Log.e("NotificationError", "NotificationManager is null.");
                    }

                    // Delay logout to ensure alert shows properly
                    // Now safely log out
                    new Handler().postDelayed(this::logoutUser, 500); // Add a slight delay
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showRegisterDialog() {
        // Create the alert dialog
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Register New Member")
                .setMessage("Are you sure you want to go to the registration page?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, go to the SignUpActivity
                        Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)  // Just dismiss if "No" is clicked
                .show();
    }

    private void logoutUser() {
        // Clear SharedPreferences and navigate to First_Impression_Activity
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, First_Impression_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkServerStatus() {
        new Thread(() -> {
            boolean isInternetWorking = NetworkUtils.isInternetAvailable();
            boolean isFirestoreWorking = NetworkUtils.isFirestoreAvailable();

            runOnUiThread(() -> {
                if (isInternetWorking) {
                    Toast.makeText(this, "📡 Internet is working ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "⚠️ No Internet Connection ❌", Toast.LENGTH_SHORT).show();
                }

                if (isFirestoreWorking) {
                    Toast.makeText(this, "🔥 Firestore Database is running ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "❌ Firestore is down!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void openMap() {
        Snackbar.make(binding.appBarHome.fab, "Opening Map...", Snackbar.LENGTH_SHORT)
                .setAnchorView(R.id.fab).show();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_content_home, new MapsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String style =
                "[ " +
                        "{ " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#212121\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.icon\", " +
                        "\"stylers\": [ { \"visibility\": \"off\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#757575\" } ] " +
                        "}, { " +
                        "\"elementType\": \"labels.text.stroke\", " +
                        "\"stylers\": [ { \"color\": \"#212121\" } ] " +
                        "}, { " +
                        "\"featureType\": \"administrative.locality\", " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"poi\", " +
                        "\"elementType\": \"labels.text.fill\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"poi.park\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#2e3b4e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#2c2c2c\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.arterial\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.highway\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"road.local\", " +
                        "\"elementType\": \"geometry\", " +
                        "\"stylers\": [ { \"color\": \"#3e3e3e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"transit.station\", " +
                        "\"elementType\": \"labels.icon\", " +
                        "\"stylers\": [ { \"color\": \"#9e9e9e\" } ] " +
                        "}, { " +
                        "\"featureType\": \"water\", " +
                        "\"elementType\": \"geometry.fill\", " +
                        "\"stylers\": [ { \"color\": \"#000000\" } ] " +
                        "} ]";

        try {
            boolean success = mMap.setMapStyle(new MapStyleOptions(style));
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapStyle", "Can't find style. Error: ", e);
        }

        getUserLocation(); // First, get user location
        fetchNearbyPlaces("movie_theater", R.drawable.popcorn_icon); // Fetch cinemas
        fetchNearbyPlaces("arcade", R.drawable.joystick_icon); // Fetch gaming hubs
    }

    private void fetchNearbyPlaces(String placeType, int iconResource) {
        double latitude = userLat;
        double longitude = userLng;
        int radius = 10000; // 15km search radius

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + latitude + "," + longitude
                + "&radius=" + radius
                + "&type=" + placeType
                + "&key=AIzaSyCsQKbCE6iRLAeDAQWVBjGA67lrjEkMZ0U";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                            String placeName = place.getString("name");
                            double lat = location.getDouble("lat");
                            double lng = location.getDouble("lng");

                            // Add marker with the icon
                            addPlaceMarker(new LatLng(lat, lng), placeName, iconResource);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error fetching places", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private void addPlaceMarker(LatLng latLng, String placeName, int iconResource) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(placeName)
                .icon(BitmapDescriptorFactory.fromResource(iconResource));
        mMap.addMarker(markerOptions);
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Only call animateCameraToLocation if mMap is not null
                            if (mMap != null) {
                                animateCameraToLocation(latitude, longitude);
                            } else {
                                // Handle the case when mMap is null
                                Log.e("EventPulse", "Google Map is not ready yet.");
                            }
                        }
                    });
        }
    }

    private void checkBlockedStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");

        if (userName.isEmpty()) {
            // Handle case where username is not available
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Profile_user")
                .whereEqualTo("username", userName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String status = doc.getString("status");
                        if ("blocked".equals(status)) {
                            // Redirect to the blocked UI
                            showBlockedUserUI();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(HomeActivity.this, "Error checking user status", Toast.LENGTH_SHORT).show()
                );
    }

    private void showBlockedUserUI() {
        setContentView(R.layout.activity_user_blocked);
        Button callAdminButton = findViewById(R.id.callAdminButton);
        callAdminButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+94751441764"));
            startActivity(callIntent);
        });
    }


    private void animateCameraToLocation(double latitude, double longitude) {
        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15)); // Zoom level 15
        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}