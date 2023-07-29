package ai.chat2db.server.web.api.controller.rdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.BeanMapUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import ai.chat2db.server.domain.api.enums.ExportSizeEnum;
import ai.chat2db.server.domain.api.enums.ExportTypeEnum;
import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.rdb.converter.RdbWebConverter;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.SQLExecutor;
import com.google.common.collect.Lists;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Export Database Exclusive
 *
 * @author Jiaju Zhuang
 */
@ConnectionInfoAspect
@RequestMapping("/api/rdb/dml")
@Controller
@Slf4j
public class RdbDmlExportController {

    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;

    /**
     * export data
     *
     * @param request
     * @return
     */
    @PostMapping("/export")
    public void export(@Valid @RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        ExportSizeEnum exportSize = EasyEnumUtils.getEnum(ExportSizeEnum.class, request.getExportSize());
        ExportTypeEnum exportType = EasyEnumUtils.getEnum(ExportTypeEnum.class, request.getExportType());
        BeanMap beanMap = BeanMap.create(request);
        log.info("x:{}", beanMap.get("sql"));

        com.alibaba.excel.support.cglib.beans.BeanMap beanMap2 = BeanMapUtils.create(request);
        log.info("te:{}", beanMap2.get("sql"));

        if (exportType == ExportTypeEnum.CSV) {
            doExportCsv(exportSize, request, response);
        }

    }

    private void doExportCsv(ExportSizeEnum exportSize, DataExportRequest request, HttpServletResponse response)
        throws IOException {
        String sql;
        if (exportSize == ExportSizeEnum.CURRENT_PAGE) {
            sql = request.getSql();
        } else {
            sql = request.getOriginalSql();
        }
        if (StringUtils.isBlank(sql)) {
            throw new ParamBusinessException("exportSize");
        }
        //
        //ublic void download(HttpServletResponse response) throws IOException {
        //    // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("text/csv");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("测试", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".csv");
        //    EasyExcel.write(response.getOutputStream(), DownloadData.class).sheet("模板").doWrite(data());

        ExcelWrapper excelWrapper = new ExcelWrapper();
        try {
            ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream())
                .excelType(ExcelTypeEnum.CSV);
            excelWrapper.setExcelWriterBuilder(excelWriterBuilder);
            SQLExecutor.getInstance().executeSql(Chat2DBContext.getConnection(), sql, headerList -> {
                excelWriterBuilder.head(
                    EasyCollectionUtils.toList(headerList, header -> Lists.newArrayList(header.getName())));
                excelWrapper.setExcelWriter(excelWriterBuilder.build());
                excelWrapper.setWriteSheet(EasyExcel.writerSheet(0).build());
            }, dataList -> {
                List<List<String>> writeDataList = Lists.newArrayList();
                writeDataList.add(dataList);
                excelWrapper.getExcelWriter().write(writeDataList, excelWrapper.getWriteSheet());
            });
        } finally {
            if (excelWrapper.getExcelWriter() != null) {
                excelWrapper.getExcelWriter().finish();
            }
        }
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcelWrapper {
        private ExcelWriterBuilder excelWriterBuilder;
        private ExcelWriter excelWriter;
        private WriteSheet writeSheet;
    }

}
