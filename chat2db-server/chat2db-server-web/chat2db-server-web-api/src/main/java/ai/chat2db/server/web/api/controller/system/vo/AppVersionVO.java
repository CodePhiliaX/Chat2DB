package ai.chat2db.server.web.api.controller.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppVersionVO {
    /**
     * primary key
     */
    private Long id;

    /**
     * new version
     */
    private String version;

//    /**
//     * Which versions can be upgraded to this version
//     */
//    private String versionUse;

//    /**
//     * state
//     */
//    private String status;

    /**
     * downloadLink
     */
    private String downloadLink;

    /**
     * Manual update, automatic forced update
     */
    private String type;

//    /**
//     * Whitelist, for testing
//     */
//    private String whiteList;

    /**
     * Hot update package address
     */
    private String hotUpgradeUrl;

    /**
     * updateLog
     */
    private String updateLog;

    /**
     * desktop
     */
    private boolean desktop;
}
