package com.ladaube.upload

class ErrorEvent extends BaseEvent {

  String fileName
  Throwable reason

  public String toString() {
    return "ErrorEvent{" +
            "fileName='" + fileName + '\'' +
            ", reason=" + reason +
            '}';
  }
}
