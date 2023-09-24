package ai.chat2db.server.web.api.controller.rdb.doc.constant;

import ai.chat2db.server.tools.common.util.I18nUtils;
import lombok.extern.java.Log;

/**
 * CommonConstant
 *
 * @author lzy
 **/
@Log
public final class CommonConstant {
    /**
     * 表head
     **/
    public static String[] INDEX_HEAD_NAMES =
            {I18nUtils.getMessage("main.indexName"),
                    I18nUtils.getMessage("main.indexFieldName"),
                    I18nUtils.getMessage("main.indexType"),
                    I18nUtils.getMessage("main.indexMethod"),
                    I18nUtils.getMessage("main.indexNote")};
    public static String[] COLUMN_HEAD_NAMES =
            {I18nUtils.getMessage("main.fieldNo"),
                    I18nUtils.getMessage("main.fieldName"),
                    I18nUtils.getMessage("main.fieldType"),
                    I18nUtils.getMessage("main.fieldLength"),
                    I18nUtils.getMessage("main.fieldIfEmpty"),
                    I18nUtils.getMessage("main.fieldDefault"),
                    I18nUtils.getMessage("main.fieldDecimalPlaces"),
                    I18nUtils.getMessage("main.fieldNote")};
}
