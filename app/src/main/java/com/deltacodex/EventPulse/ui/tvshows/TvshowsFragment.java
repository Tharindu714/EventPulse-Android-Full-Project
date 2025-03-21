package com.deltacodex.EventPulse.ui.tvshows;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.deltacodex.EventPulse.Adapters.GameAdapter;
import com.deltacodex.EventPulse.Adapters.GenreSpinnerAdapter;
import com.deltacodex.EventPulse.Adapters.MovieAdapter;
import com.deltacodex.EventPulse.Adapters.TVShowAdapter;
import com.deltacodex.EventPulse.model.Game;
import com.deltacodex.EventPulse.model.Movie;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.deltacodex.EventPulse.model.TVShow;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.deltacodex.EventPulse.R;

import java.util.ArrayList;
import java.util.List;

public class TvshowsFragment extends Fragment implements SensorEventListener {

    private RecyclerView rvMovies, rvGames, rvTvshows;
    private MovieAdapter movieAdapter;
    private GameAdapter gameAdapter;
    private TVShowAdapter tvShowAdapter;
    private List<TVShow> tvShowList;
    private List<Movie> movieList;
    private List<Game> gameList;
    private FirebaseFirestore db;
    private String selectedGenre = "";

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private static final float SCROLL_THRESHOLD = 2.0f;  // Adjust for sensitivity

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tvshows, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        // Initialize Views
        rvMovies = view.findViewById(R.id.recycler_movies_an);
        rvGames = view.findViewById(R.id.recycler_games);
        rvTvshows = view.findViewById(R.id.recycler_tvshows_an);
        EditText searchBox = view.findViewById(R.id.searchBox);
        Spinner genreSpinner = view.findViewById(R.id.genreSpinner);

        // Define genres and their corresponding icons
        String[] genres = {"All", "Action", "Comedy", "Horror", "Drama", "Sci-Fi", "Thriller", "Mystery", "Fantasy", "Sport"};
        int[] icons = {R.drawable.all, R.drawable.action, R.drawable.comedy, R.drawable.horror,
                R.drawable.drama, R.drawable.scific, R.drawable.thriller, R.drawable.mystery, R.drawable.fantasy, R.drawable.sport};

        // Set the custom adapter
        GenreSpinnerAdapter genreAdapter = new GenreSpinnerAdapter(getContext(), genres, icons);
        genreSpinner.setAdapter(genreAdapter);

        // Set genre spinner listener
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedGenre = genreSpinner.getSelectedItem().toString();
                if (selectedGenre.equals("All")) {
                    // Show all movies and TV shows when "All" is selected
                    movieAdapter = new MovieAdapter(getContext(), movieList);
                    gameAdapter = new GameAdapter(getContext(), gameList);
                    tvShowAdapter = new TVShowAdapter(tvShowList, getContext());
                } else {
                    // Filter movies and TV shows based on the selected genre
                    filterMoviesByGenre(selectedGenre);
                    filterTVShowsByGenre(selectedGenre);
                    filterGamesByGenre(selectedGenre);
                }
                rvMovies.setAdapter(movieAdapter);
                rvTvshows.setAdapter(tvShowAdapter);
                rvGames.setAdapter(gameAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default action or leave empty if no action is required
            }
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        movieList = new ArrayList<>();
        gameList = new ArrayList<>();
        tvShowList = new ArrayList<>();

        // Set RecyclerView Layout Managers
        rvMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGames.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTvshows.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set Adapters
        movieAdapter = new MovieAdapter(getContext(), movieList);
        gameAdapter = new GameAdapter(getContext(), gameList);
        tvShowAdapter = new TVShowAdapter(tvShowList, getContext());

        rvMovies.setAdapter(movieAdapter);
        rvGames.setAdapter(gameAdapter);
        rvTvshows.setAdapter(tvShowAdapter);

        // Load Data
        loadMovies();
        loadGames();
        loadTVShows();

