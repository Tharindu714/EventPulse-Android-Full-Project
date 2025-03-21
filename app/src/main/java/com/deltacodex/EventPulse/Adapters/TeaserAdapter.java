package com.deltacodex.EventPulse.Adapters;

import android.annotation.SuppressLint;
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
import com.deltacodex.EventPulse.model.TeaserModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TeaserAdapter extends RecyclerView.Adapter<TeaserAdapter.TeaserViewHolder>{
    private final Context context;
    private final List<TeaserModel> TeaserList;
    private TeaserAdapter.TeaserViewHolder lastPlayingViewHolder = null;  // Track the last playing video

    public TeaserAdapter(Context context, List<TeaserModel> TeaserList) {
        this.context = context;
        this.TeaserList = TeaserList;
    }

    @NonNull
    @Override
    public TeaserAdapter.TeaserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teaser_item, parent, false);
        return new TeaserAdapter.TeaserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeaserAdapter.TeaserViewHolder holder, int position) {
        TeaserModel teaser = TeaserList.get(position);

        // Ensure Teaser is assigned to the ViewHolder
        holder.teaser = teaser;

        //  Set headline
        holder.Teaser_Headline.setText(teaser.getHeadline_Teasers());

        //  Convert timestamp to readable format
        if (teaser.getTimestamp_Teasers() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
            holder.Teaser_Time.setText(sdf.format(teaser.getTimestamp_Teasers().toDate()));
        } else {
            holder.Teaser_Time.setText("");
        }

        //  Load thumbnail initially
        Glide.with(context).load(teaser.getThumbnailUrl_Teasers()).into(holder.Teaser_thumb);

        //  Hide the WebView initially (show thumbnail)
        holder.Teaser_Webview.setVisibility(View.GONE);
        holder.Teaser_thumb.setVisibility(View.VISIBLE);

        //  Play Video on Thumbnail Click
        holder.Teaser_thumb.setOnClickListener(v -> {
            if (lastPlayingViewHolder != null && lastPlayingViewHolder != holder) {
                //  Stop previous video and show its thumbnail again
                resetWebView(lastPlayingViewHolder.Teaser_Webview);
                lastPlayingViewHolder.Teaser_Webview.setVisibility(View.GONE); // Hide WebView
                lastPlayingViewHolder.Teaser_thumb.setVisibility(View.VISIBLE); // Show Thumbnail

                //  Null check for lastPlayingViewHolder.teaser before calling setPlaying
                if (lastPlayingViewHolder.teaser != null) {
                    lastPlayingViewHolder.teaser.setPlaying(false);
                }
            }

            //  Show WebView and Load Video
            holder.Teaser_thumb.setVisibility(View.GONE); // Hide thumbnail
            holder.Teaser_Webview.setVisibility(View.VISIBLE); // Show WebView
            loadYouTubePreview(holder.Teaser_Webview, teaser.getVideoId_Teasers());

            //  Null check for teaser before calling setPlaying
            if (holder.teaser != null) {
                holder.teaser.setPlaying(true);
            }

            //  Update last playing video
            lastPlayingViewHolder = holder;
        });
    }



    @Override
    public int getItemCount() {
        return TeaserList.size();
    }

    public static class TeaserViewHolder extends RecyclerView.ViewHolder {
        WebView Teaser_Webview;
        ImageView Teaser_thumb;
        TextView Teaser_Headline, Teaser_Time;
        TeaserModel teaser;

        public TeaserViewHolder(@NonNull View itemView) {
            super(itemView);
            Teaser_Webview = itemView.findViewById(R.id.Teaser_Webview);
            Teaser_thumb = itemView.findViewById(R.id.Teaser_thumb);  //  Added ImageView for Thumbnail
            Teaser_Headline = itemView.findViewById(R.id.Teaser_Headline);
            Teaser_Time = itemView.findViewById(R.id.Teaser_Time);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
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
