package ai.chat2db.server.start.test.druid;

import com.alibaba.fastjson2.JSON;

import ai.chat2db.server.domain.core.cache.MemoryCacheManage;
import ai.chat2db.server.start.test.dto.TestDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

@Slf4j
public class SerializationUtilsTest {

    @Test
    public void test() {
        TestDTO test = TestDTO.builder().name("test").build();

        byte[] bytes = SerializationUtils.serialize(test);

        TestDTO t2 = SerializationUtils.deserialize(bytes);

        log.info("tt{}", t2);
    }

    @Test
    public void cache() throws InterruptedException {
        TestDTO test = TestDTO.builder().name("test").build();
        MemoryCacheManage.put("t1", test);
        TestDTO t1 = MemoryCacheManage.get("t1");
        log.info("t1:{}", JSON.toJSONString(t1));
        Thread.sleep(12000);
        t1 = MemoryCacheManage.get("t1");
        log.info("t1:{}", JSON.toJSONString(t1));
    }

}
