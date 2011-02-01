package com.ladaube;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {

    private final String id;
    private final String name;
    private final List<String> trackIds;

    public Playlist(String id, String name, List<String> trackIds) {
        this.id = id;
        this.name = name;
        this.trackIds = trackIds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getTrackIds() {
        return trackIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playlist playlist = (Playlist) o;

        if (id != null ? !id.equals(playlist.id) : playlist.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
