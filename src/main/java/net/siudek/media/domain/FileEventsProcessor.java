package net.siudek.media.domain;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Reads initial list of files using {@link MediaSearch} and watches all changes in discovered folders / files.
 * All events has been stored in {@link FileEvents}
 * More: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */
@Service
public class FileEventsProcessor implements AutoCloseable, SmartLifecycle {

  private final FileEventQueue fileEvents;
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();
  private final CompositeCloseable disposer = Closeables.of(vExecutor);

  public FileEventsProcessor(FileEventQueue fileEvents) {
    this.fileEvents = fileEvents;
  }

  private Map<Path, WatchService> watchers = new ConcurrentHashMap<>();

  /* Starting point as we have initial applicatio nargs to start the flow. */
  @EventListener
  @Async
  public void on(AppEvent.RunArgs args) {
    var dirSearch = asRunnable(args.root());
    vExecutor.execute(dirSearch);
  }

  public Runnable asRunnable(Path root) {
    var fs = root.getFileSystem();
    var watcher = switch(Try.of(fs::newWatchService)) {
      case Try.Value<WatchService>(var value) -> value;
      case Try.Error(Exception ex) -> null;
    };
    // if we can't start, lets just return noop
    if (watcher == null) return () -> { };
    disposer.add(watcher);

    Consumer<Path> onExistingFile = path -> {
      var asMedia = ImageUtils.asMediaFile(path);
      switch (asMedia) {
        case Image im: {
          try {
            fileEvents.put(new FileEvent.Found(im));
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // no idea what to do else ... TODO
          }
        }
        default: break;
      }
    };

    return () -> runnable(root, watcher, onExistingFile);
  }

  private void runnable(Path root, WatchService watcher, Consumer<Path> onExistingFile) {

    switch(Try.of(() -> registerAll(root, watcher, onExistingFile))) {
      case Try.Error(var ex) -> { return; }
      case Try.Success it -> { break; }
    };
    
    while (true) {

      var watchKey = switch(Try.of(watcher::take)) {
        case Try.Value<WatchKey>(var value) -> value;
        case Try.Error(var ex) -> null;
      };
      if (watchKey == null) break;

      // more later: https://howtodoinjava.com/java8/java-8-watchservice-api-tutorial/

      
    //   for (var event: watchKey.pollEvents()) {
    //     event.
    //     WatchEvent<Path> watchEvent = castEvent(event);
    //     watchKey.reset();
    // }
    }
  };

  /**
   * Register the given directory and all its sub-directories with the WatchService.
   */
  private void registerAll(Path start, WatchService watcher, Consumer<Path> onExistingFile) throws IOException {
    // register directory and sub-directories
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            Files.list(dir).filter(Files::isRegularFile).forEach(onExistingFile);
            return FileVisitResult.CONTINUE;
        }

    });

  }

  @Override
  public void start() {
    isRunning = true;
  }

  @Override
  public void stop() {
    isRunning = false;
  }

  private volatile boolean isRunning = false;

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public void close() throws Exception {
    disposer.close();
  }
  
}
