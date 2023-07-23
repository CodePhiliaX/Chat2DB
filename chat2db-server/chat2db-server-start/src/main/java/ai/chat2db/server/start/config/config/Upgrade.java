package ai.chat2db.server.start.config.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Upgrade implements Serializable {

    private String version;

    private List<Map<String,String>> downloadFiles;
}
