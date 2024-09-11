package net.siudek.media.telegram;

import java.io.File;

import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class MyAmazingBot implements UpdateConsumer {

    private final TelegramBotsLongPollingApplication telegramBotsLongPollingApplication;
    private final TelegramProperties telegramProperties;
    private final VectorStore vectorStore;

    private TelegramClient telegramClient;

    @PostConstruct
    public void init() throws TelegramApiException {
        var token = telegramProperties.getSecret();
        telegramClient = new OkHttpTelegramClient(token);
        telegramBotsLongPollingApplication.registerBot(token, this);
    }

    @Override
    @SneakyThrows
    public void consume(Update update) {
        var hasMessage = update.hasMessage() && update.getMessage().hasText();
        if (!hasMessage)
            return;

        var userId = update.getMessage().getFrom().getId();
        if (userId != 6569540851L)
            return;

        // Set variables
        String message_text = update.getMessage().getText();
        long chat_id = update.getMessage().getChatId();


        var docs = vectorStore.similaritySearch(
          SearchRequest.defaults()
          .withQuery(message_text)
          .withTopK(1)
          .withSimilarityThreshold(0.60));

        if (docs.isEmpty()) {
          var responseMsg = SendMessage.builder()
          .chatId(chat_id)
          .text("No data")
          .build();
          telegramClient.execute(responseMsg);
        } else {
          var location = docs.get(0).getMetadata().get("location").toString();
          var responseMsg = SendPhoto.builder()
          .chatId(chat_id)
          .photo(new InputFile(new File(location)))
          .build();
          telegramClient.execute(responseMsg);
      }

    }
}
