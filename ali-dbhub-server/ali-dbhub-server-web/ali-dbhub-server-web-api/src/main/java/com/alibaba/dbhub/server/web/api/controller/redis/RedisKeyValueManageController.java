package com.alibaba.dbhub.server.web.api.controller.redis;

import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.web.api.aspect.BusinessExceptionAspect;
import com.alibaba.dbhub.server.web.api.controller.redis.request.KeyQueryRequest;
import com.alibaba.dbhub.server.web.api.controller.redis.request.KeyValueManageRequest;
import com.alibaba.dbhub.server.web.api.controller.redis.request.ValueUpdateRequest;
import com.alibaba.dbhub.server.web.api.controller.redis.vo.KeyVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis数据运维类
 *
 * @author moji
 * @version MysqlDataManageController.java, v 0.1 2022年09月16日 17:37 moji Exp $
 * @date 2022/09/16
 */
@BusinessExceptionAspect
@RequestMapping("/api/redis/kv")
@RestController
public class RedisKeyValueManageController {

    /**
     * redis ddl命令执行
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/manage",method = {RequestMethod.POST, RequestMethod.PUT})
    public DataResult<Object> manage(@RequestBody KeyValueManageRequest request) {
        return null;
    }

    /**
     * 获取缓存key详情
     *
     * @param request
     * @return
     */
    @GetMapping("/query")
    public DataResult<KeyVO> query(KeyQueryRequest request) {
        return null;
    }

    /**
     * 更新key值
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/update",method = {RequestMethod.POST, RequestMethod.PUT})
    public ActionResult update(@RequestBody ValueUpdateRequest request) {
        return null;
    }

}
