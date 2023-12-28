package ai.chat2db.server.web.api.controller.ai.dify.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class DifyChatMessage {

    @JsonProperty(value = "query")
    private String query;

    @JsonProperty(value = "inputs")
    private Map<String,String> inputs;

    @JsonProperty(value = "conversation_id")
    private String conversation_id;

    private String user;



}
