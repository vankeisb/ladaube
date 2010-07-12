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

@UrlBinding('/login')
public class LaDaubeLogin extends LoginActionBean {

  private static final Logger logger = Logger.getLogger(LaDaubeLogin.class)

  protected def authenticate() {
    def u = LaDaube.get().doInSession { LaDaubeSession s ->
      try {
        logger.info("Trying to authenticate $username")
        return s.getUser(username);
      } catch(Exception e) {
        logger.warn("Exception at login !", e)
        return null
      }
    }
    if (u==null) {
      logger.warn("User $username not found")
      return null
    }
    if (u.password != password) {
      logger.warn("Password doesn't match")
      return null
    }
    return u
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