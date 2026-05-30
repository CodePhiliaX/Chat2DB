package ai.chat2db.server.web.api.controller.ai.ollama.client;

import ai.chat2db.server.web.api.controller.ai.request.ChatRequest;
import ai.chat2db.server.web.api.controller.ai.response.ChatCompletionResponse;
import ai.chat2db.server.web.api.controller.ai.response.ChatChoice;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.common.Usage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ollama AI Client for local AI models
 * 
 * @author Chat2DB Team
 */
@Slf4j
public class OllamaAIClient {

    private static final String DEFAULT_OLLAMA_HOST = "http://localhost:11434";
    private static final String CHAT_ENDPOINT = "/api/chat";
    
    private final RestTemplate restTemplate;
    private final String ollamaApiHost;
    private final String model;
    private final ObjectMapper objectMapper;

    public OllamaAIClient(String ollamaApiHost, String model) {
        this.restTemplate = new RestTemplate();
        this.ollamaApiHost = ollamaApiHost != null ? ollamaApiHost : DEFAULT_OLLAMA_HOST;
        this.model = model;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send chat request to Ollama
     */
    public ChatCompletionResponse chatCompletion(ChatRequest request) {
        try {
            String url = ollamaApiHost + CHAT_ENDPOINT;
            
            // Build Ollama request
            Map<String, Object> ollamaRequest = buildOllamaRequest(request);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(ollamaRequest, headers);
            
            log.info("Sending request to Ollama: {} with model: {}", url, model);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            return parseOllamaResponse(response.getBody());
            
        } catch (Exception e) {
            log.error("Error calling Ollama API", e);
            return createErrorResponse(e.getMessage());
        }
    }

    /**
     * Build Ollama request from ChatRequest
     */
    private Map<String, Object> buildOllamaRequest(ChatRequest request) {
        Map<String, Object> ollamaRequest = new HashMap<>();
        
        // Model
        ollamaRequest.put("model", model);
        
        // Messages
        List<Map<String, String>> messages = new ArrayList<>();
        if (request.getPrompt() != null) {
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", request.getPrompt());
            messages.add(message);
        }
        ollamaRequest.put("messages", messages);
        
        // Stream (disable for now)
        ollamaRequest.put("stream", false);
        
        // Options
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("top_p", 0.9);
        ollamaRequest.put("options", options);
        
        return ollamaRequest;
    }

    /**
     * Parse Ollama response to ChatCompletionResponse
     */
    private ChatCompletionResponse parseOllamaResponse(String responseBody) {
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            ChatCompletionResponse response = new ChatCompletionResponse();
            
            // Extract message content
            if (rootNode.has("message") && rootNode.get("message").has("content")) {
                String content = rootNode.get("message").get("content").asText();
                
                // Create choice
                ChatChoice choice = new ChatChoice();
                choice.setMessage(new Message());
                choice.getMessage().setContent(content);
                choice.setIndex(0);
                choice.setFinishReason("stop");
                
                List<ChatChoice> choices = new ArrayList<>();
                choices.add(choice);
                response.setChoices(choices);
            }
            
            // Set usage info if available
            if (rootNode.has("prompt_eval_count") || rootNode.has("eval_count")) {
                Usage usage = new Usage();
                if (rootNode.has("prompt_eval_count")) {
                    usage.setPromptTokens(rootNode.get("prompt_eval_count").asInt());
                }
                if (rootNode.has("eval_count")) {
                    usage.setCompletionTokens(rootNode.get("eval_count").asInt());
                }
                usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());
                response.setUsage(usage);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("Error parsing Ollama response", e);
            return createErrorResponse("Failed to parse response: " + e.getMessage());
        }
    }

    /**
     * Create error response
     */
    private ChatCompletionResponse createErrorResponse(String errorMessage) {
        ChatCompletionResponse response = new ChatCompletionResponse();
        // Create an empty choice with error info
        List<ChatChoice> choices = new ArrayList<>();
        ChatChoice choice = new ChatChoice();
        choice.setMessage(new Message());
        choice.getMessage().setContent("Error: " + errorMessage);
        choice.setFinishReason("error");
        choices.add(choice);
        response.setChoices(choices);
        return response;
    }

    /**
     * Test Ollama connection
     */
    public boolean testConnection() {
        try {
            String url = ollamaApiHost + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Ollama connection test failed", e);
            return false;
        }
    }

    /**
     * Get available models
     */
    public List<String> getAvailableModels() {
        try {
            String url = ollamaApiHost + "/api/tags";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            List<String> models = new ArrayList<>();
            
            if (rootNode.has("models")) {
                for (JsonNode modelNode : rootNode.get("models")) {
                    if (modelNode.has("name")) {
                        models.add(modelNode.get("name").asText());
                    }
                }
            }
            
            return models;
            
        } catch (Exception e) {
            log.error("Error getting Ollama models", e);
            return new ArrayList<>();
        }
    }
}
