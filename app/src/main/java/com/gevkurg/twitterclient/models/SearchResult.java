package com.gevkurg.twitterclient.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;

import java.util.List;

@Parcel(analyze = {SearchResult.class})
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    @JsonProperty("statuses")
    private List<Tweet> statuses;

    public void setStatuses(List<Tweet> statuses) {
        this.statuses = statuses;
    }

    public List<Tweet> getStatuses() {
        return statuses;
    }
}
