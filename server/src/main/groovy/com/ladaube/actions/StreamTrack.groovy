package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.validation.Validate
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.StreamingResolution
import com.ladaube.model.LaDaube
import net.sourceforge.stripes.util.Log
import com.ladaube.model.LaDaubeSession
import com.ladaube.util.LaDaubeHttpSessionListener
import javax.servlet.http.HttpSession

@UrlBinding('/stream/{track}/{jsessionid}')
class StreamTrack extends BaseAction {

  private static final Log logger = Log.getInstance(StreamTrack.class);

  @Validate(required=true)
  String track

  String jsessionid

  @DefaultHandler
  Resolution stream() {

    def u = getUser()
    if (u==null && jsessionid!=null) {
      HttpSession s = LaDaubeHttpSessionListener.getSession(jsessionid)
      u = getUser(s);
    }
    if (u==null) {
      throw new IllegalStateException("Unable to find a user for session")
    }
    return LaDaube.doInSession { LaDaubeSession s->
      def t = s.getTrackForUser(track, u)
      if (t) {
        s.db.stats_downloads << [userId: u._id, date: new Date(), trackId: t._id]
        logger.debug("Streaming track $t._id")
	      def trackInfos = s.getTrackInfos(t)
        return new StreamingResolution("audio/mpeg", trackInfos.is).setFilename(t.name + ".mp3").setLength(trackInfos.len)
      } else {
        logger.warn("unable to get track $track for user ${u.username}")
        return null
      }
    }
  }

}