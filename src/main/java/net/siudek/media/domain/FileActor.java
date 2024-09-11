package net.siudek.media.domain;

import static com.google.common.io.Files.asByteSource;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

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
  private final VectorStore vectorStore; // move saving to database to a separate service, not in actor

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

        var evt1 = new FileProcessingState.Discovered(self.path());
        stateListeners.on(evt1);

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

        if (!createDescriptionFile(self.path(), description)) return;

        var evt1 = new FileProcessingState.Described(self.path());
        stateListeners.on(evt1);

        var id = self.path().toAbsolutePath().toString(); // TODO reuse hash as key. Any other ideas?

        var doc = new Document(id, description, Map.of());
        vectorStore.add(List.of(doc));
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

  Path siblingFile(Path mainFile, String extension) {
    var mediaFileName = mainFile.toFile().getName();
    var result = mainFile.resolveSibling(mediaFileName + extension);
    return result;
  }

  void createChecksumFile(Path media) {
    var checksumFile = siblingFile(media, ".sha256");

    var hash = switch (Try.of(() -> asByteSource(media.toFile()).hash(Hashing.sha256()))) {
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

  /**
   * Saves file with text description of the image. Skips description file if exist.
   */
  private boolean createDescriptionFile(Path media, String description) {
    var descFile = siblingFile(media, ".v1.desc");
    if (Files.exists(descFile)) {
      return false;
    }
    switch (Try.of(() -> Files.writeString(descFile, description, StandardOpenOption.CREATE))) {
      case Try.Value<Path>(var value): {
        // success
        return true;
      }
      case Try.Error(var ex): {
        log.error("sha256 store error", ex);
        return false;
      }
    }
  }
 
  /** List of commands supported by the actor. */
  sealed interface Command {

    record Process() implements Command {
    }

    record ApplyDescription(String description) implements Command {
    }

  }



}
