package ai.chat2db.server.web.api.controller.task;

import ai.chat2db.server.domain.api.model.Task;
import ai.chat2db.server.domain.api.param.TaskPageParam;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.tools.common.util.ContextUtils;
import ai.chat2db.server.web.api.aspect.ConnectionInfoAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;

@ConnectionInfoAspect
@RequestMapping("/api/task")
@RestController
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/list")
    public WebPageResult<Task> list() {
        TaskPageParam taskPageParam = new TaskPageParam();
        taskPageParam.setPageNo(1);
        taskPageParam.setPageSize(10);
        taskPageParam.setUserId(ContextUtils.getUserId());
        ServicePage<Task> task = taskService.page(taskPageParam);
        return WebPageResult.of(task.getData(), task.getTotal(), task.getPageNo(), task.getPageSize());
    }

    @GetMapping("/get/{id}")
    public DataResult<Task> get(@PathVariable Long id) {
        Task task = taskService.get(id);
        return DataResult.of(task);
    }

    @PostMapping("/cleanup")
    public DataResult<Integer> cleanup() {
        return DataResult.of(taskService.cleanupFinishedTasks(ContextUtils.getUserId()));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Task task = taskService.get(id);
        if(task == null){
            log.error("task is null");
            throw new RuntimeException("task is null");
        }
        if(!ContextUtils.getUserId().equals(task.getUserId())){
            log.error("task is not belong to user");
            throw new RuntimeException("task is not belong to user");
        }

        String downloadUrl = task.getDownloadUrl();
        if(downloadUrl == null || downloadUrl.isEmpty()){
            log.error("download url is null");
            throw new RuntimeException("download url is null");
        }

        File file = new File(downloadUrl);
        if (!file.exists() || !file.canRead()) {
            log.error("file not exists or not readable: {}", downloadUrl);
            throw new RuntimeException("Could not read the file: " + downloadUrl);
        }

        Resource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            log.error("malformed url: {}", downloadUrl, e);
            throw new RuntimeException(e);
        }

        String filename = file.getName();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }



}
