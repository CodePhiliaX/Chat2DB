/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect;

import java.util.List;

import ai.chat2db.server.domain.support.enums.DbTypeEnum;
import ai.chat2db.server.domain.support.model.Function;
import ai.chat2db.server.domain.support.model.Procedure;
import ai.chat2db.server.domain.support.model.Table;
import ai.chat2db.server.domain.support.model.TableColumn;
import ai.chat2db.server.domain.support.model.TableIndex;
import ai.chat2db.server.domain.support.model.Trigger;
import jakarta.validation.constraints.NotEmpty;

/**
 * @author jipengfei
 * @version : MetaSchemaManager.java, v 0.1 2022年12月14日 16:26 jipengfei Exp $
 */
public interface MetaSchema<T extends BaseMapper> {
    /**
     * 支持的数据库类型
     *
     * @return
     */
    DbTypeEnum dbType();

    /**
     * 查询所有的DATABASE
     *
     * @return
     */
    List<String> databases();

    /**
     * 修改数据库名称
     * @param databaseName
     * @param newDatabaseName
     */
    void modifyDatabase(String databaseName, String newDatabaseName);


    /**
     * 创建数据库
     * @param databaseName
     */
    void createDatabase(String databaseName);


    /**
     * 删除数据库
     * @param databaseName
     */
    void dropDatabase(String databaseName);

    /**
     * 查询 DB 下schemas
     * @param databaseName
     * @return
     */
    List<String> schemas(String databaseName);

    /**
     * 创建schema
     * @param databaseName
     * @param schemaName
     */
    void createSchema(String databaseName, String schemaName);

    /**
     * 删除schema
     * @param databaseName
     * @param schemaName
     */
    void dropSchema(String databaseName, String schemaName);

    /**
     * 修改schema
     * @param databaseName
     * @param schemaName
     * @param newSchemaName
     */
    void modifySchema(String databaseName, String schemaName, String newSchemaName);

    /**
     * 展示建表语句
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

    /**
     * 删除表结构
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    void dropTable(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);

    /**
     * 分页查询表信息
     *
     * @param databaseName
     * @return
     */
    List<Table> tables(@NotEmpty String databaseName, String schemaName,String tableName);

    /**
     * 查询所有视图
     *
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<? extends Table> views(@NotEmpty String databaseName, String schemaName);

    /**
     * 查询所有的函数
     *
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Function> functions(@NotEmpty String databaseName, String schemaName);

    /**
     * 查询所有触发器
     *
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Trigger> triggers(@NotEmpty String databaseName, String schemaName);

    /**
     * 查询所有存储过程
     *
     * @param databaseName
     * @param schemaName
     * @return
     */
    List<Procedure> procedures(@NotEmpty String databaseName, String schemaName);

    /**
     * 查询列的信息
     *
     * @param databaseName
     * @param tableName
     * @return
     */
    List<? extends TableColumn> columns(@NotEmpty String databaseName, String schemaName,
        @NotEmpty String tableName);


    /**
     * 查询database下所有的列信息
     *
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param columnName
     * @return
     */
    List<? extends TableColumn> columns(@NotEmpty String databaseName, String schemaName,String tableName, String columnName);

    /**
     * 查询列的信息
     *
     * @param databaseName
     * @param tableName    * @return
     */
    List<? extends TableIndex> indexes(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName);



    //T  getMapper();

}