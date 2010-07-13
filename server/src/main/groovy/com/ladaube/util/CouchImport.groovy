package com.ladaube.util

import static groovyx.net.http.ContentType.JSON
import groovyx.net.http.RESTClient
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.BINARY

class CouchImport {

  static void main(String[] args) {

    LaDaube.doInSession { LaDaubeSession s->


      def client = new RESTClient("http://localhost:5984/")

      // retrieve users
      client.get(path: "ladaube-couch/_design/ladaube-couch/_view/users",
              contentType: JSON,  requestContentType:  JSON).data.rows.each { row ->
        def u = row.value
        s.db.users << [username:u._id,password:u.password,email:u.email]
      }

      // buddies
      client.get(path: "ladaube-couch/_design/ladaube-couch/_view/allBuddies",
              contentType: JSON,  requestContentType:  JSON).data.rows.each { row ->
        def key = row.key
        def u1 = key[0]
        def u2 = key[1]
        s.makeBuddies(s.getUser(u1), s.getUser(u2))
      }

      // tracks
      client.get(path: "ladaube-couch/_design/ladaube-couch/_view/tracks",
              contentType: JSON,
              requestContentType:  JSON,
              query : [include_docs:'true']).data.rows.each { row ->

        // obtain the attachment
        def doc = row.doc
        def id = doc._id

        println "handling track $doc.name, posted by $doc.userId"

        def attachments = doc._attachments
        if (attachments) {
          def createdTrack
          attachments.each { att ->
            def contentType = att.value.content_type
            def attId = att.key
            def fullId = "ladaube-couch/$id/$attId"

            String tmpFileName = System.getProperty('java.io.tmpdir') + File.separator + "ladaube.tmp"
            try {
              FileOutputStream fos = new FileOutputStream(tmpFileName)
              // retrieve the file
              println "downloading $fullId"
              client.request(GET,BINARY) { req ->
                uri.path = "/$fullId"
                response.success = { resp, reader ->
                  assert resp.statusLine.statusCode == 200
                  fos << reader

                  fos.flush()
                  fos.close()

                  def userId = doc.userId

                  if (contentType == 'audio/mpeg') {
                    // track
                    createdTrack = s.createTrack(s.getUser(userId), new FileInputStream(tmpFileName), doc.fileName);
                    println "created track $createdTrack.name"
                  } else {
                    // image
                    if (createdTrack) {
                      println "associated image to track $createdTrack.name"
                      s.createImageForTrack(createdTrack.uuid, 'unknown', new FileInputStream(tmpFileName))
                    }
                  }

                }
              }
            } finally {
              new File(tmpFileName).delete()
            }
          }
        }

        println doc
        

      }

    }
    

  }
}
