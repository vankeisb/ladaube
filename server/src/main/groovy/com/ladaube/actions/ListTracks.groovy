package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.model.LaDaube
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaubeSession
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
    long startTime = System.currentTimeMillis()
    JsonUtil u = new JsonUtil()
    def tracks = LaDaube.get().doInSession { LaDaubeSession s ->
      if (playlistId) {
        def p = s.getPlaylist(playlistId)
        return s.getTracksInPlaylist(p)
      } else {
        def buddy = buddyId==null ? user : s.getUser(buddyId)
        boolean includeBuddies = buddy.username==user.username
        return s.getUserTracks(buddy, includeBuddies, query, start, limit, sort, dir)
      }
    }
    Long totalLen = tracks.count()
    long elapsed = System.currentTimeMillis() - startTime
    logger.debug("List tracks took $elapsed ms")
    return u.resolution(u.tracksToJson(tracks.toArray(), false, totalLen).toString())
  }

}