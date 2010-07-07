package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import com.ladaube.modelcouch.LaDaube
import com.ladaube.modelcouch.LaDaubeSession
import com.ladaube.util.JsonUtil
import com.ladaube.util.auth.RequiresAuthentication

@UrlBinding('/checkmd5')
@RequiresAuthentication
class CheckMD5 extends BaseAction {
  
  String md5

  @DefaultHandler
  Resolution check() {
    LaDaube.get().doInSession { LaDaubeSession s->
      boolean found = s.checkMD5(getUser(), md5)
      JsonUtil ju = new JsonUtil()
      String json = "{found:$found}"
      return ju.resolution(json);
    }
  }

}
