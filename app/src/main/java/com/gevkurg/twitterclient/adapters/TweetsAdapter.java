package com.gevkurg.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.TwitterApplication;
import com.gevkurg.twitterclient.activities.ProfileActivity;
import com.gevkurg.twitterclient.activities.TweetDetailsActivity;
import com.gevkurg.twitterclient.models.Entities;
import com.gevkurg.twitterclient.models.Media;
import com.gevkurg.twitterclient.models.Tweet;
import com.gevkurg.twitterclient.network.TwitterClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private List<Tweet> tweets;
    private Context context;
    private TwitterClient client = TwitterApplication.getRestClient();

    public TweetsAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public String getOldestTweetId() {
        return tweets.size() == 0 ? null : tweets.get(tweets.size() - 1).getId();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> tweets) {

        for (Tweet t : tweets) {
            if (!this.tweets.contains(t)) {
                this.tweets.add(t);
            }
        }
    }

    private Context getContext() {
        return context;
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
        final Tweet tweet = tweets.get(position);
        holder.tvUsername.setText("@" + tweet.getUser().getScreenName());
        holder.tvBody.setText(tweet.getBody());
        holder.tvFullName.setText(tweet.getUser().getName());
        holder.tvTweetedAt.setText(tweet.getRelativeCreatedAt());
        holder.tvRetweetCount.setText(Integer.toString(tweet.getRetweetCount()));
        holder.tvFavoriteCount.setText(Integer.toString(tweet.getFavoriteCount()));

        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivProfileImage);

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("username", tweet.getUser().getScreenName());
                getContext().startActivity(i);
            }
        });

        final ViewHolder viewHolder = holder;
        setupRetweetButton(tweet, viewHolder);
        setupFavoriteButton(tweet, viewHolder);

        //setupMedia(holder.ivImageContent, tweet);
    }

    private void setupMedia(ImageView ivImageContent, Tweet tweet) {
        ivImageContent.setImageResource(0);
        ivImageContent.setVisibility(View.GONE);
        Entities entities = null; // tweet.getEntities();
        if (entities != null) {
            List<Media> mediaList = entities.getMedia();
            if (mediaList.size() > 0) {
                Glide.with(getContext())
                        .load(mediaList.get(0).getUrl())
                        .into(ivImageContent);
                ivImageContent.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupRetweetButton(final Tweet tweet, final ViewHolder viewHolder) {
        final long tweetId = Long.valueOf(tweet.getId());
        if (tweet.isRetweeted()) {
            viewHolder.ivRetweet.setImageResource(R.drawable.icon_retweet_done_24);
        } else {
            viewHolder.ivRetweet.setImageResource(R.drawable.icon_retweet_24);
            viewHolder.ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    client.retweet(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet retweet = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {
                                });
                                viewHolder.tvRetweetCount.setText(Integer.toString(retweet.getRetweetCount()));
                                viewHolder.ivRetweet.setImageResource(R.drawable.icon_retweet_done_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                }
            });
        }
    }

    private void setupFavoriteButton(final Tweet tweet, final ViewHolder viewHolder) {
        final long tweetId = Long.valueOf(tweet.getId());
        if (tweet.isFavorited()) {
            viewHolder.ivFavorite.setImageResource(R.drawable.icon_favorite_done_24);
        } else {
            viewHolder.ivFavorite.setImageResource(R.drawable.icon_favorite_24);
        }

        viewHolder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!tweet.isFavorited()) {
                    client.favorite(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet t = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {});
                                viewHolder.tvFavoriteCount.setText(Integer.toString(t.getFavoriteCount()));
                                viewHolder.ivFavorite.setImageResource(R.drawable.icon_favorite_done_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                } else {
                    client.unFavorite(tweetId, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                ObjectMapper objectMapper = new ObjectMapper();
                                Tweet t = objectMapper.readValue(responseBody, new TypeReference<Tweet>() {});
                                viewHolder.tvFavoriteCount.setText(Integer.toString(t.getFavoriteCount()));
                                viewHolder.ivFavorite.setImageResource(R.drawable.icon_favorite_24);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            error.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        ImageView ivRetweet;
        ImageView ivFavorite;
        ImageView ivReply;
        //ImageView ivImageContent;
        TextView tvUsername;
        TextView tvBody;
        TextView tvFullName;
        TextView tvTweetedAt;
        TextView tvRetweetCount;
        TextView tvFavoriteCount;

        ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            ivReply = itemView.findViewById(R.id.ivReply);
            //ivImageContent = itemView.findViewById(R.id.ivImageContent);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvTweetedAt = itemView.findViewById(R.id.tvTweetedAt);
            tvRetweetCount = itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Tweet tweet = tweets.get(position);

                Intent intent = new Intent(getContext(), TweetDetailsActivity.class);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                getContext().startActivity(intent);
            }
        }
    }
}
