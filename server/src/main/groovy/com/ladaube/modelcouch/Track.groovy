package com.ladaube.modelcouch

class Track extends BaseObject {

  String type = 'track'  
  String name
  String userId
  String[] buddies
  String fileName
  String artist
  String album
  String albumArtist
  String composer
  Integer year
  String genre
  Integer trackNumber
  Integer contentLen
  String md5
  Date postedOn

  private boolean stringMatch(String crit, String propName) {
    String val = this[propName]
    def critLc = crit.toLowerCase()
    def valLc = val ? val.toLowerCase() : null
    return valLc!=null && valLc.indexOf(critLc)!=-1
  }

  boolean matchesSearch(String crit) {
    def stringProps = ['name', 'artist', 'album', 'albumArtist', 'composer', 'genre', 'userId']
    for (String s : stringProps) {
      if (stringMatch(crit, s)) {
        return true
      }
    }
    return false
  }

}
