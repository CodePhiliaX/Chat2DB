package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 数据库
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Database {
    /**
     * 数据库名字
     */
    private String name;

    /**
     * schema name
     */
    private List<Schema> schemas;
}
