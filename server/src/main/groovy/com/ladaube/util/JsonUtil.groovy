package com.ladaube.util

import org.json.JSONObject
import net.sourceforge.stripes.action.StreamingResolution
import org.json.JSONArray

public class JsonUtil {

  StreamingResolution resolution(String jsonData) {
    return new StreamingResolution('text/json', jsonData)
  }

  JSONObject userToJson(def u) {
    JSONObject o = new JSONObject()
    o.put('id', u.username)
    o.put('username', u.username)
    return o
  }

  JSONObject tracksToJson(def tracks, boolean includeSequence) {
    return tracksToJson(tracks, includeSequence, null)
  }

  JSONObject tracksToJson(def tracks, boolean includeSequence, Long totalCount) {
    JSONObject result = new JSONObject()
    JSONArray array = new JSONArray()
    result.put("data", array)
    int seq = 1
    tracks.each { t ->
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

  JSONObject trackToJson(def track) {
    JSONObject o = new JSONObject()
    o.put('id', track._id)
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

  JSONArray buddiesToJson(def buddies) {
    JSONArray a = new JSONArray()
    buddies.each { u ->
      a.put(userToJson(u))
    }
    return a
  }

  JSONArray playlistsToJson(def playlists) {
    JSONArray a = new JSONArray()
    playlists.each { p ->
      a.put(playlistToJson(p))
    }
    return a
  }

  JSONObject playlistToJson(def p) {
    JSONObject o = new JSONObject()
    o.put('id', p._id)
    o.put('name', p.name)
    return o
  }
}