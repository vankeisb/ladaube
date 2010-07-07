package com.ladaube.modelcouch

import com.ladaube.util.IteratorWithLength
import org.ektorp.ViewQuery

class LadaubeSessionTest extends GroovyTestCase {

  protected void tearDown() {
    cleanup()
  }

  LaDaubeSession getLadaubeSession() {
    return new LaDaubeSession();
  }

  void testIt() {
    LaDaubeSession s = new LaDaubeSession()

    // create objects
    def time = System.currentTimeMillis()
    String u1 = "remi" + time
    String u2 = "alex" + time
    User remi = s.createUser(u1, u1)
    User alex = s.createUser(u2, u2)
    Buddies buddies = s.makeBuddies(remi, alex)
    InputStream is = getClass().getResourceAsStream('/Someone New.mp3')

    Track track = s.createTrack(remi, is, 'someone.mp3')

    // list buddies
    def remiBuddies = s.getBuddies(remi)
    assert remiBuddies.size() == 1

    // list tracks
    IteratorWithLength<Track> remiTracks = s.getUserTracks(remi, false, null)
    assert remiTracks.length() == 1

    // make sure our track has an attachment
    Track t = remiTracks.next()
    assert t
    assert t.getAttachments().size() == 1
  }

  void testCreateReadUser() {

    LaDaubeSession s = getLadaubeSession()

    // create user
    User u = s.createUser('remi', 'remi');
    assert u != null
    assert u.id == 'remi'

    // retrieve
    u = s.getUser('remi')
    assert u != null
    assert u.id == 'remi'
  }

  void testUserBuddies() {
    // create users and assert they're not buddies
    LaDaubeSession session = getLadaubeSession()

    User remi = session.createUser('remi', 'remi')
    User alex = session.createUser('alex', 'alex')
    assert !session.checkBuddies(remi, alex)

    // make buddies
    session = getLadaubeSession()
    remi = session.getUser('remi')
    alex = session.getUser('alex')
    session.makeBuddies(remi, alex)

    // assert
    session = getLadaubeSession()
    remi = session.getUser('remi')
    alex = session.getUser('alex')
    assert session.checkBuddies(remi, alex)
    assert session.checkBuddies(alex, remi)
    List<User> buddies = session.getBuddies(remi)
    assert buddies.size()==1
    User buddy = buddies[0]
    assert buddy.id == alex.id
  }

  void testCreateReadTrack() {
    // create a user for the sake of testing
    User u = ladaubeSession.createUser('remi','remi')
    assert u != null

    // Create the track
    InputStream is = getClass().getResourceAsStream('/Someone New.mp3')
    Track t = ladaubeSession.createTrack(u, is, 'Someone New')
    assert t!=null
    assert t.name == 'Someone New'
    assert t.userId == u.id

    // try to find the track
    t = ladaubeSession.getTrackById(t.id)
    assert t!=null
    assert t.name == 'Someone New'
    assert t.userId == u.id
    assert t.attachments.size() == 1
  }

  void testGetTracksForUserNoBuddiesNoQuery() {
    createTracksAndUsers()

    // obtain the tracks for the user
    LaDaubeSession s = getLadaubeSession()
    User u = s.getUser('remi')
    IteratorWithLength<Track> userTracks = s.getUserTracks(u, false, null)
    assert userTracks != null
    assert userTracks.length()==1
    Track t = userTracks.next()
    assert t.name == 'Someone New'
    assert t.userId == u.id

    // try for alex as well
    u = s.getUser('alex')
    userTracks = s.getUserTracks(u, false, null)
    assert userTracks != null
    assert userTracks.length()==2
  }
  
  void testGetTracksForUserNoBuddiesQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    User u = s.getUser('alex')
    IteratorWithLength<Track> userTracks = s.getUserTracks(u, false, 'Believe')
    assert userTracks != null
    assert userTracks.length()==1
    Track t = userTracks.next()
    assert t.name == 'Believe It'
    assert t.userId == u.id
  }

  void testGetTracksForUserBuddiesNoQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    User remi = s.getUser('remi')
    User alex = s.getUser('alex')
    IteratorWithLength<Track> userTracks = s.getUserTracks(remi, true, null)
    assert userTracks != null
    assert userTracks.length()==3
    while (userTracks.hasNext()) {
      Track t = userTracks.next()
      if (t.name=='Someone New') {
        assert t.userId==remi.id
      } else if (t.name=='Personal Possessions') {
        assert t.userId==alex.id
      } else if (t.name=='Believe It') {
        assert t.userId==alex.id
      } else {
        fail("track should not have been returned : $t")
      }
    }
  }

  void testGetTracksForUserBuddiesQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    User remi = s.getUser('remi')
    User alex = s.getUser('alex')
    IteratorWithLength<Track> userTracks = s.getUserTracks(remi, true, 'Believe')
    assert userTracks != null
    assert userTracks.length()==1
    Track t = userTracks.next()
    assert t.userId == alex.id
    assert t.name == 'Believe It'
  }

  void testGetTracksForUserWhoHasPostedNoTracks() {
    createTracksAndUsers()
    // make a buddy for alex but don't add tracks to him
    LaDaubeSession s = getLadaubeSession()
    User flow = s.createUser('flow', 'flow')
    User alex = s.getUser('alex')
    s.makeBuddies(flow, alex)
    assert s.checkBuddies(alex, flow)
    assert s.getUserTracks(alex, false, null).size()

    // assert flow doesn't have any track
    flow = s.getUser('flow')
    assert flow
    def userTracks = s.getUserTracks(flow, false, null)
    assert userTracks!=null
    assert userTracks.size() == 0

    // assert flow can search in alex's tracks
    flow = s.getUser('flow')
    assert flow
    userTracks = s.getUserTracks(flow, true, null)
    assert userTracks!=null
    assert userTracks.size() == 2

    // assert flow doesn't access remi's tracks
    flow = s.getUser('flow')
    userTracks = s.getUserTracks(flow, true, 'Someone')
    assert userTracks.size() == 0
  }

  void testCreatePlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    User remi = s.getUser('remi')
    s.createPlaylist(remi, 'funky stuff')

    remi = s.getUser('remi')
    IteratorWithLength<Playlist> playlists = s.getPlaylists(remi)
    assert playlists.length() == 1
    Playlist playlist = playlists.next()
    assert playlist.name == 'funky stuff'
  }

  void testTracksInPlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    User remi = s.getUser('remi')
    Playlist p = s.createPlaylist(remi, 'funky stuff')
    IteratorWithLength<Track> tracks = s.getTracksInPlaylist(p)
    assert tracks!=null
    assert tracks.length()==0

    IteratorWithLength<Track> remiTracks = s.getUserTracks(remi, false, null)
    assert remiTracks.length() == 1
    Track t = remiTracks.next()
    assert t
    String trackName = t.name
    s.addTrackToPlaylist(t, p)

    remi = s.getUser('remi')
    IteratorWithLength playlists = s.getPlaylists(remi)
    assert playlists!=null
    assert playlists.length() == 1
    p = playlists.next()
    assert p.name == 'funky stuff'
    tracks = s.getTracksInPlaylist(p)
    assert tracks!=null
    assert tracks.length()==1
    assert tracks.next().name == trackName
  }

  void testDeletePlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    User remi = s.getUser('remi')
    Playlist pl = s.createPlaylist(remi, 'test playlist')
    assert pl
    assert pl.name == 'test playlist'

    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi)
    assert playlists!=null
    pl = null
    playlists.each{ p ->
      if (p.name == 'test playlist') {
        pl = p
      }
    }
    if (pl==null) {
      fail('playlist not found for user')
    }
    s.deletePlaylist(pl)

    // make sue we can't retrieve the deleted playlist
    remi = s.getUser('remi')
    playlists = s.getPlaylists(remi)
    assert playlists!=null
    pl = null
    playlists.each{ p ->
      if (p.name == 'test playlist') {
        pl = p
      }
    }
    if (pl!=null) {
      fail('playlist not deleted')
    }

  }

  void testRemoveTrackFromPlaylist() {
    createTracksAndUsers();

    // create a playlist for testing
    LaDaubeSession s = getLadaubeSession()
      User remi = s.getUser('remi')
    Playlist pl = s.createPlaylist(remi, 'test add / remove')
    String initialPlaylistId = pl.id
    def tracks = s.getUserTracks(remi, true, null)
    tracks.each { Track t ->
      s.addTrackToPlaylist(t, pl)
    }

    // count tracks in first playlist and remove one track
    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi)
    Playlist playlist1 = playlists.next()
    tracks = s.getTracksInPlaylist(playlist1)
    int len = tracks.length()
    Track track = tracks.next()
    s.removeTrackFromPlaylist(track, playlist1)

    // count again
    pl = s.couch.get(Playlist.class, initialPlaylistId)
    tracks = s.getTracksInPlaylist(pl)
    assert tracks.length() == len-1
  }

  void testMD5Check() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    User remi = s.getUser('remi')
    def track = s.getUserTracks(remi, false, null).next()
    def tmd5 = track.md5

    assert s.checkMD5(remi, tmd5)
    assert !s.checkMD5(remi, 'dummy')
  }

  void testCantCreateTheSameTrackTwice() {
    createTracksAndUsers()
    try {
      LaDaubeSession s = getLadaubeSession()
      User remi = s.getUser('remi')
      InputStream is = getClass().getResourceAsStream('/Someone New.mp3')
      s.createTrack(remi, is, 'Someone New')
      fail('should have failed')
    } catch(TrackAlreadyExistException e) {
      // expected
    }
  }


  private void createTracksAndUsers() {
    LaDaubeSession s = getLadaubeSession()
    def remi = s.createUser('remi', 'remi')
    InputStream is = getClass().getResourceAsStream('/Someone New.mp3')
    s.createTrack(remi, is, 'Someone New')

    def alex = s.createUser('alex', 'alex')
    is = getClass().getResourceAsStream('/Personal Possessions.mp3')
    s.createTrack(alex, is, 'Personal Possessions')
    is = getClass().getResourceAsStream('/Believe It.mp3')
    s.createTrack(alex, is, 'Believe It')

    s.makeBuddies(remi, alex)
  }

  private void cleanup() {
    LaDaubeSession s = getLadaubeSession()
    ViewQuery query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("allBuddies")
    List<Buddies> buddies = s.couch.queryView(query, Buddies.class)
    for (Buddies b : buddies) {
      s.couch.delete(b.id, b.revision)
    }

    query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("playlists")
    List<Playlist> playlists = s.couch.queryView(query, Playlist.class)
    for (Playlist p : playlists) {
      s.couch.delete(p.id, p.revision)
    }

    query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("tracks")
                        .includeDocs(true)
    List<Track> tracks = s.couch.queryView(query, Track.class)
    for (Track t : tracks) {
      s.couch.delete(t.id, t.revision)
    }

    query = new ViewQuery()
                        .designDocId("_design/ladaube-couch")
                        .viewName("users")
    List<User> users = s.couch.queryView(query, User.class)
    for (User u : users) {
      s.couch.delete(u.id, u.revision)
    }

  }

  void testSortingByNameAsc() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def res = s.getUserTracks(s.getUser('remi'),true,null,null,null,'name','ASC');
      assert res.length() == 3

      // verify order
      def t0 = res.next()
      println "t0 : $t0.name"
      def t1 = res.next()
      println "t1 : $t1.name"
      def t2 = res.next()
      println "t2 : $t2.name"

      assert t0.name.compareTo(t1.name) < 0
      assert t1.name.compareTo(t2.name) < 0
    }
  }

  void testSortingByNameDesc() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def res = s.getUserTracks(s.getUser('remi'),true,null,null,null,'name','DESC');
      assert res.length() == 3

      // verify order
      def t0 = res.next()
      println "t0 : $t0.name"
      def t1 = res.next()
      println "t1 : $t1.name"
      def t2 = res.next()
      println "t2 : $t2.name"

      assert t0.name.compareTo(t1.name) > 0
      assert t1.name.compareTo(t2.name) > 0
    }
  }

}
