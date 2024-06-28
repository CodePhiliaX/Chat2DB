package ai.chat2db.plugin.mysql.value.template;

/**
 * @author: zgq
 * @date: 2024年06月01日 13:31
 */
public class MysqlDmlValueTemplate {

    public static final String COMMON_TEMPLATE = "'%s'";
    public static final String GEOMETRY_TEMPLATE = "ST_GeomFromText('%s')";
    public static final String BIT_TEMPLATE = "b'%s'";
}
