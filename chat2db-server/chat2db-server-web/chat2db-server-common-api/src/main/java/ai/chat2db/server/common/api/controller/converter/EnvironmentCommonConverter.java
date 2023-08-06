package ai.chat2db.server.common.api.controller.converter;

import java.util.List;

import ai.chat2db.server.common.api.controller.vo.SimpleEnvironmentVO;
import ai.chat2db.server.domain.api.model.Environment;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

/**
 * converter
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Mapper(componentModel = "spring")
public abstract class EnvironmentCommonConverter {


    /**
     * convert
     *
     * @param list
     * @return
     */
    public abstract List<SimpleEnvironmentVO> dto2vo(List<Environment> list);
}
