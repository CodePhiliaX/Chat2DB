/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller;

import com.alibaba.dbhub.server.domain.support.sql.SSHManager;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.tools.common.config.AliDbhubProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jipengfei
 * @version : HomeController.java, v 0.1 2022年09月18日 14:52 jipengfei Exp $
 */
@RestController
@RequestMapping("/api/system")
@Slf4j
public class SystemController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AliDbhubProperties aliDbhubProperties;

    /**
     * 检测是否成功
     *
     * @return
     */
    @GetMapping
    public DataResult<String> get() {
        return DataResult.of("success");
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    @GetMapping("/get-version-a")
    public DataResult<String> getVersion() {
        return DataResult.of(aliDbhubProperties.getVersion());
    }

    /**
     * 退出服务
     */
    @PostMapping("/stop")
    public DataResult<String> stop() {
        log.info("退出应用");
        new Thread(() -> {
            // 会在100ms以后 退出后台
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("开始退出Spring应用");
            SSHManager.close();
            try {
                SpringApplication.exit(applicationContext);
            } catch (Exception ignore) {
            }
            // 有可能SpringApplication.exit 会退出失败
            // 直接系统退出
            log.info("开始退出系统应用");
            try {
                System.exit(0);
            } catch (Exception ignore) {
            }
        }).start();
        return DataResult.of("ok");
    }
}
