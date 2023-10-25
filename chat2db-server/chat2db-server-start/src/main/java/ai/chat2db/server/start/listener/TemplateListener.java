package ai.chat2db.server.start.listener;

import ai.chat2db.server.start.config.util.CopyTemplate;
import ai.chat2db.server.web.api.controller.rdb.doc.event.TemplateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * TemplateListener
 *
 * @author lzy
 **/
@Component
public class TemplateListener {

    @Autowired
    private CopyTemplate copyTemplate;

    @EventListener(classes = TemplateEvent.class)
    public void copyTemplate() {
        //复制模板
        copyTemplate.copyTemplateFile();
    }

}
