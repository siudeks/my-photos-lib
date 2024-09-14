package net.siudek.media.domain;

import java.nio.file.Path;

/** List of all events related to single files, which can be generated from observed folder. */
public sealed interface FileEvent {
  
  /** Media file found - already existing or just created. */
  record Found(Image path) implements FileEvent {
  }

  /** New file has been just created */
  record Created(Image value) implements FileEvent { }

  /** Some changed on the file to process. */
  record Changed(Path path) implements FileEvent {
  }

  /** File has been deleted. */
  record Deleted(Path path) implements FileEvent {
  }

}
