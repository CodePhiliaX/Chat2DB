package ai.chat2db.server.web.api.controller.rdb.doc.constant;


/**
 * PatternConstant
 *
 * @author lzy
 **/
public final class PatternConstant {

    /**
     * 公共
     */
    public static final String MD_SPLIT = "|";

    /**
     * Markdown
     */
    public static final String TITLE = "# %s";
    public static final String CATALOG = "## %s";
    public static String ALL_TABLE_HEADER = "";
    public static String TABLE_BODY = "|%s|%s|%s|%s|%s|%s|%s|%s|";
    public static String TABLE_SEPARATOR = "|:----:|----|----|----|----|----|----|----|";
    public static String ALL_INDEX_TABLE_HEADER = "";
    public static String INDEX_TABLE_BODY = "|%s|%s|%s|%s|";
    public static String INDEX_TABLE_SEPARATOR = "|:----:|----|----|----|";

    /**
     * Html
     */
    public static final String HTML_TITLE = "<h1 id=\"{0}\">{0}</h1>";
    public static final String HTML_CATALOG = "<h2 id=\"{0}\">{1}</h2>";
    public static final String HTML_INDEX_ITEM = "<a href=\"#{0}\" title=\"{0}\">{1}</a>";
    public static String HTML_TABLE_HEADER = "";
    public static String HTML_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
    public static String HTML_INDEX_TABLE_HEADER = "";
    public static String HTML_INDEX_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
}
