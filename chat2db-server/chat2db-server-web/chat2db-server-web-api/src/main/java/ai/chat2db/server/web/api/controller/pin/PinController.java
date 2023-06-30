package ai.chat2db.server.web.api.controller.pin;

import ai.chat2db.server.domain.api.service.PinService;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.web.api.controller.pin.converter.PinWebConverter;
import ai.chat2db.server.web.api.controller.pin.request.PinTableRequest;
import ai.chat2db.server.web.api.controller.rdb.request.DataExportRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/pin")
@RestController
public class PinController {

    @Autowired
    private PinService pinService;

    @Autowired
    private PinWebConverter pinWebConverter;

    @PostMapping("/table/add")
    public ActionResult add(@Valid @RequestBody PinTableRequest request) {
        return pinService.pinTable(pinWebConverter.req2param(request));
    }

    @PostMapping("/table/delete")
    public ActionResult delete(@Valid @RequestBody PinTableRequest request) {
        return pinService.deletePinTable(pinWebConverter.req2param(request));
    }


}
