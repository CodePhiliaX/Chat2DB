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
     * Export tutorial
     *
     * @param file file
     * @return DataResult<UploadVO>
     * @see <a href="https://blog.csdn.net/kkk123445/article/details/122514124?spm=1001.2014.3001.5502" />
     **/
    @SneakyThrows
    @PostMapping("/ncx/upload")
    public DataResult<UploadVO> ncxUploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Start uploading ncx");
        // Verify file suffix
        String fileExtension = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileExtension.equalsIgnoreCase(FileUtils.ConfigFile.NCX.name())) {
            return DataResult.error("1", "The uploaded file must be an ncx file！");
        }
        File temp = new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + UUID.randomUUID() + ".tmp");
        file.transferTo(temp);
        return DataResult.of(converterService.uploadFile(temp));
    }

    @SneakyThrows
    @PostMapping("/dbp/upload")
    public DataResult<UploadVO> edbpUploadFile(@RequestParam("file") MultipartFile file) {
        // Verify file suffix
        String fileExtension = FileUtils.getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileExtension.equalsIgnoreCase(FileUtils.ConfigFile.DBP.name())) {
            return DataResult.error("1", "The uploaded file must be a dbp file！");
        }
        File temp = new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + UUID.randomUUID() + ".tmp");
        file.transferTo(temp);
        return DataResult.of(converterService.dbpUploadFile(temp));
    }


    /**
     * Import the connection information of datagrip, copy the connection through ctrl/cmd + c (shift multiple selection), and then import it.
     * There is no password in the currently copied connection information, and there is no ssh connection information either.
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
