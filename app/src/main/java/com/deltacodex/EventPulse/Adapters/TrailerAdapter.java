package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.SingleTrailerViewActivity;
import com.deltacodex.EventPulse.model.TrailerModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private final Context context;
    private final List<TrailerModel> trailerList;

    public TrailerAdapter(Context context, List<TrailerModel> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        TrailerModel trailer = trailerList.get(position);

        // Load Thumbnail Image
        Glide.with(context).load(trailer.getThumbnailUrl_trailer()).into(holder.trailerThumbnail);

        // Set Trailer Headline
        holder.trailerHeadline.setText(trailer.getHeadline_trailer());

        // Open Single Trailer View on Click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SingleTrailerViewActivity.class);
            intent.putExtra("videoUrl_trailer", extractYouTubeVideoId(trailer.getVideoUrl_trailer()));
            intent.putExtra("headline_trailer", trailer.getHeadline_trailer());
            intent.putExtra("description_trailer", trailer.getDescription_trailer());
            intent.putExtra("releaseDate_trailer", trailer.getReleaseDate_trailer());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView trailerThumbnail;
        TextView trailerHeadline;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerThumbnail = itemView.findViewById(R.id.trailer_thumbnail);
            trailerHeadline = itemView.findViewById(R.id.trailer_headline);
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