        // Search Functionality
        searchBox.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        filterMovies(charSequence.toString());
                        filterGames(charSequence.toString());
                        filterTVShows(charSequence.toString());
                    }
                };
                handler.postDelayed(runnable, 500); // Wait 500ms after typing stops
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        // Initialize Accelerometer Sensor
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMovies() {
        db.collection("movies")
                .whereEqualTo("status", "approved")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            movieList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                movieList.add(doc.toObject(Movie.class));
            }
            movieAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTVShows() {
        db.collection("tv_shows")
                .whereEqualTo("status", "approved")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvShowList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                tvShowList.add(doc.toObject(TVShow.class));
            }
            tvShowAdapter.notifyDataSetChanged();
        });
    }

    // 🔹 Load Games
    @SuppressLint("NotifyDataSetChanged")
    private void loadGames() {
        db.collection("games")
                .whereEqualTo("status", "approved")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            gameList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                gameList.add(doc.toObject(Game.class));
            }
            gameAdapter.notifyDataSetChanged();
        });
    }

    private void filterMoviesByGenre(String genre) {
        List<Movie> filteredList = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.getMovie_genre().toLowerCase().contains(genre.toLowerCase()) || genre.equals("All")) {
                filteredList.add(movie);
            }
        }
        movieAdapter = new MovieAdapter(getContext(), filteredList);
        rvMovies.setAdapter(movieAdapter);
    }

    private void filterTVShowsByGenre(String genre) {
        List<TVShow> filteredList = new ArrayList<>();
        for (TVShow tvShow : tvShowList) {
            if (tvShow.getGenre().toLowerCase().contains(genre.toLowerCase()) || genre.equals("All")) {
                filteredList.add(tvShow);
            }
        }
        tvShowAdapter = new TVShowAdapter(filteredList, getContext());
        rvTvshows.setAdapter(tvShowAdapter);
    }

    private void filterGamesByGenre(String genre) {
        List<Game> filteredList = new ArrayList<>();
        for (Game game : gameList) {
            if (game.getGenre().toLowerCase().contains(genre.toLowerCase()) || genre.equals("All")) {
                filteredList.add(game);
            }
        }
        gameAdapter = new GameAdapter(getContext(), filteredList);
        rvGames.setAdapter(gameAdapter);
    }

    private void filterMovies(String text) {
        List<Movie> filteredList = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.getMovie_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(movie);
            }
        }
        movieAdapter = new MovieAdapter(getContext(), filteredList);
        rvMovies.setAdapter(movieAdapter);
    }

    private void filterTVShows(String text) {
        List<TVShow> filteredList = new ArrayList<>();
        for (TVShow tvShow : tvShowList) {
            if (tvShow.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(tvShow);
            }
        }
        tvShowAdapter = new TVShowAdapter(filteredList, getContext());
        rvTvshows.setAdapter(tvShowAdapter);
    }

    private void filterGames(String text) {
        List<Game> filteredList = new ArrayList<>();
        for (Game game : gameList) {
            if (game.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(game);
            }
        }
        gameAdapter = new GameAdapter(getContext(), filteredList);
        rvGames.setAdapter(gameAdapter);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];  // Left (-) and Right (+)
            float y = event.values[1];  // Forward (-) and Backward (+)

            if (Math.abs(x - lastX) > SCROLL_THRESHOLD) {
                if (x < lastX) {
                    // Tilted LEFT → Scroll RIGHT
                    scrollRecyclerViews(60);
                } else {
                    // Tilted RIGHT → Scroll LEFT
                    scrollRecyclerViews(-60);
                }
            }

            lastX = x;
            lastY = y;
            lastZ = event.values[2];
        }
    }

    private void scrollRecyclerViews(int dx) {
        rvMovies.smoothScrollBy(dx, 10);
        rvGames.smoothScrollBy(dx, 10);
        rvTvshows.smoothScrollBy(dx, 10);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
