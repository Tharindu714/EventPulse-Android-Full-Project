package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deltacodex.EventPulse.NewsDetailActivity;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.NewsModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final Context context;
    private final List<NewsModel> newsList;

    public NewsAdapter(Context context, List<NewsModel> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsModel news = newsList.get(position);

        // Load image using Glide
        Picasso.get().load(news.getImageUrl()).into(holder.newsImage);
        holder.newsHeadline.setText(news.getHeadline());
        holder.newsSummary.setText(news.getDescription());
        holder.newsTimestamp.setText(news.getFormattedTimestamp() != null ? news.getFormattedTimestamp() : "Unknown Date");
        holder.seeMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("headline", news.getHeadline());
            intent.putExtra("imageUrl", news.getImageUrl());
            intent.putExtra("description", news.getDescription());
            intent.putExtra("timestamp", holder.newsTimestamp.getText().toString());
            context.startActivity(intent);
        });
    }



    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImage;
        TextView newsHeadline, newsSummary,newsTimestamp;
        Button seeMoreButton;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsHeadline = itemView.findViewById(R.id.newsHeadline);
            newsSummary = itemView.findViewById(R.id.newsSummary);
            newsTimestamp = itemView.findViewById(R.id.newsTime);
            seeMoreButton = itemView.findViewById(R.id.seeMoreButton);
        }
    }
}
