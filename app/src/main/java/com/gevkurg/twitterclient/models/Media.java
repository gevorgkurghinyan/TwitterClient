package com.gevkurg.twitterclient.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcel(analyze = {Media.class})
public class Media {
    @JsonProperty("expanded_url")
    private String expandedUrl;
    private String url;
    @JsonProperty("display_url")
    private String displayUrl;

    public String getExpandedUrl() {
        return expandedUrl;
    }

    public void setExpandedUrl(String expandedUrl) {
        this.expandedUrl = expandedUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }
}
