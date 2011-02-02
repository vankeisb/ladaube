package com.ladaube;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Client {

    private HttpClient httpClient;

    private List<Playlist> playlists;
    private List<Track> tracks;

    private Client() {
        reinit();
    }

    private String baseUrl = "http://9.128.98.31:8080/ladaube";

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSessionId() {
        return httpClient.getSessionId();
    }

    public void reinit() {
        httpClient = new HttpClient(baseUrl);
        try {
            // authenticate the user
            JSONObject resp = httpClient.jsonGet("/login?login=true&json=true&username=remi&password=remi");
            // TODO check response

            // pre-load playlists and tracks list (TO BE OPTIMIZED)
            JSONArray jsonPlaylists = httpClient.jsonGetArray("/playlists?json=true");
            playlists = new ArrayList<Playlist>();
            for (int i=0 ; i<jsonPlaylists.length() ; i++) {
                JSONObject jp = (JSONObject)jsonPlaylists.get(i);
                playlists.add(new Playlist(jp.getString("id"), jp.getString("name")));
            }

            // tracks
            tracks = new ArrayList<Track>();
            JSONObject jsonTracks = httpClient.jsonGet("/list?json=true");
            JSONArray data = jsonTracks.getJSONArray("data");
            for (int i=0 ; i<data.length() ; i++) {
                JSONObject jsonTrack = data.getJSONObject(i);
                tracks.add(new Track(
                        jsonTrack.getString("id"),
                        jsonTrack.getString("name"),
                        jsonTrack.getString("album"),
                        jsonTrack.getString("artist")));
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Client INSTANCE;

    public static Client getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    public List<Album> getAlbums(Artist artist) {
        Map<String,Album> albums = new HashMap<String,Album>();
        for (Track t : tracks) {
            String albumName = t.getAlbum();
            if (!albums.containsKey(albumName)) {
                boolean matches = true;
                if (artist!=null && artist.getName()!=null) {
                    matches = artist.getName().equals(t.getArtist());
                }
                if (matches) {
                    Album a = new Album(albumName, albumName, t.getArtist());
                    albums.put(albumName, a);
                }
            }
        }
        return new ArrayList<Album>(albums.values());
    }

    public List<Artist> getArtists() {
        Map<String,Artist> artists = new HashMap<String,Artist>();
        for (Track t : tracks) {
            String aName = t.getArtist();
            if (!artists.containsKey(aName)) {
                artists.put(aName, new Artist(aName, aName));
            }
        }
        return new ArrayList<Artist>(artists.values());
    }

    public List<Track> getTracks(Album album) {
        ArrayList<Track> result = new ArrayList<Track>();
        for (Track t : tracks) {
            if (album!=null) {
                String aName = album.getName();
                if (aName!=null && aName.equals(t.getAlbum())) {
                    result.add(t);
                }
            } else {
                result.add(t);
            }
        }
        return result;
    }

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    public List<Track> getTracksInPlaylist(Playlist playlist) {
        try {
            ArrayList<Track> plTracks = new ArrayList<Track>();
            JSONObject jsonTracks = httpClient.jsonGet("/list?json=true&playlistId=" + playlist.getId());
            JSONArray data = jsonTracks.getJSONArray("data");
            for (int i=0 ; i<data.length() ; i++) {
                JSONObject jsonTrack = data.getJSONObject(i);
                plTracks.add(new Track(
                        jsonTrack.getString("id"),
                        jsonTrack.getString("name"),
                        jsonTrack.getString("album"),
                        jsonTrack.getString("artist")));
            }
            return plTracks;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
