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
 * User service
 *
 * @author Jiaju Zhuang
 */
public interface UserService {

    /**
     * Query user information
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
     * Query user information
     *
     * @param param
     * @return
     */
    PageResult<User> pageQuery(UserPageQueryParam param, UserSelector selector);

    /**
     * Update user information
     * @param user
     * @return
     */
    DataResult<Long> update(UserUpdateParam user);

    /**
     * delete users
     * @param id
     * @return
     */
   ActionResult delete(Long id);

    /**
     * Create a user
     * @param user
     * @return
     */
    DataResult<Long> create(UserCreateParam user);
}
