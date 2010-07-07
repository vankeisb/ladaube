package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.validation.Validate
import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import com.ladaube.util.rpc.FatClientEvent
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.util.JsonUtil
import com.ladaube.modelcouch.Track

@UrlBinding('/track/{track}')
@RequiresAuthentication
public class ViewTrack extends BaseAction {

  @Validate(required=true)
  Track track

  @DefaultHandler
  @FatClientEvent(alternateResolution = 'displayJson')
  Resolution display() {
    return new ForwardResolution('/WEB-INF/jsp/track.jsp')
  }

  Resolution displayJson() {
    JsonUtil u = new JsonUtil()
    return u.resolution(u.trackToJson(track).toString())
  }

}