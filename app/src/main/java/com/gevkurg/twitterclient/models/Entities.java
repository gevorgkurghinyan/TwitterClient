package com.gevkurg.twitterclient.models;


import java.util.Collections;
import java.util.List;

public class Entities {
    private List<Media> media;

    public List<Media> getMedia() {
        return media == null ? Collections.<Media>emptyList() : media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }
}
