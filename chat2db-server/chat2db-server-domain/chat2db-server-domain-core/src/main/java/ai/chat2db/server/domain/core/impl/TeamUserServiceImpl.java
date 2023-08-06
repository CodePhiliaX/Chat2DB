package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.domain.api.service.TeamUserService;
import ai.chat2db.server.domain.core.converter.TeamConverter;
import ai.chat2db.server.domain.core.converter.TeamUserConverter;
import ai.chat2db.server.domain.core.converter.UserConverter;
import ai.chat2db.server.domain.repository.entity.TeamUserDO;
import ai.chat2db.server.domain.repository.mapper.TeamUserCustomMapper;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * Team User
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Service
public class TeamUserServiceImpl implements TeamUserService {

    @Resource
    private TeamUserConverter teamUserConverter;
    @Resource
    private TeamUserCustomMapper teamUserCustomMapper;
    @Resource
    private UserConverter userConverter;
    @Resource
    private TeamConverter teamConverter;

    @Override
    public PageResult<TeamUser> comprehensivePageQuery(TeamUserComprehensivePageQueryParam param,
        TeamUserSelector selector) {
        IPage<TeamUserDO> iPage = teamUserCustomMapper.comprehensivePageQuery(
            new Page<>(param.getPageNo(), param.getPageSize()), param.getTeamId(), param.getUserId(),
            param.getTeamRoleCode());

        List<TeamUser> list = teamUserConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    private void fillData(List<TeamUser> list, TeamUserSelector selector) {
        if (CollectionUtils.isEmpty(list) || selector == null) {
            return;
        }

        fillUser(list, selector);

        fillTeam(list, selector);
    }

    private void fillUser(List<TeamUser> list, TeamUserSelector selector) {
        if (BooleanUtils.isNotTrue(selector.getUser())) {
            return;
        }
        userConverter.fillDetail(EasyCollectionUtils.toList(list, TeamUser::getUser));
    }

    private void fillTeam(List<TeamUser> list, TeamUserSelector selector) {
        if (BooleanUtils.isNotTrue(selector.getUser())) {
            return;
        }
        teamConverter.fillDetail(EasyCollectionUtils.toList(list, TeamUser::getTeam));
    }
}
