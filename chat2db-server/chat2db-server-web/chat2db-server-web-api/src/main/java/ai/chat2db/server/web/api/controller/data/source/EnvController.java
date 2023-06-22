package ai.chat2db.server.web.api.controller.data.source;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.enums.EnvTypeEnum;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.web.api.controller.data.source.vo.EnvVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 环境打标服务类
 *
 * @author moji
 * @version EnvController.java, v 0.1 2022年09月18日 14:04 moji Exp $
 * @date 2022/09/18
 */
@RequestMapping("/api/env")
@RestController
public class EnvController {

    /**
     * 查询环境列表
     *
     * @return
     */
    @GetMapping("/list")
    public ListResult<EnvVO> list() {
        List<EnvVO> envVOS = Arrays.stream(EnvTypeEnum.values()).map(envTypeEnum -> {
            EnvVO envVO = new EnvVO();
            envVO.setCode(envTypeEnum.getCode());
            envVO.setName(envTypeEnum.getDescription());
            return envVO;
        }).collect(Collectors.toList());
        return ListResult.of(envVOS);
    }

}
