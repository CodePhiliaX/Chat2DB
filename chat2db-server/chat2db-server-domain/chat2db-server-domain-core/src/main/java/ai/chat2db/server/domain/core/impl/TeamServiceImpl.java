package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.enums.RoleCodeEnum;
import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.param.team.TeamCreateParam;
import ai.chat2db.server.domain.api.param.team.TeamPageQueryParam;
import ai.chat2db.server.domain.api.param.team.TeamSelector;
import ai.chat2db.server.domain.api.param.team.TeamUpdateParam;
import ai.chat2db.server.domain.api.service.TeamService;
import ai.chat2db.server.domain.core.converter.TeamConverter;
import ai.chat2db.server.domain.core.converter.UserConverter;
import ai.chat2db.server.domain.repository.entity.DataSourceAccessDO;
import ai.chat2db.server.domain.repository.entity.TeamDO;
import ai.chat2db.server.domain.repository.entity.TeamUserDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessMapper;
import ai.chat2db.server.domain.repository.mapper.TeamMapper;
import ai.chat2db.server.domain.repository.mapper.TeamUserMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.exception.DataAlreadyExistsBusinessException;
import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.tools.common.model.EasyLambdaQueryWrapper;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
    private TeamUserMapper teamUserMapper;
    @Resource
    private DataSourceAccessMapper dataSourceAccessMapper;
    @Resource
    private TeamConverter teamConverter;
    @Resource
    private UserConverter userConverter;

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

    @Override
    public PageResult<Team> pageQuery(TeamPageQueryParam param, TeamSelector selector) {
        EasyLambdaQueryWrapper<TeamDO> queryWrapper = new EasyLambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getSearchKey())) {
            queryWrapper.and(wrapper -> wrapper.like(TeamDO::getCode, "%" + param.getSearchKey() + "%")
                .or()
                .like(TeamDO::getName, "%" + param.getSearchKey() + "%"));
        }
        Page<TeamDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setSearchCount(param.getEnableReturnCount());
        queryWrapper.orderBy(param.getOrderByList());
        IPage<TeamDO> iPage = teamMapper.selectPage(page, queryWrapper);
        List<Team> list = teamConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public DataResult<Long> create(TeamCreateParam param) {
        LambdaQueryWrapper<TeamDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamDO::getCode, param.getCode());
        Page<TeamDO> page = new Page<>(1, 1);
        page.setSearchCount(false);
        IPage<TeamDO> iPage = teamMapper.selectPage(page, queryWrapper);
        if (CollectionUtils.isNotEmpty(iPage.getRecords())) {
            throw new DataAlreadyExistsBusinessException("code", param.getCode());
        }
        if (RoleCodeEnum.DESKTOP.getCode().equals(param.getRoleCode())) {
            throw new ParamBusinessException("roleCode");
        }

        TeamDO data = teamConverter.param2do(param, ContextUtils.getUserId());
        teamMapper.insert(data);
        return DataResult.of(data.getId());
    }

    @Override
    public DataResult<Long> update(TeamUpdateParam param) {
        TeamDO data = teamConverter.param2do(param, ContextUtils.getUserId());
        teamMapper.updateById(data);
        return DataResult.of(data.getId());
    }

    @Override
    public ActionResult delete(Long id) {
        teamMapper.deleteById(id);

        LambdaQueryWrapper<TeamUserDO> teamUserQueryWrapper = new LambdaQueryWrapper<>();
        teamUserQueryWrapper.eq(TeamUserDO::getTeamId, id);
        teamUserMapper.delete(teamUserQueryWrapper);

        LambdaQueryWrapper<DataSourceAccessDO>  dataSourceAccessQueryWrapper = new LambdaQueryWrapper<>();
        dataSourceAccessQueryWrapper.eq(DataSourceAccessDO::getAccessObjectId, id)
            .eq(DataSourceAccessDO::getAccessObjectType, AccessObjectTypeEnum.TEAM.getCode())
        ;
        dataSourceAccessMapper.delete(dataSourceAccessQueryWrapper);
        return ActionResult.isSuccess();
    }

    private void fillData(List<Team> list, TeamSelector selector) {
        if (CollectionUtils.isEmpty(list) || selector == null) {
            return;
        }
        fillUser(list, selector);
    }

    private void fillUser(List<Team> list, TeamSelector selector) {
        if (BooleanUtils.isNotTrue(selector.getModifiedUser())) {
            return;
        }
        userConverter.fillDetail(EasyCollectionUtils.toList(list, Team::getModifiedUser));
    }

}
