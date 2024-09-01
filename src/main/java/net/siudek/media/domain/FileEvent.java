package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface FileEvent {
  
  /** Media file found. */
  record Found(Path path) implements FileEvent {
  }

  /** Some changed on the file to process. */
  record Changed(Path path) implements FileEvent {
  }

  /** File has been deleted. */
  record Deleted(Path path) implements FileEvent {
  }

}
