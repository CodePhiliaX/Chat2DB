package ai.chat2db.server.web.api.controller.rdb.doc.event;

import org.springframework.context.ApplicationEvent;

/**
 * TemplateEvent
 *
 * @author lzy
 **/
public class TemplateEvent extends ApplicationEvent {
    public TemplateEvent(String key) {
        super(key);
    }
}
