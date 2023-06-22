package ai.chat2db.server.start.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //// 处理逻辑
        //MyResult result = new MyResult();
        //result.setCode(200);
        //result.setMessage("处理成功");
        //request.setAttribute("myResult", result);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //// 取出处理结果并封装成实体对象
        //MyResult result = (MyResult) request.getAttribute("myResult");
        //modelAndView.setView(new MappingJackson2JsonView());
        //modelAndView.addObject(result);
        log.info("xx");
    }
}