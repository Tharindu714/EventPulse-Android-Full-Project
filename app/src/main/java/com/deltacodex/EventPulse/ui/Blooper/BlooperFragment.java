package com.deltacodex.EventPulse.ui.Blooper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deltacodex.EventPulse.Adapters.BlooperAdapter;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.BlooperModel;
import com.deltacodex.EventPulse.Utils.StatusBarUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class BlooperFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<BlooperModel> blooperList;
    private BlooperAdapter blooperAdapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blooper, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        // Initialize UI Components
        recyclerView = view.findViewById(R.id.recycler_view_movie);
        progressBar = view.findViewById(R.id.progress_bar);

        // Firestore Initialization
        db = FirebaseFirestore.getInstance();

        // RecyclerView Setup
        blooperList = new ArrayList<>();
        blooperAdapter = new BlooperAdapter(getContext(), blooperList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(blooperAdapter);
        loadBloopers();

        return view;
    }

    private void loadBloopers() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("New_Bloopers").orderBy("timestamp_blooper", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    blooperList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        BlooperModel blooper = doc.toObject(BlooperModel.class);
                        blooperList.add(blooper);
                    }
                    blooperAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Failed to load bloopers", Toast.LENGTH_SHORT).show();
                });
    }
}
