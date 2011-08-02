import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

class Feed {

  public static void main(String[] args) {
    LaDaube.doInSession { LaDaubeSession s ->
      s.getTopTracks({ t,c,i ->
        println "$i : $c - $t.name ($t.artist / $t.album)"
      })




//      ['remi', 'eva', 'flow', 'alex', 'kakou'].each { u ->
//        for (int i=0 ; i < 10000; i++) {
//          def str = u + '_' + i
//          s.db.tracks << [
//                  fileName: str,
//                  userId:u,
//                  contentLen:12345,
//                  md5:str,
//                  name:str,
//                  artist:str,
//                  albumArtist:str,
//                  year: i,
//                  genre: "toto",
//                  trackNumber: i,
//                  searchData: str,
//                  postedOn: new Date()
//          ]
//          println "Added track $i for $u"
//        }
//      }
    }    
  }

}
