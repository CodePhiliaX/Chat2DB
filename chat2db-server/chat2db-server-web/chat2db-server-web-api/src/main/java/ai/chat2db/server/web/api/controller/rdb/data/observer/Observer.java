package ai.chat2db.server.web.api.controller.rdb.data.observer;

public interface Observer {
    void notifyInfo(String log);
    void notifyError(String log);
}
