package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution

import com.ladaube.modelcouch.LaDaube
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.modelcouch.Track
import com.ladaube.modelcouch.User
import com.ladaube.modelcouch.LaDaubeSession

@UrlBinding('/populate')
public class Populate extends BaseAction {

  private Track createDummyTrack(LaDaubeSession s, User user, String name, String album, String artist) {
    Track track = new Track(name:name, album:album, artist:artist, userId:user.id)
    s.couch.create(track)
    return track
  }

  Resolution doIt() {
    LaDaube.get().doInSession { LaDaubeSession s ->

      // create a bunch of users
      User remi = s.createUser('remi', 'remi')

      User kakou = s.createUser('kakou', 'kakou')
      s.makeBuddies(remi, kakou)

      User alex = s.createUser('alex', 'alex')
      s.makeBuddies(remi, alex)
      s.makeBuddies(alex, kakou)

      User flow = s.createUser('flow', 'flow')
      s.makeBuddies(alex, flow)
      s.makeBuddies(flow, remi)

      User eva = s.createUser('eva', 'eva')
      s.makeBuddies(eva, remi)
      
      // create tracks
//      def tracks = []
//      users.each { user ->
//        for (i in 1..10) {
//          Track t = createDummyTrack(s, user, "name $user.username $i", "album $user.username $i", "artist $user.username $i")
//          tracks << t
//        }
//      }

      // create playlists for remi
//      for (i in 1..3) {
//        def pl = s.createPlaylist(remi, "playlist$i")
//        for (j in 1..10) {
//          s.addTrackToPlaylist(tracks[j], pl)
//        }
//      }
      
    }
    return new RedirectResolution('/');
  }

}