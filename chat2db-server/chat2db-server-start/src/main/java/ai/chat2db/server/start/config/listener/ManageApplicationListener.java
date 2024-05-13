//package ai.chat2db.server.start.config.listener;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.TypeReference;
//
//import ai.chat2db.server.tools.base.enums.SystemEnvironmentEnum;
//import ai.chat2db.server.tools.base.wrapper.result.DataResult;
//import cn.hutool.http.HttpUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.util.Assert;
//
///**
// * Used to manage startup
// * Prevent starting multiple
// *
// * @author zhuangjiaju
// * @date 2023/05/08
// */
//@Slf4j
//public class ManageApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
//
//    @Override
//    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
//        Integer serverPort = event.getEnvironment().getProperty("server.port", Integer.class);
//        Assert.notNull(serverPort, "server.port configuration information");
//        log.info("The startup port is: {}", serverPort);
//        String environment = event.getEnvironment().getProperty("spring.profiles.active", String.class);
//
//        // Try to access to confirm whether the application has been started
//        DataResult<String> dataResult;
//        try {
//            String body = HttpUtil.get("http://127.0.0.1:" + serverPort + "/api/system/get-version-a", 10);
//            dataResult = JSON.parseObject(body, new TypeReference<>() {});
//        } catch (Exception e) {
//            // Throwing an exception means that there is no old startup or the old one is unreliable.
//            log.info("Attempts to access old applications failed. This exception is not important. It will be output during normal startup, so please ignore it." + e.getMessage());
//
//            // Try killing the old process
//            killOldIfNecessary(environment);
//            return;
//        }
//
//        if (dataResult == null || BooleanUtils.isNotTrue(dataResult.getSuccess())) {
//            // Try killing the old process
//            killOldIfNecessary(environment);
//            return;
//        }
//
//        // Indicates that the old process is available
//        log.info("There is already a started application on the current interface, and this application is no longer started.");
//        System.exit(0);
//    }
//
//    private void killOldIfNecessary(String environment) {
//        try {
//            ProcessHandle.allProcesses().forEach(process -> {
//                String command = process.info().command().orElse(null);
//                // Not a java application
//                boolean isJava = StringUtils.endsWithIgnoreCase(command, "java") || StringUtils.endsWithIgnoreCase(
//                    command,
//                    "java.exe");
//                if (!isJava) {
//                    return;
//                }
//                String[] arguments = process.info().arguments().orElse(null);
//                // no parameters
//                if (arguments == null) {
//                    return;
//                }
//                // Is it dbhub?
//                boolean isDbhub = false;
//                String environmentArgument = null;
//                for (String argument : arguments) {
//                    if (StringUtils.equals("chat2db-server-start.jar", argument)) {
//                        isDbhub = true;
//                    }
//                    if (StringUtils.startsWith(argument, "-Dspring.profiles.active=")) {
//                        environmentArgument = StringUtils.substringAfter(argument, "-Dspring.profiles.active=");
//                    }
//                }
//                // Not dbhub
//                if (!isDbhub) {
//                    return;
//                }
//                // Determine whether it is a formal environment
//                if (StringUtils.equals(SystemEnvironmentEnum.RELEASE.getCode(), environment) && StringUtils.equals(
//                    SystemEnvironmentEnum.RELEASE.getCode(), environmentArgument)) {
//                    log.info("The formal environment requires closing the process");
//                    destroyProcess(process, command, arguments);
//                    return;
//                }
//
//                // Determine whether it is a test environment
//                if (StringUtils.equals(SystemEnvironmentEnum.TEST.getCode(), environment) && StringUtils.equals(
//                    SystemEnvironmentEnum.TEST.getCode(), environmentArgument)) {
//                    log.info("The test environment needs to shut down the process");
//                    destroyProcess(process, command, arguments);
//                    return;
//                }
//
//                // Determine whether it is a local environment
//                boolean devDestroy = StringUtils.equals(SystemEnvironmentEnum.DEV.getCode(), environment) && (
//                    environmentArgument == null
//                        || StringUtils.equals(SystemEnvironmentEnum.DEV.getCode(), environmentArgument));
//                if (devDestroy) {
//                    log.info("The local environment needs to close the process");
//                    destroyProcess(process, command, arguments);
//                }
//            });
//        } catch (Throwable t) {
//            log.warn("Attempts to close redundant processes failed and did not affect normal startup.", t);
//        }
//
//    }
//
//    private void destroyProcess(ProcessHandle process, String command, String[] arguments) {
//        log.info("Checked that there are processes that need to be shut down:{},{}", JSON.toJSONString(command), JSON.toJSONString(arguments));
//        try {
//            process.destroy();
//        } catch (Exception e) {
//            log.error("Failed to end process", e);
//        }
//    }
//}