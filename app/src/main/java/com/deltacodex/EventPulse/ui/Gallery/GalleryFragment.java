package com.deltacodex.EventPulse.ui.Gallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deltacodex.EventPulse.Adapters.PosterAdapter;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.PosterModel;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private ProgressBar progressBar;
    private List<PosterModel> posterList;
    private PosterAdapter posterAdapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_posters);
        progressBar = view.findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();

        // RecyclerView Setup
        posterList = new ArrayList<>();
        posterAdapter = new PosterAdapter(getContext(), posterList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(posterAdapter);

        loadPosters();

        return view;
    }

    private void loadPosters() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("New_Poster").orderBy("timestamp_poster", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posterList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        PosterModel poster = doc.toObject(PosterModel.class);
                        posterList.add(poster);
                    }
                    posterAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load posters", Toast.LENGTH_SHORT).show();
                });
    }
}