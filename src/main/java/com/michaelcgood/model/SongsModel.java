package com.michaelcgood.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SongsModel {

    @JsonProperty("song")
    private List<SongModel> song;

    public List<SongModel> getSong() {
        return song;
    }

    public void setSong(List<SongModel> song) {
        this.song = song;
    }
}
