package ai.chat2db.server.start.log;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * log 对象
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WebLog {
    /**
     * 调用方法
     */
    private String method;

    /**
     * 路径
     */
    private String path;

    /**
     * 查询条件
     */
    private String query;

    /**
     * 耗时 ms
     */
    private Long duration;

    /**
     * 耗时 ms
     */
    private LocalDateTime startTime;

    /**
     * 耗时 ms
     */
    private LocalDateTime endTime;

    /**
     * 请求
     */
    private String request;

    /**
     * 返回
     */
    private String response;

    /**
     * ip地址
     */
    private String ip;
}
