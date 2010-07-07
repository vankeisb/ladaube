package com.ladaube.modelcouch

import org.ektorp.http.StdHttpClient
import org.ektorp.http.HttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.ektorp.CouchDbInstance
import org.ektorp.impl.StdCouchDbConnector
import org.ektorp.ViewQuery
import org.ektorp.ListQuery
import com.ladaube.util.IteratorWithLength
import com.ladaube.util.CollectionIterator
import org.ektorp.CouchDbConnector
import org.ektorp.Attachment
import org.farng.mp3.MP3File
import org.farng.mp3.TagException
import org.farng.mp3.AbstractMP3Tag
import com.ladaube.util.TransferStreams
import org.farng.mp3.id3.AbstractID3v2
import com.ladaube.util.MD5
import org.ektorp.ViewResult
import org.apache.log4j.Logger

public class LaDaubeSession {

  private static final String DB_NAME = 'ladaube-couch'

  private CouchDbConnector couchDb

  private static final Logger logger = Logger.getLogger(LaDaubeSession.class)

  CouchDbConnector getCouch() {
    return couchDb
  }

  def LaDaubeSession() {
    HttpClient httpClient = new StdHttpClient.Builder().build();
    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    couchDb = new StdCouchDbConnector(DB_NAME, dbInstance);
  }

  User getUser(String username) {
    if (username==null) {
      throw new IllegalArgumentException("username can't be null")
    }
    return couchDb.get(User.class, username)
  }

  List<User> getUsers() {
    ViewQuery couchQuery = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("users")
    return couchDb.queryView(couchQuery, User.class)
  }

  User createUser(String username, String clearPassword) {
    User u = new User(id: username, password: clearPassword)
    couchDb.create(u)
    logger.info("User $u.id created")
    return u
  }

  void updateUser(User u) {
    couchDb.update(u)
    logger.info("User $u.id updated")
  }

  // TODO propagate track sharing
  Buddies makeBuddies(User user1, User user2) {
    if (!checkBuddies(user1, user2)) {
      // not yet buddies, create and save
      Buddies buddies = new Buddies(user1:user1.id, user2: user2.id)
      couchDb.create(buddies)

      // propagate buddy ids in tracks
      def users = [user1, user2]
      users.each { u ->
        List<User> bds = getBuddies(u)
        String[] bdIds = new String[bds.size()]
        int i = 0
        bds.each { b ->
          bdIds[i] = b.id
          i++
        }        
        ViewQuery couchQuery = new ViewQuery()
                            .designDocId("_design/ladaube-couch")
                            .viewName("tracksByUser")
                            .key(u.id)
                            .includeDocs(true)
        List<Track> userTracks = couchDb.queryView(couchQuery, Track.class)
        userTracks.each { t ->                  
          t.buddies = bdIds
          couchDb.update(t)
        }
      }
      logger.info("Users $user1.id and $user2.id are now buddies")

      return buddies
    }
    return null
  }

  boolean checkBuddies(User user1, User user2) {
    if (user1==null || user2==null) {
      throw new IllegalArgumentException("user1 and user2 must be specified")
    }
    String id1 = user1.id
    String id2 = user2.id

    ViewQuery query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("buddies")
                        .key(id1)
    ViewResult result = couchDb.queryView(query);
    for (ViewResult.Row row : result.getRows()) {
      String key = row.getKey();
      String val = row.getValue();
      if ((key==id1 && val==id2) || (key==id2 && val==id1)) {
        return true
      }
    }
    return false
  }

  Track getTrack(String id) {
    return couchDb.get(Track.class, id)
  }

  Playlist getPlaylist(String id) {
    return couchDb.get(Playlist.class, id)
  }

  List<User> getBuddies(User user) {

    ArrayList<User> users = new ArrayList<User>()
    String userId = user.id

    ViewQuery query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("buddies")
                        .key(userId)
    ViewResult result = couchDb.queryView(query)
    for (ViewResult.Row row : result.getRows()) {
      String key = row.getKey()
      String val = row.getValue()
      String buddyId = key==userId ? val : key
      users << couchDb.get(User.class, buddyId)
    }

    return users
  }

  private List<Track> filter(List<Track> tracks, String query) {
    ArrayList<Track> result = new ArrayList<Track>()
    for (Track t : tracks) {
      if (t.matchesSearch(query)) {
        result.add(t)
      }
    }
    return result
  }

