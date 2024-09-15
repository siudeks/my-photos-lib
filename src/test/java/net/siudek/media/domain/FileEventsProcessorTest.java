package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationEventPublisher;

import com.google.common.io.Files;

import net.siudek.media.domain.AppEvent.RunArgs;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class FileEventsProcessorTest {

  @Autowired
  FileEventsProcessor sut;
  @Autowired ApplicationEventPublisher publisher;
  @Autowired ImageEventQueue events;

  @Test
  @Timeout(unit = TimeUnit.SECONDS, value = 3)
  void shouldLocateFile(@TempDir Path dir) throws Exception {

    
    var jpg1 = dir.resolve("1.jpg");
    Files.touch(jpg1.toFile());

    publisher.publishEvent(new RunArgs(dir));

    var item1 = events.take();
    Assertions
      .assertThat(List.of(item1))
      .containsExactlyInAnyOrder(
        new ImageEvent.Found(new Image.JPG(jpg1)));

    var jpg2 = dir.resolve("2.jpg");
    Files.touch(jpg2.toFile());
    var item2 = events.take();

    Assertions
      .assertThat(List.of(item1, item2))
      .containsExactlyInAnyOrder(
        new ImageEvent.Found(new Image.JPG(jpg1)),
        new ImageEvent.Found(new Image.JPG(jpg2)));
  }

}
