package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution

import com.ladaube.model.LaDaube
import net.sourceforge.stripes.action.RedirectResolution
import com.ladaube.model.LaDaubeSession

@UrlBinding('/populate')
public class Populate extends BaseAction {

  Resolution doIt() {
    LaDaube.get().doInSession { LaDaubeSession s ->

      // create a bunch of users
      def remi = s.createUser('remi', 'remi')

      def kakou = s.createUser('kakou', 'kakou')
      s.makeBuddies(remi, kakou)

      def alex = s.createUser('alex', 'alex')
      s.makeBuddies(remi, alex)
      s.makeBuddies(alex, kakou)

      def flow = s.createUser('flow', 'flow')
      s.makeBuddies(alex, flow)
      s.makeBuddies(flow, remi)

      def eva = s.createUser('eva', 'eva')
      s.makeBuddies(eva, remi)
    }
    return new RedirectResolution('/');
  }

}