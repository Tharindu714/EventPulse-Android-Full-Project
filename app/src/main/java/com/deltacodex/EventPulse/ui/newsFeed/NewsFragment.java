package com.deltacodex.EventPulse.ui.newsFeed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.deltacodex.EventPulse.Adapters.NewsAdapter;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.NewsModel;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class NewsFragment extends Fragment {
    private NewsAdapter newsAdapter;
    private List<NewsModel> newsList;

    private FirebaseFirestore db;
    private CollectionReference newsCollection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        RecyclerView newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(getContext(), newsList);
        newsRecyclerView.setAdapter(newsAdapter);

        // Initialize Firestore instance
        db = FirebaseFirestore.getInstance();
        newsCollection = db.collection("news_updates");
        loadNewsData();

        return view;
    }

    private void loadNewsData() {
        newsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                newsList.clear();
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    NewsModel news = snapshot.toObject(NewsModel.class);

                    // Ensure timestamp is stored correctly as LocalDateTime
                    if (snapshot.contains("timestamp")) {
                        Timestamp timestamp = snapshot.getTimestamp("timestamp");
                        if (timestamp != null) {
                            LocalDateTime dateTime = timestamp.toDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();

                            // Format date (Example: Feb 06, 2025 - 14:30)
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
                            String formattedDate = dateTime.format(formatter);
                            news.setFormattedTimestamp(formattedDate);  // Store formatted date
                        }
                    }
                    newsList.add(news);
                }
                newsAdapter.notifyDataSetChanged();
            }
        });
    }
}

