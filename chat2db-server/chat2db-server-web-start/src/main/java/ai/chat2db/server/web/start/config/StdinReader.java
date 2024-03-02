package ai.chat2db.server.web.start.config;//package ai.chat2db.server.start.config;
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
//        // Start a thread that reads stdin
//        new Thread(() -> readStdin()).start();
//    }
//
//    private void readStdin() {
//        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNextLine()) {
//            String line = scanner.nextLine();
//            // Process the received data
//            System.out.println("data received: " + line);
//            // Call other services or logic here
//        }
//    }
//
//}
