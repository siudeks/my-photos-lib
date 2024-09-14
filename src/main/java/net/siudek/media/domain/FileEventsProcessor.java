package net.siudek.media.domain;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.StandardWatchEventKinds.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

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
  private final CompositeCloseable disposer = Closeables.of(() -> { vExecutor.shutdownNow(); vExecutor.close(); });

  public FileEventsProcessor(FileEventQueue fileEvents) {
    this.fileEvents = fileEvents;
  }

  /* Starting point as we have initial application args to start the flow. */
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
      case Try.Failure(Exception ex) -> null;
    };
    // if we can't start, lets just return noop operation to finish work as early as possible
    if (watcher == null) return () -> { };
    disposer.add(watcher);

    return () -> {
      register(root, dir -> onDir(dir, watcher), this::onFileFound);
      try {
        processEvents(watcher);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    };
  }

  private final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();

  void onDir(Path dir, WatchService watcher) {
    try {
      // The watch key remains valid until:
      // It is canceled explicitly by invoking its cancel() method, or
      // Canceled implicitly because the object is no longer accessible, or
      // By closing the watch service.
      var watchKey = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
      var key = keys.put(watchKey, dir); // do something with the key, watcher and potential duplication / resubscription
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } // TODO add unregistration
  };
  
  /**
   * Process all events for keys queued to the watcher
   * @throws InterruptedException 
   */
    void processEvents(WatchService watchService) throws InterruptedException {

    WatchKey key = null;
    while ((key = watchService.take()) != null) {
 
      Path dir = keys.get(key);
      if (dir == null) {
        System.err.println("WatchKey not recognized!!");
        continue;
      }
 
      for (WatchEvent<?> event : key.pollEvents()) {
        @SuppressWarnings("rawtypes")
        WatchEvent.Kind kind = event.kind();
 
        // Context for directory entry event is the file name of entry
        @SuppressWarnings("unchecked")
        Path name = ((WatchEvent<Path>)event).context();
        Path child = dir.resolve(name);
 
        // print out event
        System.out.format("%s: %s\n", event.kind().name(), child);
 
        // if directory is created, and watching recursively, then register it and its sub-directories
        if (kind == ENTRY_CREATE) {
            if (Files.isDirectory(child)) {
              register(child, dir1 -> onDir(dir1, watchService), this::onFileCreated);
            }
            if (Files.isRegularFile(child)) {
              onFileCreated(child);
            }
        }
      }
 
      // reset key and remove from set if directory no longer accessible
      boolean valid = key.reset();
      if (!valid) {
        keys.remove(key);
 
        // all directories are inaccessible
        if (keys.isEmpty()) {
          break;
        }
      }
    }
  }

  void onFileFound(Path path) {
    onFile(path, FileEvent.Found::new);
  }

  void onFileCreated(Path path) {
    onFile(path, FileEvent.Created::new);
  }

  void onFile(Path path, Function<Image, FileEvent> toEvent) {
    var asMedia = ImageUtils.asMediaFile(path);
    switch (asMedia) {
      case Image im: {
        try {
          var event = toEvent.apply(im);
          fileEvents.put(event);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          // no idea what to do else ... TODO
        }
      }
      default: break;
    }
  };

  // single-thread main logic loop.
  private void register(Path root, Consumer<Path> onDir, Consumer<Path> onFile) {

    switch(Try.run(() -> registerAll(root, onDir, onFile))) {
      case Try.Failure(var ex) -> { return; } // no sense to continue
      case Try.Success it -> { } // just continue
    };
    
    // while (true) {

      // wait for all threads 
      // var watchKey = switch(Try.of(watcher::take)) {
      //   case Try.Value<WatchKey>(var value) -> value;
      //   case Try.Failure(var ex) -> null;
      // };
      // if (watchKey == null) break;

      // more later: https://howtodoinjava.com/java8/java-8-watchservice-api-tutorial/

      
    //   for (var event: watchKey.pollEvents()) {
    //     event.
    //     WatchEvent<Path> watchEvent = castEvent(event);
    //     watchKey.reset();
    // }
    // }
    // };

  }

  /**
   * Register the given directory and all its sub-directories with the WatchService.
   */
  private void registerAll(Path start, Consumer<Path> onDir, Consumer<Path> onFile) throws IOException {
    // register directory, sub-directories and files
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
            onDir.accept(dir);
            Files.list(dir).filter(Files::isRegularFile).forEach(onFile);
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
