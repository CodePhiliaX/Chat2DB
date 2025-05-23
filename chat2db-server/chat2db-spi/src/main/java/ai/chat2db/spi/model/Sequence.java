package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Sequence information
 *
 * @author Sylphy
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Sequence {
    /**
     * Schema name
     */
    private String nspname;
    /**
     * Sequence name
     */
    private String relname;
    /**
     * Sequence data type
     */
    private String typname;
    /**
     * Sequence cache
     */
    private String seqcache;
    /**
     * Sequence owner
     */
    private String rolname;
    /**
     * Sequence comment
     */
    private String comment;
    /**
     * Sequence start value
     */
    private String seqstart;
    /**
     * Sequence step value
     */
    private String seqincrement;
    /**
     * Sequence max value
     */
    private String seqmax;
    /**
     * Sequence min value
     */
    private String seqmin;
    /**
     * Sequence cycle
     */
    private Boolean seqcycle;
}