  // TODO remove this method when all tests use the new API maybe ?
  IteratorWithLength<Track> getUserTracks(User user,
                                          boolean includeBuddies,
                                          String query) {
    return getUserTracks(user, includeBuddies, query, null, null, null, null)
  }

  InputStream getUserTracksStreamed(User user,
                                    boolean includeBuddies,
                                    Integer start,
                                    Integer limit,
                                    String sort,
                                    String dir) {

    // compute view name
    String viewName = sort ? "tracks_$sort" : "tracks"
    if (includeBuddies) {
      viewName += "_buddies"
    }

    String userId = user.id

    ViewQuery couchQuery = new ListQuery("tracks")
                        .designDocId("_design/ladaube-couch")
                        .viewName(viewName)
                        .includeDocs(true)
    if (dir=='DESC') {
      couchQuery
        .descending(true)
        .startKey("[\"$userId\",\"\ufff0\"]")
        .endKey("[\"$userId\"]")
    } else {
      couchQuery
        .startKey("[\"$userId\"]")
        .endKey("[\"$userId\",\"\ufff0\"]")
    }
    return couchDb.queryForStream(couchQuery);
  }

  IteratorWithLength<Track> getUserTracks(User user,
                                          boolean includeBuddies,
                                          String query,
                                          Integer start,
                                          Integer limit,
                                          String sort,
                                          String dir) {

    // compute view name
    String viewName = sort ? "tracks_$sort" : "tracks"
    if (includeBuddies) {
      viewName += "_buddies"
    }

    String userId = user.id

    ViewQuery couchQuery = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName(viewName)
                        .includeDocs(true)
    if (dir=='DESC') {
      couchQuery
        .descending(true)
        .startKey("[\"$userId\",\"\ufff0\"]")
        .endKey("[\"$userId\"]")
    } else {
      couchQuery
        .startKey("[\"$userId\"]")
        .endKey("[\"$userId\",\"\ufff0\"]")
    }
    long startTime = System.currentTimeMillis()
    List<Track> userTracks = couchDb.queryView(couchQuery, Track.class)
    long elapsed = System.currentTimeMillis() - startTime
    logger.debug("Spent $elapsed ms in Ektorp/CouchDB");
    long totalLength = userTracks.size()

    // filter in case query has been specified
    if (query) {
      userTracks = filter(userTracks, query)
      totalLength = userTracks.size()
    }

    // collect sublist
    List<Track> tracks = new ArrayList<Track>(userTracks)
    def lowBound = start == null ? 0 : start
    def highBound = tracks.size()
    if (limit!=null) {
      def i = lowBound + limit
      if (i<highBound) {
        highBound = i
      }
    }
    tracks = tracks.subList(lowBound, highBound)

    return new CollectionIterator<Track>(tracks, totalLength)
  }

  Track getTrackById(String id) {
    return couchDb.get(Track.class, id)
  }

  Track createTrack(User user, InputStream data, String originalFileName) throws TrackAlreadyExistException {
    if (user==null || data==null) {
      throw new IllegalArgumentException("user and data can't be null")
    }
    // create track from MP3 input stream and assign user id
    Track t = new Track(fileName: originalFileName, userId: user.id)

    // transfer stream to file
    String baseDir = System.getProperty('java.io.tmpdir')
    String fileName = baseDir + File.separator + UUID.randomUUID().toString() + '.mp3'
    File f = new File(fileName)                        
    try {
      FileOutputStream fos = new FileOutputStream(f)
      t.contentLen = TransferStreams.transfer(data, fos)
      fos.close()
      data.close()

      // MD5 verification
      String md5 = MD5.get(f)
      if (this.checkMD5(user, md5)) {
        throw new TrackAlreadyExistException();
      }
      t.md5 = md5      

      AbstractMP3Tag tag = getID3(f)
      if (tag==null) {
        throw new RuntimeException("can't get ID3 from file " + t.fileName)
      }
      t.name = tag.getSongTitle()
      if (!t.name) {
        t.name = originalFileName
      }
      t.artist = tag.getLeadArtist()
      t.albumArtist = null // TODO
      t.album = tag.getAlbumTitle()
      if (tag instanceof AbstractID3v2) {
        t.composer = tag.getAuthorComposer()
      }
      String year = tag.getYearReleased()
      try {
        t.year = year==null ? null : Integer.parseInt(year)
      } catch(NumberFormatException e) {
        // not a valid int
      }
      t.genre = tag.getSongGenre()
      String trackNumber = tag.getTrackNumberOnAlbum()
      try {
        t.trackNumber = trackNumber==null ? null : Integer.parseInt(trackNumber)
      } catch(NumberFormatException e) {
        // not a valid int
      }

      t.postedOn = new Date()

      // store buddy IDs in the track
      List<User> userBuddies = getBuddies(user)
      String[] userBuddiesIds = new String[userBuddies.size()]
      int bdIndex = 0
      userBuddies.each { b ->
        userBuddiesIds[bdIndex] = b.id
        bdIndex++
      }
      t.buddies = userBuddiesIds      

      couchDb.create(t)

      // track created, create attachment
      InputStream attchIs = new FileInputStream(f);
      Attachment a = new Attachment('att-' + t.id, attchIs, 'audio/mpeg', t.contentLen)
      couchDb.createAttachment(t.id, t.revision, a)

      // reload track to make sure we've got the appropriate rev
      return couchDb.get(Track.class, t.id)
      
    } finally {
      f.delete()
    }

  }

