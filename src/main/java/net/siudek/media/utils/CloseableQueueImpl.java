package net.siudek.media.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.checkerframework.checker.nullness.qual.NonNull;

public class CloseableQueueImpl<T> implements CloseableQueue<T> {
  private final BlockingQueue<@NonNull T> queue = new LinkedBlockingQueue<>();

  public void put(@NonNull T element) throws InterruptedException {
      queue.put(element);
  }

  public T take() throws InterruptedException {
    return queue.take();
  }

}
