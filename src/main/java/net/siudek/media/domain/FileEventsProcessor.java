package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileEventsProcessor implements AutoCloseable, SmartLifecycle {

  @Delegate
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();

  private final MediaSearch images;
  private final FileEvents fileEvents;

  /* Starting point as we have initial applicatio nargs to start the flow. */
  @EventListener
  @Async
  public void on(AppEvent.RunArgs args) {
    var dirSearch = asRunnable(args.root());
    vExecutor.execute(dirSearch);
  }

  public Runnable asRunnable(Path root) {
    return () -> {
      var iterables = images.find(root.toFile());
      while(iterables.hasNext()) {
        var file = iterables.next();
        switch (file) {
          case Image it: {
            try {
              fileEvents.put(new FileEvent.Found(it));
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              break;
            }
          }
          default: continue;
        }
      }
    };
  }


  @Override
  public void start() {
    isRunning = true;
  }

  @Override
  public void stop() {
    isRunning = false;
  }

  private boolean isRunning = false;
  @Override
  public boolean isRunning() {
    return isRunning;
  }
  
}
