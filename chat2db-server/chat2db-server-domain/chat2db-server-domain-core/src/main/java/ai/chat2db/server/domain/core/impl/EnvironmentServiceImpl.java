package ai.chat2db.server.domain.core.impl;

import java.util.List;

import ai.chat2db.server.domain.api.model.Environment;
import ai.chat2db.server.domain.api.param.EnvironmentPageQueryParam;
import ai.chat2db.server.domain.api.service.EnvironmentService;
import ai.chat2db.server.domain.core.converter.EnvironmentConverter;
import ai.chat2db.server.domain.repository.entity.EnvironmentDO;
import ai.chat2db.server.domain.repository.mapper.EnvironmentMapper;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * environment
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Service
public class EnvironmentServiceImpl implements EnvironmentService {

    @Resource
    private EnvironmentMapper environmentMapper;
    @Resource
    private EnvironmentConverter environmentConverter;

    @Override
    public ListResult<Environment> listQuery(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ListResult.empty();
        }
        LambdaQueryWrapper<EnvironmentDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(EnvironmentDO::getId, idList);
        List<EnvironmentDO> dataList = environmentMapper.selectList(queryWrapper);
        List<Environment> list = environmentConverter.do2dto(dataList);
        return ListResult.of(list);
    }

    @Override
    public PageResult<Environment> pageQuery(EnvironmentPageQueryParam param) {
        LambdaQueryWrapper<EnvironmentDO> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getSearchKey())) {
            queryWrapper.and(wrapper -> wrapper.like(EnvironmentDO::getName, "%" + param.getSearchKey() + "%")
                .or()
                .like(EnvironmentDO::getShortName, "%" + param.getSearchKey() + "%"));
        }
        IPage<EnvironmentDO> iPage = environmentMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()),
            queryWrapper);
        List<Environment> dataList = environmentConverter.do2dto(iPage.getRecords());
        return PageResult.of(dataList, iPage.getTotal(), param);
    }
}
