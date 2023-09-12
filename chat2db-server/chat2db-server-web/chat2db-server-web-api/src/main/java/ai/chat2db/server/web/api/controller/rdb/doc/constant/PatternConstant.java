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
    public static String HTML_TABLE_HEADER = "<tr><th>" + I18nUtils.getMessage("main.fieldNo")
            + "</th><th>" + I18nUtils.getMessage("main.fieldName")
            + "</th><th>"+ I18nUtils.getMessage("main.fieldType")
            + "</th><th>" + I18nUtils.getMessage("main.fieldLength")
            + "</th><th>" + I18nUtils.getMessage("main.fieldIfEmpty")
            +  "</th><th>" + I18nUtils.getMessage("main.fieldDefault")
            + "</th><th>" + I18nUtils.getMessage("main.fieldDecimalPlaces")
            + "</th><th>" + I18nUtils.getMessage("main.fieldNote")
            + "</th></tr>";
    public static String HTML_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
    public static String HTML_INDEX_TABLE_HEADER = "<tr><th>" + I18nUtils.getMessage("main.indexName")
            + "</th><th>" + I18nUtils.getMessage("main.indexFieldName")
            + "</th><th>" + I18nUtils.getMessage("main.indexType")
            + "</th><th>" + I18nUtils.getMessage("main.indexMethod")
            + "</th><th>" + I18nUtils.getMessage("main.indexNote") + "</th></tr>";
    public static String HTML_INDEX_TABLE_BODY = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
}
