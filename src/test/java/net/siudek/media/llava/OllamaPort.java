package net.siudek.media.llava;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Equivalent of https://github.com/ollama/ollama/blob/main/docs/api.md
 */
@HttpExchange(url = "/", accept = "application/json", contentType = "application/json")
public interface OllamaPort {

  @PostExchange("api/generate")
  RenerateResult generate(@RequestBody GenerateBody body);
  
  /** https://github.com/ollama/ollama/blob/main/docs/api.md#list-local-models */
  @GetExchange("api/tags")
  ListResult list();

  @PostExchange("api/embeddings")
  EmbeddingsResult embeddings(@RequestBody EmbeddingsBody body);

  record EmbeddingsBody(String model, // name of model to generate embeddings from
    String prompt // text to generate embeddings for
    // Advanced parameters:
    // options: additional model parameters listed in the documentation for the Modelfile such as temperature
    // int keep_alive // controls how long the model will stay loaded into memory following the request (default: 5m))
  ) { }

  record EmbeddingsResult(double[] embedding) { }

  record RenerateResult(String model, LocalDateTime createdAt, String response, boolean done, int[] context,
      long totalDuration, long loadDuration, int promptEvalCount, long promptEvalDuration, int intevalCount,
      long evalDuration) {
  }

  record ListResult(List<Model> models) {
  }

  record Model(String name, OffsetDateTime modifiedAt, long size, String digest) {
  }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class GenerateBody {
  String model;
  String prompt;
  boolean stream;
  String[] images;
}
