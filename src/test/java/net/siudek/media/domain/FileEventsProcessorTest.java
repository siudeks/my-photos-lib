package net.siudek.media.domain;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.google.common.io.Files;

import net.siudek.media.domain.AppEvent.RunArgs;
import net.siudek.media.domain.DomainConfigurer.FileEventQueueImpl;

@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = JfrEventLifecycle.class)
public class FileEventsProcessorTest {
  
  @Test
  @Timeout(threadMode = ThreadMode.SEPARATE_THREAD, unit = TimeUnit.SECONDS, value = 3000)
  void shouldLocateFile(@TempDir Path dir) throws Exception {

    var events = new FileEventQueueImpl();

    var jpg1 = dir.resolve("1.jpg");
    Files.touch(jpg1.toFile());

    try (var sut = new FileEventsProcessor(events)) {
      sut.on(new RunArgs(dir));

      var item1 = events.take();
      Assertions
        .assertThat(List.of(item1))
        .containsExactlyInAnyOrder(
          new FileEvent.Found(new Image.JPG(jpg1)));

      var jpg2 = dir.resolve("2.jpg");
      Files.touch(jpg2.toFile());
      var item2 = events.take();

      Assertions
        .assertThat(List.of(item1, item2))
        .containsExactlyInAnyOrder(
          new FileEvent.Found(new Image.JPG(jpg1)),
          new FileEvent.Created(new Image.JPG(jpg2)));
    }
  }

}
