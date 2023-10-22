package ai.chat2db.server.web.api.controller.rdb.request;

import java.util.List;

import ai.chat2db.spi.enums.IndexTypeEnum;

import ai.chat2db.spi.model.TableIndexColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 索引
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IndexRequest {

    /**
     * 索引名称
     */
    private String name;

    /**
     * 所以类型
     *
     * @see IndexTypeEnum
     */
    private String type;

    /**
     * 注释
     */
    private String comment;

    /**
     * 索引包含的列
     */
    private List<TableIndexColumn> columnList;


    private String editStatus;

}
