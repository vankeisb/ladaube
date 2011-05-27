package com.ladaube.actions

import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.StreamingResolution
import net.sourceforge.stripes.action.UrlBinding
import org.json.JSONArray
import org.json.JSONObject
import com.ladaube.util.JsonUtil

@UrlBinding('/artists/{artist}')
class Artists extends BaseAction {

  String artist

  @DefaultHandler
  Resolution display() {
    if (artist) {
      // return tracks for this artist
      def tracks = []
      LaDaube.doInSession { LaDaubeSession session ->
        session.getUserTracks(getUser(), true, null).each { t ->
          if (artist && t.artist == artist) {
            tracks << t
          }
        }
      }
      JsonUtil u = new JsonUtil()
      return u.resolution(u.tracksToJson(tracks, false).toString())
    } else {
      // grab artists as json (naive implem)
      JSONArray jsonArtists = new JSONArray()
      def allArtistsNames = []
      LaDaube.doInSession { LaDaubeSession session ->
        session.getUserTracks(getUser(), true, null).each { t ->
          if (!allArtistsNames.contains(t.artist)) {
            allArtistsNames << t.artist
            JSONObject jsonArtist = new JSONObject()
            jsonArtist.put("name", t.artist)
            jsonArtists.put(jsonArtist)
          }
        }
      }
      return new StreamingResolution("text/json", jsonArtists.toString())
    }
  }
}