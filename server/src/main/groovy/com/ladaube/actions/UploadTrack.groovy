package com.ladaube.actions

import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.DontValidate
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.FileBean
import net.sourceforge.stripes.validation.Validate

import net.sourceforge.stripes.action.SimpleMessage
import net.sourceforge.stripes.validation.SimpleError
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.model.LaDaube
import com.ladaube.util.auth.RequiresAuthentication
import com.ladaube.util.rpc.FatClientEvent
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaubeSession
import com.ladaube.model.TrackAlreadyExistException
import org.apache.log4j.Logger

@UrlBinding("/upload")
@RequiresAuthentication
public class UploadTrack extends BaseAction {

  private static final Logger logger = Logger.getLogger(UploadTrack.class)

  @Validate(required=true)
  FileBean data

  // used to store created track for RPC resolution
  private def track;
  
  @DefaultHandler
  @DontValidate
  Resolution display() {
    return new ForwardResolution("/WEB-INF/jsp/upload.jsp")    
  }

  @FatClientEvent(alternateResolution = 'uploadRpc')
  Resolution upload() {
    track = null
    LaDaube.get().doInSession { LaDaubeSession s ->
      String fileName = data.fileName
      int i = data.fileName.lastIndexOf('.')
      if (i!=-1) {
        fileName = fileName.substring(0, i)
      }
      try {
        track = s.createTrack(user, data.inputStream, fileName)                
      } catch(TrackAlreadyExistException e) {
        getContext().getValidationErrors().addGlobalError(new SimpleError('This track already exist.'))
      }
    }
    if (track) {
      getContext().getMessages().add(new SimpleMessage("Track $track.name uploaded"))
      logger.debug("$user.id uploaded track $track.name ($track.id)")
    } else {
      String fileName = data.fileName
      getContext().getValidationErrors().addGlobalError(new SimpleError("Unable to create track for file $fileName"))
    }
    return new RedirectResolution('/booz')
  }

  Resolution uploadRpc() {
    JsonUtil u = new JsonUtil()
    if (track) {
      return u.resolution(u.trackToJson(track).toString())
    } else {
      return u.resolution('{error: true}')
    }
  }

}