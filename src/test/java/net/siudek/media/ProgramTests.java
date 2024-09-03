package net.siudek.media;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;

import lombok.SneakyThrows;
import net.siudek.media.domain.FileView;
import net.siudek.media.domain.Image;
import net.siudek.media.domain.StateListener;
import net.siudek.media.domain.StateValue;

/** All images are located in root directory named 'data' */
@SpringBootTest(
  args={"--dir=data"},
  classes = Program.class,
  properties = {
		// interactive mode stops the test and waits for input
		// more: https://docs.spring.io/spring-shell/reference/execution.html
		"spring.shell.interactive.enabled=false"}
)
@DirtiesContext
@Import(value = ProgramTests.MyListener.class)
@Timeout(threadMode = ThreadMode.SEPARATE_THREAD, unit = TimeUnit.SECONDS, value = 10)
public class ProgramTests {

  @Autowired
  FileView fileView;

  @Autowired
  MyListener listener;

  static File dogHeic;

  @BeforeAll
  static void initClass() {
    var dataPath = Path.of("./data");
    Assertions.assertThat(dataPath.toFile()).exists();

    dogHeic = dataPath.resolve("dog.heic").toFile();
    Assertions.assertThat(dogHeic).exists();
  }

  @Test
  @SneakyThrows
  void shouldFindNoFile() {
    // wait when all files are discovered
    // we know there are 3 images
    var awaiter = listener.registerWaiter(it -> {
      return it.get() == 3;
    });
    awaiter.await();

    var files = fileView.find("Image with a bird");

    Assertions.assertThat(files).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldFindDogFile() {

    // wait when all files are discovered
    // we know there are 3 images
    var awaiter = listener.registerWaiter(it -> it.get() == 3);
    awaiter.await();

    var files = fileView.find("Image with a dog");

    Assertions.assertThat(files).containsExactly(new Image.HEIC(dogHeic.toPath()));
  }

  record Projection(int discovered) {
  }

  @Component
  public static class MyListener implements StateListener {

    private AtomicInteger discovered = new AtomicInteger();
    private Map<Function<AtomicInteger, Boolean>, CountDownLatch> listeners = new HashMap<>();

    @Override
    public void on(StateValue event) {
      switch (event) {
        case StateValue.Discovered it:
          discovered.incrementAndGet();
          break;
        case StateValue.Hashed it:
          break;
        case StateValue.Described it:
          break;
        case StateValue.Indexed it:
          break;
      }
      // notify listeners if any
      for (var listener: listeners.entrySet()) {
        var checker = listener.getKey();
        var awaiter = listener.getValue();
        var accepted = checker.apply(discovered);
        if (accepted) {
          awaiter.countDown();
        }

      }
    }

    CountDownLatch registerWaiter(Function<AtomicInteger, Boolean> stateValidator) {
      var awaiter = new CountDownLatch(1);
      listeners.put(stateValidator, awaiter);
      return awaiter;
    }

  }
}
