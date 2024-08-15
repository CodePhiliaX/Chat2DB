package ai.chat2db.server.start.test.core;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.user.UserCreateParam;
import ai.chat2db.server.domain.api.param.user.UserPageQueryParam;
import ai.chat2db.server.domain.api.param.user.UserSelector;
import ai.chat2db.server.domain.api.param.user.UserUpdateParam;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.domain.repository.Dbutils;
import ai.chat2db.server.start.test.TestApplication;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.model.Context;
import ai.chat2db.server.tools.common.model.LoginUser;
import ai.chat2db.server.tools.common.util.ContextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Juechen
 * @version : UserServiceTest.java
 */
public class UserServiceTest extends TestApplication {

    @Autowired
    private UserService userService;

    @Test
    public void testAllMethods() {
        userLoginIdentity(false,8L);

        UserCreateParam userCreateParam = new UserCreateParam();
        userCreateParam.setUserName("test_username08");
        userCreateParam.setEmail("123456789@gmail.com");
        userCreateParam.setPassword("123456");
        userCreateParam.setRoleCode("TEST");
        userCreateParam.setStatus("VALID");
        userCreateParam.setNickName("test_username686");
        DataResult<Long> dataResult = userService.create(userCreateParam);
        System.out.println("create id:" + dataResult.getData());
        Assertions.assertTrue(dataResult.getSuccess(),dataResult.getErrorMessage());

        DataResult<User> query = userService.query(dataResult.getData());
        System.out.println("Specify id：" + query.getData());
        Assertions.assertTrue(query.getSuccess(),query.getErrorMessage());

        DataResult<User> user_name = userService.query("_desktop_default_user_name");
        System.out.println("Specify user_name: " + user_name.getData());
        Assertions.assertTrue(user_name.getSuccess(),user_name.getErrorMessage());

        UserPageQueryParam param = new UserPageQueryParam();
        param.setPageNo(1);
        param.setPageSize(8);
        param.setEnableReturnCount(false);
        param.setSearchKey("");
        UserSelector selector = new UserSelector();
        selector.setModifiedUser(false);

        PageResult<User> result = userService.pageQuery(param, selector);
        for (User user : result.getData()) {
            System.out.println("list:" + user);
        }
        Assertions.assertTrue(result.getSuccess(),result.getErrorMessage());

        // if id is 1, an BusinessException will be thrown
        ActionResult actionResult = userService.delete(dataResult.getData());
        Assertions.assertTrue(actionResult.getSuccess(),actionResult.getErrorMessage());

        PageResult<User> pageQuery = userService.pageQuery(param, selector);
        for (User user : pageQuery.getData()) {
            System.out.println("After deletion list:" + user);
        }
        Assertions.assertTrue(pageQuery.getSuccess(),pageQuery.getErrorMessage());

    }

    @Test
    public void testUpdate() {
        userLoginIdentity(false,8L);

        UserUpdateParam userUpdateParam = new UserUpdateParam();
        // If the id is 1, a "user.canNotOperateSystemAccount" exception will be thrown.
        // userUpdateParam.setId(1L);
        userUpdateParam.setId(3L);
        userUpdateParam.setRoleCode("TEST05");
        userUpdateParam.setStatus("INVALID");
        userUpdateParam.setEmail("385962@gmail.com");
        userUpdateParam.setPassword("385962");

        DataResult<User> query = userService.query(userUpdateParam.getId());
        System.out.println("Original data :" + query.getData());
        Assertions.assertTrue(query.getSuccess(),query.getErrorMessage());

        DataResult<Long> update = userService.update(userUpdateParam);
        System.out.println("update id :" + update.getData());
        Assertions.assertTrue(update.getSuccess(),update.getErrorMessage());

        DataResult<User> result = userService.query(userUpdateParam.getId());
        System.out.println("update data :" + result.getData());
        Assertions.assertTrue(result.getSuccess(),result.getErrorMessage());
    }

    /**
     * Save the current user identity (administrator or normal user) and user ID to the context and database session for subsequent use.
     *
     * @param isAdmin
     * @param userId
     */
    private static void userLoginIdentity(boolean isAdmin, Long userId) {
        Context context = Context.builder().loginUser(
                LoginUser.builder().admin(isAdmin).id(userId).build()
        ).build();
        ContextUtils.setContext(context);
        Dbutils.setSession();
    }
}
