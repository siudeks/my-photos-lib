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
  void shouldFileFiles() {

    var dir = newDir("a",
        newJPG("image1"),
        newDir("dir1"),
        newJPG("image2"));
    create(temp, dir);

    var search = new Images();
    var actualIter = search.find(temp);
    var actual = IteratorUtils.toList(actualIter);

    Assertions.assertThat(actual).containsExactlyInAnyOrder(new Image.JPG("image1"), new Image.JPG("image2"));
  }

  sealed interface DirOrFile {

    sealed interface Image extends DirOrFile {

      record JPG(String name) implements Image {
      }
    }

    record Dir(String name, DirOrFile... images) implements DirOrFile {
    }

  }

  DirOrFile.Image newJPG(String name) {
    return new DirOrFile.Image.JPG(name);
  }

  DirOrFile.Dir newDir(String name, DirOrFile... images) {
    return new DirOrFile.Dir(name, images);
  }

  @SneakyThrows
  static void create(File root, DirOrFile.Dir dir) {
    var curDir = new File(root, dir.name);
    curDir.mkdir();
    for (var f : dir.images()) {
      if (f instanceof DirOrFile.Image.JPG i) {
        Path file = Paths.get(curDir.getAbsolutePath(), i.name());
        Files.touch(file.toFile());
      } else if (f instanceof DirOrFile.Dir d) {
        Path subdir = Paths.get(curDir.getAbsolutePath(), d.name());
        subdir.toFile().mkdir();
        create(subdir.toFile(), d);
      }
    }

    for (var f : dir.images()) {
      if (f instanceof DirOrFile.Image.JPG i) {
        Path file = Paths.get(curDir.getAbsolutePath(), i.name());
        Files.touch(file.toFile());
      }
    }
  }

}
