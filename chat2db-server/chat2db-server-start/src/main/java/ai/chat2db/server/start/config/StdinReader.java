//package ai.chat2db.server.start.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Scanner;
//
//@Component
//public class StdinReader implements ApplicationListener<ApplicationReadyEvent> {
//
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent event) {
//        // 启动读取 stdin 的线程
//        new Thread(() -> readStdin()).start();
//    }
//
//    private void readStdin() {
//        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            // 处理接收到的数据
//            System.out.println("接收到数据: " + line);
//            // 在这里调用其他服务或逻辑
//        }
//    }
//
//}
