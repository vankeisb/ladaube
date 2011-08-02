package com.ladaube.model

import com.gmongo.GMongo
import com.ladaube.util.MD5
import com.mongodb.BasicDBObject
import com.mongodb.gridfs.GridFS
import com.mongodb.gridfs.GridFSFile
import java.util.regex.Pattern
import org.apache.log4j.Logger
import org.bson.types.ObjectId
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import com.ladaube.util.TransferStreams

class LaDaubeSession {

  def db
  private boolean indexCreated = false

  private static final Logger logger = Logger.getLogger(LaDaubeSession.class)

  def LaDaubeSession(GMongo mongo) {
    String dbName = System.getProperty("ladaube.db.name", "ladaube")
    db = mongo.getDB(dbName)
    ensureIndexes()
  }

  def ensureIndexes() {
    if (!indexCreated) {
      [
              [userId:1],
              [md5:1],
              [name:1],
              [artist:1],
              [albumArtist:1],
              [year:1],
              [genre:1],
              [trackNumber:1],
              [searchData:1],
              [postedOn:1]
      ].each { indx ->
        db.tracks.ensureIndex(indx)
      }
      indexCreated = true
    }
  }

  def getUser(String username) {
    if (username==null) {
      throw new IllegalArgumentException("username can't be null")
    }
    return db.users.findOne(username: username)
  }

  def getUsers() {
    return db.users.find()
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
    if (id instanceof String) {
      id = new ObjectId(id)
    }    
    return mongoCollection.findOne([_id:id])
  }

  def getTrack(def id) {
    return byId(db.tracks, id)
  }

  def getPlaylist(def id) {
    return byId(db.playlists, id)
  }

  def getBuddies(def user) {
    def userNames = []
    String username = user.username
    db.buddies.find(user1:username).each { buddyDoc ->
      userNames << buddyDoc.user2
    }
    return db.users.find([username:[$in:userNames]])
  }

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

    def findCrit = [userId: [$in: allUsers]]

    if (query) {
      def p = Pattern.compile(query, Pattern.CASE_INSENSITIVE)
      findCrit.searchData = p
    }

