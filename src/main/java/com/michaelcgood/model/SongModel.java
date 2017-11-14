package com.michaelcgood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SongModel {
    @Id
    private String id;
    @Indexed
    private String artist;
    @Indexed
    private String songTitle;
    @Indexed
    private Boolean updated;
    
    public Boolean getUpdated() {
        return updated;
    }
    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getSongTitle() {
        return songTitle;
    }
    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @JsonCreator
    public SongModel(
        @JsonProperty("artist") String artist,
        @JsonProperty("song-title") String songTitle){
        this.artist = artist;
        this.songTitle = songTitle;
    }
 
    @Override
    public String toString() {
      return "Person [id=" + id + ", artist=" + artist + ", song-title=" + songTitle + "]";
    }

}
