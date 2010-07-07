package com.ladaube.util.stripes

import net.sourceforge.stripes.validation.ValidationError
import com.ladaube.modelcouch.LaDaube
import com.ladaube.modelcouch.Playlist
import net.sourceforge.stripes.validation.TypeConverter
import com.ladaube.modelcouch.LaDaubeSession

public class PlaylistTypeConverter implements TypeConverter<Playlist> {

  public void setLocale(Locale locale) {
  }

  public Playlist convert(String s, Class<? extends Playlist> aClass, Collection<ValidationError> validationErrors) {
    return LaDaube.get().doInSession { LaDaubeSession session ->
      return session.getPlaylist(s)
    }    
  }


}