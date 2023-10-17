package ai.chat2db.server.web.api.controller.ai.DocParser;

import java.io.InputStream;
import java.util.List;

/**
 * @author CYY
 * @date 2023年03月20日 上午8:13
 * @description
 */
public abstract class AbstractParser {
    public abstract List<String> parse(InputStream inputStream) throws Exception;
}
