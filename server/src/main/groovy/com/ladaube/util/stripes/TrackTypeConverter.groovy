package com.ladaube.util.stripes

import net.sourceforge.stripes.validation.ValidationError
import com.ladaube.modelcouch.LaDaube
import com.ladaube.modelcouch.Track
import net.sourceforge.stripes.validation.TypeConverter
import com.ladaube.modelcouch.LaDaubeSession

public class TrackTypeConverter implements TypeConverter<Track> {

  public void setLocale(Locale locale) {
  }

  public Track convert(String s, Class<? extends Track> aClass, Collection<ValidationError> validationErrors) {
    return LaDaube.get().doInSession { LaDaubeSession session ->
      return session.getTrack(s)
    }
  }
}