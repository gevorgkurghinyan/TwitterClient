package com.gevkurg.twitterclient.models;

import com.gevkurg.twitterclient.helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;


@Parcel
public class Tweet {
    private String id;
    private String body;
    private User user;
    private String createdAt;
    private int retweetCount;
    private int favoriteCount;
    //private Entities entities;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRelativeCreatedAt() {
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /*
    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }
    */

    public static Tweet fromJson(JSONObject json) {
        Tweet tweet = new Tweet();
        try {
            tweet.id = json.getString("id_str");
            tweet.body = json.getString("text");
            tweet.createdAt = json.getString("created_at");
            tweet.retweetCount = json.getInt("retweet_count");
            tweet.user = User.fromJSON(json.getJSONObject("user"));
            tweet.favoriteCount = json.getInt("favorite_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray json) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(json.length());
        for (int i = 0; i < json.length(); i++) {
            try {
                Tweet tweet = fromJson(json.getJSONObject(i));
                if (tweet != null) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }
        return tweets;
    }
}
