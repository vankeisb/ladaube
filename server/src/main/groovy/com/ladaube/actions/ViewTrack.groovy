package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.validation.Validate
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import com.ladaube.util.rpc.FatClientEvent
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

@UrlBinding('/track/{track}')
@RequiresAuthentication
public class ViewTrack extends BaseAction {

  @Validate(required=true)
  String track

  @DefaultHandler
  Resolution display() {
    JsonUtil u = new JsonUtil()
    def t = LaDaube.doInSession{ LaDaubeSession s ->
      return s.getTrackForUser(track, user)
    }
    if (t) {
      return u.resolution(u.trackToJson(t).toString())
    } else {
      return u.resolution(u.jsonError("Could not load track for the user").toString())
    }
  }

}