package com.gevkurg.twitterclient.models;


public class Media {
    private String type;
    private String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPhoto() {
        return "photo".equals(type);
    }
}
