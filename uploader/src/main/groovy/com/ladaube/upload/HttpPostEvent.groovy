package com.ladaube.upload

class HttpPostEvent extends BaseEvent {

  // true=>track, false=>image
  boolean isTrack

  String fileName

  long postEventId

  // true -> upload finished, false -> upload starting
  boolean isCompleted = false

  // set when upload has completed successfuly
  String trackId

  public String toString() {
    return "HttpPostEvent{" +
            "isTrack=" + isTrack +
            ", fileName='" + fileName + '\'' +
            ", postEventId=" + postEventId +
            ", isCompleted=" + isCompleted +
            ", trackId='" + trackId + '\'' +
            '}';
  }
}
