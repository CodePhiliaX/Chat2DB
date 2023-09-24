package ai.chat2db.server.domain.api.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version Dashboard.java, v 0.1 2023年06月09日 15:32 moji Exp $
 * @date 2023/06/09
 */
@Data
public class Dashboard {

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 报表名称
     */
    private String name;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 报表布局信息
     */
    private String schema;

    /**
     * 是否被删除,y表示删除,n表示未删除
     */
    private String deleted;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 图表ID列表
     */
    private List<Long> chartIds;

}
