/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect;

import java.util.List;

import ai.chat2db.server.domain.support.model.Table;
import ai.chat2db.server.domain.support.model.TableColumn;
import ai.chat2db.server.domain.support.model.TableIndexColumn;
import org.apache.ibatis.annotations.Param;

/**
 * @author jipengfei
 * @version : BaseMapper.java
 */
public interface BaseMapper {
    /**
     * 查询Database
     *
     * @return
     */
    List<String> showDatabases();

    /**
     * 查询所有表中所有列信息
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    List<TableColumn> selectColumns(@Param("databaseName") String databaseName,@Param("tableSchema") String tableSchema, @Param("tableName") String tableName);

    /**
     * 删除表
     *
     * @param databaseName
     * @param tableName
     */
    void dropTable(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    /**
     * 查询所有的表
     *
     * @param databaseName
     * @param tableSchema
     * @return
     */
    List<Table> selectTables(@Param("databaseName") String databaseName, @Param("tableSchema") String tableSchema,@Param("tableName") String tableName);

    /**
     * @param databaseName
     * @return
     */
    Long selectTableCount(@Param("databaseName") String databaseName);

    /**
     * 查询建表语句
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    String showCreateTable(@Param("databaseName") String databaseName,@Param("tableSchema") String tableSchema,
        @Param("tableName") String tableName);

    /**
     * 查询表索引信息
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    List<TableIndexColumn> selectTableIndexes(@Param("databaseName") String databaseName,@Param("tableSchema") String tableSchema,
        @Param("tableName") String tableName);
}