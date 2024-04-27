package ai.chat2db.server.web.api.controller.rdb.data.observer;

import java.util.ArrayList;
import java.util.List;

public class LoggingDataExportObserver implements DataExportObserver {
    private final List<String> logs = new ArrayList<>();

    @Override
    public void onDataExported(String log) {
        logs.add(log);
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}
