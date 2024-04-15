package net.siudek.media.llava;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.SneakyThrows;

@ApplicationModuleTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
public class ModuleTest {

  @Autowired
  ChatClient chatClient;

  @Value("${spring.ai.ollama.base-url}")
  String apiHost;

  @Test
  @SneakyThrows
  void shouldAccestHost() {

    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
    var restClient = RestClient.builder().baseUrl(apiHost).build();
    var adapter = RestClientAdapter.create(restClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();

    var llavaService = factory.createClient(LlavaPort.class);

    byte[] data = new ClassPathResource("llava/example1.jpg").getContentAsByteArray();
    String imageStr = Base64.getEncoder().encodeToString(data);
    var response2 = llavaService.generate(new GenerateBody("llava", "Describe the photo.", false,
        new String[] { imageStr }));
    Assertions.assertThat(response2).isEqualTo("SPARTAA");
  }

  @Test
  @SneakyThrows
  // not yet available
  // https://github.com/spring-projects/spring-ai/issues/421
  void useSpringAi() {
    byte[] data = new ClassPathResource("llava/example1.jpg").getContentAsByteArray();
    var userMessage = new UserMessage("Explain what do you see on this picture?",
        List.of(new Media(MimeTypeUtils.IMAGE_JPEG, data)));

    var options = OllamaOptions.create()
        .withModel("llava")
        .withTemperature(0.4f);
    var response = chatClient.call(new Prompt(List.of(userMessage), options));
    Assertions.assertThat(response).isNotNull();
  }

}
