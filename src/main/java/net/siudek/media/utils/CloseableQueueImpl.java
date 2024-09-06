package net.siudek.media.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

public class CloseableQueueImpl<T> implements CloseableQueue<T> {
  private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

  public void put(T element) throws InterruptedException {
      queue.put(element);
  }

  public T take() throws InterruptedException {
    return queue.take();
  }

}
