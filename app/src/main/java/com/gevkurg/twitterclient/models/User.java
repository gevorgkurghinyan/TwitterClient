package com.gevkurg.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;


@Parcel
public class User {

    private long id;
    private String name;
    private String screenName;
    private String profileImageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String userProfileImage) {
        this.profileImageUrl = userProfileImage;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String userScreenName) {
        this.screenName = userScreenName;
    }

    public static User fromJSON (JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name = jsonObject.getString("name");
        user.id = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");

        return user;
    }
}
