package net.siudek.media.telegram;

import java.io.File;
import java.util.HashMap;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.google.errorprone.annotations.concurrent.LazyInit;

import jakarta.annotation.PostConstruct;

@Component
public class MyAmazingBot implements UpdateConsumer {

    private final TelegramBotsLongPollingApplication telegramBotsLongPollingApplication;
    private final TelegramProperties telegramProperties;
    private final VectorStore vectorStore;

    public MyAmazingBot(TelegramBotsLongPollingApplication telegramBotsLongPollingApplication, TelegramProperties telegramProperties, VectorStore vectorStore) {
      this.telegramBotsLongPollingApplication = telegramBotsLongPollingApplication;
      this.telegramProperties = telegramProperties;
      this.vectorStore = vectorStore;
    }

    private @LazyInit @MonotonicNonNull TelegramClient telegramClient;

    @PostConstruct
    @EnsuresNonNull("telegramClient")
    public void init() throws TelegramApiException {
        var token = telegramProperties.secret();
        telegramClient = new OkHttpTelegramClient(token);
        telegramBotsLongPollingApplication.registerBot(token, this);
    }

    @Override
    public void consume(Update update) throws TelegramApiException {
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
