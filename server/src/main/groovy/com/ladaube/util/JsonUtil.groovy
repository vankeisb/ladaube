package com.ladaube.util

import org.json.JSONObject
import net.sourceforge.stripes.action.StreamingResolution
import org.json.JSONArray
import com.ladaube.modelcouch.User
import com.ladaube.modelcouch.Track
import com.ladaube.modelcouch.Playlist

public class JsonUtil {

  StreamingResolution resolution(String jsonData) {
    return new StreamingResolution('text/json', jsonData)
  }

  JSONObject userToJson(User u) {
    JSONObject o = new JSONObject()
    o.put('id', u.id)
    o.put('username', u.id)
    return o
  }

  JSONObject tracksToJson(List<Track> tracks, boolean includeSequence) {
    return tracksToJson(tracks, includeSequence, null)
  }

  JSONObject tracksToJson(List<Track> tracks, boolean includeSequence, Long totalCount) {
    JSONObject result = new JSONObject()
    JSONArray array = new JSONArray()
    result.put("data", array)
    int seq = 1
    tracks.each { Track t ->
      JSONObject json = trackToJson(t)
      if (includeSequence) {
        json.put('sequence', seq)
      }
      array.put(json)
      seq++
    }
    result.put("totalCount", totalCount==null ? seq-1 : totalCount)
    return result
  }

  JSONObject trackToJson(Track track) {
    JSONObject o = new JSONObject()
    o.put('id', track.id)
    o.put('name', track.name)
    o.put('artist', track.artist)
    o.put('albumArtist', track.albumArtist)
    o.put('album', track.album)
    o.put('composer', track.composer)
    o.put('year', track.year)
    o.put('genre', track.genre)
    o.put('trackNumber', track.trackNumber)
    o.put('userId', track.userId)
    o.put('postedOn', track.postedOn)
    return o
  }

  JSONArray buddiesToJson(List<User> buddies) {
    JSONArray a = new JSONArray()
    buddies.each { User u ->
      a.put(userToJson(u))
    }
    return a
  }

  JSONArray playlistsToJson(List<Playlist> playlists) {
    JSONArray a = new JSONArray()
    playlists.each { Playlist p ->
      a.put(playlistToJson(p))
    }
    return a
  }

  JSONObject playlistToJson(Playlist p) {
    JSONObject o = new JSONObject()
    o.put('id', p.id)
    o.put('name', p.name)
    return o
  }
}