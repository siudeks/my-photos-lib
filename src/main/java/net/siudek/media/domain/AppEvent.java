package net.siudek.media.domain;
import java.nio.file.Path;

/** Domain-level events. */
sealed public interface AppEvent {

  public record RunArgs(Path root) implements AppEvent {  
  }

}
