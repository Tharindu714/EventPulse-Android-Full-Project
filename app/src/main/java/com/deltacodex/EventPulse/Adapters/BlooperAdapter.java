package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.model.BlooperModel;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BlooperAdapter extends RecyclerView.Adapter<BlooperAdapter.BlooperViewHolder> {
    private final Context context;
    private final List<BlooperModel> blooperList;
    private BlooperViewHolder lastPlayingViewHolder = null;  // Track the last playing video

    public BlooperAdapter(Context context, List<BlooperModel> blooperList) {
        this.context = context;
        this.blooperList = blooperList;
    }

    @NonNull
    @Override
    public BlooperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blooper_item, parent, false);
        return new BlooperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlooperViewHolder holder, int position) {
        BlooperModel blooper = blooperList.get(position);

        // Ensure blooper is assigned to the ViewHolder
        holder.blooper = blooper;  // Added this line to ensure blooper is assigned

        //Set headline
        holder.Blooper_Headline.setText(blooper.getHeadline_blooper());

        //Convert timestamp to readable format
        if (blooper.getTimestamp_blooper() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
            holder.Blooper_Time.setText(sdf.format(blooper.getTimestamp_blooper().toDate()));
        } else {
            holder.Blooper_Time.setText("Timestamp missing");
        }

        //Load thumbnail initially
        Glide.with(context).load(blooper.getThumbnailUrl_blooper()).into(holder.Blooper_thumb);

        // Hide the WebView initially (show thumbnail)
        holder.Blooper_Webview.setVisibility(View.GONE);
        holder.Blooper_thumb.setVisibility(View.VISIBLE);

        // Play Video on Thumbnail Click
        holder.Blooper_thumb.setOnClickListener(v -> {
            if (lastPlayingViewHolder != null && lastPlayingViewHolder != holder) {
                //  Stop previous video and show its thumbnail again
                resetWebView(lastPlayingViewHolder.Blooper_Webview);
                lastPlayingViewHolder.Blooper_Webview.setVisibility(View.GONE);
                lastPlayingViewHolder.Blooper_thumb.setVisibility(View.VISIBLE);

                //  Null check for lastPlayingViewHolder.blooper before calling setPlaying
                if (lastPlayingViewHolder.blooper != null) {
                    lastPlayingViewHolder.blooper.setPlaying(false);
                }
            }

            //  Show WebView and Load Video
            holder.Blooper_thumb.setVisibility(View.GONE);
            holder.Blooper_Webview.setVisibility(View.VISIBLE);
            loadYouTubePreview(holder.Blooper_Webview, blooper.getVideoId_blooper());

            // Null check for blooper before calling setPlaying
            if (holder.blooper != null) {
                holder.blooper.setPlaying(true);
            }

            // Update last playing video
            lastPlayingViewHolder = holder;
        });
    }


    @Override
    public int getItemCount() {
        return blooperList.size();
    }

    public static class BlooperViewHolder extends RecyclerView.ViewHolder {
        WebView Blooper_Webview;
        ImageView Blooper_thumb;
        TextView Blooper_Headline, Blooper_Time;
        BlooperModel blooper;

        public BlooperViewHolder(@NonNull View itemView) {
            super(itemView);
            Blooper_Webview = itemView.findViewById(R.id.Blooper_Webview);
            Blooper_thumb = itemView.findViewById(R.id.Blooper_thumb);  // Added ImageView for Thumbnail
            Blooper_Headline = itemView.findViewById(R.id.Blooper_Headline);
            Blooper_Time = itemView.findViewById(R.id.Blooper_Time);
        }
    }

    private void loadYouTubePreview(WebView webView, String videoId) {
        if (videoId == null || videoId.isEmpty()) return;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        String embedUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&controls=1";
        String iframeHtml = "<html><body style='margin:0;padding:0;background-color:black;'>" +
                "<iframe width='100%' height='100%' src='" + embedUrl +
                "' frameborder='0' allowfullscreen></iframe></body></html>";

        webView.loadData(iframeHtml, "text/html", "utf-8");
    }

    private void resetWebView(WebView webView) {
        String blackScreenHtml = "<html><body style='background-color:black;'></body></html>";
        webView.loadData(blackScreenHtml, "text/html", "utf-8");
    }
}

