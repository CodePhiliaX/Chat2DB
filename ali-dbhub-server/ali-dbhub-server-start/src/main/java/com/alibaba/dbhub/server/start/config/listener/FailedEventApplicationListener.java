package com.alibaba.dbhub.server.start.config.listener;

import com.alibaba.dbhub.server.start.config.util.SystemUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 应用启动失败的监听器
 * 应用启动失败了只是停止了tomcat 并没有停止应用 这里停止xia
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class FailedEventApplicationListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("启动失败，停止应用", event.getException());
        SystemUtils.stop();
    }
}