package ai.chat2db.plugin.mysql.value.template;

/**
 * @author: zgq
 * @date: 2024年06月01日 13:31
 */
public class MysqlDmlValueTemplate {

    public static final String COMMON_TEMPLATE = "'%s'";
    public static final String GEOMETRY_TEMPLATE = "ST_GeomFromText('%s')";
    public static final String BIT_TEMPLATE = "b'%s'";
    public static final String BINARY_TEMPLATE = "0x%s";

    /**
     * example: [VARBINARY] 525x542 JPEG image 34.67 KB
     */
    public static final String IMAGE_TEMPLATE = "[%s] %dx%d JPEG image %d %s";
}
