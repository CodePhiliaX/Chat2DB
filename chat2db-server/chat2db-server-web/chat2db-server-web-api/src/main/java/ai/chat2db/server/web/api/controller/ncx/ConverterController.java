package ai.chat2db.server.web.api.controller.ncx;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.web.api.controller.ncx.service.ConverterService;
import ai.chat2db.server.web.api.controller.ncx.vo.UploadVO;
import ai.chat2db.server.web.api.util.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

/**
 * ConverterController
 *
 * @author lzy
 **/
@RequestMapping("/api/converter")
@RestController
@Slf4j
public class ConverterController {

    @Autowired
    private ConverterService converterService;

    /**
     * 导出教程
     *
     * @param file file
     * @return DataResult<UploadVO>
     * @see <a href="https://blog.csdn.net/kkk123445/article/details/122514124?spm=1001.2014.3001.5502" />
     **/
    @SneakyThrows
    @PostMapping("/ncx/upload")
    public DataResult<UploadVO> ncxUploadFile(@RequestParam("file") MultipartFile file) {
        log.info("开始上传ncx");
        // 验证文件后缀
        String fileExtension = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileExtension.equalsIgnoreCase(FileUtils.ConfigFile.NCX.name())) {
            return DataResult.error("1", "上传的文件必须是ncx文件！");
        }
        File temp = new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + UUID.randomUUID() + ".tmp");
        file.transferTo(temp);
        return DataResult.of(converterService.uploadFile(temp));
    }

    @SneakyThrows
    @PostMapping("/dbp/upload")
    public DataResult<UploadVO> edbpUploadFile(@RequestParam("file") MultipartFile file) {
        // 验证文件后缀
        String fileExtension = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileExtension.equalsIgnoreCase(FileUtils.ConfigFile.DBP.name())) {
            return DataResult.error("1", "上传的文件必须是dbp文件！");
        }
        File temp = new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + UUID.randomUUID() + ".tmp");
        file.transferTo(temp);
        return DataResult.of(converterService.dbpUploadFile(temp));
    }


    /**
     * 导入datagrip的连接信息，通过 ctrl/cmd + c（shift多选）复制连接，再导入进来
     * 目前复制的连接信息里面是没有密码的、ssh连接信息也没有
     *
     * @param text text
     * @return DataResult<UploadVO>
     **/
    @SneakyThrows
    @PostMapping("/datagrip/upload")
    public DataResult<UploadVO> datagripUploadFile(@RequestParam("text") String text) {
        return DataResult.of(converterService.datagripUploadFile(text));
    }


}
