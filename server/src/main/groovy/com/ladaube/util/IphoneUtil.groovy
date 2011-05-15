package com.ladaube.util

import javax.servlet.http.HttpServletRequest

class IphoneUtil {

  static boolean isIphone(HttpServletRequest r) {
    String userAgent = r.getHeader( "User-Agent" );
    boolean isIphone = userAgent != null && userAgent.indexOf("iPhone") != -1;
    if (!isIphone) {
      isIphone = r.getParameter("iphone") != null
    }
    return isIphone
  }

}
