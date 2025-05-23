package ai.chat2db.plugin.postgresql.consts;


/**
 * Sequence operation common constants
 *
 * @author Sylphy
 */
public class SequenceCommonConst {
    private SequenceCommonConst() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CREATE_SEQUENCE = "CREATE SEQUENCE ";
    public static final String START_WITH = "\tSTART WITH ";
    public static final String INCREMENT_BY = "\tINCREMENT BY ";
    public static final String MAXVALUE = "\tMAXVALUE ";
    public static final String MINVALUE = "\tMINVALUE ";
    public static final String CYCLE = "\tCYCLE ";
    public static final String NO_CYCLE = "\tNO CYCLE ";
    public static final String CACHE = "\tCACHE ";
    public static final String RESTART_WITH = "\tRESTART WITH ";
    public static final String ALTER_SEQUENCE = "ALTER SEQUENCE ";
    public static final String COMMENT_ON_SEQUENCE = "COMMENT ON SEQUENCE ";
    public static final String RENAME_TO = " RENAME TO ";
    public static final String OWNER_TO = " OWNER TO ";
    public static final String OWNED_BY = " OWNED BY ";
    public static final String IS = " IS ";
    public static final String AS = " AS ";
}
