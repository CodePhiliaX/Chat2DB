package com.alibaba.dbhub.server.domain.api.service;

import com.alibaba.dbhub.server.domain.api.model.User;
import com.alibaba.dbhub.server.domain.api.param.UserQueryParam;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.PageResult;

/**
 * 用户服务
 *
 * @author Jiaju Zhuang
 */
public interface UserService {

    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    DataResult<User> query(Long id);

    /**
     * gen
     * @param userName
     * @return
     */
    DataResult<User> query(String userName);

    /**
     * 查询用户信息
     *
     * @param param
     * @return
     */
    PageResult<User> queryPage(UserQueryParam param);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    DataResult<Boolean> update(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    DataResult<Boolean> delete(Long id);

    /**
     * 创建一个用户
     * @param user
     * @return
     */
    DataResult<Long> create(User user);
}
