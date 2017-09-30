package com.gevkurg.twitterclient.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcel(analyze = {Entities.class})
public class Entities {
    @JsonProperty("urls")
    private List<Media> media;

    public List<Media> getMedia() {
        return media == null ? Collections.<Media>emptyList() : media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }
}