//package ai.chat2db.server.start.config.listener;
//
//import ai.chat2db.server.web.api.controller.system.util.SystemUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.LifecycleState;
//import org.apache.catalina.connector.Connector;
//import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.stereotype.Component;
//
///**
// * Custom tomcat parameters
// *
// * @author Jiaju Zhuang
// */
//@Component
//@Slf4j
//public class DbhubTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
//    @Override
//    public void customize(Connector connector) {
//        connector.addLifecycleListener(event -> {
//            // Exit the system directly after receiving the shutdown event, because sometimes the system will not exit.
//            if (LifecycleState.STOPPING == event.getLifecycle().getState()) {
//                SystemUtils.stop();
//            }
//        });
//    }
//}
