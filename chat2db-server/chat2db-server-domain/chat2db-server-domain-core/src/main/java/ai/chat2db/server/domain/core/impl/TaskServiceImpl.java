package ai.chat2db.server.domain.core.impl;

import ai.chat2db.server.domain.api.enums.DeletedTypeEnum;
import ai.chat2db.server.domain.api.enums.TaskStatusEnum;
import ai.chat2db.server.domain.api.model.Task;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskPageParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.domain.api.service.TaskService;
import ai.chat2db.server.domain.core.converter.TaskConverter;
import ai.chat2db.server.domain.repository.MapperUtils;
import ai.chat2db.server.domain.repository.entity.TaskDO;
import ai.chat2db.server.tools.base.wrapper.ServicePage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    /**
     * task converter
     */
    @Autowired
    private TaskConverter taskConverter;

    @Override
    public Long create(TaskCreateParam param) {
        TaskDO taskDO = taskConverter.todo(param);
        taskDO.setDeleted(DeletedTypeEnum.N.name());
        taskDO.setTaskStatus(TaskStatusEnum.INIT.name());
        MapperUtils.getTaskMapper().insert(taskDO);
        return taskDO.getId();
    }

    @Override
    public void updateStatus(TaskUpdateParam param) {
        TaskDO taskDO = new TaskDO();
        taskDO.setId(param.getId());
        taskDO.setTaskStatus(param.getTaskStatus());
        taskDO.setTaskProgress(param.getTaskProgress());
        taskDO.setDownloadUrl(param.getDownloadUrl());
        taskDO.setContent(param.getContent());
        MapperUtils.getTaskMapper().updateById(taskDO);
        
    }

    @Override
    public ServicePage<Task> page(TaskPageParam param) {
        if (param.getDeleted() == null) {
            param.setDeleted(DeletedTypeEnum.N.name());
        }
        Page<TaskDO> page = new Page<>();
        page.setCurrent(param.getPageNo());
        page.setSize(param.getPageSize());
        page.setOrders(Lists.newArrayList(OrderItem.desc("gmt_create")));
        IPage<TaskDO> iPage = MapperUtils.getTaskMapper().pageQuery(page, param);
        if (iPage != null) {
            return ServicePage.of(taskConverter.toModel(iPage.getRecords()), iPage.getTotal(), param.getPageNo(), param.getPageSize());
        }
        return ServicePage.empty(param.getPageNo(), param.getPageSize());
    }

    @Override
    public Task get(Long id) {
        TaskDO task = MapperUtils.getTaskMapper().selectById(id);
        return taskConverter.toModel(task);
    }

    @Override
    public int cleanupFinishedTasks(Long userId) {
        if (userId == null) {
            return 0;
        }
        List<String> cleanableStatuses = Lists.newArrayList(TaskStatusEnum.FINISH.name(), TaskStatusEnum.ERROR.name());
        LambdaQueryWrapper<TaskDO> queryWrapper = new LambdaQueryWrapper<TaskDO>()
                .eq(TaskDO::getUserId, userId)
                .and(wrapper -> wrapper.eq(TaskDO::getDeleted, DeletedTypeEnum.N.name()).or().isNull(TaskDO::getDeleted))
                .in(TaskDO::getTaskStatus, cleanableStatuses);
        List<TaskDO> tasks = MapperUtils.getTaskMapper().selectList(queryWrapper);
        if (tasks == null || tasks.isEmpty()) {
            return 0;
        }

        tasks.forEach(this::deleteTaskTempFile);

        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<TaskDO>()
                .eq(TaskDO::getUserId, userId)
                .and(wrapper -> wrapper.eq(TaskDO::getDeleted, DeletedTypeEnum.N.name()).or().isNull(TaskDO::getDeleted))
                .in(TaskDO::getTaskStatus, cleanableStatuses)
                .set(TaskDO::getDeleted, DeletedTypeEnum.Y.name());
        return MapperUtils.getTaskMapper().update(null, updateWrapper);
    }

    private void deleteTaskTempFile(TaskDO task) {
        if (task == null || StringUtils.isBlank(task.getDownloadUrl())) {
            return;
        }
        File file = new File(task.getDownloadUrl());
        if (!isSafeTempFile(file)) {
            log.warn("Skip deleting non-temp task file, taskId={}, path={}", task.getId(), task.getDownloadUrl());
            return;
        }
        if (file.exists() && file.isFile() && !file.delete()) {
            log.warn("Failed to delete task temp file, taskId={}, path={}", task.getId(), task.getDownloadUrl());
        }
    }

    private boolean isSafeTempFile(File file) {
        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            String tempPath = tempDir.getCanonicalPath() + File.separator;
            String filePath = file.getCanonicalPath();
            return filePath.startsWith(tempPath);
        } catch (IOException e) {
            log.warn("Failed to validate task temp file path: {}", file.getPath(), e);
            return false;
        }
    }
}

