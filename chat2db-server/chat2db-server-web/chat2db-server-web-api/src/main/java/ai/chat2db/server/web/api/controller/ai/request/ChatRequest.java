package ai.chat2db.server.web.api.controller.ai.request;

import java.util.List;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * Chat query input parameters
 *
 * @author moji
 * @version ChatQueryRequest.java, v 0.1 April 2, 2023 13:28 moji Exp $
 * @date 2023/04/02
 */
@Data
public class ChatRequest {

    private String prompt;

}
