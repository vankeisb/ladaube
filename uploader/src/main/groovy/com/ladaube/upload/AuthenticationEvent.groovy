package com.ladaube.upload

class AuthenticationEvent extends BaseEvent {

  boolean success
  String username

  public String toString() {
    return "AuthenticationEvent{" +
            "success=" + success +
            ", username='" + username + '\'' +
            '}';
  }
}
