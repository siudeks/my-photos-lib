package net.siudek.media.domain;

import java.io.File;
import java.nio.file.Path;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.siudek.media.host.MediaApplication;

/** All images are located in root directory named 'data' */
@SpringBootTest(
  args={"dir=data"},
  classes = MediaApplication.class
)
public class DomainModuleTests {

  @Autowired
  FileView fileView;

  static File dogHeic;

  @BeforeAll
  static void initClass() {
    var dataPath = Path.of("./data");
    Assertions.assertThat(dataPath.toFile()).exists();

    dogHeic = dataPath.resolve("dog.heic").toFile();
    Assertions.assertThat(dogHeic).exists();
  }

  @Test
  void shouldFindNoFile() {
    var files = fileView.find("Image with a bird");

    Assertions.assertThat(files).isEmpty();
  }

  @Test
  void shouldFindDogFile() {
    var files = fileView.find("Image with a dog");

    Assertions.assertThat(files).containsExactly(new Image.HEIC(dogHeic.toPath()));
  }

}
