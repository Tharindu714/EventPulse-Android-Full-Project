package com.deltacodex.EventPulse.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.GalleryActivity;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.PosterModel;

import java.util.List;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    private final Context context;
    private final List<PosterModel> posterList;

    public PosterAdapter(Context context, List<PosterModel> posterList) {
        this.context = context;
        this.posterList = posterList;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_poster, parent, false);
        return new PosterViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        PosterModel poster = posterList.get(position);


        Glide.with(context).load(poster.getImageUrl_poster()).into(holder.posterImage);

        holder.posterHeadline.setText(poster.getHeadline_poster());

        holder.posterHeadline.setVisibility(View.GONE);

        holder.posterImage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                holder.posterHeadline.setVisibility(View.VISIBLE);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                holder.posterHeadline.setVisibility(View.GONE);
                holder.posterImage.performClick();
            }
            return true;
        });

        holder.posterImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, GalleryActivity.class);
            intent.putExtra("imageUrl_poster", poster.getImageUrl_poster());
            intent.putExtra("headline_poster", poster.getHeadline_poster());
            intent.putExtra("description_poster", poster.getDescription_poster());
            context.startActivity(intent);
        });
        holder.posterHeadline.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return posterList.size();
    }

    public static class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView posterHeadline;

        public PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.poster_image);
            posterHeadline = itemView.findViewById(R.id.poster_headline);
        }
    }
}

