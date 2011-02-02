package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.util.auth.RequiresAuthentication
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Cookie

@UrlBinding('/booz')
@RequiresAuthentication
public class Booz extends BaseAction {

  Resolution display() {
    HttpServletRequest r = getContext().getRequest();
    Cookie[] cookies = r.getCookies();
    System.out.println("Cookies : ");
    for (Cookie c : cookies) {
      System.out.println(" -> " + c.getName() + ":" + c.getValue());
    }
    return new ForwardResolution('/WEB-INF/jsp/booz.jsp')
  }

}