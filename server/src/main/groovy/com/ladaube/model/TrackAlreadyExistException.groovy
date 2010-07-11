package com.ladaube.model

class TrackAlreadyExistException extends Exception {

  public TrackAlreadyExistException() {
    super('A track already exists for this file.')
  }
}
