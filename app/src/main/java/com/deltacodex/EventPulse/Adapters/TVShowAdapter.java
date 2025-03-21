package com.deltacodex.EventPulse.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.SingleTVShowActivity;
import com.deltacodex.EventPulse.model.TVShow;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.TVShowViewHolder> {
    private final List<TVShow> tvShows;
    private final Context context;

    private final SharedPreferences sharedPreferences;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public TVShowAdapter(List<TVShow> tvShows, Context context) {
        this.tvShows = tvShows;
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_show, parent, false);
        return new TVShowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {
        TVShow tvShow = tvShows.get(position);
        holder.tvShowName.setText(tvShow.getName());
        holder.imdbRating.setText("IMDB: " + tvShow.getImdb());
        holder.rottenTomatoes.setText("Rotten Tomatoes: " + tvShow.getRottenTomatoes() + "%");
        Glide.with(context).load(tvShow.getThumbnailUrl()).into(holder.thumbnail);

        holder.watchNowButton_tv.setOnClickListener(v -> {
            String[] genres = tvShow.getGenre().split("\\s*,\\s*");

            boolean hasCrime = false;
            boolean hasAdultContent = false;

            for (String g : genres) {
                g = g.trim().toLowerCase();
                if (g.equals("crime")) {
                    hasCrime = true;
                }
                if (g.equals("18+")) {
                    hasAdultContent = true;
                }
            }

            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (hasCrime && hasAdultContent) {
                if (!isLoggedIn) {
                    Toast.makeText(context, "Please log in to access 18+ Crime content", Toast.LENGTH_SHORT).show();
                } else {
                    checkParentalPin_TV(tvShow);
                }
            } else if (hasCrime || hasAdultContent) {
                if (!isLoggedIn) {
                    Toast.makeText(context, "Please log in to access 18+ Crime content", Toast.LENGTH_SHORT).show();
                } else {
                    checkParentalPin_TV(tvShow);
                }
            } else {
                openTvDetails(tvShow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    // ViewHolder class for TV Show items
    public static class TVShowViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView tvShowName, imdbRating, rottenTomatoes;
        Button watchNowButton_tv;

        public TVShowViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.TVShowThumbnail);
            tvShowName = itemView.findViewById(R.id.TVShowName);
            imdbRating = itemView.findViewById(R.id.TVShowRating);
            rottenTomatoes = itemView.findViewById(R.id.rottenTomatoes_TVShow);
            watchNowButton_tv = itemView.findViewById(R.id.watchNowButton_tv);
        }
    }

    private void openTvDetails(TVShow tvShow) {
        Intent intent = new Intent(context, SingleTVShowActivity.class);
        intent.putExtra("name", tvShow.getName());
        intent.putExtra("description", tvShow.getDescription());
        intent.putExtra("imdb", tvShow.getImdb()); // You can add a description
        intent.putExtra("rottenTomatoes", tvShow.getRottenTomatoes());
        intent.putExtra("userLove", tvShow.getUserLove());
        intent.putExtra("creator", tvShow.getCreator());
        intent.putExtra("genre", tvShow.getGenre());
        intent.putExtra("seasons", tvShow.getSeasons());
        intent.putExtra("episodes", tvShow.getEpisodes());
        intent.putExtra("downloadLink", tvShow.getDownloadLink());
        intent.putExtra("thumbnailUrl", tvShow.getThumbnailUrl());
        intent.putExtra("trailerLink", extractYouTubeVideoId(tvShow.getTrailerLink()));
        intent.putExtra("largeImageUrl",tvShow.getLargeImageUrl());
        context.startActivity(intent);
    }

    private void checkParentalPin_TV(TVShow tvShow) {
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail == null) {
            Toast.makeText(context, "Error: User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Parental Password").document(userEmail).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String storedPin = document.getString("registeredPin");
                        showPinDialog_TV(storedPin, tvShow); // Show PIN dialog
                    } else {
                        showRegisterPinDialog_TV(); // If no PIN exists, show the registration dialog
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking PIN", Toast.LENGTH_SHORT).show());
    }

    private void showPinDialog_TV(String storedPin, TVShow tvShow) {
        if (context == null) return;  // Ensure context is not null to avoid potential crashes

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Parental Control PIN");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String enteredPin = input.getText().toString();
            if (enteredPin.equals(storedPin)) {
                openTvDetails(tvShow); // Open the TV show details if PIN is correct
            } else {
                Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show(); // Handle incorrect PIN
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Forgot PIN?", (dialog, which) -> {
            sendForgotPinEmail_TV();
            Toast.makeText(context, "Admin has been notified", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void showRegisterPinDialog_TV() {
        if (context == null) return;  // Ensure context is not null to avoid potential crashes

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set a New Parental Control PIN");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Register", (dialog, which) -> {
            String newPin = input.getText().toString();
            if (newPin.length() == 3) {
                savePinToFirebase_TV(newPin); // Save the new PIN to Firebase
            } else {
                Toast.makeText(context, "PIN must be 3 digits!", Toast.LENGTH_SHORT).show(); // Handle invalid PIN length
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void savePinToFirebase_TV(String pin) {
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail == null) {
            Toast.makeText(context, "Error: User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pinData = new HashMap<>();
        pinData.put("userEmail", userEmail);
        pinData.put("registeredPin", pin);

        db.collection("Parental Password").document(userEmail).set(pinData)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "PIN Registered Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error Registering PIN", Toast.LENGTH_SHORT).show());
    }

    private void sendForgotPinEmail_TV() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tharinduchanaka6@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Forgot Parental PIN");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello, I forgot my parental control PIN. Please assist.");

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(context, "No email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private String extractYouTubeVideoId(String url) {
        if (url == null || url.isEmpty()) return "";

        String pattern = "(?:(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/.*(?:\\?|&)v=|youtu\\.be\\/|youtube\\.com\\/embed\\/|youtube\\.com\\/v\\/))([a-zA-Z0-9_-]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        return matcher.find() ? matcher.group(1) : "";
    }
}

