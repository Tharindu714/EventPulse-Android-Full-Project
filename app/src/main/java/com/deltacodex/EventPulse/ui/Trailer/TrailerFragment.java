package com.deltacodex.EventPulse.ui.Trailer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.deltacodex.EventPulse.Adapters.TrailerAdapter;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.deltacodex.EventPulse.model.TrailerModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class TrailerFragment extends Fragment {
    private ProgressBar progressBar;
    private List<TrailerModel> trailerList;
    private TrailerAdapter trailerAdapter;
    private FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trailer, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_trailers_vertical);
        progressBar = view.findViewById(R.id.progress_bar_trailers);

        db = FirebaseFirestore.getInstance();

        // RecyclerView Setup
        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(getContext(), trailerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(trailerAdapter);

        loadTrailers();

        return view;
    }

    private void loadTrailers() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("New_Trailers")
                .orderBy("timestamp_trailer", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            trailerList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                trailerList.add(doc.toObject(TrailerModel.class));
            }
            trailerAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });
    }
}