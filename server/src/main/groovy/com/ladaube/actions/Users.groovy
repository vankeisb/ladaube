package com.ladaube.actions

import com.ladaube.util.auth.RequiresAuthentication
import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import com.ladaube.modelcouch.User
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession
import net.sourceforge.stripes.validation.Validate
import net.sourceforge.stripes.action.SimpleMessage
import net.sourceforge.stripes.action.RedirectResolution
import net.sourceforge.stripes.action.DefaultHandler

@UrlBinding("/users")
@RequiresAuthentication
class Users extends BaseAction {

  private Map<User,List<User>> usersMap = null
  private List<User> users = null

  @Validate(required=true, on=["addUser", "makeBuddies"])
  String username

  @Validate(required=true, on=["addUser"])
  String email

  @Validate(required=true, on=["makeBuddies"])
  String buddy

  @Validate(required=true)
  String password

  @DefaultHandler 
  Resolution display() {
    if (password!="youcandoit") {
      throw new IllegalStateException("this page is protected")
    }

    users = []
    usersMap = [:]
    LaDaube.get().doInSession { LaDaubeSession s ->
      s.getUsers().each { User u ->
        users << u
        usersMap.put(u, s.getBuddies(u))
      }
    }
    return new ForwardResolution('/WEB-INF/jsp/users.jsp');
  }

  Map<User,List<User>> getUsersMap() {
    return usersMap
  }

  List<User> getUsers() {
    return users
  }


  Resolution addUser() {

    if (password!="youcandoit") {
      throw new IllegalStateException("this page is protected")
    }

    LaDaube.get().doInSession { LaDaubeSession s ->
      def u = s.createUser(username, username)
      u.email = email
      s.updateUser(u)
    }
    context.messages.add(new SimpleMessage("user $username created"))
    return new RedirectResolution(getClass()).addParameter("password", password)
  }

  Resolution makeBuddies() {

    if (password!="youcandoit") {
      throw new IllegalStateException("this page is protected")
    }
    
    LaDaube.get().doInSession { LaDaubeSession s ->
      User u1 = s.getUser(username)
      User u2 = s.getUser(buddy)
      s.makeBuddies(u1, u2)
    }
    context.messages.add(new SimpleMessage("$username and $buddy are now connected"))
    return new RedirectResolution(getClass()).addParameter("password", password)
  }

}
