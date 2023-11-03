package ai.chat2db.server.web.api.controller.system.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppVersionVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 新版本
     */
    private String version;

//    /**
//     * 哪些版本可以升级到该版本
//     */
//    private String versionUse;

//    /**
//     * 状态
//     */
//    private String status;

    /**
     * 下载地址
     */
    private String downloadLink;

    /**
     * 手工更新，自动强制更新
     */
    private String type;

//    /**
//     * 白名单，用于测试
//     */
//    private String whiteList;

    /**
     * 热更新包地址
     */
    private String hotUpgradeUrl;

    /**
     * 更新日志
     */
    private String updateLog;

    /**
     * 桌面
     */
    private boolean desktop;
}
