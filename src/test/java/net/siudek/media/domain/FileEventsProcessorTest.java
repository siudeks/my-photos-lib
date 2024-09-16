package net.siudek.media.domain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.queue.SynchronizedQueue;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import com.google.common.io.Files;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;
import net.siudek.media.domain.AppEvent.RunArgs;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class FileEventsProcessorTest {

  final static Logger log = LoggerFactory.getLogger(FileEventsProcessorTest.class);
  @Autowired
  FileEventsProcessor sut;

  @Autowired
  ApplicationEventPublisher publisher;

  BlockingQueue<Path> imageFound = new LinkedBlockingQueue<>();

  @Test
  @Timeout(unit = TimeUnit.SECONDS, value = 3000)
  void shouldLocateFile(@TempDir Path dir) throws Exception {

    try (var rs = new RecordingStream()) {
      var eventName = Events.ImageFound.CLASS_NAME;
      rs.enable(eventName).withPeriod(Duration.ofMillis(100));
      rs.onEvent(eventName, ev -> {
        log.warn("Thread pinning detected! {}", ev);
        var asString = ev.getString(Events.ImageFound.FIELD_NAME_FILE_PATH);
        var foundFile = Paths.get(asString);
        imageFound.add(foundFile);
      });

      rs.startAsync();

      var jpg1 = dir.resolve("1.jpg");
      Files.touch(jpg1.toFile());

      publisher.publishEvent(new RunArgs(dir));

      var item1 = imageFound.take();
      Assertions.assertThat(List.of(item1)).containsExactlyInAnyOrder(item1);

      var jpg2 = dir.resolve("2.jpg");
      Files.touch(jpg2.toFile());
      var item2 = imageFound.take();
      Assertions
        .assertThat(List.of(item1, item2)).containsExactlyInAnyOrder(item1, item2);
    } 

  }

}
