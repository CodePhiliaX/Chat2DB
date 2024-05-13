package ai.chat2db.server.web.api.controller.redis;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.controller.redis.request.KeyQueryRequest;
import ai.chat2db.server.web.api.controller.redis.request.KeyValueManageRequest;
import ai.chat2db.server.web.api.controller.redis.request.ValueUpdateRequest;
import ai.chat2db.server.web.api.controller.redis.vo.KeyVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis data operation and maintenance class
 *
 * @author moji
 * @version MysqlDataManageController.java, v 0.1 September 16, 2022 17:37 moji Exp $
 * @date 2022/09/16
 */
@RequestMapping("/api/redis/kv")
@RestController
public class RedisKeyValueManageController {

    /**
     * redis ddl command execution
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/manage",method = {RequestMethod.POST, RequestMethod.PUT})
    public DataResult<Object> manage(@RequestBody KeyValueManageRequest request) {
        return null;
    }

    /**
     * Get cache key details
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public DataResult<KeyVO> query(KeyQueryRequest request) {
        return null;
    }

    /**
     * Update key value
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update",method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody ValueUpdateRequest request) {
        return null;
    }

}
