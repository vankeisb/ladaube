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

@UrlBinding('/stream/{track}')
@RequiresAuthentication
class StreamTrack extends BaseAction {

  private static final Log logger = Log.getInstance(StreamTrack.class);

  @Validate(required=true)
  String track

  @DefaultHandler
  Resolution stream() {
    def t = LaDaube.get().doInSession { s->
      return s.getTrack(track)
    }
    logger.debug("Streaming track $t._id")
    return new TrackResolution(t)
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
     LaDaube.get().doInSession { LaDaubeSession s ->
      s.writeTrackDataToStream(t, response.getOutputStream())
     }
  }

}