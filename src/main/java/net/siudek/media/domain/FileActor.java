package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import org.springframework.util.Assert;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Blocking actor, using provided queueas source of messages. */
@Slf4j
@RequiredArgsConstructor
class FileActor implements Runnable {
  private final Path self;
  private final BlockingQueue<Command> messages;
  private final StateListeners stateListeners;

  @Override
  public void run() {
    while (true) {
      try {
        var message = messages.take();
        handle(message);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  private void handle(Command cmd) {
    switch (cmd) {
      case Command.Process it: {
        var evt = new FileProcessingState.Discovered(self);
        stateListeners.on(evt);
        createChecksumFile(self);
        break;
      }
    }
  }

  void createChecksumFile(Path media) {
    var mediaFile = media.toFile();
    var mediaFileName = media.toFile().getName();
    var checksumFile = media.resolveSibling(mediaFileName + ".sha256");

    var hash = switch (Try.of(() -> Files.asByteSource(mediaFile).hash(Hashing.sha256()))) {
      case Try.Value<HashCode>(HashCode value) -> {
        yield value.toString();
      }
      case Try.Error it -> {
        log.error("sha256 calc error", it.ex());
        yield null;
      }
    };

    if (hash == null) return;

    switch (Try.of(() -> java.nio.file.Files.writeString(checksumFile, hash))) {
      case Try.Value<Path>(Path value): {
        // success
        break;
      }
      case Try.Error it: {
        log.error("sha256 store error", it.ex());
      }
    }

  }

  sealed interface Command {
    record Process() implements Command {
    }
  }
}
