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

    var dir = newDir("a",
        newJPG("image1"),
        newDir("dir1", newPNG("image2")),
        newHEIC("image3"));
    create(temp, dir);

    var search = new Images();
    var actualIter = search.find(temp);
    var actual = IteratorUtils.toList(actualIter);

    Assertions.assertThat(actual).containsExactlyInAnyOrder(new Image.JPG("image1"), new Image.PNG("image2"), new Image.HEIC("image3"));
  }

  sealed interface DirOrFile {

    record Image(String name, String ext) implements DirOrFile { }

    record Dir(String name, DirOrFile... images) implements DirOrFile { }

  }

  DirOrFile.Image newJPG(String name) {
    return new DirOrFile.Image(name, "jpg");
  }

  DirOrFile.Image newPNG(String name) {
    return new DirOrFile.Image(name, "png");
  }

  DirOrFile.Image newHEIC(String name) {
    return new DirOrFile.Image(name, "heic");
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
          Path file = Paths.get(curDir.getAbsolutePath(), it.name());
          Files.touch(file.toFile());
        }
      }
    }
  }
}
