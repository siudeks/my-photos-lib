package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/** Shared, in-memory state of the processing */
@Component
class State {

  private final ConcurrentHashMap<Path, FileProcessingState> watchedFiles = new ConcurrentHashMap<>();

}
