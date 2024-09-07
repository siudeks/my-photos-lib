package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FileActor implements Runnable {
  private final Path self;
  private final BlockingQueue<FileProcessingState> messages;
  private final StateListeners stateListeners;

  @Override
  public void run() {
    while(true) {
      try {
        var message = messages.take();
        switch (message) {
          case FileProcessingState.Discovered it: {
            stateListeners.on(it);
            break;
          }
          case FileProcessingState.Hashed it: {
            break;
          }
          case FileProcessingState.Described it: {
            break;
          }
          case FileProcessingState.Indexed it: {
            break;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
