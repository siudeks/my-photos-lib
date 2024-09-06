package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.siudek.media.utils.CloseableQueue;

/** The processor is responsible to handle all file-events and processing each file   */
@Component
@Slf4j
@RequiredArgsConstructor
public class FileEventProcessor implements Runnable, SmartLifecycle {
  
  private final FileEvents fileEvents;
  private final StateListeners stateListeners;
  private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  // Path - identitiy of collecting events
  // PriorityBlockingQueue - with prioretization to allow inform early about change / delete so that some waiting operations can be cancelled
  private final ConcurrentHashMap<Path, LinkedBlockingQueue<FileProcessingState>> actors = new ConcurrentHashMap<>();

  @Override
  public void run() {
    while (true) {
      try {
        var event = fileEvents.take();
        switch (event) {
          case FileEvent.Found it: {
            var actor = it.path();
            actors.compute(actor, (Path key, LinkedBlockingQueue<FileProcessingState> b) -> {
              if (b != null) {
                throw new IllegalStateException("Found already processed file.");
              }
              try {
              var messages = new LinkedBlockingQueue<FileProcessingState>();
              var initialMessage = new FileProcessingState.Discovered(key);
              messages.offer(initialMessage);
              var actor1 = new Actor(key, messages, stateListeners);
              executorService.submit(actor1);
              return messages;
              } catch (Exception ex) {
                throw ex;
              }
            });
            break;
          }
          case FileEvent.Changed it: {
            break;
          }
          case FileEvent.Deleted it: {
            break;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  @Override
  public void start() {
    executorService.submit(this);
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

  @RequiredArgsConstructor
  static class Actor implements Runnable {
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

}
