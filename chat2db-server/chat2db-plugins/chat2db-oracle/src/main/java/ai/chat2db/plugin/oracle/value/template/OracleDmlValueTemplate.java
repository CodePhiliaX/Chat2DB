package ai.chat2db.plugin.oracle.value.template;

/**
 * @author: zgq
 * @date: 2024年06月01日 13:35
 */
public class OracleDmlValueTemplate {

    public static final String DATE_TEMPLATE = "TO_DATE('%s', 'YYYY-MM-DD HH24:MI:SS')";

    public static final String TIMESTAMP_TEMPLATE = "TO_TIMESTAMP('%s', 'YYYY-MM-DD HH24:MI:SS.FF%d')";

    public static final String TIMESTAMP_TZ_TEMPLATE = "TO_TIMESTAMP_TZ('%s', 'YYYY-MM-DD HH24:MI:SS.FF%d TZH:TZM')";
    public static final String TIMESTAMP_TZ_WITHOUT_NANOS_TEMPLATE = "TO_TIMESTAMP_TZ('%s', 'YYYY-MM-DD HH24:MI:SS TZH:TZM')";

    public static final String INTEGER_YEAR_TO_MONTH_TEMPLATE = "INTERVAL '%s' YEAR(%d) TO MONTH";
    public static final String INTEGER_DAY_TO_SECOND_TEMPLATE = "INTERVAL '%s' DAY(%d) TO SECOND(%d)";
}
