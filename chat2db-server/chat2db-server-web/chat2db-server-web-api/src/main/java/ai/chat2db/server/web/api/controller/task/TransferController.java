package ai.chat2db.server.web.api.controller.task;

import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.web.api.controller.task.biz.TransferBizService;
import ai.chat2db.server.web.api.controller.task.request.DataTransferRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据传输控制器
 */
@RequestMapping("/api/transfer")
@RestController
public class TransferController {

    @Autowired
    private TransferBizService transferBizService;

    @PostMapping("/data")
    public DataResult<Long> transferData(@Valid @RequestBody DataTransferRequest request) {
        return transferBizService.transferData(request);
    }
}
