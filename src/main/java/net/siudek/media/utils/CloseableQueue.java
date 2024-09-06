package net.siudek.media.utils;

public interface CloseableQueue<T> {
  void put(T element) throws InterruptedException;
  T take() throws InterruptedException;
}
