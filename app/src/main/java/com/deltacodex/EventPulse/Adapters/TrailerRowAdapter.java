package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.TrailerRowModel;

import java.util.List;

public class TrailerRowAdapter extends RecyclerView.Adapter<TrailerRowAdapter.TrailerRowViewHolder> {
    private final Context context;
    private final List<TrailerRowModel> trailerRowList;

    public TrailerRowAdapter(Context context, List<TrailerRowModel> trailerRowList) {
        this.context = context;
        this.trailerRowList = trailerRowList;
    }

    @NonNull
    @Override
    public TrailerRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trailer_row, parent, false);
        return new TrailerRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerRowViewHolder holder, int position) {
        TrailerRowModel row = trailerRowList.get(position);
        holder.trailerCategory.setText(row.getCategory());

        TrailerAdapter adapter = new TrailerAdapter(context, row.getTrailers());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return trailerRowList.size();
    }

    public static class TrailerRowViewHolder extends RecyclerView.ViewHolder {
        TextView trailerCategory;
        RecyclerView recyclerView;

        public TrailerRowViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerCategory = itemView.findViewById(R.id.trailerCategory);
            recyclerView = itemView.findViewById(R.id.recycler_view_horizontal);
        }
    }
}
