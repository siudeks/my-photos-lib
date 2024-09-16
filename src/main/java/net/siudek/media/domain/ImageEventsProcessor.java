package net.siudek.media.domain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

/**
 * Reads initial list of files using {@link MediaSearch} and watches all changes in discovered folders / files.
 * All events has been stored in {@link FileEvents}
 * More: https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */
@Service
public class ImageEventsProcessor implements AutoCloseable, SmartLifecycle {

  private final FileEventQueue fileEvents;
  private final ImageEventQueue imageEvents;
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();
  private final CompositeCloseable disposer = Closeables.of(() -> { vExecutor.shutdownNow(); vExecutor.close(); });

  public ImageEventsProcessor(FileEventQueue fileEvents, ImageEventQueue imageEvents) {
    this.fileEvents = fileEvents;
    this.imageEvents = imageEvents;
  }

  private void main() {
    while (true) {
      try {
        var fileEvent = fileEvents.take();

        var asPath = switch(fileEvent) {
          case FileEvent.FoundFile it -> it.value();
        };

        var asMedia = ImageUtils.asMediaFile(asPath);
        switch (asMedia) {
          case Image im: {
            var je = new Events.ImageFound(asPath.toString());
            je.begin();
            je.end();
            je.commit();
            try {
              var event = new ImageEvent.Found(im);
              imageEvents.put(event);
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              // no idea what to do else ... TODO
            }
          }
          default: break;
        }
    
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  @Override
  public void start() {
    if (isRunning) return;
    isRunning = true;

    vExecutor.execute(this::main);

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
