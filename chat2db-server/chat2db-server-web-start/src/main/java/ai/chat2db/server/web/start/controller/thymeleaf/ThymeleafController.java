package ai.chat2db.server.web.start.controller.thymeleaf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Template engine configuration
 *
 * @author Jiaju Zhuang
 */
@Controller
@Slf4j
@Order(Integer.MIN_VALUE)
public class ThymeleafController {

    /**
     * Front-end template settings
     *
     * @return
     */
    @GetMapping(value = {"/", "/web/", "/web/**","/login","/workspace","/dashboard","/connections","/team"})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/chat.html", method={RequestMethod.GET}, produces="text/html;charset=utf-8")
    public String chat(){

        return "chat";
    }
}
