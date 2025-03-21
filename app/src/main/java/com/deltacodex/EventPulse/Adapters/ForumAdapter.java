package com.deltacodex.EventPulse.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deltacodex.EventPulse.R;
import com.deltacodex.EventPulse.SingleForumPostActivity;
import com.deltacodex.EventPulse.model.ForumPost;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {
    private final Context context;
    private final List<ForumPost> forumPostList;

    public ForumAdapter(Context context, List<ForumPost> forumPostList) {
        this.context = context;
        this.forumPostList = forumPostList;
    }

    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forum_post, parent, false);
        return new ForumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForumViewHolder holder, int position) {
        ForumPost post = forumPostList.get(position);

        if (post == null) return;

        // Set title, content, and other fields
        holder.postTitle.setText(post.getPostTitle() != null ? post.getPostTitle() : "No Title");
        holder.postContent.setText(post.getPostContent() != null ? post.getPostContent() : "No Content");

        // Format and display timestamp correctly
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDate = (post.getTimestamp() != null)
                ? sdf.format(post.getTimestamp().toDate())
                : "Unknown Date";
        holder.postDate.setText(formattedDate);

        // Show likes and comments count
        holder.postLikes.setText(post.getLikes() + " Likes");
        holder.postComments.setText(post.getComments() + " Comments");

        // Show like icon based on whether the post has likes or not
        if (post.getLikes() > 0) {
            holder.likeButton.setImageResource(R.drawable.ic_like_filled);  // Filled heart icon
        } else {
            holder.likeButton.setImageResource(R.drawable.ic_like);  // Empty heart icon
        }

        // Load Image (if available)
        if (post.getPostImageUrl() != null && !post.getPostImageUrl().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(post.getPostImageUrl()).into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        // Open Post on Click
        holder.itemView.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                Intent intent = new Intent(context, SingleForumPostActivity.class);
                intent.putExtra("postId", post.getPostId());  // Ensure postId is being passed here
                context.startActivity(intent);
            } else {
                Log.e("ForumAdapter", "Error: Post ID is null");
                Toast.makeText(context, "Error: Post ID is null", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return forumPostList.size();
    }

    public static class ForumViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postContent, postDate, postLikes, postComments;
        ImageView postImage;
        ImageButton likeButton;  // ImageButton for Like functionality

        public ForumViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postDate = itemView.findViewById(R.id.postDate);
            postLikes = itemView.findViewById(R.id.postLikes);
            postComments = itemView.findViewById(R.id.postComments);
            postImage = itemView.findViewById(R.id.postImage);
            likeButton = itemView.findViewById(R.id.likeButton);  // Initialize like button
        }
    }
}



