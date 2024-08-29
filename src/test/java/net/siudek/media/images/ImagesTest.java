package net.siudek.media.images;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.collections4.IteratorUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tngtech.archunit.thirdparty.com.google.common.io.Files;

import lombok.SneakyThrows;
import net.siudek.media.images.Images.Image;

public class ImagesTest {

  @TempDir
  File temp;


  @Test
  void shouldFindFiles() {
    var dirSchema = newDir("rootDir",
        newImage("image1.jpg"),
        newDir("dir1", newImage("image2.png")),
        newImage("image3.heic"));
    create(temp, dirSchema);

    var search = new Images();
    var actualIter = search.find(temp);
    var actual = IteratorUtils.toList(actualIter);

    Assertions.assertThat(actual)
      .as("Finds all images in cluding those located in subfolders")
      .containsExactlyInAnyOrder(new Image.JPG("image1"), new Image.PNG("image2"), new Image.HEIC("image3"));
  }

  @Test
  void shouldFindDataFiles() {
    var exampleMediaDir = Path.of("./data");

    var search = new Images();
    var actualIter = search.find(exampleMediaDir.toFile());
    var actual = IteratorUtils.toList(actualIter);

    Assertions.assertThat(actual)
      .as("Finds all images in cluding those located in subfolders")
      .containsExactlyInAnyOrder(new Image.JPG("vegetables"), new Image.PNG("cat"), new Image.HEIC("dog"));
  }

  sealed interface DirOrFile {

    record Image(String name) implements DirOrFile { }

    record Dir(String name, DirOrFile... images) implements DirOrFile { }

  }

  DirOrFile.Image newImage(String name) {
    return new DirOrFile.Image(name);
  }

  DirOrFile.Dir newDir(String name, DirOrFile... images) {
    return new DirOrFile.Dir(name, images);
  }

  @SneakyThrows
  static void create(File root, DirOrFile.Dir dir) {
    var curDir = new File(root, dir.name);
    curDir.mkdir(); // try to create current folder if it does not exist
    for (var f : dir.images()) {
      switch (f) {
        case DirOrFile.Dir it: {
          Path subdir = Paths.get(curDir.getAbsolutePath(), it.name());
          subdir.toFile().mkdir();
          create(subdir.toFile(), it);
          break;
        }
        case DirOrFile.Image it: {
          var file = Paths.get(curDir.getAbsolutePath(), it.name());
          Files.touch(file.toFile());
        }
      }
    }
  }
}
