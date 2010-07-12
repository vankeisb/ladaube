package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.model.LaDaube
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil
import net.sourceforge.stripes.validation.Validate
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.model.LaDaubeSession

@UrlBinding('/playlists')
@RequiresAuthentication
public class Playlists extends BaseAction {

  def playlist

  @Validate(required = true, on = ['tracks','addTracks','delete', 'removeTracks'])
  String playlistId

  @Validate(required=true, on=['addTracks', 'removeTracks'])
  List<String> tracks

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
      playlist = s.getPlaylist(playlistId)
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
      playlist = s.getPlaylist(playlistId)       
      s.deletePlaylist(playlist)
    }
    return new RedirectResolution('/playlists')
  }

  @FatClientEvent(alternateResolution = 'jsonTracks')
  Resolution removeTracks() {
    LaDaube.get().doInSession { LaDaubeSession s ->
      playlist = s.getPlaylist(playlistId)      
      tracks.each { String tId ->
        def t = s.getTrack(tId)
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
      return s.getPlaylists(user)
    }
  }

  def getTracksInPlaylist() {
    def tracks = []
    LaDaube.get().doInSession {LaDaubeSession s ->
      return s.getTracksInPlaylist(playlist)
    }
  }

}