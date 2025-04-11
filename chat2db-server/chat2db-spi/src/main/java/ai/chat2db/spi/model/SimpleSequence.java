package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Simple sequence information
 *
 * @author Sylphy
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Sequence Name
     */
    private String name;

    /**
     * description
     */
    private String comment;
}
