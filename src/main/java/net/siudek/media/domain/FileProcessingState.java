package net.siudek.media.domain;

import java.nio.file.Path;

public sealed interface FileProcessingState {

  /** The file is found and will be processed soon. */
  record Discovered(Path path) implements FileProcessingState { }

  /** Hash of the file is calculated in meta file */
  record Hashed(Path path) implements FileProcessingState { } // meta file created

  /** Description of the file is already done using AI. */
  record Described(Path path) implements FileProcessingState { } // meta file created

  /** Image data is already in vector database, ready for use. */
  record Indexed(Path path) implements FileProcessingState { } 
}
