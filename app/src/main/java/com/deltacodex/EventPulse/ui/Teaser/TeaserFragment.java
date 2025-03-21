package com.deltacodex.EventPulse.ui.Teaser;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.deltacodex.EventPulse.Adapters.TeaserAdapter;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.deltacodex.EventPulse.model.TeaserModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TeaserFragment extends Fragment {
    private ProgressBar progressBar;
    private List<TeaserModel> TeaserList;
    private TeaserAdapter TeaserAdapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teaser, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        // Initialize UI Components
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_movie);
        progressBar = view.findViewById(R.id.progress_bar);

        // Firestore Initialization
        db = FirebaseFirestore.getInstance();

        // RecyclerView Setup
        TeaserList = new ArrayList<>();
        TeaserAdapter = new TeaserAdapter(getContext(), TeaserList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(TeaserAdapter);
        loadTeasers();

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTeasers() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("New_Teasers").orderBy("timestamp_Teasers", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    TeaserList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        TeaserModel tease = doc.toObject(TeaserModel.class);
                        TeaserList.add(tease);
                    }
                    TeaserAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load Teaser", Toast.LENGTH_SHORT).show();
                });
    }
}