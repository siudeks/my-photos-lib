package net.siudek.media.domain;

import jdk.jfr.consumer.RecordingStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

// source: https://mikemybytes.com/2024/04/17/continuous-monitoring-of-pinned-threads-with-spring-boot-and-jfr/
@Component
public class JfrEventLifecycle implements SmartLifecycle {

  private static final Logger log = LoggerFactory.getLogger(JfrEventLifecycle.class);

  private final AtomicBoolean running = new AtomicBoolean(false);
  private RecordingStream rs;

  @Override
  public void start() {
      if (!isRunning()) {
          running.set(true);

          rs = new RecordingStream();
          rs.enable("jdk.VirtualThreadPinned").withStackTrace();
          rs.onEvent(
                  "jdk.VirtualThreadPinned",
                  ev -> log.warn("Thread pinning detected! {}", ev)
          );

          // prevents long-running app from memory leaks
          rs.setMaxAge(Duration.ofSeconds(10));

          rs.startAsync();
      }
  }

  @Override
  public void stop() {
      if (isRunning()) {
          rs.close();
          running.set(false);
      }
  }

  @Override
  public boolean isRunning() {
      return running.get();
  }
}
