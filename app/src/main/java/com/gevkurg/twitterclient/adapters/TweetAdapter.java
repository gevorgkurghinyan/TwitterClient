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
import com.gevkurg.twitterclient.R;
import com.gevkurg.twitterclient.activities.TweetDetailsActivity;
import com.gevkurg.twitterclient.models.Entities;
import com.gevkurg.twitterclient.models.Media;
import com.gevkurg.twitterclient.models.Tweet;

import org.parceler.Parcels;

import java.util.List;


public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private List<Tweet> tweets;
    Context context;

    public TweetAdapter (List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public Long getOldestTweetId() {
        return tweets.size() == 0 ? 1L : Long.valueOf(tweets.get(tweets.size()-1).getId());
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
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
        Tweet tweet = tweets.get(position);
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

        //setupMedia(holder.ivImageContent, tweet);
    }

    private void setupMedia(ImageView ivImageContent , Tweet tweet) {
        ivImageContent.setImageResource(0);
        ivImageContent.setVisibility(View.GONE);
        Entities entities = null; // tweet.getEntities();
        if (entities != null) {
            List<Media> mediaList = entities.getMedia();
            if(mediaList.size() > 0) {
                Glide.with(getContext())
                        .load(mediaList.get(0).getUrl())
                        .into(ivImageContent);
                ivImageContent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        ImageView ivProfileImage;
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
