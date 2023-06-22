package ai.chat2db.server.domain.core.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.repository.entity.DbhubUserDO;

import org.mapstruct.Mapper;

/**
 * 转换器
 *
 * @author Jiaju Zhuang
 */
@Mapper(componentModel = "spring")
public abstract class UserConverter {

    /**
     * 转换
     *
     * @param data
     * @return
     */
    public abstract User do2dto(DbhubUserDO data);

    /**
     * 转换
     *
     * @param datas
     * @return
     */
    public abstract List<User> do2dto(List<DbhubUserDO> datas);

    /**
     *
     * @param user
     * @return
     */
    public abstract DbhubUserDO dto2do(User user);
}
