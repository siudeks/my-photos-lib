package net.siudek.media.domain;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import lombok.experimental.Delegate;

@Component
public class FileEvents implements Queue<FileEvent> {

  @Delegate
  private final ConcurrentLinkedQueue<FileEvent> events = new ConcurrentLinkedQueue<>();
}
