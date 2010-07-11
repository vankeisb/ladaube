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

import com.ladaube.modelcouch.Track
import com.ladaube.model.LaDaubeSession

@UrlBinding('/image/{track}')
@RequiresAuthentication
public class TrackImage extends BaseAction {

  @Validate(required = true)
  Track track

  @Validate(required = true, on = ['upload'])
  FileBean data

  @DontValidate
  Resolution uploadForm() {
    return new ForwardResolution('/WEB-INF/jsp/upload-image.jsp')
  }

  Resolution upload() {
    LaDaube.get().doInSession { LaDaubeSession s ->
      s.createImageForTrack(track, data.fileName, data.inputStream)
    }
    context.messages << new SimpleMessage('Image uploaded and associated to track.')
    return new RedirectResolution(TrackImage.class).addParameter('track', track.id)
  }

  def getUserTracks() {
    def tracks = []
    LaDaube.get().doInSession { LaDaubeSession s ->
      def it = s.getUserTracks(user, false, null)
      while (it.hasNext()) {
        tracks << it.next()
      }
    }
    return tracks
  }

  @DefaultHandler
  Resolution stream() {
    return null
  }

}