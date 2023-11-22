package ai.chat2db.server.web.api.controller.rdb.vo;

import java.util.List;


import ai.chat2db.spi.model.Header;
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
     * Original SQL without pagination
     */
    private String originalSql;

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
     * Total number of fuzzy rows
     * Only select statements have
     */
    private String fuzzyTotal;

    /**
     * 执行持续时间
     */
    private Long duration;

    /**
     * 返回结果是否可以编辑
     */
    private boolean canEdit;

    /**
     * 表名
     */
    private String tableName;
}
