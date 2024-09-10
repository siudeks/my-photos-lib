package net.siudek.media.domain;

import static com.google.common.io.Files.asByteSource;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;

import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Blocking actor, using provided queueas source of messages. */
@Slf4j
@RequiredArgsConstructor
class FileActor implements Runnable {
  private final Image self;
  private final BlockingQueue<Command> messages;
  private final StateListeners stateListeners;
  private final ImageDescService imageDescService;

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

        {
          createChecksumFile(self.path());
          var evt2 = new FileProcessingState.Hashed(self.path());
          stateListeners.on(evt2);
        }

        {
          requestImageDesc(self);
        }

        break;
      }

      case Command.ApplyDescription(var description): {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.info(description);
        log.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        var evt1 = new FileProcessingState.Discovered(self.path());
        stateListeners.on(evt1);
      }
    }
  }

  private void handleResponse(String imageDesc) {
    var msg = new Command.ApplyDescription(imageDesc);
    messages.offer(msg); // TODO handle failure
  }

  private void requestImageDesc(Image mediaFile) {
    var jpgBase64 = ImageUtils.asJpegBase64(mediaFile);
    imageDescService.request(jpgBase64, this::handleResponse);
  }

  void createChecksumFile(Path media) {
    var mediaFile = media.toFile();
    var mediaFileName = media.toFile().getName();
    var checksumFile = media.resolveSibling(mediaFileName + ".sha256");

    var hash = switch (Try.of(() -> asByteSource(mediaFile).hash(Hashing.sha256()))) {
      case Try.Value<HashCode>(HashCode value) -> {
        yield value.toString();
      }
      case Try.Error(var ex) -> {
        log.error("sha256 calc error", ex);
        yield null;
      }
    };

    if (hash == null)
      return;

    // read already created hash file, if exists
    var skipChecksum = Files.exists(checksumFile) && switch (Try.of(() -> Files.readAllBytes(checksumFile))) {
    case Try.Value<byte[]>(var value) -> Objects.equal(new String(value), hash);
    case Try.Error(var ex) -> false;
    };

    if (!skipChecksum) {
      switch (Try.of(() -> Files.writeString(checksumFile, hash, StandardOpenOption.CREATE))) {
      case Try.Value<Path>(var value): {
        // success
        break;
      }
      case Try.Error(var ex): {
        log.error("sha256 store error", ex);
      }
      }
    }

  }

  sealed interface Command {

    record Process() implements Command {
    }

    record ApplyDescription(String description) implements Command {

    }
  }
}
