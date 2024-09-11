package net.siudek.media.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

/**
 * The processor is responsible to handle all file-events and processing each
 * file
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FileActorFactory implements Runnable, SmartLifecycle, AutoCloseable {

  private final FileEvents fileEvents;
  private final StateListeners stateListeners;
  private final ImageDescService imageDescService;
  private final VectorStore vectorStore;
  @Delegate
  private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  // Path - identitiy of collecting events
  // PriorityBlockingQueue - with prioretization to allow inform early about
  // change / delete so that some waiting operations can be cancelled
  private final ConcurrentHashMap<Image, LinkedBlockingQueue<FileActor.Command>> actors = new ConcurrentHashMap<>();

  @Override
  public void run() {
    while (true) {
      try {
        var event = fileEvents.take();
        switch (event) {
        case FileEvent.Found it: {
          var actorId = it.path();
          actors.compute(actorId, (Image key, LinkedBlockingQueue<FileActor.Command> b) -> {
            if (b != null) {
              throw new IllegalStateException("Found already processed file.");
            }
            try {
              // interesting - it hangs on when priority queue is used
              var messages = new LinkedBlockingQueue<FileActor.Command>();
              var initialMessage = new FileActor.Command.Process();
              messages.offer(initialMessage);
              var actor1 = new FileActor(key, messages, stateListeners, imageDescService, vectorStore);
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

}
