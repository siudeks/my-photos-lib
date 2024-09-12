package net.siudek.media.images;

import java.io.File;
import java.io.IOException;
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

import net.siudek.media.domain.Image;
import net.siudek.media.domain.ImageUtils;

public class ImagesTest {

  @Test
  void shouldFindMediaFiles(@TempDir Path temp) throws IOException {
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
  void shouldProcessExampleFiles() throws Exception {
    var exampleMediaDir = Path.of("./data");

    var actualIter = new Images().find(exampleMediaDir);
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
  static Collection<Image> create(Path root, DirOrFile.Dir dir) throws IOException {
    return create(root, dir, new ArrayList<>());
  }

  static Collection<Image> create(Path root, DirOrFile.Dir dir, Collection<Image> expected) throws IOException {
    var curDir = new File(root.toFile(), dir.name);
    curDir.mkdir(); // try to create current folder if it does not exist
    for (var f : dir.images()) {
      switch (f) {
        case DirOrFile.Dir it: {
          Path subdir = Paths.get(curDir.getAbsolutePath(), it.name());
          subdir.toFile().mkdir();
          create(subdir, it, expected);
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
