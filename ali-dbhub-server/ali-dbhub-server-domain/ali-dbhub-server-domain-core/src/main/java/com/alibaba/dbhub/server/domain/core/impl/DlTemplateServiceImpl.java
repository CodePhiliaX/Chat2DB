package com.alibaba.dbhub.server.domain.core.impl;

import com.alibaba.dbhub.server.domain.api.param.DlCountParam;
import com.alibaba.dbhub.server.domain.api.param.DlExecuteParam;
import com.alibaba.dbhub.server.domain.api.param.SqlAnalyseParam;
import com.alibaba.dbhub.server.domain.api.service.DlTemplateService;
import com.alibaba.dbhub.server.domain.support.enums.SqlTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.ExecuteResult;
import com.alibaba.dbhub.server.domain.support.sql.DbhubContext;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;
import com.alibaba.dbhub.server.domain.support.util.JdbcUtils;
import com.alibaba.dbhub.server.tools.base.constant.EasyToolsConstant;
import com.alibaba.dbhub.server.tools.base.excption.BusinessException;
import com.alibaba.dbhub.server.tools.base.excption.DatasourceErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ListResult;
import com.alibaba.dbhub.server.tools.common.util.EasyCollectionUtils;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Slf4j
@Service
public class DlTemplateServiceImpl implements DlTemplateService {

    @Override
    public ListResult<ExecuteResult> execute(DlExecuteParam param) {
        if (StringUtils.isBlank(param.getSql())) {
            return ListResult.empty();
        }
        // 解析sql
        SqlAnalyseParam sqlAnalyseParam = new SqlAnalyseParam();
        sqlAnalyseParam.setDataSourceId(param.getDataSourceId());
        sqlAnalyseParam.setSql(param.getSql());
        DbType dbType =
            JdbcUtils.parse2DruidDbType(DbhubContext.getConnectInfo().getDbType());
        List<String> sqlList = SQLParserUtils.splitAndRemoveComment(param.getSql(), dbType);
        if (CollectionUtils.isEmpty(sqlList)) {
            throw new BusinessException(DatasourceErrorEnum.SQL_ANALYSIS_ERROR);
        }

        List<ExecuteResult> result = new ArrayList<>();
        ListResult<ExecuteResult> listResult = ListResult.of(result);
        // 执行sql
        for (String sql : sqlList) {
            int pageNo = 0;
            int pageSize = 0;
            String sqlType = SqlTypeEnum.UNKNOWN.getCode();

            // 解析sql分页
            SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
            // 是否需要代码帮忙分页
            boolean autoLimit = false;
            if (sqlStatement instanceof SQLSelectStatement) {
                //  不是查询全部数据 而且 用户自己没有传分页
                autoLimit = BooleanUtils.isNotTrue(param.getPageSizeAll()) && SQLUtils.getLimit(sqlStatement, dbType)
                    == null;
                if (autoLimit) {
                    pageNo = Optional.ofNullable(param.getPageNo()).orElse(1);
                    pageSize = Optional.ofNullable(param.getPageSize()).orElse(EasyToolsConstant.MAX_PAGE_SIZE);
                    int offset = (pageNo - 1) * pageSize;
                    sql = PagerUtils.limit(sql, dbType, offset, pageSize);
                }
                sqlType = SqlTypeEnum.SELECT.getCode();
            }

            ExecuteResult executeResult = execute(sql);
            executeResult.setSqlType(sqlType);
            // 自动分页
            if (autoLimit) {
                executeResult.setPageNo(pageNo);
                executeResult.setPageSize(pageSize);
                executeResult.setHasNextPage(
                    CollectionUtils.size(executeResult.getDataList()) >= executeResult.getPageSize());
            } else {
                executeResult.setPageNo(1);
                executeResult.setPageSize(CollectionUtils.size(executeResult.getDataList()));
                executeResult.setHasNextPage(Boolean.FALSE);
            }
            result.add(executeResult);
            if (!executeResult.getSuccess()) {
                listResult.setSuccess(false);
                listResult.errorCode(executeResult.getDescription());
                listResult.setErrorMessage(executeResult.getMessage());
            }
        }
        return listResult;
    }

    @Override
    public DataResult<Long> count(DlCountParam param) {
        if (StringUtils.isBlank(param.getSql())) {
            return DataResult.of(0L);
        }
        DbType dbType =
            JdbcUtils.parse2DruidDbType(DbhubContext.getConnectInfo().getDbType());
        String sql = param.getSql();
        // 解析sql分页
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        if (!(sqlStatement instanceof SQLSelectStatement)) {
            throw new BusinessException("当前sql不是查询语句");
        }
        sql = PagerUtils.count(sql, dbType);
        ExecuteResult executeResult = execute(sql);

        List<List<String>> dataList = executeResult.getDataList();
        if (CollectionUtils.isEmpty(dataList)) {
            return DataResult.of(0L);
        }
        String count = EasyCollectionUtils.stream(executeResult.getDataList())
            .findFirst()
            .orElse(Collections.emptyList())
            .stream()
            .findFirst()
            .orElse("0");
        return DataResult.of(Long.valueOf(count));
    }

    private ExecuteResult execute(String sql) {
        ExecuteResult executeResult;
        try {
            executeResult = SQLExecutor.getInstance().execute(sql);
        } catch (SQLException e) {
            log.warn("执行sql:{}异常", sql, e);
            executeResult = ExecuteResult.builder()
                .sql(sql)
                .success(Boolean.FALSE)
                .message(e.getMessage())
                .build();
        }
        return executeResult;
    }

}
