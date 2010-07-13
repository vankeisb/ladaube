package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.DontValidate
import net.sourceforge.stripes.validation.Validate
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.FileBean
import com.ladaube.model.LaDaube
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.SimpleMessage
import com.ladaube.util.auth.RequiresAuthentication

import com.ladaube.model.LaDaubeSession
import net.sourceforge.stripes.action.StreamingResolution
import com.ladaube.util.TransferStreams
import com.mongodb.BasicDBObject
import com.mongodb.gridfs.GridFSFile
import javax.servlet.http.HttpServletResponse

@UrlBinding('/image/{track}')
@RequiresAuthentication
public class TrackImage extends BaseAction {

  @Validate(required = true)
  String track

  @Validate(required = true, on = ['upload'])
  FileBean data

  @DontValidate
  Resolution uploadForm() {
    return new ForwardResolution('/WEB-INF/jsp/upload-image.jsp')
  }

  Resolution upload() {
    LaDaube.doInSession { LaDaubeSession s ->
      def t = s.getTrack(track)
      s.createImageForTrack(t, data.fileName, data.inputStream)
    }
    context.messages << new SimpleMessage('Image uploaded and associated to track.')
    return new RedirectResolution(TrackImage.class).addParameter('track', track)
  }

  def getUserTracks() {
    LaDaube.doInSession { LaDaubeSession s ->
      return s.getUserTracks(user, false, null)
    }
  }

  @DefaultHandler
  Resolution stream() {
    InputStream is
    def t = LaDaube.doInSession { LaDaubeSession s ->
      return s.getTrack(track)
    }
    return new ImageResolution(t)
  }

}

class ImageResolution extends StreamingResolution {

  private def t

  def ImageResolution(def t) {
    super('image/jpeg')
    this.t = t
    setFilename('trackImage.mp3')
  }

  void stream(HttpServletResponse response) throws Exception {
     LaDaube.doInSession { LaDaubeSession s ->
      s.writeTrackImageToStream(t, response.getOutputStream())
     }
  }

}