package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.util.auth.RequiresAuthentication
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

@UrlBinding('/booz')
@RequiresAuthentication
public class Booz extends BaseAction {

  Resolution display() {
    HttpServletRequest r = getContext().getRequest();
    String userAgent = r.getHeader( "User-Agent" );
    boolean isIphone = userAgent != null && userAgent.indexOf("iPhone") != -1;
    if (!isIphone) {
      isIphone = r.getParameter("iphone") != null
    }
    if (isIphone) {
      return new ForwardResolution('/WEB-INF/jsp/booz-iphone.jsp')
    }
    return new ForwardResolution('/WEB-INF/jsp/booz.jsp')
  }


  def getUserTracksByAlbum() {
    LaDaube.doInSession { LaDaubeSession s ->
      return s.getUserTracks(user, true, null, null, null, "album", "ASC")
    }
  }

  def getUserTracksByArtist() {
    LaDaube.doInSession { LaDaubeSession s ->
      return s.getUserTracks(user, true, null, null, null, "artist", "ASC")
    }
  }

}