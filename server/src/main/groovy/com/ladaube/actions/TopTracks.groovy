package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

@UrlBinding("/topTracks")
@RequiresAuthentication
class TopTracks extends BaseAction {

  @DefaultHandler
  Resolution display() {
    return new ForwardResolution("/WEB-INF/jsp/stats/topTracks.jsp")
  }

  def getTracksAndCounts() {
    def res = []
    LaDaube.doInSession { LaDaubeSession s ->
      s.getTopTracks { t,c,i ->
        res.add([
            track: t,
            count: c.intValue(),
            index: i
        ]);
        return false
      }
    }
    return res
  }


}
