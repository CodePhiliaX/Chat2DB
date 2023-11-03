package ai.chat2db.server.start.config.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;

import ai.chat2db.server.tools.base.enums.SystemEnvironmentEnum;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

/**
 * 用来管理启动
 * 防止启动多个
 *
 * @author zhuangjiaju
 * @date 2023/05/08
 */
@Slf4j
public class ManageApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Integer serverPort = event.getEnvironment().getProperty("server.port", Integer.class);
        Assert.notNull(serverPort, "server.port配置信息");
        log.info("启动端口为：{}", serverPort);
        String environment = event.getEnvironment().getProperty("spring.profiles.active", String.class);

        // 尝试访问确认应用是否已经启动
        DataResult<String> dataResult;
        try {
            String body = HttpUtil.get("http://127.0.0.1:" + serverPort + "/api/system/get-version-a", 10);
            dataResult = JSON.parseObject(body, new TypeReference<>() {});
        } catch (Exception e) {
            // 抛出异常 代表没有旧的启动 或者旧的不靠谱
            log.info("尝试访问旧的应用失败。本异常不重要，正常启动启动都会输出，请忽略。" + e.getMessage());

            // 尝试杀死旧的进程
            killOldIfNecessary(environment);
            return;
        }

        if (dataResult == null || BooleanUtils.isNotTrue(dataResult.getSuccess())) {
            // 尝试杀死旧的进程
            killOldIfNecessary(environment);
            return;
        }

        // 代表旧的进程是可以用的
        log.info("当前接口已经存在启动的应用了，本应用不在启动");
        System.exit(0);
    }

    private void killOldIfNecessary(String environment) {
        try {
            ProcessHandle.allProcesses().forEach(process -> {
                String command = process.info().command().orElse(null);
                // 不是java应用
                boolean isJava = StringUtils.endsWithIgnoreCase(command, "java") || StringUtils.endsWithIgnoreCase(
                    command,
                    "java.exe");
                if (!isJava) {
                    return;
                }
                String[] arguments = process.info().arguments().orElse(null);
                // 没有参数
                if (arguments == null) {
                    return;
                }
                // 是否是dbhub
                boolean isDbhub = false;
                String environmentArgument = null;
                for (String argument : arguments) {
                    if (StringUtils.equals("chat2db-server-start.jar", argument)) {
                        isDbhub = true;
                    }
                    if (StringUtils.startsWith(argument, "-Dspring.profiles.active=")) {
                        environmentArgument = StringUtils.substringAfter(argument, "-Dspring.profiles.active=");
                    }
                }
                // 不是dbhub
                if (!isDbhub) {
                    return;
                }
                // 判断是否是正式环境
                if (StringUtils.equals(SystemEnvironmentEnum.RELEASE.getCode(), environment) && StringUtils.equals(
                    SystemEnvironmentEnum.RELEASE.getCode(), environmentArgument)) {
                    log.info("正式环境需要关闭进程");
                    destroyProcess(process, command, arguments);
                    return;
                }

                // 判断是否是测试环境
                if (StringUtils.equals(SystemEnvironmentEnum.TEST.getCode(), environment) && StringUtils.equals(
                    SystemEnvironmentEnum.TEST.getCode(), environmentArgument)) {
                    log.info("测试环境需要关闭进程");
                    destroyProcess(process, command, arguments);
                    return;
                }

                // 判断是否是本地环境
                boolean devDestroy = StringUtils.equals(SystemEnvironmentEnum.DEV.getCode(), environment) && (
                    environmentArgument == null
                        || StringUtils.equals(SystemEnvironmentEnum.DEV.getCode(), environmentArgument));
                if (devDestroy) {
                    log.info("本地环境需要关闭进程");
                    destroyProcess(process, command, arguments);
                }
            });
        } catch (Throwable t) {
            log.warn("尝试关闭多余的进程失败，不影响正常启动", t);
        }

    }

    private void destroyProcess(ProcessHandle process, String command, String[] arguments) {
        log.info("检查到存在需要关闭的进程:{},{}", JSON.toJSONString(command), JSON.toJSONString(arguments));
        try {
            process.destroy();
        } catch (Exception e) {
            log.error("结束进程失败", e);
        }
    }
}