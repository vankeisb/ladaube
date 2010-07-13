package com.ladaube.util

import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

class LaDaubeInitListener implements ServletContextListener {

  void contextInitialized(ServletContextEvent servletContextEvent) {
    LaDaube.doInSession { LaDaubeSession s -> s.ensureIndexes() }
  }

  void contextDestroyed(ServletContextEvent servletContextEvent) {

  }


}
