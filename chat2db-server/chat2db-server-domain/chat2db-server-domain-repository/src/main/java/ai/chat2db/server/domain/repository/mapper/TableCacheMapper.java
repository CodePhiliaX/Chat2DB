package ai.chat2db.server.domain.repository.mapper;

import ai.chat2db.server.domain.repository.entity.TableCacheDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * table cache Mapper 接口
 * </p>
 *
 * @author chat2db
 * @since 2023-10-11
 */
public interface TableCacheMapper extends BaseMapper<TableCacheDO> {

    void batchInsert(List<TableCacheDO> list);
}
