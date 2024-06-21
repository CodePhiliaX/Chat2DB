package ai.chat2db.spi.model;

public interface AsyncCall {


    void setProgress(int progress);


    void info(String message);


    void error(String message);


    void finish();

}
