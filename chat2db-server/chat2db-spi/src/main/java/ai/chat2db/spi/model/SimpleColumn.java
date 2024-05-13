package ai.chat2db.spi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleColumn  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Column name
     */
    @JsonAlias({"COLUMN_NAME"})
    private String name;


    @JsonAlias({"TYPE_NAME"})
    private String columnType;

    /**
     * Comment
     */
    @JsonAlias({"REMARKS"})
    private String comment;
}
