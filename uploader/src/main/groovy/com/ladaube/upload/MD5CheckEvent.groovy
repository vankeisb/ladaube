package com.ladaube.upload

class MD5CheckEvent extends BaseEvent {

  boolean trackAlreadyPresent
  String fileName

  public String toString() {
    return "MD5CheckEvent{" +
            "trackAlreadyPresent=" + trackAlreadyPresent +
            ", fileName='" + fileName + '\'' +
            '}';
  }
}
