package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import org.json.JSONObject
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession
import org.json.JSONArray
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.StreamingResolution
import com.ladaube.util.JsonUtil

@UrlBinding("/albums/{album}")
class Albums extends BaseAction {

  String album

  @DefaultHandler
  Resolution display() {
    if (album) {
      // return tracks for this album
      def tracks = []
      LaDaube.doInSession { LaDaubeSession session ->
        session.getUserTracks(getUser(), true, null).each { t ->
          if (album && t.album == album) {
            tracks << t
          }
        }
      }
      JsonUtil u = new JsonUtil()
      return u.resolution(u.tracksToJson(tracks, false).toString())
    } else {
      // grab albums as json (naive implem)
      JSONObject result = new JSONObject()
      JSONArray jsonAlbums = new JSONArray()
      result.put("albums", jsonAlbums)
      def allAlbumNames = []
      LaDaube.doInSession { LaDaubeSession session ->
        session.getUserTracks(getUser(), true, null).each { t ->
          if (!allAlbumNames.contains(t.album)) {
            allAlbumNames << t.album
            JSONObject jsonAlbum = new JSONObject()
            jsonAlbum.put("name", t.album)
            jsonAlbum.put("artist", t.artist)
            jsonAlbums.put(jsonAlbum)
          }
        }
      }
      return new StreamingResolution("text/json", result.toString())
    }
  }

}
