package ai.chat2db.server.web.api.controller.operation.saved.converter;

import java.util.List;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationCreateRequest;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationQueryRequest;
import ai.chat2db.server.web.api.controller.operation.saved.request.OperationUpdateRequest;
import ai.chat2db.server.web.api.controller.operation.saved.vo.OperationVO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author moji
 * @version DdlManageWebConverter.java, v 0.1 September 26, 2022 10:08 moji Exp $
 * @date 2022/09/26
 */
@Mapper(componentModel = "spring")
public abstract class OperationWebConverter {

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract OperationSavedParam req2param(OperationCreateRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract OperationUpdateParam updateReq2param(OperationUpdateRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract OperationPageQueryParam queryReq2param(OperationQueryRequest request, Long userId);

    /**
     * Model conversion
     *
     * @param ddlDTO
     * @return
     */
    @Mappings({
        @Mapping(target = "connectable", expression = "java(ddlDTO.getDataSourceName() != null)"),
    })
    public abstract OperationVO dto2vo(Operation ddlDTO);

    /**
     * Model conversion
     *
     * @param ddlDTOS
     * @return
     */
    public abstract List<OperationVO> dto2vo(List<Operation> ddlDTOS);
}
