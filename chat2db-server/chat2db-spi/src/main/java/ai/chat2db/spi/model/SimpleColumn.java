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
public class SimpleColumn {

    /**
     * 列名
     */
    @JsonAlias({"COLUMN_NAME"})
    private String name;


    @JsonAlias({"TYPE_NAME"})
    private String columnType;

    /**
     * 注释
     */
    @JsonAlias({"REMARKS"})
    private String comment;
}
