package com.ladaube.upload

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.multipart.Part
import org.apache.commons.httpclient.methods.multipart.FilePart
import org.apache.commons.httpclient.methods.multipart.StringPart
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity
import org.json.JSONObject

import com.ladaube.util.MD5

public class Uploader {

  public static Closure EVT_LOG_CLOSURE = { evt ->
    println "Uploader Event : $evt"
  }

  private String _url
  private String _username
  private String _password
  private File _baseDir
  private def _listeners = []
  private boolean stopped = false
  private HttpClient _httpClient

  def stop() {
    stopped = true
  }

  Uploader url(String url) {
    this._url = url
    return this
  }

  Uploader username(String username) {
    this._username = username
    return this
  }

  Uploader password(String password) {
    this._password = password
    return this
  }

  Uploader baseDir(File baseDir) {
    this._baseDir = baseDir
    return this
  }

  Uploader addListeners(def listeners) {
    listeners.each {
      _listeners.add(it)
    }
    return this
  }

  Uploader login() {
    if (_url==null) {
      throw new IllegalArgumentException("URL cannot be null")
    }
    if (_username==null) {
      throw new IllegalArgumentException("username cannot be null")
    }
    if (_password==null) {
      throw new IllegalArgumentException("password cannot be null")
    }
    
    _httpClient = new HttpClient()
    _httpClient.getHttpConnectionManager().
            getParams().setConnectionTimeout(5000)

    // authentication request...
    String loginUrl = _url + "/login?login=true&json=true&username=$_username&password=$_password"
    GetMethod firstReq = new GetMethod(loginUrl)
    _httpClient.executeMethod(firstReq)
    String response = firstReq.getResponseBodyAsString()
    if (response.indexOf(_username)==-1) {
      notifyListeners(new AuthenticationEvent(username: _username, success: false))
      _httpClient = null
      return this
    }
    notifyListeners(new AuthenticationEvent(username: _username, success:true))
    return this
  }

  boolean isAuthenticated() {
    return _httpClient != null
  }

  private void notifyListeners(BaseEvent evt) {
    _listeners.each { l ->
      l.call(evt)
    }
  }

  void upload() {
    if (_httpClient==null) {
      login()
    }
    if (_httpClient==null) {
      throw new IllegalArgumentException("Not authenticated. Register listeners to see what's happening.")      
    }
    if (_baseDir==null) {
      throw new IllegalArgumentException("baseDir cannot be null")
    }


    // TODO remove population
//    String populateUrl = _url + "/populate";
//    GetMethod popReq = new GetMethod(populateUrl)
//    _httpClient.executeMethod(popReq)

    String targetUrl = _url + "/upload"
    String targetImgUrl = _url + "/image"

    int postEventId = 0;

    _baseDir.eachFileRecurse { File f ->

      if (stopped) {
        // just to avoid actually doing anything but we can't stop the groovy loop
        // and I'm too lazy to improve this now...
        return 0
      }

      if (f.name.endsWith('.mp3')) {
        try {
          // it's an mp3, let's do a MD5 check against the
          // server
          String md5 = MD5.get(f)
          String checkUrl = _url + '/checkmd5?md5=' + md5
          GetMethod checkReq = new GetMethod(checkUrl)
          _httpClient.executeMethod(checkReq)
          String checkResp = checkReq.getResponseBodyAsString()
          if (checkResp.indexOf('true')!=-1) {
            // MD5 check returned true, non need to upload the file
            notifyListeners(new MD5CheckEvent(trackAlreadyPresent:true, fileName: f.absolutePath))
          } else {
            // MD5 check ok, track doesn't exist for this user
            // on the server, we can upload
            postEventId++
            notifyListeners(new MD5CheckEvent(trackAlreadyPresent:false, fileName: f.absolutePath))
            notifyListeners(new HttpPostEvent(
                    isTrack: true,
                    fileName:f.absolutePath,
                    postEventId: postEventId))
            PostMethod filePost = new PostMethod(targetUrl);
            String trackId
            try {
                Part[] parts = [
                        new FilePart("data", f),
                        new StringPart("upload", "true"),
                        new StringPart("json", "true")]
                filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
                _httpClient.executeMethod(filePost);
                String response = filePost.getResponseBodyAsString();
                JSONObject o = new JSONObject(response)
                trackId = o.getString('id')
            } finally {
                filePost.releaseConnection();
            }
            notifyListeners(new HttpPostEvent(
                    isTrack: true,
                    fileName:f.absolutePath,
                    postEventId: postEventId,
                    isCompleted: true,
                    trackId: trackId))

            // try to find an image in the file's containing folder
            File folder = f.getParentFile()
            File imageFile = null
            File[] files = folder.listFiles()
            for (int i=0 ; i<files.length && imageFile==null ; i++) {
              String fn = files[i].name.toLowerCase()
              if (fn.endsWith('.jpg') || fn.endsWith('.jpeg')) {
                imageFile = files[i]
              }
            }
            if (imageFile!=null) {
              // upload the image
              postEventId++
              notifyListeners(new HttpPostEvent(
                      isTrack: false,
                      fileName:imageFile.absolutePath,
                      postEventId: postEventId))

              filePost = new PostMethod(targetImgUrl);
              try {
                  Part[] parts = [
                          new FilePart("data", imageFile),
                          new StringPart("upload", "true"),
                          new StringPart("track", trackId.toString())]
                  filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
                  _httpClient.executeMethod(filePost);
              } finally {
                  filePost.releaseConnection();
              }
              notifyListeners(new HttpPostEvent(
                      isTrack: false,
                      fileName:imageFile.absolutePath,
                      postEventId: postEventId,
                      isCompleted: true))
            }
          }
        } catch(Exception e) {
          notifyListeners(new ErrorEvent(fileName:f.absolutePath, reason: e))
        }
      }
    }
  }

  public static void main(String[] args) {
    new Uploader().
      url('http://localhost:8080/ladaube').
      username('remi').
      password('remi').
      baseDir(new File('C:/Users/vankeisb/Music/Music')).
      addListeners([EVT_LOG_CLOSURE]).
      upload()
  }

}