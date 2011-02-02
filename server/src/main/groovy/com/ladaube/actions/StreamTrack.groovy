package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.validation.Validate
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.StreamingResolution
import com.ladaube.model.LaDaube
import javax.servlet.http.HttpServletResponse
import net.sourceforge.stripes.util.Log
import com.ladaube.model.LaDaubeSession
import com.ladaube.util.LaDaubeHttpSessionListener
import javax.servlet.http.HttpSession

@UrlBinding('/stream/{track}')
class StreamTrack extends BaseAction {

  private static final Log logger = Log.getInstance(StreamTrack.class);

  @Validate(required=true)
  String track

  String jsessionid

  @DefaultHandler
  Resolution stream() {
    def u = getUser();
    if (u==null && jsessionid!=null) {
      HttpSession s = LaDaubeHttpSessionListener.getSession(jsessionid);
      u = getUser(s);
    }
    if (u==null) {
      throw new IllegalStateException("Unable to find a user for session")
    }
    return LaDaube.doInSession { s->
      def t =s.getTrack(track)
      if (t) {
        s.db.stats_downloads << [userId: u._id, date: new Date(), trackId: t._id]
        logger.debug("Streaming track $t._id")
        return new TrackResolution(t)
      }
      return null
    }
  }

}

class TrackResolution extends StreamingResolution {

  private def t

  def TrackResolution(def t) {
    super('audio/mpeg')
    this.t = t
    setFilename(t.name + '.mp3')
  }

  void stream(HttpServletResponse response) throws Exception {
     LaDaube.doInSession { LaDaubeSession s ->
      s.writeTrackDataToStream(t, response.getOutputStream())
     }
  }

}