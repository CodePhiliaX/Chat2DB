package ai.chat2db.server.web.start.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * log object
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WebLog {
    /**
     * call method
     */
    private String method;

    /**
     * path
     */
    private String path;

    /**
     * Query conditions
     */
    private String query;

    /**
     * Time consuming ms
     */
    private Long duration;

    /**
     * Time consuming ms
     */
    private LocalDateTime startTime;

    /**
     * Time consuming ms
     */
    private LocalDateTime endTime;

    /**
     * request
     */
    private String request;

    /**
     * response
     */
    private String response;

    /**
     * IP address
     */
    private String ip;
}
