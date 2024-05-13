package ai.chat2db.server.web.api.controller.rdb.factory;

import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.web.api.controller.rdb.data.export.strategy.*;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * @author: zgq
 * @date: 2024年03月24日 12:53
 */
public class ExportDBDataStrategyFactory {

    public static final Map<String, Class<? extends ExportDBDataStrategy>> SERVICE_MAP = Map.of(
            ExportTypeEnum.SQL.getCode(), ExportDBData2SqlStrategy.class,
            ExportTypeEnum.CSV.getCode(), ExportDBData2CsvStrategy.class,
            ExportTypeEnum.EXCEL.getCode(), ExportDBData2ExcelStrategy.class,
            ExportTypeEnum.JSON.getCode(), ExportDBData2JsonStrategy.class
    );

    @SneakyThrows
    public static Class<?> get(String type) {
        Class<?> dataResult = SERVICE_MAP.get(type);
        if (dataResult == null) {
            throw new ClassNotFoundException("no ExportUI was found");
        } else {
            return dataResult;
        }
    }
}