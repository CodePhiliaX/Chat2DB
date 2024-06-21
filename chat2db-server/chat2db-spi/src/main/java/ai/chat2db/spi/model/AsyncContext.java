package ai.chat2db.spi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.PrintWriter;
import java.util.function.Consumer;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsyncContext {

    private PrintWriter writer;

    private boolean containsData;

    private AsyncCall call;

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