  void writeTrackDataToStream(Track track, OutputStream out) {
    Attachment a = couchDb.getAttachment(track.id, 'att-' + track.id);    
    InputStream is = a.getData()
    TransferStreams.transfer(is, out)
  }

  Playlist createPlaylist(User user, String name) {
    if (!user || !name) {
      throw new IllegalArgumentException("user and name cannot be null")
    }
    Playlist p = new Playlist(name:name, userId:user.id, tracks: new String[0])
    couchDb.create(p)
    return p
  }

  IteratorWithLength<Playlist> getPlaylists(User user) {
    if (!user) {
      throw new IllegalArgumentException('user cannot be null')
    }

    ViewQuery couchQuery = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("playlists")
                        .key(user.id)
    List<Playlist> playlists = couchDb.queryView(couchQuery, Playlist.class)
    ArrayList<Playlist> result = new ArrayList<Playlist>(playlists)

    return new CollectionIterator<Playlist>(result)
  }

  void addTrackToPlaylist(Track t, Playlist p) {
    List<String> trackIds = new ArrayList<String>(Arrays.asList(p.tracks))
    trackIds << t.id
    String[] arr = new String[trackIds.size()]
    arr = trackIds.toArray(arr)
    p.setTracks(arr)
    couchDb.update(p)    
  }

  IteratorWithLength<Track> getTracksInPlaylist(Playlist p) {
    List<Track> result = new ArrayList<Track>()
    String[] tracks = p.tracks
    if (tracks) {
      for (String trackId : tracks) {
        result << couchDb.get(Track.class, trackId)
      }
    }
    return new CollectionIterator<Track>(result)
  }

  void deletePlaylist(Playlist pl) {
    couchDb.delete(pl.id, pl.revision)
  }

  void removeTrackFromPlaylist(Track track, Playlist playlist) {
    String[] tIds = playlist.tracks
    if (tIds) {
      List<String> ids = new ArrayList<String>(Arrays.asList(tIds))
      if (ids.remove(track.id)) {
        tIds = new String[ids.size()]
        tIds = ids.toArray(tIds)
        playlist.tracks = tIds
        couchDb.update(playlist)
      }
    }
  }

  boolean checkMD5(User u, String md5) {
    String userId = u.id
    ViewQuery couchQuery = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("md5s")
                        .key(md5)
    ViewResult result = couchDb.queryView(couchQuery)
    for (ViewResult.Row row : result.getRows()) {
      String uId = row.getValue()
      if (uId==userId) {
        return true
      }
    }
    return false
  }

  void createImageForTrack(Track track, String originalFileName, InputStream data) {
    String baseDir = System.getProperty('java.io.tmpdir')
    String fileName = baseDir + File.separator + UUID.randomUUID().toString() + '.mp3'
    File f = new File(fileName)
    try {
      FileOutputStream fos = new FileOutputStream(f)
      int len = TransferStreams.transfer(data, fos)
      fos.close()
      data.close()
      InputStream attchIs = new FileInputStream(f);
      Attachment a = new Attachment('img-' + track.id, attchIs, 'image/jpeg', len)
      couchDb.createAttachment(track.id, track.revision, a)
    } finally {
      f.delete()
    }
  }

  static AbstractMP3Tag getID3(File f) throws IOException, TagException {
      MP3File mp3 = new MP3File(f)
      if (mp3.hasID3v1Tag() || mp3.hasID3v2Tag()) {
          return mp3.hasID3v2Tag() ? mp3.getID3v2Tag() : mp3.getID3v1Tag()
      } else {
          return null;
      }
  }
  
}