package ai.chat2db.server.start.controller.thymeleaf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 模板引擎 配置
 *
 * @author Jiaju Zhuang
 */
@Controller
@Slf4j
@Order(Integer.MIN_VALUE)
public class ThymeleafController {

    /**
     * 前端的模板设置
     *
     * @return
     */
    @GetMapping(value = {"/", "/web/", "/web/**","/login"})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/chat.html", method={RequestMethod.GET}, produces="text/html;charset=utf-8")
    public String chat(){

        return "chat";
    }
}
