package ai.chat2db.server.domain.api.service;

import java.util.List;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.user.UserCreateParam;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserSelector;
import ai.chat2db.server.domain.api.param.user.UserUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;

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
    User query(Long id);

    /**
     * gen
     * @param userName
     * @return
     */
    User query(String userName);

    /**
     * List Query Data
     *
     * @param idList
     * @return
     */
    List<User> listQuery(List<Long> idList);

    /**
     * 查询用户信息
     *
     * @param param
     * @return
     */
    ServicePage<User> pageQuery(UserPageQueryParam param, UserSelector selector);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Long update(UserUpdateParam user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    void delete(Long id);

    /**
     * 创建一个用户
     * @param user
     * @return
     */
    Long create(UserCreateParam user);
}
