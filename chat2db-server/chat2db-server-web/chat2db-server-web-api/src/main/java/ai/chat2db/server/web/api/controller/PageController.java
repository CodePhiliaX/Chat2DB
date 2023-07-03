
package ai.chat2db.server.web.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author jipengfei
 * @version : PageController.java
 *     patterns.add("/register.html");
 *         patterns.add("/login.html");
 *         patterns.add("/users/reg");
 *         patterns.add("/users/login");
 */
@Slf4j
@RequestMapping("/")
@Controller
public class PageController {

    @RequestMapping(value = "/login.html", method={RequestMethod.GET}, produces="text/html;charset=utf-8")
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/register.html", method={RequestMethod.GET}, produces="text/html;charset=utf-8")
    public String register(){
        return "register";
    }

}
