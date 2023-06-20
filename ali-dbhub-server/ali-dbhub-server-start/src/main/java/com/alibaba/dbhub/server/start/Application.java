package com.alibaba.dbhub.server.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 *
 * @author Jiaju Zhuang
 */
@SpringBootApplication
@ComponentScan(value = {"com.alibaba.dbhub.server"})
@MapperScan("com.alibaba.dbhub.server.domain.repository.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
