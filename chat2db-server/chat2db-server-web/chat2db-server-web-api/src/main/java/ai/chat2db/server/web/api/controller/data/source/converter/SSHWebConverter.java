
package ai.chat2db.server.web.api.controller.data.source.converter;

import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.server.web.api.controller.data.source.request.SSHTestRequest;

import org.mapstruct.Mapper;

/**
 * @author jipengfei
 * @version : SSHWebConverter.java
 */
@Mapper(componentModel = "spring")
public abstract class SSHWebConverter {

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract SSHInfo toInfo(SSHTestRequest request);
}