package net.siudek.media.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

/** TO simplify dev life we offer only subset of queue methods used in publish / subscribe approach. */
public interface CloseableQueue<@NonNull T> {

  /**
   * Inserts the specified element into this queue
   *
   * @param e the element to add
   * @throws InterruptedException if interrupted while waiting
   */
  void put(@NonNull T e) throws InterruptedException;

  T take() throws InterruptedException;
}
