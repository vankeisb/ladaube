package com.ladaube.actions

import net.sourceforge.stripes.action.ActionBean
import net.sourceforge.stripes.action.ActionBeanContext

import com.ladaube.util.auth.AuthConstants
import com.ladaube.modelcouch.User

public abstract class BaseAction implements ActionBean {

  private ActionBeanContext context
  private User user

  ActionBeanContext getContext() {
    return context
  }

  void setContext(ActionBeanContext actionBeanContext) {
    this.context = actionBeanContext
  }

  // TODO restrict binding for this prop
  public User getUser() {
    return context.request.session.getAttribute(AuthConstants.SESSION_ATTR_CURRENT_USER)
  }
  
}