package ai.chat2db.server.web.api.controller.rdb.doc;

import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.model.IndexInfo;
import ai.chat2db.server.domain.api.model.TableParameter;
import ai.chat2db.server.web.api.controller.rdb.doc.conf.ExportOptions;
import ai.chat2db.server.web.api.controller.rdb.vo.ColumnVO;
import ai.chat2db.server.web.api.controller.rdb.vo.IndexVO;
import ai.chat2db.server.web.api.controller.rdb.vo.TableVO;
import ai.chat2db.server.web.api.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DatabaseExportService
 *
 * @author lzy
 **/
public class DatabaseExportService {
    protected ExportTypeEnum exportTypeEnum;
    @Getter
    public String suffix;

    @Getter
    public String contentType;
    /**
     * 导出excel 集合
     **/
    @Setter
    @Getter
    public List<TableVO> exportList;
    /**
     * 导出word、excel 表信息 集合
     **/
    public static Map<String, List<TableParameter>> listMap = new LinkedHashMap<>();
    /**
     * 导出word 索引 集合
     **/
    public static Map<String, List<IndexInfo>> indexMap = new HashMap<>(0);
    /**
     * 连接符
     **/
    public final static String JOINER = "---";

    private void init() {
        listMap.clear();
        indexMap.clear();
    }

    public void generate(String databaseName, OutputStream outputStream, ExportOptions exportOptions) {
        init();
        exportList.forEach(item -> {
            dataAssemble(databaseName, exportOptions, item);
        });
        try {
            export(outputStream, exportOptions);
        } catch (Exception e) {
            throw new RuntimeException("导出失败！请联系开发者，邮箱：963565242@qq.com" + e);
        }
        init();
    }

    /**
     * 数据处理
     *
     * @param exportOptions 配置信息
     **/
    public void dataAssemble(String databaseName, ExportOptions exportOptions, TableVO item) {
        boolean isExportIndex = Optional.ofNullable(exportOptions.getIsExportIndex()).orElse(false);
        val t = new TableParameter();
        t.setFieldName(item.getName() + "[" + StringUtils.isNull(item.getComment()) + "]");
        List<TableParameter> colForTable = new LinkedList<>();
        for (ColumnVO info : item.getColumnList()) {
            val p = new TableParameter();
            p.setFieldName(info.getName()).setColumnDefault(info.getDefaultValue())
                    .setColumnComment(info.getComment())
                    .setColumnType(info.getColumnType())
                    .setLength(String.valueOf(info.getCharacterMaximumLength())).setIsNullAble(String.valueOf(info.getNullable()))
                    .setDecimalPlaces(String.valueOf(info.getNumericPrecision()));
            colForTable.add(p);
        }
        String key = databaseName + JOINER + t.getFieldName();
        listMap.put(key, colForTable);
        if (isExportIndex) {
            int index = key.lastIndexOf("[");
            String str = key.substring(0, index);
            indexMap.put(str, vo2Info(item.getIndexList()));
        }
        //赋值序号
        for (Map.Entry<String, List<TableParameter>> map : listMap.entrySet()) {
            //赋值序号
            List<TableParameter> list = map.getValue();
            IntStream.range(0, list.size()).forEach(x -> {
                list.get(x).setNo(String.valueOf(x + 1));
            });
        }
    }

    private List<IndexInfo> vo2Info(List<IndexVO> indexList) {
        return indexList.stream().map(v -> {
            IndexInfo info = new IndexInfo();
            info.setName(v.getName());
            info.setColumnName(v.getColumns());
            info.setIndexType(v.getType());
            info.setComment(v.getComment());
            return info;
        }).collect(Collectors.toList());
    }

    /**
     * 导出
     *
     * @param outputStream      文件流
     **/
    public void export(OutputStream outputStream, ExportOptions exportOptions) {

    }

    /**
     * 处理空串或null字符
     *
     * @param source 源字符
     * @return java.lang.String
     **/
    public String dealWith(String source) {
        return StringUtils.isNullOrEmpty(source);
    }

    @SneakyThrows
    public Object[] getIndexValues(IndexInfo indexInfoVO) {
        Object[] values = new Object[IndexInfo.class.getDeclaredFields().length];
        values[0] = dealWith(indexInfoVO.getName());
        values[1] = dealWith(indexInfoVO.getColumnName());
        values[2] = dealWith(indexInfoVO.getIndexType());
        values[3] = dealWith(indexInfoVO.getIndexMethod());
        values[4] = dealWith(indexInfoVO.getComment());
        return values;
    }

    @SneakyThrows
    public Object[] getColumnValues(TableParameter tableParameter) {
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
