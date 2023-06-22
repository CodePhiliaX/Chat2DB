package ai.chat2db.server.domain.support.dialect.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 表信息
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SpiExample {
    /**
     * 建表语句
     */
    private String createTable;

    /**
     * 修改表结构
     */
    private String alterTable;
}

