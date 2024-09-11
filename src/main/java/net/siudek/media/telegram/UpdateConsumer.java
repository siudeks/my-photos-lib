package net.siudek.media.telegram;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateConsumer extends LongPollingUpdateConsumer {
    ExecutorService updatesProcessorExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    default void consume(List<Update> updates) {
        updates.forEach(update -> updatesProcessorExecutor.execute(() -> consume(update)));
    }

    void consume(Update update);
}
