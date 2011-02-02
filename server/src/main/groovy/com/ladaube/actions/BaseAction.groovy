package com.ladaube.actions

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ActionBeanContext

import com.ladaube.util.auth.AuthConstants
import javax.servlet.http.HttpSession

public abstract class BaseAction implements ActionBean {

  private ActionBeanContext context
  private def user

  ActionBeanContext getContext() {
    return context
  }

  void setContext(ActionBeanContext actionBeanContext) {
    this.context = actionBeanContext
  }

  public def getUser() {
    return getUser(context.request.session)
  }

  public def getUser(HttpSession s) {
    return s.getAttribute(AuthConstants.SESSION_ATTR_CURRENT_USER)
  }
  
}