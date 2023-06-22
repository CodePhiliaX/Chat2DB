package ai.chat2db.server.domain.core.impl;

import java.util.Objects;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.UserQueryParam;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.domain.core.converter.UserConverter;
import ai.chat2db.server.domain.repository.entity.DbhubUserDO;
import ai.chat2db.server.domain.repository.mapper.DbhubUserMapper;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 *
 * @author 是仪
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private DbhubUserMapper dbhubUserMapper;
    @Resource
    private UserConverter userConverter;

    @Override
    public DataResult<User> query(Long id) {
        return DataResult.of(userConverter.do2dto(dbhubUserMapper.selectById(id)));
    }

    @Override
    public DataResult<User> query(String userName) {
        LambdaQueryWrapper<DbhubUserDO> query = new LambdaQueryWrapper<>();
        if (Objects.nonNull(userName)) {
            query.eq(DbhubUserDO::getUserName, userName);
        }
        DbhubUserDO dbhubUserDO = dbhubUserMapper.selectOne(query);
        return DataResult.of(userConverter.do2dto(dbhubUserDO));
    }

    @Override
    public PageResult<User> queryPage(UserQueryParam param) {
        LambdaQueryWrapper<DbhubUserDO> query = new LambdaQueryWrapper<>();
        if (Objects.nonNull(param.getKeyWord())) {
            query.like(DbhubUserDO::getUserName, param.getKeyWord());
        }
        Page<DbhubUserDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setOptimizeCountSql(false);
        IPage<DbhubUserDO> iPage = dbhubUserMapper.selectPage( page,query);
       return PageResult.of(userConverter.do2dto(iPage.getRecords()), iPage.getTotal(), param);
    }

    @Override
    public DataResult<Boolean> update(User user) {
        DbhubUserDO dbhubUserDO = userConverter.dto2do(user);
        if(Objects.nonNull(dbhubUserDO.getPassword())){
            String bcryptPassword = DigestUtil.bcrypt(dbhubUserDO.getPassword());
            dbhubUserDO.setPassword(bcryptPassword);
        }
        int n = dbhubUserMapper.updateById(dbhubUserDO);
        return DataResult.of(n == 1);
    }

    @Override
    public DataResult<Boolean> delete(Long id) {
        int n = dbhubUserMapper.deleteById(id);
        return DataResult.of(n == 1);
    }

    @Override
    public DataResult<Long> create(User user) {
        DbhubUserDO data = userConverter.dto2do(user);
        String bcryptPassword = DigestUtil.bcrypt(data.getPassword());
        data.setPassword(bcryptPassword);
        dbhubUserMapper.insert(data);
        return DataResult.of(data.getId());
    }
}
