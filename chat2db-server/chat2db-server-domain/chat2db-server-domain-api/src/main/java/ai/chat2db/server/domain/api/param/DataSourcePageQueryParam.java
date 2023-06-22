package ai.chat2db.server.domain.api.param;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version DataSourcePageQueryParam.java, v 0.1 2022年09月23日 15:27 moji Exp $
 * @date 2022/09/23
 */
@Data
public class DataSourcePageQueryParam extends PageQueryParam {

    /**
     * 搜索关键词
     */
    private String searchKey;
}
