package net.siudek.media.domain;

import lombok.RequiredArgsConstructor;

/** The processor is responsible to handle all file-events and processing each file   */
@RequiredArgsConstructor
public class FileEventProcessor implements Runnable {
  
  private final FileEvent fileEvent;
  private final State state;

  @Override
  public void run() {
  }
}
