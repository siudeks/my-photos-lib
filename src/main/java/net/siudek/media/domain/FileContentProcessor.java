package net.siudek.media.domain;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

// @Service
@RequiredArgsConstructor
public class FileContentProcessor implements SmartLifecycle, Runnable, AutoCloseable {

  private final FileEvents fileEvents;

  @Delegate(types = AutoCloseable.class)
  private ExecutorService vExecutor = Executors.newVirtualThreadPerTaskExecutor();

  @Override
  public void start() {
    vExecutor.submit(this);
    isRunning = true;
  }

  @Override
  public void stop() {
    isRunning = false;
  }

  private boolean isRunning = false;
  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public void run() {
    while (true) {
      try {
        var event = fileEvents.take();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }


}
