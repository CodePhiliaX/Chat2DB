package com.alibaba.dbhub.server.domain.support.model;

import java.util.List;

import com.alibaba.dbhub.server.domain.support.enums.SqlTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 执行结果
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteResult {

    /**
     * 是否成功标志位
     */
    private Boolean success;

    /**
     * 失败消息提示
     * 只有失败的情况下会有
     */
    private String message;

    /**
     * 执行的sql
     */
    private String sql;

    /**
     * 描述
     */
    private String description;

    /**
     * 修改行数 查询sql不会返回
     */
    private Integer updateCount;

    /**
     * 展示头的列表
     */
    private List<Header> headerList;

    /**
     * 数据的列表
     */
    private List<List<String>> dataList;

    /**
     * sql 类型
     *
     * @see SqlTypeEnum
     */
    private String sqlType;

    /**
     * 是否存在下一页
     * 只有select语句才有
     */
    private Boolean hasNextPage;

    /**
     * 分页编码
     * 只有select语句才有
     */
    private Integer pageNo;

    /**
     * 分页大小
     * 只有select语句才有
     */
    private Integer pageSize;

    /**
     * 执行持续时间
     */
    private Long duration;
}
