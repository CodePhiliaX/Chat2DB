package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.PinTableParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

import java.util.List;

public interface PinService {

    /**
     * User pin table
     * @param param
     * @return
     */
    ActionResult pinTable(PinTableParam param);


    /**
     * Delete pin table
     * @param param
     * @return
     */
    ActionResult deletePinTable(PinTableParam param);


    /**
     * Query user pin tables
     * @param param
     * @return
     */
    List<String> queryPinTables(PinTableParam param);
}
