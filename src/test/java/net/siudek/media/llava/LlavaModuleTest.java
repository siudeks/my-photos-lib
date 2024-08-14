package net.siudek.media.llava;

import java.util.Base64;
import java.util.List;

import org.assertj.core.api.Assertions;
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
    var llavaModelName = Models.Llava.getModelName();
    var response2 = ollamaService.generate(new GenerateBody(llavaModelName, "Describe the photo.", false, new String[] { imageStr }));

    var embeddingsModelName = Models.Llama.getModelName();
    var instruction = "Represent this sentence for searching relevant passages: ";
    var query = "A man is eating a piece of bread";
    var embeddingResponse1 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, "A man is eating food."));


    Assertions.assertThat(embeddingResponse1.embedding()).hasSize(1024);

        // .call(new EmbeddingRequest(
        //     // List.of(instruction + query,
        //     List.of(query,
        //             mixedbread-ai/mxbai-embed-large-v1,
        //             "A man is eating pasta.",
        //             "The girl is carrying a baby.",
        //             "A man is riding a horse."),
        //     OllamaOptions.create().withModel(embeddingsModelName)));
    // var results = embeddingResponse1.getResults();

    // var embeddings_q = results.get(0).getOutput();
    // var embeddings_1 = results.get(1).getOutput();
    // var embeddings_2 = results.get(2).getOutput();
    // var embeddings_3 = results.get(3).getOutput();
    // var embeddings_4 = results.get(4).getOutput();

    // var similarity0 = Similarity.cosine(embeddings_q, embeddings_q);
    // var similarity1 = Similarity.cosine(embeddings_q, embeddings_1);
    // var similarity2 = Similarity.cosine(embeddings_q, embeddings_2);
    // var similarity3 = Similarity.cosine(embeddings_q, embeddings_3);
    // var similarity4 = Similarity.cosine(embeddings_q, embeddings_4);

    // Assertions.assertThat(similarity1).as("Actual vs Expected:\n[%s]\n <> \n[%s]", "Today is Monday", "???").isGreaterThan(0.90);

    // Assertions.assertThat(response2).isEqualTo("SPARTAA");
  }

  @Test
  @SneakyThrows
  // not yet available
  // https://github.com/spring-projects/spring-ai/issues/421
  void useSpringAi() {
    byte[] data = new ClassPathResource("llava/example1.jpg").getContentAsByteArray();
    var userMessage = new UserMessage("Explain what do you see on this picture?", List.of(new Media(MimeTypeUtils.IMAGE_JPEG, data)));
    var options = OllamaOptions.create().withModel("llava").withTemperature(0f);
    var prompt = new Prompt(userMessage, options);

    var chatClient = ChatClient.builder(chatModel).defaultOptions(options).build();
    
    var response = chatClient.prompt(prompt).call();
    var actual = response.content();

    var expected = "The image shows a vast, dry landscape with cracked earth and sparse vegetation. In the foreground, there is a large rock sitting on the ground, which appears to be in the center of the frame. The sky above is clear with some clouds, suggesting it might be either dawn or dusk given the soft lighting. There are no visible human-made structures or signs of recent activity, which gives the scene a remote and untouched appearance. The overall color palette is dominated by earth tones, with the rock providing a contrasting element.";

    var expectedAsEmbeddings = embeddingModel.call(new EmbeddingRequest(List.of(expected), options));
    var actualAsEmbeddings = embeddingModel.call(new EmbeddingRequest(List.of(actual), options));
    var similarity = Similarity.cosine(expectedAsEmbeddings.getResult().getOutput(), actualAsEmbeddings.getResult().getOutput());
    Assertions.assertThat(similarity).as("Actual vs Expected:\n[%s]\n <> \n[%s]", actual, expected).isGreaterThan(0.90);
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

    var embeddingsModelName = Models.Llama.getNameAndTag();
    var embed1 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, sent1)).embedding();
    var embed2 = ollamaService.embeddings(new EmbeddingsBody(embeddingsModelName, sent2)).embedding();
    
    var similarity1 = Similarity.cosine(embed1, embed2);

    Assertions.assertThat(similarity1).as("Actual vs Expected:\n[%s]\n <> \n[%s]", sent1, sent2).isGreaterThan(0.90);
  }

}
