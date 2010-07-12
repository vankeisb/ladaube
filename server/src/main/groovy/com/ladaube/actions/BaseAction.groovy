package com.ladaube.actions

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ActionBeanContext

import com.ladaube.util.auth.AuthConstants

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
    return context.request.session.getAttribute(AuthConstants.SESSION_ATTR_CURRENT_USER)
  }
  
}