package net.siudek.media.images;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.ollama.api.OllamaApi.ChatResponse;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.ai.chat.prompt.Prompt;

@SpringBootTest(properties = {
    // interactive mode stops the test and waits for input
    // more: https://docs.spring.io/spring-shell/reference/execution.html
    "spring.shell.interactive.enabled=false"
})
public class AiTest {

  @Autowired
  ChatClient chatClient;

  @Test
  void aaa() {
    var response = chatClient.call(
        new Prompt(
            "Generate the names of 5 famous pirates.",
            OllamaOptions.create()
                .withModel("llama2")
                .withTemperature(0.4f)));
  }

}
