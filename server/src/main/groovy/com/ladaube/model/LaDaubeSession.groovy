package com.ladaube.model

import org.farng.mp3.MP3File
import org.farng.mp3.TagException
import org.farng.mp3.AbstractMP3Tag
import com.ladaube.util.TransferStreams
import org.farng.mp3.id3.AbstractID3v2
import com.ladaube.util.MD5
import org.apache.log4j.Logger
import com.gmongo.GMongo

import com.ladaube.modelcouch.Track
import com.mongodb.DBObject
import com.mongodb.BasicDBObject

public class LaDaubeSession {

  private static final String DB_NAME = 'ladaube-couch'

  GMongo mongo
  def db

  private static final Logger logger = Logger.getLogger(LaDaubeSession.class)

  def LaDaubeSession() {
    mongo = new GMongo("127.0.0.1", 27017)
    db = mongo.getDB('ladaube')
  }

  def getUser(String username) {
    if (username==null) {
      throw new IllegalArgumentException("username can't be null")
    }
    return db.users.findOne(username: username)
  }

  def getUsers() {
    return db.users
  }

  def createUser(String username, String clearPassword) {
    def user = getUser(username)
    if(!user) {
      user = [username: username, password: clearPassword]
      db.users << user
      logger.info("User $user.username updated")
    }
    return user
  }

  void updateUser(def u) {
    db.users.update([username:u.username], u)
    logger.info("User $u.username updated")
  }

  void makeBuddies(def user1, def user2) {
    if (!checkBuddies(user1, user2)) {
      // not yet buddies, create and save
      def buddies1 = [user1:user1.username, user2:user2.username]
      def buddies2 = [user1:user2.username, user2:user1.username]
      db.buddies << buddies1
      db.buddies << buddies2

      // propagate buddy ids in tracks
//      def users = [user1, user2]
//      users.each { u ->
//        def bds = getBuddies(u)
//        String[] bdIds = new String[bds.size()]
//        int i = 0
//        bds.each { b ->
//          bdIds[i] = b.username
//          i++
//        }
//        db.tracks.update([userId: u.username], [$set: [buddies: bdIds]])
//      }
      logger.info("Users $user1.username and $user2.username are now buddies")
    }
  }

  boolean checkBuddies(def user1, def user2) {
    if (user1==null || user2==null) {
      throw new IllegalArgumentException("user1 and user2 must be specified")
    }
    return db.buddies.count([user1:user1.username,user2:user2.username]) == 1
  }

  private def byId(def mongoCollection, def id) {
    return mongoCollection.findOne([_id:id])
  }

  def getTrack(def id) {
    return byId(db.tracks, id)
  }

  def getPlaylist(def id) {
    return byId(db.playlists, id)
  }

  def getBuddies(def user) {
    def users = []
    String username = user.username
    def res = db.buddies.find(user1:username)
    while (res.hasNext()) {
      users << getUser(res.next().user2)
    }
    return users
  }

  // TODO update tests and remove this method (or use named arguments ?)
  def getUserTracks(def user,
                    Boolean includeBuddies,
                    String query) {
    return getUserTracks(user, includeBuddies, query, null, null, null, null)
  }

  def getUserTracks(def user,
                    Boolean includeBuddies,
                    String query,
                    Integer start,
                    Integer limit,
                    String sort,
                    String dir) {

    def allUsers = []
    allUsers << user.username
    if (includeBuddies) {
      def buddies = getBuddies(user)
      buddies.each { b ->
        allUsers << b.username
      }      
    }

    def res = db.tracks.find([userId: [$in: allUsers]])
    if (sort) {
      int dirInt = dir == 'ASC' ? 1 : -1
      res = res.sort(new BasicDBObject(sort, dirInt))
    }

    res = res.toArray()

    // ugly iteration...
    if (query) {
      def filtered = []
      res.each { r ->
        if (matchesSearch(r, query)) {
          filtered << r
        }
      }
      res = filtered
    }
    return res
  }

  private boolean stringMatch(def track, String crit, String propName) {
    String val = track[propName]
    def critLc = crit.toLowerCase()
    def valLc = val ? val.toLowerCase() : null
    return valLc!=null && valLc.indexOf(critLc)!=-1
  }

  private matchesSearch(def track, String crit) {
    def stringProps = ['name', 'artist', 'album', 'albumArtist', 'composer', 'genre', 'userId']
    for (String s : stringProps) {
      if (stringMatch(track, crit, s)) {
        return true
      }
    }
    return false
  }


  def getTrackById(String id) {
    return byId(db.tracks, id)
  }

