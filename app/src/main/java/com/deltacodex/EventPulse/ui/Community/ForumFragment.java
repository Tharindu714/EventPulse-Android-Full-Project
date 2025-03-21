package com.deltacodex.EventPulse.ui.Community;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.deltacodex.EventPulse.Adapters.ForumAdapter;
import com.deltacodex.EventPulse.AddPostActivity;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.Utils.NetworkUtils;
import com.deltacodex.EventPulse.model.ForumPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;
import java.util.List;

public class ForumFragment extends Fragment {
    private ForumAdapter forumAdapter;
    private List<ForumPost> postList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyView, shakeText;
    private ImageView shakeIcon;
    private RecyclerView recyclerView;
    private View overlay; // Overlay view
    private TextView addPostInstruction; // Instruction text for the Add Post button
    private Vibrator vibrator;
    private SensorManager sensorManager;
    private float lastX, lastY, lastZ;
    private static final float SHAKE_THRESHOLD = 10f; // Adjust sensitivity
    private long lastShakeTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        new Thread(() -> {
            boolean isInternetWorking = NetworkUtils.isInternetAvailable();
            boolean isFirestoreWorking = NetworkUtils.isFirestoreAvailable();

            // Update UI on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                if (isInternetWorking) {
                    Toast.makeText(getContext(), "📡 Internet is working ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "⚠️ No Internet Connection ❌", Toast.LENGTH_SHORT).show();
                }

                if (isFirestoreWorking) {
                    Toast.makeText(getContext(), "🔥 Database Health is good ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "❌ Database is down! Try from a while Method ⚠️", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        FloatingActionButton addPostButton = view.findViewById(R.id.addPostButton);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);

        // Add instruction view and overlay to the layout
        overlay = view.findViewById(R.id.overlay);
        addPostInstruction = view.findViewById(R.id.addPostInstruction);

        shakeIcon = view.findViewById(R.id.shakeIcon);
        shakeText = view.findViewById(R.id.shakeText);

        db = FirebaseFirestore.getInstance();

        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);

        //  Setup RecyclerView
        postList = new ArrayList<>();
        forumAdapter = new ForumAdapter(getContext(), postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(forumAdapter);

        loadForumPosts();

        // Set up Add Post button click listener
        addPostButton.setOnClickListener(v -> startActivity(new Intent(getContext(), AddPostActivity.class)));

        // Display spotlight effect and instruction on fragment load
        showSpotlightEffect();

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadForumPosts() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("CommunityForum")
                .whereEqualTo("status", "approved")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("Fire_storeCheck", "Error fetching posts", error);
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    postList.clear();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot) {
                            ForumPost post = doc.toObject(ForumPost.class);
                            assert post != null;
                            post.setPostId(doc.getId());
                            post.setLikesCount(doc.getLong("likes") != null ? doc.getLong("likes") : 0); // Set likes count
                            postList.add(post);
                        }
                        Log.d("Fire_storeCheck", "Posts loaded: " + postList.size());
                        emptyView.setVisibility(View.GONE);
                    } else {
                        Log.d("Fire_storeCheck", "No posts available.");
                        emptyView.setVisibility(View.VISIBLE);
                    }

                    forumAdapter.notifyDataSetChanged();
                    recyclerView.scheduleLayoutAnimation();  // Forces UI refresh
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void showSpotlightEffect() {
        // Initially show overlay and instruction for the Add Post button
        overlay.setVisibility(View.VISIBLE);
        addPostInstruction.setVisibility(View.VISIBLE);

        // Optional: Add a slight delay to show the instruction
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Add pulse animation or scaling effect to the Add Post button (optional)
            FloatingActionButton addPostButton = getView().findViewById(R.id.addPostButton);

            // Pulse effect - Grow the Add Post button slightly
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(addPostButton, "scaleX", 1.2f, 1f);
            scaleX.setDuration(500);
            scaleX.start();

            ObjectAnimator scaleY = ObjectAnimator.ofFloat(addPostButton, "scaleY", 1.2f, 1f);
            scaleY.setDuration(500);
            scaleY.start();

            // Fade out the overlay and instruction after showing it
            ObjectAnimator fadeOutOverlay = ObjectAnimator.ofFloat(overlay, "alpha", 0f);
            fadeOutOverlay.setDuration(500);
            fadeOutOverlay.start();

            ObjectAnimator fadeOutInstruction = ObjectAnimator.ofFloat(addPostInstruction, "alpha", 0f);
            fadeOutInstruction.setDuration(500);
            fadeOutInstruction.start();

            // Hide overlay and instruction after the animation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                overlay.setVisibility(View.GONE);
                addPostInstruction.setVisibility(View.GONE);
            }, 500); // 500ms after the animation finishes
        }, 1000); // Delay of 1 second to show the instructions
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float deltaX = Math.abs(lastX - x);
            float deltaY = Math.abs(lastY - y);
            float deltaZ = Math.abs(lastZ - z);

            lastX = x;
            lastY = y;
            lastZ = z;

            // Detect shake
            if ((deltaX > SHAKE_THRESHOLD && deltaY > SHAKE_THRESHOLD) ||
                    (deltaX > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD) ||
                    (deltaY > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD)) {

                long currentTime = System.currentTimeMillis();
                if ((currentTime - lastShakeTime) > 1000) { // Prevent multiple triggers
                    lastShakeTime = currentTime;

                    //  Trigger Vibration
                    if (vibrator.hasVibrator()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(150);
                        }
                    }

                    refreshForumPosts();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    public void onResume() {
        super.onResume();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void refreshForumPosts() {
        Toast.makeText(getActivity(), "Refreshing....", Toast.LENGTH_SHORT).show();
        //  Show Shake Animation
        Animation shakeAnim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_animation);
        shakeIcon.startAnimation(shakeAnim);
        shakeText.setVisibility(View.VISIBLE);
        shakeIcon.setVisibility(View.VISIBLE);

        // Hide RecyclerView while shake animation is active
        recyclerView.setVisibility(View.GONE);

        // Center the shake icon and text on the screen
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shakeText.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        shakeText.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams) shakeIcon.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        shakeIcon.setLayoutParams(params);

        new Handler().postDelayed(() -> {
            shakeText.setVisibility(View.GONE);
            shakeIcon.setVisibility(View.GONE);

            // Show the RecyclerView again after 2 seconds
            recyclerView.setVisibility(View.VISIBLE);
            loadForumPosts();  // Reload the posts
        }, 2000);  // Duration of the shake effect and hiding posts
    }
}
