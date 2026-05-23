package ai.chat2db.server.web.api.controller.task.biz.doc;

import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.server.web.api.controller.rdb.doc.DatabaseExportService;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.CommonConstant;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.PatternConstant;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.server.web.api.util.StringUtils;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public abstract class AbstractSchemaDocExportStrategy implements SchemaDocExportStrategy {

    @Override
    public void export(SchemaDocExportContext context) {
        initConstants();
        Map<String, List<TableParameter>> tableParameterMap = buildTableParameterMap(context);
        Map<String, List<IndexInfo>> indexMap = buildIndexMap(context);

        context.setTableParameterMap(tableParameterMap);
        context.setIndexMap(indexMap);

        try (FileOutputStream fos = new FileOutputStream(context.getFile())) {
            doExport(fos, context);
        } catch (Exception e) {
            log.error("export schema doc error, strategy: {}", this.getClass().getSimpleName(), e);
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }

    protected abstract void doExport(java.io.OutputStream outputStream, SchemaDocExportContext context) throws Exception;

    private void initConstants() {
        CommonConstant.INDEX_HEAD_NAMES = new String[]{
                I18nUtils.getMessage("main.indexName"),
                I18nUtils.getMessage("main.indexFieldName"),
                I18nUtils.getMessage("main.indexType"),
                I18nUtils.getMessage("main.indexMethod"),
                I18nUtils.getMessage("main.indexNote")
        };
        CommonConstant.COLUMN_HEAD_NAMES = new String[]{
                I18nUtils.getMessage("main.fieldNo"),
                I18nUtils.getMessage("main.fieldName"),
                I18nUtils.getMessage("main.fieldType"),
                I18nUtils.getMessage("main.fieldLength"),
                I18nUtils.getMessage("main.fieldIfEmpty"),
                I18nUtils.getMessage("main.fieldDefault"),
                I18nUtils.getMessage("main.fieldDecimalPlaces"),
                I18nUtils.getMessage("main.fieldNote")
        };

        StringBuilder mdIndex = new StringBuilder(PatternConstant.MD_SPLIT);
        StringBuilder htmlIndex = new StringBuilder("<tr><th>");
        for (int i = 0; i < CommonConstant.INDEX_HEAD_NAMES.length; i++) {
            mdIndex.append(CommonConstant.INDEX_HEAD_NAMES[i]).append(i == CommonConstant.INDEX_HEAD_NAMES.length - 1 ? "" : PatternConstant.MD_SPLIT);
            htmlIndex.append(CommonConstant.INDEX_HEAD_NAMES[i]).append(i == CommonConstant.INDEX_HEAD_NAMES.length - 1 ? "" : "</th><th>");
        }
        mdIndex.append(PatternConstant.MD_SPLIT);
        htmlIndex.append("</th></tr>");

        StringBuilder mdColumn = new StringBuilder(PatternConstant.MD_SPLIT);
        StringBuilder htmlColumn = new StringBuilder("<tr><th>");
        for (int i = 0; i < CommonConstant.COLUMN_HEAD_NAMES.length; i++) {
            mdColumn.append(CommonConstant.COLUMN_HEAD_NAMES[i]).append(i == CommonConstant.COLUMN_HEAD_NAMES.length - 1 ? "" : PatternConstant.MD_SPLIT);
            htmlColumn.append(CommonConstant.COLUMN_HEAD_NAMES[i]).append(i == CommonConstant.COLUMN_HEAD_NAMES.length - 1 ? "" : "</th><th>");
        }
        mdColumn.append(PatternConstant.MD_SPLIT);
        htmlColumn.append("</th></tr>");

        PatternConstant.ALL_INDEX_TABLE_HEADER = mdIndex.toString();
        PatternConstant.HTML_INDEX_TABLE_HEADER = htmlIndex.toString();
        PatternConstant.ALL_TABLE_HEADER = mdColumn.toString();
        PatternConstant.HTML_TABLE_HEADER = htmlColumn.toString();
    }

    private Map<String, List<TableParameter>> buildTableParameterMap(SchemaDocExportContext context) {
        Map<String, List<TableParameter>> listMap = new LinkedHashMap<>();

        for (Table table : context.getTables()) {
            TableParameter t = new TableParameter();
            t.setFieldName(table.getName() + "[" + StringUtils.isNull(table.getComment()) + "]");
            List<TableParameter> colForTable = new LinkedList<>();
            for (TableColumn info : table.getColumnList()) {
                TableParameter p = new TableParameter();
                p.setFieldName(info.getName())
                        .setColumnDefault(info.getDefaultValue())
                        .setColumnComment(info.getComment())
                        .setColumnType(info.getColumnType())
                        .setLength(String.valueOf(info.getColumnSize()))
                        .setIsNullAble(String.valueOf(info.getNullable()))
                        .setDecimalPlaces(String.valueOf(info.getDecimalDigits()));
                colForTable.add(p);
            }
            String key = context.getDatabaseName() + DatabaseExportService.JOINER + t.getFieldName();
            listMap.put(key, colForTable);
        }

        for (Map.Entry<String, List<TableParameter>> map : listMap.entrySet()) {
            List<TableParameter> list = map.getValue();
            IntStream.range(0, list.size()).forEach(x -> {
                list.get(x).setNo(String.valueOf(x + 1));
            });
        }
        return listMap;
    }

    private Map<String, List<IndexInfo>> buildIndexMap(SchemaDocExportContext context) {
        Map<String, List<IndexInfo>> indexMap = new LinkedHashMap<>();
        boolean isExportIndex = Optional.ofNullable(context.getExportOptions().getIsExportIndex()).orElse(false);
        if (!isExportIndex) {
            return indexMap;
        }
        for (Table table : context.getTables()) {
            String key = context.getDatabaseName() + DatabaseExportService.JOINER + table.getName();
            indexMap.put(key, vo2Info(table.getIndexList()));
        }
        return indexMap;
    }

    private List<IndexInfo> vo2Info(List<TableIndex> indexList) {
        if (indexList == null) {
            return Collections.emptyList();
        }
        return indexList.stream().map(v -> {
            IndexInfo info = new IndexInfo();
            info.setName(v.getName());
            List<TableIndexColumn> columnList = v.getColumnList();
            info.setColumnName(columnList != null ? columnList.stream().map(TableIndexColumn::getColumnName).collect(Collectors.joining(",")) : "");
            info.setIndexType(v.getType());
            info.setComment(v.getComment());
            return info;
        }).collect(Collectors.toList());
    }

    protected String dealWith(String source) {
        return StringUtils.isNullOrEmpty(source);
    }

    protected Object[] getIndexValues(IndexInfo indexInfoVO) {
        Object[] values = new Object[IndexInfo.class.getDeclaredFields().length];
        values[0] = dealWith(indexInfoVO.getName());
        values[1] = dealWith(indexInfoVO.getColumnName());
        values[2] = dealWith(indexInfoVO.getIndexType());
        values[3] = dealWith(indexInfoVO.getIndexMethod());
        values[4] = dealWith(indexInfoVO.getComment());
        return values;
    }

    protected Object[] getColumnValues(TableParameter tableParameter) {
        Object[] values = new Object[TableParameter.class.getDeclaredFields().length];
        values[0] = StringUtils.isNull(tableParameter.getNo());
        values[1] = StringUtils.isNull(tableParameter.getFieldName());
        values[2] = StringUtils.isNull(tableParameter.getColumnType());
        values[3] = StringUtils.isNull(tableParameter.getLength());
        values[4] = StringUtils.isNull(tableParameter.getIsNullAble());
        values[5] = StringUtils.isNull(tableParameter.getColumnDefault());
        values[6] = StringUtils.isNull(tableParameter.getDecimalPlaces());
        values[7] = StringUtils.isNull(tableParameter.getColumnComment());
        return values;
    }
}
