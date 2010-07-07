package com.ladaube.modelcouch

class TrackAlreadyExistException extends Exception {

  public TrackAlreadyExistException() {
    super('A track already exists for this file.')
  }
}
