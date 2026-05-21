package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.Task;
import ai.chat2db.server.domain.api.param.TaskCreateParam;
import ai.chat2db.server.domain.api.param.TaskPageParam;
import ai.chat2db.server.domain.api.param.TaskUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import ai.chat2db.server.tools.base.wrapper.ServicePage;

public interface TaskService {

    /**
     * create task
     *
     * @param param task param
     * @return task id
     */
    Long create(TaskCreateParam param);

    /**
     * update task status
     *
     * @param param task param
     * @return action result
     */
    void updateStatus(TaskUpdateParam param);


    /**
     * get task list
     *
     * @param param task id
     * @return task
     */
    ServicePage<Task> page(TaskPageParam param);

    /**
     * get task
     *
     * @param id task id
     * @return task
     */
    Task get(Long id);
}
