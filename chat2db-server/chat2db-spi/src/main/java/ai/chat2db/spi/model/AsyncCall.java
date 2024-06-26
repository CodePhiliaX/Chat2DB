package ai.chat2db.spi.model;

import java.util.Map;

public interface AsyncCall {


    void setProgress(int progress);


    void info(String message);


    void error(String message);


    void update(Map<String,Object> map);


    void finish();

}
