package ai.chat2db.server.web.api.controller.pin.converter;

import ai.chat2db.server.domain.api.param.PinTableParam;
import ai.chat2db.server.web.api.controller.pin.request.PinTableRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PinWebConverter {


    public abstract PinTableParam req2param(PinTableRequest request);
}
