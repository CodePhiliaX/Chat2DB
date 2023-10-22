package ai.chat2db.spi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleTable {
    /**
     * 表名
     */
    @JsonAlias({"TABLE_NAME"})
    private String name;

    /**
     * 描述
     */
    @JsonAlias({"REMARKS"})

    private String comment;
}
