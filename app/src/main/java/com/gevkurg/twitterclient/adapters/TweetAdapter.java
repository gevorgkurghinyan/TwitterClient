package com.gevkurg.twitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.models.Tweet;

import java.util.List;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> tweets;
    Context context;

    public TweetAdapter (List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public Long getOldestTweetId() {
        return tweets.size() == 0 ? 1L : tweets.get(tweets.size()-1).getId();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.tvUsername.setText(tweet.getUser().getScreenName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvFullName.setText(tweet.getUser().getName());
        holder.tvTweetedAt.setText(tweet.getCreatedAt());
        holder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
        holder.tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));

        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvUsername;
        TextView tvBody;
        TextView tvFullName;
        TextView tvTweetedAt;
        TextView tvRetweetCount;
        TextView tvFavoriteCount;

        ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvTweetedAt = itemView.findViewById(R.id.tvTweetedAt);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
        }
    }
}
