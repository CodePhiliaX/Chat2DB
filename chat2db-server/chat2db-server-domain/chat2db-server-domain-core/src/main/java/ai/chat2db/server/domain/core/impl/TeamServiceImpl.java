package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.service.TeamService;
import ai.chat2db.server.domain.core.converter.TeamConverter;
import ai.chat2db.server.domain.repository.entity.TeamDO;
import ai.chat2db.server.domain.repository.mapper.TeamMapper;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * team
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

    @Resource
    private TeamMapper teamMapper;
    @Resource
    private TeamConverter teamConverter;

    @Override
    public ListResult<Team> listQuery(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ListResult.empty();
        }
        LambdaQueryWrapper<TeamDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TeamDO::getId, idList);
        List<TeamDO> dataList = teamMapper.selectList(queryWrapper);
        List<Team> list = teamConverter.do2dto(dataList);
        return ListResult.of(list);
    }
}
