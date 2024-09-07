package net.siudek.media.domain;
import java.nio.file.Path;

/** Domain-level events. */
public interface Events {

  public record RunArgs(Path root) {  
  }

}
