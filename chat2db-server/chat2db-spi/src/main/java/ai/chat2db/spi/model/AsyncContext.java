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

    private Consumer<Long> consumer;

    public void addProgress(Long progress) {
        if (consumer != null) {
            consumer.accept(progress);
        }
    }

    public void write(String message) {
        if (writer != null) {
            writer.write(message);
        }
    }
}
