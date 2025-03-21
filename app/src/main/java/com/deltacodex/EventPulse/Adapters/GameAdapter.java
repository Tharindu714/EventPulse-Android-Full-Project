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
import com.deltacodex.EventPulse.SingleGamesActivity;
import com.deltacodex.EventPulse.model.Game;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private final Context context;
    private final List<Game> gameList;

    private final SharedPreferences sharedPreferences;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GameAdapter(Context context, List<Game> gameList) {
        this.context = context;
        this.gameList = gameList;
        this.sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.gameName.setText(game.getName());
        holder.gameDeveloper.setText(game.getDeveloper());
        holder.ReleasedDate.setText(game.getReleased_Date());

        Glide.with(context).load(game.getThumbnailUrl()).into(holder.gameThumbnail);

        holder.watchNowBtn_game.setOnClickListener(v -> {
            String[] genres = game.getGenre().split("\\s*,\\s*");

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
                    checkParentalPin(game);
                }
            } else if (hasCrime || hasAdultContent) {
                if (!isLoggedIn) {
                    Toast.makeText(context, "Please log in to access 18+ Crime content", Toast.LENGTH_SHORT).show();
                } else {
                    checkParentalPin(game);
                }
            } else {
                openGameDetails(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameName, gameDeveloper, ReleasedDate;
        ImageView gameThumbnail;
        Button watchNowBtn_game;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameDeveloper = itemView.findViewById(R.id.gameDeveloper);
            ReleasedDate = itemView.findViewById(R.id.ReleasedDate);
            gameThumbnail = itemView.findViewById(R.id.gameThumbnail);
            watchNowBtn_game = itemView.findViewById(R.id.watchNowButton_games);
        }
    }

    private void openGameDetails(Game game) {
        Intent intent = new Intent(context, SingleGamesActivity.class);
        intent.putExtra("name", game.getName());
        intent.putExtra("Released_Date", game.getReleased_Date());
        intent.putExtra("description", game.getDescription()); // You can add a description
        intent.putExtra("genre", game.getGenre());
        intent.putExtra("Developer", game.getDeveloper());
        intent.putExtra("Platforms", game.getPlatforms());
        intent.putExtra("largeImageUrl", game.getLargeImageUrl());
        intent.putExtra("thumbnailUrl", game.getThumbnailUrl());
        intent.putExtra("trailerLink", extractYouTubeVideoId(game.getTrailerLink()));
        intent.putExtra("downloadLink",game.getDownloadLink());
        context.startActivity(intent);
    }

    private void checkParentalPin(Game game) {
        String userEmail = sharedPreferences.getString("userEmail", null);

        if (userEmail == null) {
            Toast.makeText(context, "Error: User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Parental Password").document(userEmail).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String storedPin = document.getString("registeredPin");
                        showPinDialog(storedPin, game);
                    } else {
                        showRegisterPinDialog();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error checking PIN", Toast.LENGTH_SHORT).show());
    }

    private void showPinDialog(String storedPin, Game game) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Parental Control PIN");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            if (input.getText().toString().equals(storedPin)) {
                openGameDetails(game);
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
