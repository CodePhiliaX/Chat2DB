package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.PrintWriter;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AsyncContext {

    protected PrintWriter writer;

    protected boolean containsData;

    protected AsyncCall call;

    public void setProgress(Integer progress) {
        if (call != null) {
            call.setProgress(progress);
        }
    }

    public void info(String message) {
        if (call != null) {
            call.info(message);
        }
    }

    public void error(String message) {
        if (call != null) {
            call.error(message);
        }
    }

    public void finish() {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
        if (call != null) {
            call.finish();
        }

    }

    public void write(String message) {
        if (writer != null) {
            writer.write(message);
        }
    }

}
