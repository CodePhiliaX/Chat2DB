package ai.chat2db.server.domain.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 虚拟外键推断结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferVirtualFkResultVO {

    /**
     * 新增的虚拟外键数量
     */
    private int addedCount;

    /**
     * 删除的虚拟外键数量
     */
    private int deletedCount;

    /**
     * 新增的虚拟外键列表
     */
    private java.util.List<VirtualFkItem> added;

    /**
     * 删除的虚拟外键列表
     */
    private java.util.List<VirtualFkItem> deleted;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VirtualFkItem {
        private String tableName;
        private String columnName;
        private String referencedTable;
        private String referencedColumnName;
    }
}
