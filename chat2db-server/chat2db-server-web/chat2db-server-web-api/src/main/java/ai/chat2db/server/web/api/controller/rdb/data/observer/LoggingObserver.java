package ai.chat2db.server.web.api.controller.rdb.data.observer;

import cn.hutool.core.date.DatePattern;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class LoggingObserver implements Observer {
    private final List<String> logs = new ArrayList<>();
    private int errorCounter = 0;

    @Override
    public void notifyInfo(String log) {
        logs.add(String.format("[%s] [Info]:%s", getTime(), log));
    }


    @Override
    public void notifyError(String log) {
        errorCounter++;
        logs.add("[" + getTime() + "] [Error]:execute sql failed, error sql:"+log+"");
    }

    public String getLogs() {
        return String.join("\n", logs);
    }

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
        return now.format(dateTimeFormatter);
    }
}
