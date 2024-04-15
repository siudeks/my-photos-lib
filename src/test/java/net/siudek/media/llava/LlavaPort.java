package net.siudek.media.llava;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@HttpExchange(url = "/", accept = "application/json", contentType = "application/json")
public interface LlavaPort {

  @PostExchange("api/generate")
  public String generate(@RequestBody GenerateBody body);
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
