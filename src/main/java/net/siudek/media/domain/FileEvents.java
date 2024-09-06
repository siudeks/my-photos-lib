package net.siudek.media.domain;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.stereotype.Component;

import lombok.experimental.Delegate;
import net.siudek.media.utils.CloseableQueue;
import net.siudek.media.utils.CloseableQueueImpl;

@Component
@ThreadSafe
// shared data component to be reused between components
public class FileEvents implements CloseableQueue<FileEvent> {

  @Delegate
  private final CloseableQueue<FileEvent> events = new CloseableQueueImpl<>();

}
