package ai.chat2db.server.web.api.controller.rdb.doc.constant;


import ai.chat2db.server.tools.common.util.I18nUtils;

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
    public static final String ALL_TABLE_HEADER = MD_SPLIT + I18nUtils.getMessage("main.fieldNo")  + MD_SPLIT +
            I18nUtils.getMessage("main.fieldName")  + MD_SPLIT +
            I18nUtils.getMessage("main.fieldType")  + MD_SPLIT +
            I18nUtils.getMessage("main.fieldLength")  + MD_SPLIT +
            I18nUtils.getMessage("main.fieldIfEmpty") + MD_SPLIT +
            I18nUtils.getMessage("main.fieldDefault") + MD_SPLIT +
            I18nUtils.getMessage("main.fieldDecimalPlaces") + MD_SPLIT +
            I18nUtils.getMessage("main.fieldNote") + MD_SPLIT;
    public static String TABLE_BODY = "|%s|%s|%s|%s|%s|%s|%s|%s|";
    public static String TABLE_SEPARATOR = "|:----:|----|----|----|----|----|----|----|";
    public static final String ALL_INDEX_TABLE_HEADER = MD_SPLIT + I18nUtils.getMessage("main.indexName") + MD_SPLIT +
            I18nUtils.getMessage("main.indexFieldName") + MD_SPLIT +
            I18nUtils.getMessage("main.indexType") + MD_SPLIT +
            I18nUtils.getMessage("main.indexMethod") + MD_SPLIT +
            I18nUtils.getMessage("main.indexNote") + MD_SPLIT;
    public static String INDEX_TABLE_BODY = "|%s|%s|%s|%s|";
    public static String INDEX_TABLE_SEPARATOR = "|:----:|----|----|----|";

    /**
     * Html
     */
    public static final String HTML_TITLE = "<h1 id=\"{0}\">{0}</h1>";
    public static final String HTML_CATALOG = "<h2 id=\"{0}\">{1}</h2>";
    public static final String HTML_INDEX_ITEM = "<a href=\"#{0}\" title=\"{0}\">{1}</a>";
    public static String HTML_TABLE_HEADER = "<tr><th>序号</th><th>字段名</th><th>类型</th><th>长度</th><th>是否为空</th><th>默认值</th><th>小数位</th><th>注释</th></tr>";
    public static String HTML_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
    public static String HTML_INDEX_TABLE_HEADER = "<tr><th>名称</th><th>字段</th><th>DDL</th><th>注释</th></tr>";
    public static String HTML_INDEX_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
}
