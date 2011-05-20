package com.ladaube;

import java.io.Serializable;

public class Track implements Serializable {

    private final String id;
    private final String name;
    private final String album;
    private final String artist;

    public Track(String id, String name, String album, String artist) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (id != null ? !id.equals(track.id) : track.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
