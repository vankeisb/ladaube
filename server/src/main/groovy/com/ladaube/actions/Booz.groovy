package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.util.auth.RequiresAuthentication

@UrlBinding('/booz')
@RequiresAuthentication
public class Booz extends BaseAction {

  Resolution display() {
    return new ForwardResolution('/WEB-INF/jsp/booz.jsp')
  }

}