  def createTrack(def user, InputStream data, String originalFileName) throws TrackAlreadyExistException {
    if (user==null || data==null) {
      throw new IllegalArgumentException("user and data can't be null")
    }
    // create track from MP3 input stream and assign user id
    def t = [fileName: originalFileName, userId: user.username]

    // transfer stream to file
    String baseDir = System.getProperty('java.io.tmpdir')
    String fileName = baseDir + File.separator + UUID.randomUUID().toString() + '.mp3'
    File f = new File(fileName)                        
    try {
      FileOutputStream fos = new FileOutputStream(f)
      t['contentLen'] = TransferStreams.transfer(data, fos)
      fos.close()
      data.close()

      // MD5 verification
      String md5 = MD5.get(f)
      if (this.checkMD5(user, md5)) {
        throw new TrackAlreadyExistException();
      }
      t['md5'] = md5

      AbstractMP3Tag tag = getID3(f)
      if (tag==null) {
        throw new RuntimeException("can't get ID3 from file " + t.fileName)
      }
      t['name'] = tag.getSongTitle()
      if (!t['name']) {
        t['name'] = originalFileName
      }
      t['artist'] = tag.getLeadArtist()
      t['albumArtist'] = null // TODO
      t['album'] = tag.getAlbumTitle()
      if (tag instanceof AbstractID3v2) {
        t['composer'] = tag.getAuthorComposer()
      }
      String year = tag.getYearReleased()
      try {
        t['year'] = year==null ? null : Integer.parseInt(year)
      } catch(NumberFormatException e) {
        // not a valid int
      }
      t['genre'] = tag.getSongGenre()
      String trackNumber = tag.getTrackNumberOnAlbum()
      try {
        t['trackNumber'] = trackNumber==null ? null : Integer.parseInt(trackNumber)
      } catch(NumberFormatException e) {
        // not a valid int
      }

      t['postedOn'] = new Date()

      // store buddy IDs in the track
//      def userBuddies = getBuddies(user)
//      String[] userBuddiesIds = new String[userBuddies.size()]
//      int bdIndex = 0
//      userBuddies.each { b ->
//        userBuddiesIds[bdIndex] = b.id
//        bdIndex++
//      }
//      t['buddies'] = userBuddiesIds

      db.tracks.insert(t)

      // TODO create attachment (gridFS ?)

    } finally {
      f.delete()
    }

    return t
  }

  def createPlaylist(def user, String name) {
    if (!user || !name) {
      throw new IllegalArgumentException("user and name cannot be null")
    }
    def p = [name:name, userId:user.username, tracks: new String[0]]
    db.playlists << p
    return p
  }

  def getPlaylists(def user) {
    if (!user) {
      throw new IllegalArgumentException('user cannot be null')
    }
    return db.playlists.find(userId: user.username).toArray()
  }

  void addTrackToPlaylist(def t, def playlist) {
    def tracks = playlist.tracks
    def all = []
    tracks.each { tId ->
      all << tId
    }
    all << t._id
    playlist.tracks = all
    db.playlists.update(_id:playlist._id, playlist)
  }

  def getTracksInPlaylist(def p) {
    def result = []
    def tracks = p.tracks
    if (tracks) {
      for (def trackId : tracks) {
        result << byId(db.tracks, trackId)
      }
    }
    result
  }

  void deletePlaylist(def pl) {
    db.playlists.remove(pl)
  }

  void removeTrackFromPlaylist(def track, def playlist) {
    def tIds = playlist.tracks
    if (tIds) {
      tIds.remove(track._id)
      db.playlists.update(_id:playlist._id, playlist)
    }
  }

  boolean checkMD5(def u, String md5) {
    return db.tracks.count([userId:u.username, md5:md5]) > 0
  }

  void createImageForTrack(Track track, String originalFileName, InputStream data) {
    // TODO
//    String baseDir = System.getProperty('java.io.tmpdir')
//    String fileName = baseDir + File.separator + UUID.randomUUID().toString() + '.mp3'
//    File f = new File(fileName)
//    try {
//      FileOutputStream fos = new FileOutputStream(f)
//      int len = TransferStreams.transfer(data, fos)
//      fos.close()
//      data.close()
//      InputStream attchIs = new FileInputStream(f);
//      Attachment a = new Attachment('img-' + track.id, attchIs, 'image/jpeg', len)
//      couchDb.createAttachment(track.id, track.revision, a)
//    } finally {
//      f.delete()
//    }
  }

  static AbstractMP3Tag getID3(File f) throws IOException, TagException {
      MP3File mp3 = new MP3File(f)
      if (mp3.hasID3v1Tag() || mp3.hasID3v2Tag()) {
          return mp3.hasID3v2Tag() ? mp3.getID3v2Tag() : mp3.getID3v1Tag()
      } else {
          return null;
      }
  }

  void clearCollections() {
    db.buddies.remove([:])
    db.users.remove([:])
    db.tracks.remove([:])
    db.playlists.remove([:])
  }
  
}