package net.siudek.media.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.Files;

import net.siudek.media.domain.AppEvent.RunArgs;
import net.siudek.media.domain.DomainConfigurer.FileEventQueueImpl;

public class FileEventsProcessorTest {
  
  @Test
  @Timeout(unit = TimeUnit.SECONDS, value = 3)
  void shouldLocateFile(@TempDir Path dir) throws InterruptedException, IOException {

    var events = new FileEventQueueImpl();

    var jpg1 = dir.resolve("1.jpg");
    Files.touch(jpg1.toFile());

    var sut = new FileEventsProcessor(events);
    sut.on(new RunArgs(dir));

    Thread.sleep(1000);
    var jpg2 = dir.resolve("2.jpg");
    Files.touch(jpg2.toFile());

    var item1 = events.take();
    Assertions
      .assertThat(List.of(item1))
      .containsExactlyInAnyOrder(
        new FileEvent.Found(new Image.JPG(jpg1)));


    // var item1 = events.take();
    // var item2 = events.take();
    // Assertions
    //   .assertThat(List.of(item1, item2))
    //   .containsExactlyInAnyOrder(
    //     new FileEvent.Found(new Image.JPG(jpg1)),
    //     new FileEvent.Created(jpg2));
  }

}
