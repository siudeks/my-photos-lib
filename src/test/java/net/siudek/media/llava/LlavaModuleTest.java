package net.siudek.media.llava;

import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import lombok.SneakyThrows;
import net.siudek.media.host.MediaApplication;
import net.siudek.media.llava.OllamaPort.EmbeddingsBody;

@SpringBootTest(classes = MediaApplication.class)
@ActiveProfiles("test")
@Disabled
  public class LlavaModuleTest {

  @Autowired
  ChatModel chatModel;

  @Autowired
  EmbeddingModel embeddingModel;

  @Value("${spring.ai.ollama.base-url}")
  String apiHost;

  @Test
  @SneakyThrows
  void shouldAccestHost() {

    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
    var restClient = RestClient.builder().baseUrl(apiHost).build();
    var adapter = RestClientAdapter.create(restClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();

    var ollamaService = factory.createClient(OllamaPort.class);

    Models.assureModelsAvailable(ollamaService.list());

    byte[] data = new ClassPathResource("llava/example1.jpg").getContentAsByteArray();
    String imageStr = Base64.getEncoder().encodeToString(data);
    var llavaModelName = Models.Llava_v16_p7b.getNameAndTag();
    var response = ollamaService.generate(new GenerateBody(llavaModelName, "Describe the photo.", false, new String[] { imageStr }));
    var responseText = response.response();

    var embeddingsModelName = Models.Llama_v31_p8b.getModelName();
    var embeddingResponse1 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, responseText));


    Assertions.assertThat(embeddingResponse1.embedding()).hasSize(4096);
  }

  @Test
  @SneakyThrows
  // not yet available
  // https://github.com/spring-projects/spring-ai/issues/421
  void useSpringAi() {
    byte[] data = new ClassPathResource("llava/example1.jpg").getContentAsByteArray();
    var userMessage = new UserMessage("Explain what do you see on this picture?", List.of(new Media(MimeTypeUtils.IMAGE_JPEG, data)));
    var options1 = OllamaOptions.create().withModel(Models.Llava_v16_p7b.getNameAndTag()).withTemperature(0f);
    var prompt = new Prompt(userMessage, options1);

    var chatClient = ChatClient.builder(chatModel).defaultOptions(options1).build();
    
    var response = chatClient.prompt(prompt).call();
    var actual = response.content();

    var expected_v1 = "The image shows dry landscape with cracked earth and sparse vegetation. In the foreground, there is a large rock sitting on the ground, which appears to be in the center of the frame. The sky above is clear with some clouds, suggesting it might be either dawn or dusk given the soft lighting. There are no visible human-made structures or signs of recent activity, which gives the scene a remote and untouched appearance. The overall color palette is dominated by earth tones, with the rock providing a contrasting element.";
    var expected_v2 = "The image shows dry landscape with cracked earth and sparse vegetation. There is a large rock sitting on the ground. The sky above is clear with some clouds. The overall color palette is dominated by earth tones, with the rock providing a contrasting element.";

    Stream.of(expected_v1, expected_v2).forEach(expected -> {
      var similarity = similarity(actual, expected, Models.mxbai_embed_large);
      Assertions.assertThat(similarity).as("Actual vs Expected:\n[%s]\n <> \n[%s]", actual, expected_v1).isGreaterThan(0.95);
    });
  }

  @Test
  void generateEmbeddingsUsingNativeHttpService() {

    // to simplify testing we use pure httpclient instead of not yet mature Spring AI solutions
    // https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
    var restClient = RestClient.builder().baseUrl(apiHost).build();
    var adapter = RestClientAdapter.create(restClient);
    var factory = HttpServiceProxyFactory.builderFor(adapter).build();

    var ollamaService = factory.createClient(OllamaPort.class);

    Models.assureModelsAvailable(ollamaService.list());

    var sent1 = "This is the first sentence.";
    var sent2 = "This is the second sentence.";

    var embeddingsModelName = Models.Llama_v31_p8b.getNameAndTag();
    var embed1 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, sent1)).embedding();
    var embed2 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, sent2)).embedding();
    
    var similarity1 = Similarity.cosine(embed1, embed2);

    Assertions.assertThat(similarity1).as("Actual vs Expected:\n[%s]\n <> \n[%s]", sent1, sent2).isGreaterThan(0.90);
  }

  @Test
  void generateEmbeddings() {
    var sent1 = "This is the first sentence.";
    var sent2 = "This is the second sentence.";
    var options = OllamaOptions.create().withModel("llama3.1:8b").withTemperature(0f);
    var embed1m = embeddingModel.call(new EmbeddingRequest(List.of(sent1), options));
    var embed2m = embeddingModel.call(new EmbeddingRequest(List.of(sent2), options));
    var embed1 = embed1m.getResult().getOutput();
    var embed2 = embed2m.getResult().getOutput();
    var similarity = Similarity.cosine3(embed1, embed2);
    Assertions.assertThat(similarity).as("Actual vs Expected:\n[%s]\n <> \n[%s]", sent1, sent2).isGreaterThan(0.90);
  }

  double similarity(String actual, String expected, Models model) {
    var options2 = OllamaOptions.create().withModel(model.getNameAndTag()).withTemperature(0f);
    var expectedAsEmbeddings = embeddingModel.call(new EmbeddingRequest(List.of(expected), options2));
    var actualAsEmbeddings = embeddingModel.call(new EmbeddingRequest(List.of(actual), options2));
    return Similarity.cosine(expectedAsEmbeddings.getResult().getOutput(), actualAsEmbeddings.getResult().getOutput());
  }

}
