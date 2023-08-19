package ai.chat2db.server.domain.repository.mapper;

import ai.chat2db.server.domain.repository.entity.DataSourceAccessDO;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

/**
 * Data Source Access Mapper
 *
 * @author Jiaju Zhuang
 */
public interface DataSourceAccessCustomMapper extends Mapper<DataSourceAccessDO> {

    IPage<DataSourceAccessDO> comprehensivePageQuery(IPage<DataSourceAccessDO> page, @Param("dataSourceId") Long dataSourceId,
        @Param("accessObjectType") String accessObjectType,
        @Param("accessObjectId") Long accessObjectId,
        @Param("userOrTeamSearchKey") String userOrTeamSearchKey,
        @Param("dataSourceSearchKey") String dataSourceSearchKey);

    DataSourceAccessDO checkTeamPermission( @Param("dataSourceId") Long dataSourceId, @Param("userId") Long userId);
}
