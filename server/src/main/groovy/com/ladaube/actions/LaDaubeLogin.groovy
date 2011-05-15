package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import com.ladaube.model.LaDaube
import net.sourceforge.stripes.action.Resolution
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil

import javax.servlet.http.HttpSession
import com.ladaube.util.auth.AuthConstants
import net.sourceforge.stripes.action.DontValidate
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.model.LaDaubeSession
import org.apache.log4j.Logger
import com.ladaube.util.auth.LoginActionBean
import javax.servlet.http.HttpServletRequest
import com.ladaube.util.IphoneUtil

@UrlBinding('/login')
public class LaDaubeLogin extends LoginActionBean {

  private static final Logger logger = Logger.getLogger(LaDaubeLogin.class)

  @Override
  protected String getJspPath() {
    if (IphoneUtil.isIphone(context.request)) {
      return '/WEB-INF/auth/login-iphone.jsp'
    }
    return super.getJspPath()
  }

  protected def authenticate() {
    def ip = getContext().getRequest().getRemoteAddr()
    return LaDaube.doInSession { LaDaubeSession s ->
      def u
      try {
        logger.info("Trying to authenticate $username")
        u = s.getUser(username);
      } catch(Exception e) {
        logger.warn("Exception at login !", e)
        s.db.stats_authentications << [username:username,date:new Date(), code:1, addr:ip]
        return null
      }
      if (u==null) {
        logger.warn("User $username not found")
        s.db.stats_authentications << [username:username,date:new Date(), code:2, addr:ip]
        return null
      }
      if (u.password != password) {
        logger.warn("Password doesn't match")
        s.db.stats_authentications << [username:username,date:new Date(), code:3, addr:ip]
        return null
      }
      s.db.stats_authentications << [username:username,userId:u._id,date:new Date(), code:0, addr:ip]
      return u
    }
  }

  // redeclare login to enable fat client behavior
  @FatClientEvent(alternateResolution = 'rpcLogin')
  Resolution login() {
    return super.login()
  }

  Resolution rpcLogin() {
    def user = getCurrentUser(context.request.session)
    JsonUtil util = new JsonUtil()
    return util.resolution(util.userToJson(user).toString())
  }

  static def getCurrentUser(HttpSession session) {
    return session.getAttribute(AuthConstants.SESSION_ATTR_CURRENT_USER)
  }

  @DontValidate
  Resolution logout() {
    getContext().getRequest().getSession().invalidate()
    return new RedirectResolution("/")
  }

}