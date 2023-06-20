package com.alibaba.dbhub.server.test.temp;

import com.alibaba.dbhub.server.test.common.BaseTest;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UserTest extends BaseTest {

    @Test
    public void test() {
        log.info("password:{}", DigestUtil.bcrypt("dbhub"));
    }
}
