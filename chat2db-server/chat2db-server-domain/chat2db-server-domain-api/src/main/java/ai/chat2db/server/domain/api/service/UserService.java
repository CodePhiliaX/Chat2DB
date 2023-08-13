package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.user.UserCreateParam;
import ai.chat2db.server.domain.api.param.user.UserSelector;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

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
     * List Query Data
     *
     * @param idList
     * @return
     */
    ListResult<User> listQuery(List<Long> idList);

    /**
     * 查询用户信息
     *
     * @param param
     * @return
     */
    PageResult<User> pageQuery(UserPageQueryParam param, UserSelector selector);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    DataResult<Long> update(UserUpdateParam user);

    /**
     * 删除用户
     * @param id
     * @return
     */
   ActionResult delete(Long id);

    /**
     * 创建一个用户
     * @param user
     * @return
     */
    DataResult<Long> create(UserCreateParam user);
}
