package com.michaelcgood.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class UpdatedSong {
    
    @Id
    private String id;
    @Indexed
    private String artist;
    @Indexed
    private String songTitle;
    @Indexed
    private String html;
    @Indexed
    private String sid;
    
    public String getSid() {
        return sid;
    }
    public void setSid(String sid) {
        this.sid = sid;
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getHtml() {
        return html;
    }
    public void setHtml(String html) {
        this.html = html;
    }

}
