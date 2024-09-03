package net.siudek.media.domain;
import java.nio.file.Path;

public interface Events {

  public record RunArgs(Path root) {  
  }

}