    def res = db.tracks.find(findCrit)
    if (sort) {
      int dirInt = dir == 'ASC' ? 1 : -1
      res.sort(new BasicDBObject(sort, dirInt))
    }
    if (limit) {
      res.limit(limit)
    }
    if (start) {
      res.skip(start)
    }
    return res
  }

  def getPlaylists(def user) {
    if (!user) {
      throw new IllegalArgumentException('user cannot be null')
    }
    def allUsers = []
    allUsers << user.username
    def buddies = getBuddies(user)
    buddies.each { b ->
      allUsers << b.username
    }
    def findCrit = [userId: [$in: allUsers]]
    return db.playlists.find(findCrit)
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

  private String convertFields(Tag tag, FieldKey k) {
    return tag.getFirst(k)
//    def items = tag.getFields(k);
//    if (items==null) {
//      return null
//    }
//    StringBuilder sb = new StringBuilder()
//    for (Iterator<TagField> it = items.iterator() ; it.hasNext() ; ) {
//      TagField tf = it.next();
//      tf.get
//      if (tf!=null) {
//        sb.append(tf.toString())
//      }
//      if (it.hasNext()) {
//        sb.append(" ")
//      }
//    }
//    return sb
  }

  def extractTrackTagsFromFile(File trackFile, String originalFileName) {
      def t = [:]
      def sb = new StringBuilder()

      Tag tag = null
      AudioFile f = AudioFileIO.read(trackFile);
      if (f!=null) {
        tag = f.getTag()
      }
      if (tag==null) {
        throw new RuntimeException("can't get ID3 from file " + t.fileName)
      }
      t['name'] = convertFields(tag, FieldKey.TITLE)
      if (!t['name']) {
        t['name'] = originalFileName
      }
      sb.append(t.name)
      t['artist'] = convertFields(tag, FieldKey.ARTIST)
      sb.append(" ").append(t.artist)
      t['albumArtist'] = convertFields(tag, FieldKey.ALBUM_ARTIST)
      sb.append(" ").append(t.albumArtist)
      t['album'] = convertFields(tag, FieldKey.ALBUM)
      sb.append(" ").append(t.album)
      String year = convertFields(tag, FieldKey.YEAR)
      try {
        t['year'] = year==null ? null : Integer.parseInt(year)
        sb.append(" ").append(Integer.toString(t.year))
      } catch(NumberFormatException e) {
        // not a valid int
      }
      t['genre'] = convertFields(tag, FieldKey.GENRE)
      sb.append(" ").append(t.genre)
      String trackNumber = convertFields(tag, FieldKey.TRACK)
      try {
        t['trackNumber'] = trackNumber==null ? null : Integer.parseInt(trackNumber)
      } catch(NumberFormatException e) {
        // not a valid int
      }
      t['searchData'] = sb.toString()
      return t
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

      def tags = extractTrackTagsFromFile(f, originalFileName)
      t.putAll(tags)

      t['postedOn'] = new Date()

      def uuid = UUID.randomUUID().toString()
      t['uuid'] = uuid

      // create the attachment for the file
      GridFS fs = new GridFS(db)
      GridFSFile fsFile = fs.createFile(f)
      fsFile.put('uuid', uuid)
      fsFile.save()

      // insert in db
      db.tracks.insert(t)

      // reload the track to make sure we have an _id prop
      t = db.tracks.findOne([uuid:uuid])      
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
    return db.tracks.find([_id: [$in : p.tracks]])
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

  void createImageForTrack(def track, String originalFileName, InputStream data) {
    GridFS fs = new GridFS(db)
    GridFSFile fsFile = fs.createFile(data)
    fsFile.put('uuid', 'img' + track.uuid)
    fsFile.save()
  }

  void clearCollections() {
    db.buddies.remove([:])
    db.users.remove([:])
    db.tracks.remove([:])
    db.playlists.remove([:])
  }

  int writeTrackDataToStream(def track, OutputStream os) {
    def ti = getTrackInfos(track)
    return TransferStreams.transfer(ti.is, os)
  }

  def getTrackInfos(def track) {
    GridFS fs = new GridFS(db)
    GridFSFile file = fs.findOne(new BasicDBObject('uuid',track.uuid))
    return ["is": file.getInputStream(), "len": file.length]
  }

  int writeTrackImageToStream(def track, OutputStream os) {
    GridFS fs = new GridFS(db)
    GridFSFile file = fs.findOne(new BasicDBObject('uuid','img' + track.uuid))
    if (file) {
      return TransferStreams.transfer(file.getInputStream(), os)
    } else {
      return TransferStreams.transfer(getClass().getResourceAsStream('/unknown.jpg'), os)
    }
  }

  def getTrackForUser(def track, def user) {
    if (track instanceof String) {
      track = getTrack(track)
    }
    if (!track) {
      return null
    }
    if (user instanceof String) {
      user = getUser(user)
    }
    if (!user) {
      return null
    }
    if (track.userId==user.username.toString()) {
      return track
    }
    def buddies = getBuddies(user)
    for (def b : buddies) {
      if (track.userId==b.username) {
        return track
      }
    }
    return null
  }

  def getTopTracks(Closure callback) {
    // map/reduce on the downloads collection
    db.stats_downloads.mapReduce(
      """
      function map() {
          emit(this.trackId, 1)
      }
      """,
      """
      function reduce(key, values) {
          var count = 0
          for (var i = 0; i < values.length; i++)
              count += values[i]
          return count
      }
      """,
      "trackscount",
      [:] // No Query
    )

    // query map/reduce result and invoke callback for the tracks
    int nbTracks = 0
    db.trackscount.find().sort([value:-1] as BasicDBObject).find { tc ->
      def track = getTrack(tc._id)
      def count = tc.value
      return callback.call([track, count, nbTracks++])
    }

  }

}