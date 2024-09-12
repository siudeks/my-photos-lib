package net.siudek.media.domain;
import java.nio.file.Path;

/** Domain-level events. */
sealed public interface AppEvent {

  /** Input application arguments, converted to an event. */
  public record RunArgs(Path root) implements AppEvent {  
  }

}
