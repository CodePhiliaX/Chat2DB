package ai.chat2db.server.domain.core.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.domain.api.model.DataSourceAccess;
import ai.chat2db.server.domain.api.model.DataSourceAccessObject;
import ai.chat2db.server.domain.api.model.Team;
import ai.chat2db.server.domain.api.model.User;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessBatchCreatParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessComprehensivePageQueryParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessObjectParam;
import ai.chat2db.server.domain.api.param.datasource.access.DataSourceAccessSelector;
import ai.chat2db.server.domain.api.service.DataSourceAccessService;
import ai.chat2db.server.domain.api.service.TeamService;
import ai.chat2db.server.domain.api.service.UserService;
import ai.chat2db.server.domain.core.converter.DataSourceAccessConverter;
import ai.chat2db.server.domain.repository.entity.DataSourceAccessDO;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessCustomMapper;
import ai.chat2db.server.domain.repository.mapper.DataSourceAccessMapper;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
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
    private UserService userService;
    @Resource
    private TeamService teamService;

    @Override
    public PageResult<DataSourceAccess> comprehensivePageQuery(DataSourceAccessComprehensivePageQueryParam param,
        DataSourceAccessSelector selector) {
        IPage<DataSourceAccessDO> iPage = dataSourceAccessCustomMapper.comprehensivePageQuery(
            new Page<>(param.getPageNo(), param.getPageSize()), param.getDataSourceId(), param.getSearchKey());

        List<DataSourceAccess> list = dataSourceAccessConverter.do2dto(iPage.getRecords());

        fillData(list, selector);

        return PageResult.of(list, iPage.getTotal(), param);
    }

    @Override
    public ActionResult batchCreate(DataSourceAccessBatchCreatParam param) {
        if (CollectionUtils.isEmpty(param.getAccessObjectList())) {
            return ActionResult.isSuccess();
        }
        for (DataSourceAccessObjectParam dataSourceAccessObjectParam : param.getAccessObjectList()) {
            LambdaQueryWrapper<DataSourceAccessDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DataSourceAccessDO::getAccessObjectType, dataSourceAccessObjectParam.getType());
            queryWrapper.eq(DataSourceAccessDO::getDataSourceId, dataSourceAccessObjectParam.getId());
            DataSourceAccessDO query = dataSourceAccessMapper.selectOne(queryWrapper);
            if (query != null) {
                log.info("The data source already exists, no need to add it again,{}",
                    JSON.toJSONString(dataSourceAccessObjectParam));
                continue;
            }
            dataSourceAccessMapper.insert(
                dataSourceAccessConverter.param2do(param.getDataSourceId(), dataSourceAccessObjectParam.getId(),
                    dataSourceAccessObjectParam.getType(), ContextUtils.getUserId()));
        }
        return ActionResult.isSuccess();
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
