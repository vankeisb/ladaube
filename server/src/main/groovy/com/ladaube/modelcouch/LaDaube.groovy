package com.ladaube.modelcouch

public class LaDaube {

  private String baseDir

  private static final LaDaube INSTANCE = new LaDaube()

  public static LaDaube get() {
    return INSTANCE
  }

  private LaDaube() {
  }

  LaDaubeSession createSession() {
    return new LaDaubeSession()
  }

  def doInSession(Closure c) {
    LaDaubeSession s = createSession()
    return c.call(s)
  }
}