package ai.chat2db.server.web.api.controller.ncx;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import ai.chat2db.server.web.api.controller.ncx.service.ConverterService;
import ai.chat2db.server.web.api.controller.ncx.vo.UploadVO;
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

    @SneakyThrows
    @PostMapping("/ncx/upload")
    public DataResult<UploadVO> uploadFile(@RequestParam("file") MultipartFile file) {
        // 验证文件后缀
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        if (!isAllowedExtension(fileExtension)) {
            return DataResult.error("1", "上传的文件必须是ncx文件！");
        }
        File temp = new File(ConfigUtils.CONFIG_BASE_PATH + File.separator + "temp.tmp");
        file.transferTo(temp);
        return DataResult.of(converterService.uploadFile(temp));
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }

    private boolean isAllowedExtension(String extension) {
        // 只允许上传的文件后缀
        String[] allowedExtensions = {"ncx"};
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
