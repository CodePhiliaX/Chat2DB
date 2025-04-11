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
    public static final String START_WITH = " START WITH ";
    public static final String INCREMENT_BY = " INCREMENT BY ";
    public static final String MAXVALUE = " MAXVALUE ";
    public static final String MINVALUE = " MINVALUE ";
    public static final String CYCLE = " CYCLE ";
    public static final String NO_CYCLE = " NO CYCLE ";
    public static final String CACHE = " CACHE ";
    public static final String RESTART_WITH = " RESTART WITH ";
    public static final String ALTER_SEQUENCE = "ALTER SEQUENCE ";
    public static final String COMMENT_ON_SEQUENCE = "COMMENT ON SEQUENCE ";
    public static final String RENAME_TO = " RENAME TO ";
    public static final String OWNER_TO = " OWNER TO ";
    public static final String OWNED_BY = " OWNED BY ";
    public static final String IS = " IS ";
    public static final String AS = " AS ";
    public static final String BLANK_LINE = "\n\n";
    public static final String NEW_LINE = "\n";
    public static final String SEMICOLON = ";";
    public static final String DOT = ".";
    public static final String SINGLE_QUOTE = "'";
}
