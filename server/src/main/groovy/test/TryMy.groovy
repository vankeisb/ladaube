package test

import org.ektorp.http.HttpClient
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import org.ektorp.CouchDbInstance
import org.ektorp.impl.StdCouchDbConnector
import org.ektorp.ViewQuery
import com.ladaube.modelcouch.Track
/**
 * Created by IntelliJ IDEA.
 * User: vankeisb
 * Date: 5 juil. 2010
 * Time: 23:09:23
 * To change this template use File | Settings | File Templates.
 */

public class TryMy {

  public static void mainzzz(String[] args) {
    HttpClient httpClient = new StdHttpClient.Builder().build();
    CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    def couchDb = new StdCouchDbConnector('ladaube-couch', dbInstance);

    ViewQuery q = new ViewQuery()
            .viewName('tracks_name')
            .designDocId('_design/ladaube-couch')
            .includeDocs(true)
            .startKey("[\"eva\",0]")
            .endKey("[\"eva\",\"ZZZZZZ\"]")

    def tracks = couchDb.queryView(q, Track.class)
    println "Nb results : " + tracks.size()
    tracks.each { t ->
      println "Track : $t.name posted by $t.userId"
    }
  }

}

