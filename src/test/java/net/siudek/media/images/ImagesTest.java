package net.siudek.media.images;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.apache.commons.collections4.IteratorUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tngtech.archunit.thirdparty.com.google.common.io.Files;

import lombok.SneakyThrows;
import net.siudek.media.domain.Image;
import net.siudek.media.domain.ImageUtils;
import net.siudek.media.domain.MediaFile;

public class ImagesTest {

  @Test
  void shouldFindMediaFiles(@TempDir File temp) {
    // some list of files, to include subfolder and all image types which should be supported
    var dirSchema = newDir("rootDir",
        newImage("image1.jpg"),
        newDir("dir1", newImage("image2.png")),
        newImage("image3.heic"));
    var expected = create(temp, dirSchema);

    var actualIter = new Images().find(temp);
    var actual = IteratorUtils.toList(actualIter);


    Assertions.assertThat(actual)
      .as("Finds all images including those located in subfolders")
      .containsExactlyInAnyOrderElementsOf(expected);
  }

  @Test
  void shouldProcessExampleFiles() {
    var exampleMediaDir = Path.of("./data");

    var actualIter = new Images().find(exampleMediaDir.toFile());
    var actualSpliter = Spliterators.spliteratorUnknownSize(actualIter, Spliterator.NONNULL);
    var images = StreamSupport.stream(actualSpliter, false)
        .toList();
    

    for (var image: images) {
      switch (image) {
        case Image it: {
          @SuppressWarnings("unused")
          var asBase64 = ImageUtils.asJpegBase64(it);
        }
        default:
          break;
      }
    }
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

  // creates requested list of files and returns expected list of images 
  static Collection<Image> create(File root, DirOrFile.Dir dir) {
    return create(root, dir, new ArrayList<>());
  }

  @SneakyThrows
  static Collection<Image> create(File root, DirOrFile.Dir dir, Collection<Image> expected) {
    var curDir = new File(root, dir.name);
    curDir.mkdir(); // try to create current folder if it does not exist
    for (var f : dir.images()) {
      switch (f) {
        case DirOrFile.Dir it: {
          Path subdir = Paths.get(curDir.getAbsolutePath(), it.name());
          subdir.toFile().mkdir();
          create(subdir.toFile(), it, expected);
          break;
        }
        case DirOrFile.Image it: {
          var asPath = Paths.get(curDir.getAbsolutePath(), it.name());
          Files.touch(asPath.toFile());
          var asImage = ImageUtils.asMediaFile(asPath);
          switch (asImage) {
            case Image im: {
              expected.add(im);
            }
            default: break;
          }
          
        }
      }
    }
    return expected;
  }
}
