package ai.chat2db.server.domain.core.impl;

import java.util.List;
import java.util.Map;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.model.DataSourceAccess;
import ai.chat2db.server.domain.api.model.DataSourceAccessObject;
import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.model.TeamUser;
import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessPageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.domain.api.param.team.user.TeamUserSelector;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.domain.api.service.TeamService;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.domain.core.converter.DataSourceAccessConverter;
import ai.chat2db.server.domain.core.converter.DataSourceConverter;
import ai.chat2db.server.domain.repository.entity.DataSourceAccessDO;
import ai.chat2db.server.domain.repository.entity.TeamUserDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessCustomMapper;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Service
public class DataSourceAccessServiceImpl implements DataSourceAccessService {

    @Resource
    private DataSourceAccessCustomMapper dataSourceAccessCustomMapper;
    @Resource
    private DataSourceAccessMapper dataSourceAccessMapper;
    @Resource
    private DataSourceAccessConverter dataSourceAccessConverter;
    @Resource
    private DataSourceConverter dataSourceConverter;
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;

    @Override
    public PageResult<DataSourceAccess> pageQuery(DataSourceAccessPageQueryParam param, DataSourceAccessSelector selector) {
        LambdaQueryWrapper<DataSourceAccessDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DataSourceAccessDO::getDataSourceId, param.getDataSourceId())
            .eq(DataSourceAccessDO::getAccessObjectType, param.getAccessObjectType())
            .eq(DataSourceAccessDO::getAccessObjectId, param.getAccessObjectId())
        ;

        Page<DataSourceAccessDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setSearchCount(param.getEnableReturnCount());
        IPage<DataSourceAccessDO> iPage = dataSourceAccessMapper.selectPage(page, queryWrapper);

        List<DataSourceAccess> list = dataSourceAccessConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public PageResult<DataSourceAccess> comprehensivePageQuery(DataSourceAccessComprehensivePageQueryParam param,
        DataSourceAccessSelector selector) {
        Page<DataSourceAccessDO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page.setSearchCount(param.getEnableReturnCount());
        IPage<DataSourceAccessDO> iPage = dataSourceAccessCustomMapper.comprehensivePageQuery(page,
            param.getDataSourceId(), param.getAccessObjectType(), param.getAccessObjectId(),
            param.getUserOrTeamSearchKey(),
            param.getDataSourceSearchKey());

        List<DataSourceAccess> list = dataSourceAccessConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public DataResult<Long> create(DataSourceAccessCreatParam param) {
        DataSourceAccessDO data = dataSourceAccessConverter.param2do(param, ContextUtils.getUserId());

        dataSourceAccessMapper.insert(data);
        return DataResult.of(data.getId());
    }

    @Override
    public ActionResult delete(Long id) {
        dataSourceAccessMapper.deleteById(id);
        return ActionResult.isSuccess();
    }

    private void fillData(List<DataSourceAccess> list, DataSourceAccessSelector selector) {
        if (CollectionUtils.isEmpty(list) || selector == null) {
            return;
        }

        fillAccessObject(list, selector);

        fillDataSource(list, selector);
    }

    private void fillDataSource(List<DataSourceAccess> list, DataSourceAccessSelector selector) {
        if (BooleanUtils.isNotTrue(selector.getDataSource())) {
            return;
        }
        dataSourceConverter.fillDetail(EasyCollectionUtils.toList(list, DataSourceAccess::getDataSource),
            selector.getDataSourceSelector());
    }

    private void fillAccessObject(List<DataSourceAccess> list, DataSourceAccessSelector selector) {
        if (BooleanUtils.isNotTrue(selector.getAccessObject())) {
            return;
        }
        List<Long> userIdList = Lists.newArrayList();
        List<Long> teamIdList = Lists.newArrayList();
        for (DataSourceAccess data : list) {
            if (AccessObjectTypeEnum.TEAM.getCode().equals(data.getAccessObjectType())) {
                teamIdList.add(data.getAccessObjectId());
            } else if (AccessObjectTypeEnum.USER.getCode().equals(data.getAccessObjectType())) {
                userIdList.add(data.getAccessObjectId());
            }
        }
        List<User> userList = userService.listQuery(userIdList).getData();
        Map<Long, User> userMap = EasyCollectionUtils.toIdentityMap(userList, User::getId);
        List<Team> teamList = teamService.listQuery(teamIdList).getData();
        Map<Long, Team> teamMap = EasyCollectionUtils.toIdentityMap(teamList, Team::getId);
        for (DataSourceAccess data : list) {
            DataSourceAccessObject dataSourceAccessObject = data.getAccessObject();
            if (dataSourceAccessObject == null) {
                continue;
            }
            if (AccessObjectTypeEnum.TEAM.getCode().equals(data.getAccessObjectType())) {
                Team team = teamMap.get(data.getAccessObjectId());
                if (team == null) {
                    continue;
                }
                dataSourceAccessObject.setCode(team.getCode());
                dataSourceAccessObject.setName(team.getName());
            } else if (AccessObjectTypeEnum.USER.getCode().equals(data.getAccessObjectType())) {
                User user = userMap.get(data.getAccessObjectId());
                if (user == null) {
                    continue;
                }
                dataSourceAccessObject.setCode(user.getUserName());
                dataSourceAccessObject.setName(user.getNickName());
            }
        }
    }

}
