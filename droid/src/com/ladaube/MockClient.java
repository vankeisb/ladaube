package com.ladaube;

import java.util.*;

public class MockClient {

    private List<Album> albums = new ArrayList<Album>();
    private List<Artist> artists = new ArrayList<Artist>();
    private Map<Artist, List<Album>> albumsForArtist = new HashMap<Artist, List<Album>>();
    private List<Track> tracks = new ArrayList<Track>();
    private List<Playlist> playlists = new ArrayList<Playlist>();

    private MockClient() {
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

        tracks.add(new Track("1", "Track1", rootsAndGrooves.getName(), rootsAndGrooves.getArtist()));
        tracks.add(new Track("2", "Track2", rootsAndGrooves.getName(), rootsAndGrooves.getArtist()));
        tracks.add(new Track("4", "The Bug", callingElvis.getName(), callingElvis.getArtist()));

        Playlist groovyStuff = new Playlist("1", "Groovy Stuff");
        playlists.add(groovyStuff);
    }

    private static MockClient INSTANCE;

    public static MockClient getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new MockClient();
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
        return Collections.unmodifiableList(Arrays.asList(tracks.get(0), tracks.get(1)));
    }




}
