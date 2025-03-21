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
import com.deltacodex.EventPulse.SingleViewActivity;
import com.deltacodex.EventPulse.model.Movie;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private final List<Movie> movieList;
    private final SharedPreferences sharedPreferences;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
        this.sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.movieName.setText(movie.getMovie_name());
        holder.movieRating.setText("IMDB: " + movie.getMovie_imdb());
        holder.rottenTomatoes.setText("Rotten Tomatoes: " + movie.getMovie_rottenTomatoes() + "%");
        Glide.with(context).load(movie.getMovie_thumbnailUrl()).into(holder.movieThumbnail);

        holder.watchNowBtn.setOnClickListener(v -> {
            String[] genres = movie.getMovie_genre().split("\\s*,\\s*");
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
                    checkParentalPin(movie);
                }
            } else if (hasCrime || hasAdultContent) {
                if (!isLoggedIn) {
                    Toast.makeText(context, "Please log in to access 18+ Crime content", Toast.LENGTH_SHORT).show();
                } else {
                    checkParentalPin(movie);
                }
            } else {
                openMovieDetails(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieName, movieRating, rottenTomatoes;
        ImageView movieThumbnail;
        Button watchNowBtn;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieName = itemView.findViewById(R.id.movieName);
            movieRating = itemView.findViewById(R.id.movieRating);
            rottenTomatoes = itemView.findViewById(R.id.rottenTomatoes_movies);
            movieThumbnail = itemView.findViewById(R.id.movieThumbnail);
            watchNowBtn = itemView.findViewById(R.id.watchNowButton);
        }
    }

    private void openMovieDetails(Movie movie) {
        Intent intent = new Intent(context, SingleViewActivity.class);
        intent.putExtra("Movie_name", movie.getMovie_name());
        intent.putExtra("Movie_duration", movie.getMovie_duration());
        intent.putExtra("Movie_description", movie.getMovie_description());
        intent.putExtra("Movie_imdb", movie.getMovie_imdb());
        intent.putExtra("Movie_rottenTomatoes", movie.getMovie_rottenTomatoes());
        intent.putExtra("Movie_userLove", movie.getMovie_userLove());
        intent.putExtra("Movie_genre", movie.getMovie_genre());
        intent.putExtra("Movie_creator", movie.getMovie_creator());
        intent.putExtra("Movie_distributed", movie.getMovie_distributed());
        intent.putExtra("Movie_largeImageUrl", movie.getMovie_largeImageUrl());
        intent.putExtra("Movie_thumbnailUrl", movie.getMovie_thumbnailUrl());
        intent.putExtra("Movie_trailerLink", extractYouTubeVideoId(movie.getMovie_trailerLink()));
        intent.putExtra("Movie_downloadLink", movie.getMovie_downloadLink());
        context.startActivity(intent);
    }

    private void checkParentalPin(Movie movie) {
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail == null) {
            Toast.makeText(context, "Error: User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Parental Password").document(userEmail).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String storedPin = document.getString("registeredPin");
                        showPinDialog(storedPin, movie);
                    } else {
                        showRegisterPinDialog();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking PIN", Toast.LENGTH_SHORT).show());
    }

    private void showPinDialog(String storedPin, Movie movie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Parental Control PIN");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            if (input.getText().toString().equals(storedPin)) {
                openMovieDetails(movie);
            } else {
                Toast.makeText(context, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Forgot PIN?", (dialog, which) -> {
            sendForgotPinEmail();
            Toast.makeText(context, "Admin has been notified", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    private void showRegisterPinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Set a New Parental Control PIN");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Register", (dialog, which) -> {
            String newPin = input.getText().toString();
            if (newPin.length() == 3) {
                savePinToFirebase(newPin);
            } else {
                Toast.makeText(context, "PIN must be 3 digits!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void savePinToFirebase(String pin) {
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

    private void sendForgotPinEmail() {
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


