package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;

import lombok.RequiredArgsConstructor;

/** Blocking actor, using provided queueas source of messages. */
@RequiredArgsConstructor
class FileActor implements Runnable {
  private final Path self;
  private final BlockingQueue<Command> messages;
  private final StateListeners stateListeners;

  @Override
  public void run() {
    while(true) {
      try {
        var message = messages.take();
        switch (message) {
          case Command.Process it: {
            var evt = new FileProcessingState.Discovered(self);
            stateListeners.on(evt);
            break;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  sealed interface Command {
    record Process() implements Command { }
  }
}
