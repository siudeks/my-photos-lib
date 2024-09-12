package net.siudek.media;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

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

import net.siudek.media.domain.FileProcessingState;
import net.siudek.media.domain.FileView;
import net.siudek.media.domain.Image;
import net.siudek.media.domain.StateListener;
import net.siudek.media.utils.SafeCloseable;

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
@Timeout(threadMode = ThreadMode.SEPARATE_THREAD, unit = TimeUnit.SECONDS, value = 300)
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
  void shouldFindNoFile() throws InterruptedException {
    // wait when all files are discovered
    // we know there are 3 images
    var awaiter = listener.registerWaiter(it -> {
      return it.indexed() == 3;
    });
    awaiter.await();

    var files = fileView.find("Image with a bird");

    Assertions.assertThat(files).isEmpty();
  }

  @Test
  void shouldFindDogFile() throws InterruptedException {

    // wait when all files are discovered
    // we know there are 3 images
    var awaiter = listener.registerWaiter(it -> it.indexed() == 3);
    awaiter.await();

    var files = fileView.find("Image with a dog");

    Assertions.assertThat(files).containsExactly(new Image.HEIC(dogHeic.toPath()));
  }

  record Projection(int discovered) {
  }

  @Component
  public static class MyListener implements StateListener {
    record Context(State lastKnown, CountDownLatch locker) { }

    private State current = new State(0, 0);

    private Semaphore notifyListenersLock = new Semaphore(1);
    private HashMap<Function<State, Boolean>, Context> listeners = new HashMap<>();

    

    @Override
    public void on(FileProcessingState event) {
      withLock(() -> {
        changeState(event);
        notifyListeners();
      });
    }

    CountDownLatch registerWaiter(Function<State, Boolean> stateValidator) {
      return withLock(() -> {
        var result = registerWaiterInter(stateValidator);
        notifyListeners();
        return result;
      }, () -> new CountDownLatch(1000));
    }

    private CountDownLatch registerWaiterInter(Function<State, Boolean> stateValidator) {
      var awaiter = new CountDownLatch(1);
      var context = new Context(null, awaiter);
      listeners.put(stateValidator, context);
      return awaiter;
    }

    private void changeState(FileProcessingState event) {
      switch (event) {
        case FileProcessingState.Discovered it:
          current = new State(current.discovered() + 1, current.indexed());
          break;
        case FileProcessingState.Hashed it:
          break;
        case FileProcessingState.Described it:
          break;
        case FileProcessingState.Indexed it:
          current = new State(current.discovered(), current.indexed()+1);
          break;
      }
    }

    // notifies all new listeners about current state
    private void notifyListeners() {
      for (var listener: listeners.entrySet()) {
        var checker = listener.getKey();
        var context = listener.getValue();

        var lastKnownState = context.lastKnown();
        if (Objects.equals(current, lastKnownState)) {
          // already informed about state
          continue;
        }

        var accepted = checker.apply(current);
        if (accepted) {
          var awaiter = context.locker;
          awaiter.countDown();
        }
      }
    }

    void withLock(Runnable task) {
      var asSupplier = (Supplier<?>) () -> {
        task.run();
        return null;
      };
      withLock(asSupplier, () -> null);
    }

    <T> T withLock(Supplier<T> task, Supplier<T> defaultWhenInterrupted) {
      try {
        notifyListenersLock.acquire();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return defaultWhenInterrupted.get();
      }

      
      try (var closer = (SafeCloseable) () -> notifyListenersLock.release()) { }
      return task.get();
    }
  }

  @Test
  public void readEvents() {
    try (var stream = new jdk.jfr.consumer.RecordingStream()) {
      // do some magic here for demo purpose, with extending Assertions
    }
  }
}

