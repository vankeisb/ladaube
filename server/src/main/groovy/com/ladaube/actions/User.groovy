package com.ladaube.actions

import net.sourceforge.stripes.action.UrlBinding
import net.sourceforge.stripes.action.Resolution
import net.sourceforge.stripes.action.ForwardResolution
import net.sourceforge.stripes.action.DefaultHandler
import net.sourceforge.stripes.validation.Validate
import org.json.JSONObject
import com.ladaube.util.JsonUtil
import com.ladaube.model.LaDaube
import com.ladaube.model.LaDaubeSession

@UrlBinding("/user")
class User extends BaseAction {

    @Validate(required=true, on=["save"])
    String email

    String pwd1
    String pwd2

    @DefaultHandler
    Resolution display() {
        JsonUtil u = new JsonUtil()
        return u.resolution(u.userToJson(getUser()))
    }

    Resolution save() {
        JsonUtil j = new JsonUtil()
        def u = getUser()
        u.email = email
        if (pwd1 || pwd2) {
            if (pwd1!=pwd2) {
                // passwords don't match
                return j.resolution(j.jsonError("Passwords don't match"))
            }
            // passwords do match : update
            u.password = pwd1
        }
        LaDaube.doInSession { LaDaubeSession s->
            s.updateUser(u)
        }
        JSONObject result = new JSONObject()
        result.put("error", false)
        return j.resolution(result)
    }

}
