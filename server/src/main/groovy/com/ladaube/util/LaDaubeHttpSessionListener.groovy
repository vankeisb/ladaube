package com.ladaube.util

import javax.servlet.http.HttpSessionListener
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSession
import java.util.concurrent.ConcurrentHashMap

class LaDaubeHttpSessionListener implements HttpSessionListener {

  private final static Map<String,HttpSession> sessions = new ConcurrentHashMap<String,HttpSession>();

  void sessionCreated(HttpSessionEvent httpSessionEvent) {
    sessions.put(httpSessionEvent.session.id, httpSessionEvent.session)
  }

  void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    sessions.remove(httpSessionEvent.session.id)
  }

  public static HttpSession getSession(String id) {
    return sessions.get(id)
  }


}
