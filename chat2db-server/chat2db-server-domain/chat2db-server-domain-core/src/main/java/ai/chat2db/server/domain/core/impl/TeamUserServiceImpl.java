package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.param.team.user.TeamUserComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserCreatParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserPageQueryParam;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.domain.api.service.TeamUserService;
import ai.chat2db.server.domain.core.converter.TeamConverter;
import ai.chat2db.server.domain.core.converter.TeamUserConverter;
import ai.chat2db.server.domain.core.converter.UserConverter;
import ai.chat2db.server.domain.repository.entity.TeamUserDO;
import ai.chat2db.server.domain.repository.mapper.TeamUserCustomMapper;
import ai.chat2db.server.domain.repository.mapper.TeamUserMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private TeamUserMapper teamUserMapper;
    @Resource
    private UserConverter userConverter;
    @Resource
    private TeamConverter teamConverter;

    @Override
    public PageResult<TeamUser> pageQuery(TeamUserPageQueryParam param, TeamUserSelector selector) {
        LambdaQueryWrapper<TeamUserDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamUserDO::getTeamId, param.getTeamId())
            .eq(TeamUserDO::getUserId, param.getUserId())
        ;

        Page<TeamUserDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setSearchCount(param.getEnableReturnCount());
        IPage<TeamUserDO> iPage = teamUserMapper.selectPage(page, queryWrapper);

        List<TeamUser> list = teamUserConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public PageResult<TeamUser> comprehensivePageQuery(TeamUserComprehensivePageQueryParam param,
        TeamUserSelector selector) {
        Page<TeamUserDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setSearchCount(param.getEnableReturnCount());
        IPage<TeamUserDO> iPage = teamUserCustomMapper.comprehensivePageQuery(page, param.getTeamId(),
            param.getUserId(), param.getTeamSearchKey(), param.getUserSearchKey());

        List<TeamUser> list = teamUserConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public DataResult<Long> create(TeamUserCreatParam param) {
        TeamUserDO data = teamUserConverter.param2do(param, ContextUtils.getUserId());

        teamUserMapper.insert(data);
        return DataResult.of(data.getId());
    }

    @Override
    public ActionResult delete(Long id) {
        teamUserMapper.deleteById(id);
        return ActionResult.isSuccess();
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
        if (BooleanUtils.isNotTrue(selector.getTeam())) {
            return;
        }
        teamConverter.fillDetail(EasyCollectionUtils.toList(list, TeamUser::getTeam));
    }
}
