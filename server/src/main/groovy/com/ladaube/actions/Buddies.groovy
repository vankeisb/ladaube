package com.ladaube.actions

import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.DefaultHandler
import com.ladaube.model.LaDaube
import net.sourceforge.stripes.action.UrlBinding
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.auth.RequiresAuthentication
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaubeSession

@UrlBinding('/buddies')
@RequiresAuthentication
public class Buddies extends BaseAction {
  
  @DefaultHandler
  @FatClientEvent(alternateResolution = 'listJson')
  Resolution list() {
    return new ForwardResolution('/WEB-INF/jsp/buddies.jsp')
  }

  Resolution listJson() {
    JsonUtil u = new JsonUtil()
    return u.resolution(u.buddiesToJson(buddies).toString())
  }

  def getBuddies() {
    return LaDaube.get().doInSession { LaDaubeSession s ->
      return s.getBuddies(user)
    }
  }

}