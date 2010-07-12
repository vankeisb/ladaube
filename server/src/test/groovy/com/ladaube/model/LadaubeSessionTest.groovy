package com.ladaube.model

class LadaubeSessionTest extends GroovyTestCase {

  protected void setUp() {
    cleanup()
  }

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
    def remi = s.createUser(u1, u1)
    def alex = s.createUser(u2, u2)
    s.makeBuddies(remi, alex)
    InputStream is = getClass().getResourceAsStream('/Someone New.mp3')

    def track = s.createTrack(remi, is, 'someone.mp3')

    // list buddies
    def remiBuddies = s.getBuddies(remi).toArray()
    assert remiBuddies.size() == 1

    // list tracks
    def remiTracks = s.getUserTracks(remi, false, null).toArray()
    assert remiTracks.size() == 1

    // make sure our track has an attachment
    def t = remiTracks[0]
    assert t
    // TODO
    // assert t.getAttachments().size() == 1
  }

  void testCreateReadUser() {

    LaDaubeSession s = getLadaubeSession()

    // create user
    def u = s.createUser('remi', 'remi');
    assert u != null
    assert u.username == 'remi'

    // retrieve
    u = s.getUser('remi')
    assert u != null
    assert u.username == 'remi'
  }

  void testUserBuddies() {
    // create users and assert they're not buddies
    LaDaubeSession session = getLadaubeSession()

    def remi = session.createUser('remi', 'remi')
    def alex = session.createUser('alex', 'alex')
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
    def buddies = session.getBuddies(remi).toArray()
    assert buddies.size()==1
    def buddy = buddies[0]
    assert buddy.username == alex.username
  }

  void testCreateReadTrack() {
    // create a user for the sake of testing
    def u = ladaubeSession.createUser('remi','remi')
    assert u != null

    // Create the track
    InputStream is = getClass().getResourceAsStream('/Someone New.mp3')
    def t = ladaubeSession.createTrack(u, is, 'Someone New')
    assert t!=null
    assert t.name == 'Someone New'
    assert t.userId == u.username

    // try to find the track
    t = ladaubeSession.getUserTracks(u, false, null).toArray()[0]
    assert t!=null
    assert t.name == 'Someone New'
    assert t.userId == u.username
// TODO    assert t.attachments.size() == 1
  }

  void testGetTracksForUserNoBuddiesNoQuery() {
    createTracksAndUsers()

    // obtain the tracks for the user
    LaDaubeSession s = getLadaubeSession()
    def u = s.getUser('remi')
    def userTracks = s.getUserTracks(u, false, null).toArray()
    assert userTracks.size()==1
    def t = userTracks[0]
    assert t.name == 'Someone New'
    assert t.userId == u.username

    // try for alex as well
    u = s.getUser('alex')
    userTracks = s.getUserTracks(u, false, null).toArray()
    assert userTracks.size()==2
  }
  
  void testGetTracksForUserNoBuddiesQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    def u = s.getUser('alex')
    def userTracks = s.getUserTracks(u, false, 'Believe').toArray()
    assert userTracks != null
    assert userTracks.size()==1
    def t = userTracks[0]
    assert t.name == 'Believe It'
    assert t.userId == u.username
  }

  void testGetTracksForUserBuddiesNoQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    def remi = s.getUser('remi')
    def alex = s.getUser('alex')
    def userTracks = s.getUserTracks(remi, true, null).toArray()
    assert userTracks != null
    assert userTracks.size()==3
    userTracks.each { t ->
      if (t.name=='Someone New') {
        assert t.userId==remi.username
      } else if (t.name=='Personal Possessions') {
        assert t.userId==alex.username
      } else if (t.name=='Believe It') {
        assert t.userId==alex.username
      } else {
        fail("track should not have been returned : $t")
      }
    }
  }

  void testGetTracksForUserBuddiesQuery() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    // obtain the tracks for the user
    def remi = s.getUser('remi')
    def alex = s.getUser('alex')
    def userTracks = s.getUserTracks(remi, true, 'Believe').toArray()
    assert userTracks != null
    assert userTracks.size()==1
    def t = userTracks[0]
    assert t.userId == alex.username
    assert t.name == 'Believe It'
  }

  void testGetTracksForUserWhoHasPostedNoTracks() {
    createTracksAndUsers()
    // make a buddy for alex but don't add tracks to him
    LaDaubeSession s = getLadaubeSession()
    def flow = s.createUser('flow', 'flow')
    def alex = s.getUser('alex')
    s.makeBuddies(flow, alex)
    assert s.checkBuddies(alex, flow)
    assert s.getUserTracks(alex, false, null).toArray().size()

    // assert flow doesn't have any track
    flow = s.getUser('flow')
    assert flow
    def userTracks = s.getUserTracks(flow, false, null).toArray()
    assert userTracks!=null
    assert userTracks.size() == 0

    // assert flow can search in alex's tracks
    flow = s.getUser('flow')
    assert flow
    userTracks = s.getUserTracks(flow, true, null).toArray()
    assert userTracks!=null
    assert userTracks.size() == 2

    // assert flow doesn't access remi's tracks
    flow = s.getUser('flow')
    userTracks = s.getUserTracks(flow, true, null).toArray()
    userTracks.each { t ->
      assert t.userId != 'remi'
    }
  }

  void testCreatePlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    def remi = s.getUser('remi')
    def p = s.createPlaylist(remi, 'funky stuff')

    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi).toArray()
    assert playlists.size() == 1
    def playlist = playlists[0]
    assert playlist.name == 'funky stuff'
  }

  void testTracksInPlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    def remi = s.getUser('remi')
    def p = s.createPlaylist(remi, 'funky stuff')
    def tracks = s.getTracksInPlaylist(p).toArray()
    assert tracks!=null
    assert tracks.size()==0

    def remiTracks = s.getUserTracks(remi, false, null).toArray()
    assert remiTracks.size() == 1
    def t = remiTracks[0]
    assert t
    String trackName = t.name
    p = s.getPlaylists(remi).toArray()[0]
    s.addTrackToPlaylist(t, p)

    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi).toArray()
    assert playlists!=null
    assert playlists.size() == 1
    p = playlists[0]
    assert p.name == 'funky stuff'
    tracks = s.getTracksInPlaylist(p).toArray()
    assert tracks!=null
    assert tracks.size()==1
    assert tracks[0].name == trackName
  }

  void testDeletePlaylist() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    def remi = s.getUser('remi')
    def pl = s.createPlaylist(remi, 'test playlist')
    assert pl
    assert pl.name == 'test playlist'

    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi).toArray()
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
    playlists = s.getPlaylists(remi).toArray()
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
    def remi = s.getUser('remi')
    s.createPlaylist(remi, 'test add / remove')
    def pl = s.getPlaylists(remi).toArray()[0]
    def tracks = s.getUserTracks(remi, true, null).toArray()
    tracks.each { t ->
      s.addTrackToPlaylist(t, pl)
    }

    // count tracks in first playlist and remove one track
    remi = s.getUser('remi')
    def playlists = s.getPlaylists(remi).toArray()
    def playlist1 = playlists[0]
    tracks = s.getTracksInPlaylist(playlist1).toArray()
    int len = tracks.size()
    def track = tracks[0]
    s.removeTrackFromPlaylist(track, playlist1)

    // count again
    pl = s.getPlaylists(remi).toArray()[0]
    tracks = s.getTracksInPlaylist(pl).toArray()
    assert tracks.size() == len-1
  }

  void testMD5Check() {
    createTracksAndUsers()

    LaDaubeSession s = getLadaubeSession()
    def remi = s.getUser('remi')
    def track = s.getUserTracks(remi, false, null).toArray()[0]
    def tmd5 = track.md5

    assert s.checkMD5(remi, tmd5)
    assert !s.checkMD5(remi, 'dummy')
  }

  void testCantCreateTheSameTrackTwice() {
    createTracksAndUsers()
    try {
      LaDaubeSession s = getLadaubeSession()
      def remi = s.getUser('remi')
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

    // drop all collections
    s.clearCollections()

  }

  void testSortingByNameAsc() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def res = s.getUserTracks(s.getUser('remi'),true,null,null,null,'name','ASC').toArray();
      assert res.size() == 3

      // verify order
      def t0 = res[0]
      println "t0 : $t0.name"
      def t1 = res[1]
      println "t1 : $t1.name"
      def t2 = res[2]
      println "t2 : $t2.name"

      assert t0.name.compareTo(t1.name) < 0
      assert t1.name.compareTo(t2.name) < 0
    }
  }

  void testSortingByNameDesc() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def res = s.getUserTracks(s.getUser('remi'),true,null,null,null,'name','DESC').toArray();
      assert res.size() == 3

      // verify order
      def t0 = res[0]
      println "t0 : $t0.name"
      def t1 = res[1]
      println "t1 : $t1.name"
      def t2 = res[2]
      println "t2 : $t2.name"

      assert t0.name.compareTo(t1.name) > 0
      assert t1.name.compareTo(t2.name) > 0
    }
  }

  void testAttachment() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def track = s.getUserTracks(s.getUser('remi'), false, null).toArray()[0]
      int len = track.contentLen;
      String fileName = System.getProperty('java.io.tmpdir') + File.separator + 'test.data'
      try {
        FileOutputStream fos = new FileOutputStream(fileName)
        s.writeTrackDataToStream(track, fos)
        fos.flush()
        fos.close()

        File f = new File(fileName)
        assert f.length() == len
      } finally {
        new File(fileName).delete()
      }      
    }
  }

  void testGetTrackById() {
    createTracksAndUsers()
    LaDaube.get().doInSession { LaDaubeSession s ->
      def t = s.getUserTracks(s.getUser('remi'), false, null).next()
      assert t
      assert t._id

      def t2 = s.getTrack(t._id.toString())
      assert t2
      assert t2._id == t._id
    }
  }

}
