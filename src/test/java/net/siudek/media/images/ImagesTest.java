package net.siudek.media.images;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    Assertions.assertThat(search.find(temp)).containsExactly(new Image.JPG("image1"), new Image.JPG("image2"));
  }

  sealed interface DirOrFile {

    sealed interface Image extends DirOrFile {

      record JPG() implements Image {
      }
    }

    record Dir(String name, DirOrFile... images) implements DirOrFile {
    }

  }

  DirOrFile.Image newJPG(String name) {
    return new DirOrFile.Image.JPG();
  }

  DirOrFile.Dir newDir(String name, DirOrFile... images) {
    return new DirOrFile.Dir(name, images);
  }

  static void create(File root, DirOrFile.Dir dir) {
    new File(root, dir.name).mkdir();
  }

}
