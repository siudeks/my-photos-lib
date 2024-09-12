package net.siudek.media.domain;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.stereotype.Component;

import net.siudek.media.utils.CloseableQueue;
import net.siudek.media.utils.CloseableQueueImpl;

@Component
@ThreadSafe
// shared data component to be reused between components
public class FileEvents implements CloseableQueue<FileEvent> {

  private final CloseableQueue<FileEvent> events = new CloseableQueueImpl<>();

  @Override
  public void put(FileEvent e) throws InterruptedException {
    events.put(e);
  }

  @Override
  public FileEvent take() throws InterruptedException {
    return events.take();
  }

}
