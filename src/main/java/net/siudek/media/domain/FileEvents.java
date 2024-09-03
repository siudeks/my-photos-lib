package net.siudek.media.domain;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import lombok.experimental.Delegate;

@Component
// shared data component to be reused between components
public class FileEvents implements Queue<FileEvent> {

  @Delegate
  private final ConcurrentLinkedQueue<FileEvent> events = new ConcurrentLinkedQueue<>();

}
