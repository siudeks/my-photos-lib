package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface StateValue {

  /** The file is found and will be processed soon. */
  record Discovered(Path path) implements StateValue { }

  /** Hash of the file is calculated in meta file */
  record Hashed(Path path) implements StateValue { } // meta file created

  /** Description of the file is already done using AI. */
  record Described(Path path) implements StateValue { } // meta file created

  /** Image data is already in vector database, ready for use. */
  record Indexed(Path path) implements StateValue { } // 
}
