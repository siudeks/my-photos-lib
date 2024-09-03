package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileEventsProcessor implements AutoCloseable {
  private final ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();

  private final MediaSearch images;
  private final FileEvents fileEvents;
  private final List<StateListener> stateListeners;

  @EventListener
  @Async
  public void on(Events.RunArgs args) {
    var dirSearch = asRunnable(args.root());
    vExecutor.execute(dirSearch);
  }

  public Runnable asRunnable(Path root) {
    return () -> {
      var iterables = images.find(root.toFile());
      while(iterables.hasNext()) {
        var file = iterables.next();
        var filePath = file.path();
        fileEvents.add(new FileEvent.Found(filePath));
        for (var listener: stateListeners) {
          listener.on(new StateValue.Discovered(filePath));
        }
      }
    };
  }


  @Override
  public void close() throws Exception {
    vExecutor.close();
  }
  
}
