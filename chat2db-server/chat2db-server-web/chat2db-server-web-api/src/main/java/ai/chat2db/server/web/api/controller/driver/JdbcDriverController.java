package ai.chat2db.server.web.api.controller.driver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.server.domain.api.service.JdbcDriverService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.controller.driver.request.JdbcDriverRequest;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.JdbcJarUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * JDBC driver management
 *
 * @author moji
 * @version JdbcDriverController.java, v 0.1 2022年09月16日 17:41 moji Exp $
 * @date 2022/09/16
 */
@RequestMapping("/api/jdbc/driver")
@RestController
public class JdbcDriverController {

    @Autowired
    private JdbcDriverService jdbcDriverService;

    /**
     * 查询当前DB驱动信息
     *
     * @param dbType
     * @return
     */
    @GetMapping("/list")
    public DataResult<DBConfig> list(@RequestParam String dbType) {
        return jdbcDriverService.getDrivers(dbType);
    }

    /**
     * 下载驱动
     *
     * @param dbType
     * @return
     */

    @GetMapping("/download")
    public ActionResult download(@RequestParam String dbType) {
        return jdbcDriverService.download(dbType);
    }

    /**
     * 上传驱动
     *
     * @param multipartFiles
     * @return
     */
    @PostMapping("/upload")
    public ListResult<String> upload(@RequestParam MultipartFile[] multipartFiles) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < multipartFiles.length; i++) {

            MultipartFile multipartFile = multipartFiles[i];
            String originalFilename = FilenameUtils.getName(multipartFile.getOriginalFilename());
            String location = JdbcJarUtils.PATH + originalFilename;
            try {
                multipartFile.transferTo(new File(location));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            list.add(originalFilename);
        }
        return ListResult.of(list);
    }

    /**
     * save
     *
     * @param request
     * @return
     */
    @PostMapping("/save")
    public ActionResult save(@RequestBody JdbcDriverRequest request) {

        return jdbcDriverService.upload(request.getDbType(), request.getJdbcDriverClass(),
            String.join(",", request.getJdbcDriver()));
    }

    ///**
    // * 删除驱动
    // *
    // * @param request
    // * @return
    // */
    //@DeleteMapping("/delete")
    //public ActionResult delete(@RequestBody KeyDeleteRequest request) {
    //    return null;
    //}
}
