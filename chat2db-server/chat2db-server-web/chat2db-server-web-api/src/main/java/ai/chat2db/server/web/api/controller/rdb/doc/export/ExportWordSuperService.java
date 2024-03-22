package ai.chat2db.server.web.api.controller.rdb.doc.export;

import ai.chat2db.server.domain.api.enums.ExportFileSuffix;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.tools.common.config.GlobalDict;
import ai.chat2db.server.web.api.controller.rdb.doc.DatabaseExportService;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.doc.constant.CommonConstant;
import ai.chat2db.server.web.api.util.AddToTopic;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Tables;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WordSuperActionListener
 *
 * @author lzy
 **/
public class ExportWordSuperService extends DatabaseExportService {

    public ExportWordSuperService() {
        exportTypeEnum = ExportTypeEnum.WORD;
        suffix = ExportFileSuffix.WORD.getSuffix();
        contentType = "application/msword";
    }

    /**
     * Word导出
     **/
    @SneakyThrows
    @Override
    public void export(OutputStream outputStream, ExportOptions exportOptions) {
        boolean isExportIndex = exportOptions.getIsExportIndex();
        InputStream filePath = this.getClass().getClassLoader().getResourceAsStream("template/" + GlobalDict.TEMPLATE_FILE.get(1));
        InputStream subFile = this.getClass().getClassLoader().getResourceAsStream("template/" + GlobalDict.TEMPLATE_FILE.get(2));
        Map<String, List<Map.Entry<String, List<TableParameter>>>> allMap = listMap.entrySet()
                .stream().collect(Collectors.groupingBy(v -> v.getKey().split("---")[0]));
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> myDataMap = new HashMap<>(2);
        //索引表头
        RowRenderData indexHeaderRow = Rows.of(CommonConstant.INDEX_HEAD_NAMES).center().textBold().textColor("000000").bgColor("bfbfbf").create();
        //字段表头
        RowRenderData tableHeaderRow = Rows.of(CommonConstant.COLUMN_HEAD_NAMES).center().textBold().textColor("000000").bgColor("bfbfbf").create();
        for (Map.Entry<String, List<Map.Entry<String, List<TableParameter>>>> myMap : allMap.entrySet()) {
            //数据库名
            String database = myMap.getKey();
            int i = 1;
            for (Map.Entry<String, List<TableParameter>> parameterMap : myMap.getValue()) {
                //初始化容量 3/0.75 + 1
                Map<String, Object> tableData = new HashMap<>(8);
                //索引Table
                if (isExportIndex) {
                    String name = parameterMap.getKey().split("\\[")[0];
                    List<IndexInfo> indexInfoVOList = indexMap.get(name);
                    List<RowRenderData> rowList = getIndexValues(indexInfoVOList, indexHeaderRow);
                    tableData.put("indexTable", Tables.create(rowList.toArray(new RowRenderData[0])));
                }
                if (i == 1) {
                    Map<String, String> map = new HashMap<>(2);
                    map.put("dataBase", database);
                    tableData.put("ifDatabase", map);
                }
                //表名
                String tableName = parameterMap.getKey().split("---")[1];
                tableData.put("number", i);
                tableData.put("name", tableName);
                List<TableParameter> tableParameterList = parameterMap.getValue();
                List<RowRenderData> rowList = getColumnValues(tableParameterList, tableHeaderRow);
                tableData.put("table", Tables.create(rowList.toArray(new RowRenderData[0])));
                i++;
                list.add(tableData);
            }
        }
        myDataMap.put("mydata", Includes.ofStream(subFile).setRenderModel(list).create());
        /*根据模板生成文档*/
        XWPFTemplate template = XWPFTemplate.compile(filePath).render(myDataMap);
        AddToTopic.generateTOC(template.getXWPFDocument(), outputStream);
    }

    @SneakyThrows
    public List<RowRenderData> getColumnValues(List<TableParameter> list, RowRenderData tableHeaderRow) {
        List<RowRenderData> rowRenderDataList = new ArrayList<>();
        rowRenderDataList.add(tableHeaderRow);
        for (TableParameter tableParameter : list) {
            String[] values = Arrays.stream(getColumnValues(tableParameter)).toArray(String[]::new);
            rowRenderDataList.add(Rows.of(values).center().create());
        }
        return rowRenderDataList;
    }


    @SneakyThrows
    public List<RowRenderData> getIndexValues(List<IndexInfo> list, RowRenderData tableHeaderRow) {
        List<RowRenderData> rowRenderDataList = new ArrayList<>();
        rowRenderDataList.add(tableHeaderRow);
        if (list.isEmpty()) {
            String[] values = Arrays.stream(getIndexValues(new IndexInfo())).toArray(String[]::new);
            rowRenderDataList.add(Rows.of(values).center().create());
            return rowRenderDataList;
        }
        for (IndexInfo indexInfo : list) {
            String[] values = Arrays.stream(getIndexValues(indexInfo)).toArray(String[]::new);
            rowRenderDataList.add(Rows.of(values).center().create());
        }
        return rowRenderDataList;
    }
}
