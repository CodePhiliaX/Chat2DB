package ai.chat2db.server.domain.core.converter;

import ai.chat2db.server.domain.api.param.PinTableParam;
import ai.chat2db.server.domain.api.param.TablePageQueryParam;
import ai.chat2db.server.domain.repository.entity.PinTableDO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class PinTableConverter {

    /**
     *
     * @param param
     * @return
     */
    public abstract PinTableDO param2do(PinTableParam param);



    public abstract PinTableParam toPinTableParam (TablePageQueryParam param);
}
