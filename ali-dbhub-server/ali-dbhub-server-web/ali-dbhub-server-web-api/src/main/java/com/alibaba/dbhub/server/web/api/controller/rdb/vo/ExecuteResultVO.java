package com.alibaba.dbhub.server.web.api.controller.rdb.vo;

import java.util.List;

import com.alibaba.dbhub.server.domain.support.enums.SqlTypeEnum;

import lombok.Data;

/**
 * @author moji
 * @version ExecuteResultVO.java, v 0.1 2022年10月23日 11:20 moji Exp $
 * @date 2022/10/23
 */
@Data
public class ExecuteResultVO {

    /**
     * 执行的sql
     */
    private String sql;

    /**
     * 描述
     */
    private String description;

    /**
     * 失败消息提示
     */
    private String message;

    /**
     * 是否成功标志位
     */
    private Boolean success;

    /**
     * 展示头的列表
     */
    private List<HeaderVO> headerList;

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
