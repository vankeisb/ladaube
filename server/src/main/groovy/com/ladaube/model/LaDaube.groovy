package com.ladaube.model

import com.gmongo.GMongo

public class LaDaube {

  private final GMongo mongo

  private static final LaDaube INSTANCE = new LaDaube()

  private LaDaube() {
    // one Mongo for the whole App
    println """
 _          _               _
| |  __    | |  __   _   _ | | __  ___
| | /  \\  _| | /  \\ | | | || |/  \\/ _ \\_
| |/ __ \\|   |/ __ \\| |_| ||   * || __/ /
|___/  \\_____/_/  \\_\\_____/|_|\\__/\\____/  v1.0-alpha
"""
    mongo = new GMongo("127.0.0.1", 27017)
  }

  private LaDaubeSession createSession() {
    return new LaDaubeSession(mongo)
  }

  static def doInSession(Closure c) {
    LaDaubeSession s = INSTANCE.createSession()
    return c.call(s)
  }
}