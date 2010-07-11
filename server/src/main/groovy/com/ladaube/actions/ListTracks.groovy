package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.model.LaDaube
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaubeSession
import com.ladaube.modelcouch.User
import com.ladaube.modelcouch.Playlist
import org.apache.log4j.Logger

@UrlBinding('/list/{query}')
public class ListTracks extends BaseAction {

  private static final Logger logger = Logger.getLogger(ListTracks.class)

  String query
  String buddyId
  String playlistId

  String dir
  String sort
  Integer start
  Integer limit

  @DefaultHandler
  @FatClientEvent(alternateResolution = 'displayJson')
  Resolution display() {
    logger.debug("Listing tracks for $user.id")
    return new ForwardResolution('/WEB-INF/jsp/list.jsp')
  }

  Resolution displayJson() {
//    if (query==null && playlistId==null) {
//      Resolution res = null;
//      LaDaube.get().doInSession{ LaDaubeSession s ->
//        User buddy = buddyId==null ? user : s.getUser(buddyId)
//        boolean includeBuddies = buddy.id==user.id
//        InputStream is = s.getUserTracksStreamed(buddy, includeBuddies, start, limit, sort, dir);
//        res = new StreamingResolution('text/json', is);
//      }
//      return res;
//    } else {
      long startTime = System.currentTimeMillis()
      JsonUtil u = new JsonUtil()
      Long totalLen = null
      def tracks = []
      LaDaube.get().doInSession { LaDaubeSession s ->
        def it
        if (playlistId) {
          Playlist p = s.getPlaylist(playlistId)
          it = s.getTracksInPlaylist(p)
        } else {
          User buddy = buddyId==null ? user : s.getUser(buddyId)
          boolean includeBuddies = buddy.id==user.id
          it = s.getUserTracks(buddy, includeBuddies, query, start, limit, sort, dir)
        }
        totalLen = it.totalLength()
        while (it.hasNext()) {
          tracks << it.next()
        }
      }
      long elapsed = System.currentTimeMillis() - startTime
      logger.debug("List tracks took $elapsed ms")
      return u.resolution(u.tracksToJson(tracks, false, totalLen).toString())
//    }
  }


  def getTracks() {
    def tracks = []
    LaDaube.get().doInSession { LaDaubeSession s ->
      def it
      if (playlistId) {
        Playlist p = s.getPlaylist(playlistId)
        it = s.getTracksInPlaylist(p)
      } else {
        User buddy = buddyId==null ? user : s.getUser(buddyId)
        boolean includeBuddies = buddy.id==user.id
        it = s.getUserTracks(buddy, includeBuddies, query, start, limit, sort, dir)
      }
      while (it.hasNext()) {
        tracks << it.next()
      }      
    }
    return tracks
  }

}