package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.modelcouch.LaDaube
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil
import net.sourceforge.stripes.validation.Validate
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.modelcouch.Playlist
import com.ladaube.modelcouch.Track
import com.ladaube.modelcouch.LaDaubeSession

@UrlBinding('/playlists')
@RequiresAuthentication
public class Playlists extends BaseAction {

  @Validate(required = true, on = ['tracks','addTracks','delete', 'removeTracks'])
  Playlist playlist

  @Validate(required=true, on=['addTracks', 'removeTracks'])
  List<Track> tracks

  @Validate(required=true, on=['createPlaylist'])
  String name

  @DefaultHandler
  @FatClientEvent(alternateResolution = 'jsonList')
  Resolution list() {
    return new ForwardResolution('/WEB-INF/jsp/playlists.jsp')
  }

  @FatClientEvent(alternateResolution = 'jsonTracks')
  Resolution tracks() {
    return new ForwardResolution('/WEB-INF/jsp/tracksInPlaylist.jsp')
  }

  @FatClientEvent(alternateResolution = 'jsonList')
  Resolution createPlaylist() {
    LaDaube.get().doInSession { LaDaubeSession s ->
      playlist = s.createPlaylist(user, name)
    }
    return new RedirectResolution('/playlists');
  }

  @FatClientEvent(alternateResolution = 'jsonTracks')
  Resolution addTracks() {
    LaDaube.get().doInSession{ LaDaubeSession s ->
      tracks.each { t->
        s.addTrackToPlaylist(t, playlist)
      }
    }
    return new RedirectResolution('/playlists').
            addParameter('playlist', playlist.id).
            addParameter('tracks', 'true')
  }

  @FatClientEvent(alternateResolution = 'jsonList')
  Resolution delete() {
    LaDaube.get().doInSession { LaDaubeSession s ->
      s.deletePlaylist(playlist)
    }
    return new RedirectResolution('/playlists')
  }

  @FatClientEvent(alternateResolution = 'jsonTracks')
  Resolution removeTracks() {
    LaDaube.get().doInSession { LaDaubeSession s ->
      tracks.each { Track t ->
        s.removeTrackFromPlaylist(t, playlist)
      }
    }
    return new RedirectResolution('/playlists').
            addParameter('playlist', playlist.id).
            addParameter('tracks', 'true')
  }

  Resolution jsonList() {
    JsonUtil u = new JsonUtil()
    return u.resolution(u.playlistsToJson(playlists).toString())
  }

  Resolution jsonTracks() {
    JsonUtil u = new JsonUtil()
    return u.resolution(u.tracksToJson(tracksInPlaylist, true).toString())
  }

  def getPlaylists() {
    def res = []
    LaDaube.get().doInSession { LaDaubeSession s ->
      def hits = s.getPlaylists(user)
      while (hits.hasNext()) {
        res << hits.next()
      }
    }
    return res
  }

  def getTracksInPlaylist() {
    def tracks = []
    LaDaube.get().doInSession {LaDaubeSession s ->
      def it = s.getTracksInPlaylist(playlist)
      while (it.hasNext()) {
        tracks << it.next()
      }
    }
    return tracks
  }

}