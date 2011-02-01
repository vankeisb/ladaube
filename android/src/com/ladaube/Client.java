package com.ladaube;

import java.util.*;

public class Client {

    private List<Album> albums = new ArrayList<Album>();
    private List<Artist> artists = new ArrayList<Artist>();
    private Map<Artist, List<Album>> albumsForArtist = new HashMap<Artist, List<Album>>();
    private List<Track> tracks = new ArrayList<Track>();
    private List<Playlist> playlists = new ArrayList<Playlist>();

    private Client() {
        Album rootsAndGrooves = new Album("1", "Roots and Grooves", "Maceo Parker");
        Album lifeOnPlanetGroove = new Album("2", "Life on planet Groove", "Maceo Parker");
        Album callingElvis = new Album("3", "Calling Elvis", "Dire Straits");
        albums.add(rootsAndGrooves);
        albums.add(lifeOnPlanetGroove);
        albums.add(callingElvis);

        Artist maceo = new Artist("1", "Maceo Parker");
        Artist direStraits = new Artist("2", "Dire Straits");
        artists.add(maceo);
        artists.add(direStraits);

        albumsForArtist.put(maceo, Arrays.asList(rootsAndGrooves, lifeOnPlanetGroove));
        albumsForArtist.put(direStraits, Arrays.asList(callingElvis));

        tracks.add(new Track("1", "Track1", rootsAndGrooves));
        tracks.add(new Track("2", "Track2", rootsAndGrooves));
        tracks.add(new Track("4", "The Bug", callingElvis));

        Playlist groovyStuff = new Playlist("1", "Groovy Stuff", Arrays.asList("1", "4"));
        playlists.add(groovyStuff);
    }

    private static Client INSTANCE;

    public static Client getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    public List<Album> getAlbums(Artist artist) {
        if (artist==null) {
            // return all albums
            return Collections.unmodifiableList(albums);
        } else {
            // return albums for passed artist
            List<Album> l = albumsForArtist.get(artist);
            if (l==null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(l);
        }
    }

    public List<Artist> getArtists() {
        return Collections.unmodifiableList(artists);
    }

    public List<Track> getTracks(Album album) {
        if (album==null) {
            // return all tracks
            return Collections.unmodifiableList(tracks);
        } else {
            // return tracks for album
            ArrayList<Track> filtered = new ArrayList<Track>();
            for (Track t : tracks) {
                if (t.getAlbum().equals(album.getName())) {
                    filtered.add(t);
                }
            }
            return Collections.unmodifiableList(filtered);

        }
    }

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);

    }

    public List<Track> getTracksInPlaylist(Playlist playlist) {
        List<String> ids = playlist.getTrackIds();
        ArrayList<Track> result = new ArrayList<Track>();
        for (String id:ids) {
            for (Track current : tracks) {
                if (current.getId().equals(id)) {
                    result.add(current);
                    break;
                }
            }
        }
        return result;
    }


}
