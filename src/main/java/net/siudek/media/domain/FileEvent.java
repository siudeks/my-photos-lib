package net.siudek.media.domain;

import java.nio.file.Path;

/** List of all events related to single files, which can be generated from observed folder. */
public sealed interface FileEvent {
  
  /** Media file found - already existing or just created. */
  record FoundDir(Path path) implements FileEvent {
  }

  /** New file has been just created, or existing file has been discovered. */
  record FoundFile(Image value) implements FileEvent { }

}
